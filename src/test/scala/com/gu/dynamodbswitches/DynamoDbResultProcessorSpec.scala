package com.gu.dynamodbswitches

import org.scalacheck._
import Prop._
import Arbitrary.arbitrary
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DynamoDbResultProcessorSpec extends Properties("DynamoDbResultProcessor") {
  val switchNameGenerator: Gen[String] = Gen.oneOf((1 to 20).map(i => s"switch_$i"))

  implicit class RichList[A](xs: List[A]) {
    def distinctBy[B](key: A => B) = xs.groupBy(key).mapValues(_.head).values.toList
  }

  implicit val arbitrarySwitch = Arbitrary {
    for {
      switchName <- switchNameGenerator
      enabled <- arbitrary[Boolean]
    } yield Switch(switchName, enabled)
  }

  implicit val arbitraryProcessor: Arbitrary[DynamoDbResultProcessor] = Arbitrary {
    for {
      switches <- arbitrary[List[Switch]].suchThat(_.nonEmpty)
    } yield new DynamoDbResultProcessor(switches.distinctBy(_.name))
  }

  property("process") = forAll { (processor: DynamoDbResultProcessor) =>
    implicit val arbitraryDynamoDbSwitch = Arbitrary {
      for {
        switch <- Gen.oneOf(processor.switches)
        state <- arbitrary[Boolean]
      } yield Map(
        processor.DynamoDbKeyName -> AttributeValue.builder().s(switch.name).build(),
        processor.DynamoDbValueName -> AttributeValue.builder().n((if (state) 1 else 0).toString).build()
      )
    }

    implicit val arbitraryUpdateSet = Arbitrary {
      for {
        updates <- arbitrary[List[Map[String, AttributeValue]]]
      } yield updates.distinctBy(_(processor.DynamoDbKeyName).s())
    }

    forAll { (updates: List[Map[String, AttributeValue]]) =>
      val ProcessingResults(switchUpdates, missing) = processor.process(updates)

      val missingSwitchKeys =
        processor.switches.map(_.name).toSet.diff(updates.map(_(processor.DynamoDbKeyName).s()).toSet).size

      val stateUpdates = updates count { update =>
        processor.switches.exists(switch => switch.name == update(processor.DynamoDbKeyName).s() &&
          switch.enabled != (update(processor.DynamoDbValueName).n().toInt == 1))
      }

      switchUpdates.size == stateUpdates && missing.size == missingSwitchKeys
    }
  }
}
