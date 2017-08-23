package com.gu.dynamodbswitches.lib

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.amazonaws.services.dynamodbv2.model.{ScanResult, ScanRequest}
import com.amazonaws.handlers.AsyncHandler
import scala.concurrent.Promise
import scala.util.{Failure, Success}

object DynamoDB {
  implicit class RichDynamoDbClient(client: AmazonDynamoDBAsync) {
    private def createPromiseHandler[A <: com.amazonaws.AmazonWebServiceRequest, B]() = {
      val promise = Promise[B]()

      val handler = new AsyncHandler[A, B] {
        override def onSuccess(request: A, result: B): Unit = promise.complete(Success(result))

        override def onError(exception: Exception): Unit = promise.complete(Failure(exception))
      }

      (promise, handler)
    }

    def scanFuture(scanRequest: ScanRequest) = {
      val (promise, handler) = createPromiseHandler[ScanRequest, ScanResult]()
      client.scanAsync(scanRequest, handler)
      promise.future
    }
  }
}
