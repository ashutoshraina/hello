package com.lightbend.lagom.impl

import com.lightbend.external.api.{LibertyService, LagomService}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.dns.DnsServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class LagomLoader extends LagomApplicationLoader{

  override def load(context: LagomApplicationContext): LagomApplication =
    new MyLagomApplication(context) {
      override def serviceLocator: ServiceLocator = DnsServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MyLagomApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LibertyService])
}

abstract class MyLagomApplication(context: LagomApplicationContext) extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[LagomService](wire[LagomServiceImpl])
  lazy val libertyService = serviceClient.implement[LibertyService]

}
