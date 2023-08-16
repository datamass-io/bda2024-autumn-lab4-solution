package io.datamass

import akka.actor.ActorSystem
import akka.kafka.{ProducerSettings}
import akka.kafka.scaladsl.{SendProducer}
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{DurationInt}

object EventBProducer extends App {
  implicit val system = ActorSystem("SimpleStream")
  val config = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)


  val producer = SendProducer(producerSettings)
  for(i <- 1 to 30) {
    val send: Future[RecordMetadata] = producer
      .send(new ProducerRecord("topicb", "{\"id\":\"abc123\", \"value\":\"" + i.toString + "\"}"))
    println("msg sent " + i.toString )
    Await.result(send, 10.seconds)
  }
}
