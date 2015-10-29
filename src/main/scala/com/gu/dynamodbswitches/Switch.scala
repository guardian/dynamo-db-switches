package com.gu.dynamodbswitches

case class Switch(name: String, default: Boolean = false) {
  @volatile private var state: Boolean = default

  def enabled: Boolean = state

  def enabled_=(value: Boolean) = state = value
}