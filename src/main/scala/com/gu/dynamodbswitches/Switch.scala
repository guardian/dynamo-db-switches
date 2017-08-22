package com.gu.dynamodbswitches

import com.amazonaws.services.dynamodbv2.model.AttributeValue

case class Switch(name: String, default: Boolean = false) {
  @volatile private var state: Boolean = default

  def enabled: Boolean = state

  def enabled_=(value: Boolean) = state = value

  def toStringAttribute() = {
    new AttributeValue().withS(this.name)
  }

  def asAttributeValue(b: Boolean) = {
    val str = if(b) "1" else "0"
    new AttributeValue().withN(str)
  }
}