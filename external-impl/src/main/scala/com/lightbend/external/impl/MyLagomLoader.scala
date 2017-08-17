package com.lightbend.external.impl

import com.lightbend.external.api.{ExternalService, LagomService}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class MyLagomLoader extends LagomApplicationLoader{

  override def load(context: LagomApplicationContext): LagomApplication =
    new MyLagomApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MyLagomApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[ExternalService])
}

abstract class MyLagomApplication(context: LagomApplicationContext) extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[LagomService](wire[LagomServiceImpl])
  lazy val externalService = serviceClient.implement[ExternalService]

}
