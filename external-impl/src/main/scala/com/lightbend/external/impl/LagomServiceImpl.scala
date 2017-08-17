package com.lightbend.external.impl

import java.net.URI

import akka.NotUsed
import com.google.inject.Inject
import com.lightbend.external.api.{ExternalService, LagomService}
import com.lightbend.lagom.scaladsl.api.{ServiceCall, ServiceLocator}
import com.lightbend.lagom.scaladsl.client.{LagomClientApplication, StaticServiceLocatorComponents}
import play.api.libs.ws.ahc.AhcWSComponents


class LagomServiceImpl @Inject() (serviceLocator: ServiceLocator) extends LagomService {

  override def speak(id: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    val spring = serviceLocator.locate("external")
    print(spring)
    callExternalService(id, serviceLocator)
  }

  private def callExternalService (id: String, serviceLocator: ServiceLocator) = {
    val clientApp = new LagomClientApplication("external-client")
      with StaticServiceLocatorComponents
      with AhcWSComponents {
      override def staticServiceUri: URI = URI.create("http://localhost:8080")
    }

    val externalService = clientApp.serviceClient.implement[ExternalService]
    externalService.echo(id).invoke()
  }

}