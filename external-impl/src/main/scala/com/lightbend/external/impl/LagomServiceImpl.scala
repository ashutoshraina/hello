package com.lightbend.external.impl

import java.net.URI

import akka.NotUsed
import com.lightbend.external.api.{ExternalService, LagomService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.client.{LagomClientApplication, StaticServiceLocatorComponents}
import play.api.libs.ws.ahc.AhcWSComponents


class LagomServiceImpl extends LagomService {

  override def speak(id: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    callExternalService(id)
  }

  private def callExternalService (id: String) = {
    val clientApp = new LagomClientApplication("external-client")
      with StaticServiceLocatorComponents
      with AhcWSComponents {
      override def staticServiceUri: URI = URI.create("http://localhost:8080")
    }

    val externalService = clientApp.serviceClient.implement[ExternalService]
    externalService.echo(id).invoke()
  }

}