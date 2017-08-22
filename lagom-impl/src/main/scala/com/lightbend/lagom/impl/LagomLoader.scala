package com.lightbend.lagom.impl

import java.net.URI
import javax.inject.{Inject, Named}

import akka.actor.ActorSystem
import akka.pattern.AskableActorRef
import com.lightbend.dns.locator.{Settings, ServiceLocator => ServiceLocatorService}
import com.lightbend.external.api.{LagomService, LibertyService}
import com.lightbend.lagom.scaladsl.api
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.client.LagomServiceClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.{ExecutionContext, Future}


class LagomLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new MyLagomApplication(context) with KubernetesRuntimeComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MyLagomApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LibertyService])
}

sealed trait KubernetesRuntimeComponents extends LagomServiceClientComponents {
  val serviceLocatorService = actorSystem.actorOf(ServiceLocatorService.props, ServiceLocatorService.Name)

  lazy val serviceLocator: DnsServiceLocator =
    new DnsServiceLocator(
      serviceLocatorService,
      this.actorSystem,
      this.actorSystem.dispatcher
    )
}

abstract class MyLagomApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[LagomService](wire[LagomServiceImpl])
  lazy val libertyService = serviceClient.implement[LibertyService]

}

class DnsServiceLocator @Inject()(
                                   @Named("ServiceLocatorService") serviceLocatorService: AskableActorRef,
                                   system: ActorSystem,
                                   implicit val ec: ExecutionContext) extends api.ServiceLocator {

  val settings = Settings(system)

  override def locate(name: String, serviceCall: Descriptor.Call[_, _]): Future[Option[URI]] =
    serviceLocatorService
      .ask(ServiceLocatorService.GetAddress(name))(settings.resolveTimeout1 + settings.resolveTimeout1 + settings.resolveTimeout2)
      .mapTo[ServiceLocatorService.Addresses]
      .map {
        case ServiceLocatorService.Addresses(addresses) =>
          addresses
            .headOption
            .map(sa => new URI(sa.protocol, null, sa.host, sa.port, null, null, null))
      }


  override def doWithService[T](name: String, serviceCall: Descriptor.Call[_, _])(block: (URI) => Future[T])
                               (implicit ec: ExecutionContext): Future[Option[T]] = {
    locate(name).flatMap(uriOpt => {
      uriOpt.fold(Future.successful(Option.empty[T])) { uri =>
        block.apply(uri)
          .map(v => Option.apply(v))
      }
    })

  }

  override def locate(name: String): Future[Option[URI]] = locate(name, Descriptor.NoCall)
}
