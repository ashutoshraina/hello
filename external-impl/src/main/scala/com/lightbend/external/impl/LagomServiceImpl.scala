package com.lightbend.external.impl

import akka.NotUsed
import com.lightbend.external.api.{ExternalService, LagomService}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext


class LagomServiceImpl(externalService: ExternalService)(implicit ec: ExecutionContext) extends LagomService {

  override def speak(id: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    externalService.echo(id).invoke().map(result => s"External Service Replied with --- $result")
  }

}