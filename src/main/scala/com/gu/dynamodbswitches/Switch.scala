package com.gu.dynamodbswitches

import software.amazon.awssdk.services.dynamodb.model.{AttributeValue, PutRequest, WriteRequest}

import scala.jdk.CollectionConverters._

sealed trait Switchable {

  def name: String

  def default: Boolean = false

  @volatile private var state: Boolean = default

  def enabled: Boolean = state

  def enabled_=(value: Boolean): Unit = state = value

  def writeRequest: WriteRequest
}

object Switchable {
  def fromAttributeValues(attribVals: Map[String, AttributeValue]): Switchable = {
    val name = attribVals("name").s()
    val maybeString = attribVals.get("enabled").map(_.n())
    maybeString match {
      case None =>
        val percentage = attribVals("percentage").n().toDouble
        DimmerSwitch(name, percentage)
      case Some(m) =>
        val enabled = m == "1"
        Switch(name, enabled)
    }
  }
}

case class Switch(name: String, override val default: Boolean = false) extends Switchable {
  override def writeRequest: WriteRequest = {
    val putRequest: PutRequest = PutRequest
      .builder()
      .item(
        Map(
          "name" -> AttributeValue.builder().s(name).build(),
          "enabled" -> AttributeValue.builder().n((if (enabled) 1 else 0).toString).build()
        ).asJava
      )
      .build()
    val writeRequest: WriteRequest = WriteRequest.builder().putRequest(putRequest).build()
    writeRequest
  }
}

case class DimmerSwitch(name: String, defaultPercentage: Double = 0) extends Switchable {

  override def default: Boolean = defaultPercentage > 0

  def percentage: Double = defaultPercentage

  override def writeRequest: WriteRequest = ???
}
