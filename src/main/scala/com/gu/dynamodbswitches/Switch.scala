package com.gu.dynamodbswitches

case class Switch(name: String, default: Boolean) {
  @volatile private var state: Boolean = default

  def enabled: Boolean = state

  private[dynamodbswitches] def enabled_=(value: Boolean) = state = value
}