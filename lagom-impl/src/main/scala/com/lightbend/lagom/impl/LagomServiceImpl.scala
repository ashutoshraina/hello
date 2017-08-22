package com.lightbend.lagom.impl

import akka.NotUsed
import com.lightbend.external.api.{LibertyService, LagomService}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext


class LagomServiceImpl(externalService: LibertyService)(implicit ec: ExecutionContext) extends LagomService {

  override def speak(): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    externalService.echo().invoke().map(result => s"External Service Replied with --- \n$result")
  }

}