package io.datamass

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer, SendProducer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import io.datamass.EventBProducer.{producer, producerSettings}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}
import sun.security.rsa.RSAUtil.KeyType

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object EventConsBProdC extends App{
  implicit val system:ActorSystem = ActorSystem.apply("akka-stream-kafka")
  implicit val materializer:ActorMaterializer = ActorMaterializer()

  // grab our settings from the resources/application.conf file
  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val config = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)

  val producer = SendProducer(producerSettings)

  // listen to our topic with our settings, until the program is exited
  Consumer.plainSource(consumerSettings, Subscriptions.topics("topicb"))
    .mapAsync(1) ( msg => {
      // print out our message once it's received
      println(s"Message Received : ${msg.timestamp} - ${msg.value}")
      Future.successful(msg)
    })
    .map(msg => {
      val data = ujson.read(msg.value)
      println(data("id").str)
      println(data("value").str)

      val send: Future[RecordMetadata] = producer
        .send(new ProducerRecord("topicc", "{\"id\":\"abc123\", \"value\":\"" + data("value").str + "--modified-by-ConsumerB" + "\"}"))
      println("msg sent to topic C" )
      Await.result(send, 10.seconds)

    })
    .runWith(Sink.ignore)



}
