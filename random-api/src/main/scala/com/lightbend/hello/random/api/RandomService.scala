package com.lightbend.hello.random.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json._

trait RandomService extends Service {
  def random(): ServiceCall[NotUsed, RandomPayload]

  override final def descriptor = {
    import Service._

    named("random").withCalls(restCall(Method.GET, "/", random _))
  }
}

case class RandomPayload(yourNumber: Int)

object RandomPayload {
  implicit val format: Format[RandomPayload] = Json.format
}