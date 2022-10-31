package com.gu.dynamodbswitches

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

case class Switch(name: String, default: Boolean = false) {
  @volatile private var state: Boolean = default

  def enabled: Boolean = state

  def enabled_=(value: Boolean): Unit = state = value

  def toStringAttribute(): AttributeValue = {
    AttributeValue.builder().s(this.name).build()
  }

  def asAttributeValue(b: Boolean): AttributeValue = {
    val str = if(b) "1" else "0"
    AttributeValue.builder().n(str).build()
  }
}