package com.gu.dynamodbswitches

import collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScanRequest}
import grizzled.slf4j.Logging
import com.amazonaws.AmazonServiceException
import lib.DynamoDB._
import scala.concurrent.ExecutionContext

trait Switches extends Logging {
  val all: List[Switch]

  def dynamoDbClient: AmazonDynamoDBAsyncClient
  val dynamoDbTableName: String = "featureSwitches"

  lazy private val processor = DynamoDbResultProcessor(all)

  /** Use a scheduler to call this once per minute */
  def update(implicit executionContext: ExecutionContext): Unit = {
    try {
      dynamoDbClient.scanFuture(new ScanRequest(dynamoDbTableName)) foreach { scanResult =>
        val results = scanResult.getItems.asScala.toList.map(_.asScala.toMap)
        val ProcessingResults(updates, missing) = processor.process(results)

        if (missing.nonEmpty) {
          warn(s"DynamoDB did not return some switches: ${missing.toList.map(_.name).sorted.mkString(", ")}")
        }

        for ((switch, newState) <- updates) {
          info(s"Setting switch ${switch.name} to ${newState.toString}")
          switch.enabled = newState
        }
      }
    } catch {
      case exception: AmazonServiceException =>
        error(s"Encountered Amazon service error when trying to update switches from DynamoDB: ${exception.getMessage}")
    }
  }
}

private [dynamodbswitches] case class ProcessingResults(updates: Set[(Switch, Boolean)], missing: Set[Switch])

private [dynamodbswitches] case class DynamoDbResultProcessor(switches: List[Switch]) {
  val DynamoDbKeyName: String = "name"
  val DynamoDbValueName: String = "enabled"

  private val byName = switches.map(switch => switch.name -> switch).toMap
  private val switchSet = switches.toSet

  def process(updates: List[Map[String, AttributeValue]]) = {
    val switchesAndStates = (updates flatMap { attributeMap =>
      for {
        keyAttribute <- attributeMap.get(DynamoDbKeyName)
        key <- Option(keyAttribute.getS)
        enabledAttribute <- attributeMap.get(DynamoDbValueName)
        enabled <- Option(enabledAttribute.getN).map(_.toInt == 1)
        switch <- byName.get(key)
      } yield switch -> enabled
    }).toSet

    ProcessingResults(
      switchesAndStates filter { case (switch, state) => switch.enabled != state },
      switchSet.diff(switchesAndStates.map(_._1).toSet)
    )
  }
}