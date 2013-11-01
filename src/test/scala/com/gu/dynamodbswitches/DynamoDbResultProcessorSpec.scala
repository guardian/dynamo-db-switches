package com.gu.dynamodbswitches

import org.scalacheck._
import Prop._
import Arbitrary.arbitrary
import com.amazonaws.services.dynamodbv2.model.AttributeValue

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
      switches <- arbitrary[List[Switch]]
    } yield new DynamoDbResultProcessor(switches.distinctBy(_.name))
  }

  property("process") = forAll { (processor: DynamoDbResultProcessor) =>
    implicit val arbitraryDynamoDbSwitch = Arbitrary {
      for {
        switch <- Gen.oneOf(processor.switches)
        state <- arbitrary[Boolean]
      } yield Map(
        processor.DynamoDbKeyName -> new AttributeValue(processor.DynamoDbKeyName).withS(switch.name),
        processor.DynamoDbValueName -> new AttributeValue(processor.DynamoDbValueName).withN(
          (if (state) 1 else 0).toString)
      )
    }

    implicit val arbitraryUpdateSet = Arbitrary {
      for {
        updates <- arbitrary[List[Map[String, AttributeValue]]]
      } yield updates.distinctBy(_(processor.DynamoDbKeyName).getS)
    }

    forAll { (updates: List[Map[String, AttributeValue]]) =>
      val ProcessingResults(switchUpdates, missing) = processor.process(updates)

      val missingSwitchKeys =
        processor.switches.map(_.name).toSet.diff(updates.map(_(processor.DynamoDbKeyName).getS).toSet).size

      val stateUpdates = updates count { update =>
        processor.switches.exists(switch => switch.name == update(processor.DynamoDbKeyName).getS &&
          switch.enabled != (update(processor.DynamoDbValueName).getN.toInt == 1))
      }

      switchUpdates.size == stateUpdates && missing.size == missingSwitchKeys
    }
  }
}
