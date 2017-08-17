package com.lightbend.hello.impl

import com.lightbend.hello.api
import com.lightbend.hello.api.HelloService
import com.lightbend.hello.random.api.RandomService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import scala.concurrent.ExecutionContext

class HelloServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                       randomService: RandomService)
                      (implicit ec: ExecutionContext) extends HelloService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the Hello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    // Ask the entity the Hello command, and get their random number

    val greetingResult = ref.ask(Hello(id))
    val randomNumberResult = randomService.random().invoke()

    for {
      greeting <- greetingResult
      randomNumber <- randomNumberResult
    } yield greeting.replaceAllLiterally("%num%", randomNumber.yourNumber.toString)
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[HelloEntity](id)

    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(HelloEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[HelloEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
