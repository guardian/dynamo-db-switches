# Dynamo DB Feature Switches

Feature switches in Dynamo DB.

## Usage

### Schema

Create a DynamoDB table called 'featureSwitches' with the following schema:

```
+-------------------+---------------+
| Hash key (String) | Number        |
+-------------------+---------------+
| name              | enabled       |
+-------------------+---------------+
```

Set enabled to `1` to enable the feature switch, `0` to disable it.

### Switch instantiation

Define your switches in an object

```scala

object ApplicationSwitches extends Switches {
  val dynamoDbClient = // define your dynamo DB client here

  val someWonderfulFeatureSwitch = Switch("nameOfSwitch", default = false)

  // make sure you put all switches in here or they won't update
  val all = List(someWonderfulFeatureSwitch)
}

```

### Updates

Use a scheduler to update the switches from Dynamo DB once per minute

```scala

Akka.scheduler.schedule(1.minute, 1.minute) { ApplicationSwitches.update() }

```

### Testing switches

Use as follows:

```scala

if (ApplicationSwitches.someWonderfulFeatureSwitch.enabled) {
  // do something
}

```

## Copyright

Copyright 2013 Guardian Media Group. Licensed under Apache 2.0. (See `LICENSE`.)
