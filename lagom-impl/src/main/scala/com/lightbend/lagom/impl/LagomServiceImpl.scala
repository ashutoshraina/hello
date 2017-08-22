package com.lightbend.lagom.impl

import akka.NotUsed
import com.lightbend.external.api.{LagomService, LibertyService}
import com.lightbend.lagom.scaladsl.api.{ServiceCall, ServiceLocator}

import scala.concurrent.ExecutionContext


class LagomServiceImpl(externalService: LibertyService, serviceLocator: ServiceLocator)(implicit ec: ExecutionContext) extends LagomService {

  override def speak(): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    // TODO: if the echo() doesn't work, uncomment below, and see if the service locator can resolve the service using
    // its service name or raw SRV entry.
    // Also confirm that serviceLocator is an instance of DnsServiceLocator.
    //val libertyServiceName = "libertyservice"
    //serviceLocator.locate(libertyServiceName ).map(v => s"SRV $libertyServiceSrvName: got $v")

    //val libertyServiceSrvName = "liberty service SRV value record goes here"
    //serviceLocator.locate(libertyServiceSrvName).map(v => s"SRV $libertyServiceSrvName: got $v")

    externalService.echo().invoke().map(result => s"External Service Replied with --- \n$result")
  }

}