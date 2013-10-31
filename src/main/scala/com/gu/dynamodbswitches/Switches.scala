package com.gu.dynamodbswitches

import collection.JavaConverters._

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import grizzled.slf4j.Logging

trait Switches extends Logging {
  val all: List[Switch]

  private val switchSet = all.toSet

  private val byName = all.map(switch => switch.name -> switch).toMap

  val dynamoDbClient: AmazonDynamoDBClient
  val dynamoDbTableName: String = "switches"
  val dynamoDbKeyName: String = "name"
  val dynamoDbValueName: String = "enabled"

  /** Use a scheduler to call this once per minute */
  def update(): Unit = {
    val result = dynamoDbClient.scan(new ScanRequest(dynamoDbTableName))

    val switchesAndStates = result.getItems.asScala flatMap { attributeMap =>
      val scalaMap = attributeMap.asScala

      for {
        keyAttribute <- scalaMap.get(dynamoDbKeyName)
        key <- Option(keyAttribute.getS)
        enabledAttribute <- scalaMap.get(dynamoDbValueName)
        enabled <- Option(enabledAttribute.getN).map(_.toInt == 1)
        switch <- byName.get(key)
      } yield switch -> enabled
    }

    val switchesNotFound = switchSet.diff(switchesAndStates.map(_._1).toSet)

    if (switchesNotFound.nonEmpty) {
      warn(s"DynamoDB did not return some switches: ${switchesNotFound.toList.map(_.name).sorted.mkString(", ")}")
    }

    for {
      (switch, newState) <- switchesAndStates
    } if (switch.enabled != newState) {
      info(s"Setting switch ${switch.name} to ${newState.toString}")
      switch.enabled = newState
    }
  }
}
