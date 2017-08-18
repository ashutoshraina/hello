package com.lightbend.external.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait LagomService extends Service {

  def speak(id: String): ServiceCall[NotUsed, String]

  override final def descriptor = {
    import Service._
    named("lagom")
      .withCalls(
        pathCall("/lagom/:toEcho", speak _)
      ).withAutoAcl(true)
  }
}
