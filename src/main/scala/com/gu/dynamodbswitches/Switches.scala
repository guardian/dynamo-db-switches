package com.gu.dynamodbswitches

import collection.JavaConverters._
import software.amazon.awssdk.core.exception.SdkServiceException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.{
  AttributeValue,
  BatchWriteItemRequest,
  PutRequest,
  ScanRequest,
  WriteRequest
}
import grizzled.slf4j.Logging

trait Switches extends Logging {
  val all: List[Switch]

  def dynamoDbClient: DynamoDbClient
  val dynamoDbTableName: String = "featureSwitches"

  lazy private val processor = DynamoDbResultProcessor(all)

  /** Use a scheduler to call this once per minute */
  def update(): Unit = {
    try {
      val scanRequest: ScanRequest = ScanRequest.builder().tableName(dynamoDbTableName).build()
      val results = dynamoDbClient.scan(scanRequest).items().asScala.toList.map(_.asScala.toMap)
      val ProcessingResults(updates, missing) = processor.process(results)
      if (missing.nonEmpty) {
        warn(
          s"DynamoDB did not return some switches: ${missing.toList.map(_.name).sorted.mkString(", ")}"
        )
      }
      for ((switch, newState) <- updates) {
        info(s"Setting switch ${switch.name} to ${newState.toString}")
        switch.enabled = newState
      }
    } catch {
      case exception: SdkServiceException =>
        error(
          s"Encountered Amazon service error when trying to update switches from DynamoDB: ${exception.getMessage}"
        )
    }
  }

  def updateDynamo(switches: List[Switchable]) = {
    val listWR = switches
      .map(switch => {
        switch.writeRequest
      })
      .asJava
    try {
      dynamoDbClient.batchWriteItem(
        BatchWriteItemRequest
          .builder()
          .requestItems(Map(dynamoDbTableName -> listWR).asJava)
          .build()
      )
    } catch {
      case e: Exception => error(e)
      case _            => warn("Something went wrong")
    }
  }

  def getSwitchboardState: List[Switch] = {
    dynamoDbClient
      .scan(ScanRequest.builder().tableName(dynamoDbTableName).build())
      .items
      .asScala
      .toList
      .map(item => {
        val scalaItem = item.asScala.toMap
        Switch(scalaItem("name").s(), scalaItem("enabled").n() == "1")
      })
  }
}

private[dynamodbswitches] case class ProcessingResults(
    updates: Set[(Switch, Boolean)],
    missing: Set[Switch]
)

private[dynamodbswitches] case class DynamoDbResultProcessor(switches: List[Switch]) {
  val DynamoDbKeyName: String = "name"
  val DynamoDbValueName: String = "enabled"

  private val byName = switches.map(switch => switch.name -> switch).toMap
  private val switchSet = switches.toSet

  def process(updates: List[Map[String, AttributeValue]]): ProcessingResults = {
    val switchesAndStates = (updates flatMap { attributeMap =>
      f(attributeMap)
    }).toSet

    ProcessingResults(
      switchesAndStates filter { case (switch, state) => switch.enabled != state },
      switchSet.diff(switchesAndStates.map(_._1).toSet)
    )
  }

  private def f(attributeMap: Map[String, AttributeValue]) = {
    for {
      keyAttribute <- attributeMap.get(DynamoDbKeyName)
      key <- Option(keyAttribute.s())
      enabledAttribute <- attributeMap.get(DynamoDbValueName)
      enabled <- Option(enabledAttribute.n()).map(_.toInt == 1)
      switch <- byName.get(key)
    } yield switch -> enabled
  }
}
