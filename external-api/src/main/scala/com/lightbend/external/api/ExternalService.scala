package com.lightbend.external.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait ExternalService extends Service {

  def echo(id: String): ServiceCall[NotUsed, String]

  override final def descriptor = {
    import Service._
    named("external")
      .withCalls(
        restCall(Method.GET, "/echo?name", echo _)
      ).withAutoAcl(true)
  }
}

