package com.lightbend.external.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait LibertyService extends Service {

  def echo(): ServiceCall[NotUsed, String]

  override final def descriptor = {
    import Service._
    named("liberty")
      .withCalls(
        restCall(Method.GET, "/servlet/servlet", echo)
      ).withAutoAcl(true)
  }
}

