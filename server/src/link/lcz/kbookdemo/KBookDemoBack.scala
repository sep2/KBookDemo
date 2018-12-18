package link.lcz.kbookdemo

import java.io.File
import java.time.Duration
import java.util.Properties

import ch.qos.logback.classic.Level
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.avro.generic.{GenericData, GenericRecord, GenericRecordBuilder}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.{Schema, SchemaBuilder}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.kstream.KTable



object KBookDemoBack {
//  val sbr = SchemaBuilder.record("GR1").fields()
//    .name("name")
////  val schema = new Schema.Parser().parse(new File("user.json"))
//  val grb = new GenericRecordBuilder(schema)
//  grb.set("name", "felix")
//  grb.set("age", 30)
//  grb.set("color", "green")
//  println(grb.build())
//
//  KTable[String, String]
  //  org.slf4j.LoggerFactory.getLogger("org.apache.kafka").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)
  //
  //  import org.apache.kafka.streams.scala.Serdes._
  //  //import org.apache.kafka.streams.scala.ImplicitConversions._
  //
  //  private val inputTopic = "input-topic"
  //  private val outputTopic = "output-topic"
  //  private val schemaRegistryUrl = "http://localhost:8081"
  //
  //  val streamsConfiguration: Properties = {
  //    val p = new Properties()
  //    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "generic-avro-scala-integration-test")
  //    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  //    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
  //    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  //    p
  //  }
  //
  //  implicit val genericAvroValueSerde: GenericAvroSerde = {
  //    val gas = new GenericAvroSerde
  //    gas.configure(java.util.Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl), false)
  //    gas
  //  }
  //
  //  val builder = new StreamsBuilder
  //
  //  // Write the input data as-is to the output topic.
  //  builder.stream[String, String](inputTopic).map[String, GenericRecord]((key, value) => {
  //    import org.apache.avro.Schema
  //    val schema = new Schema.Parser().parse(new File("user.avsc"))
  //    val grb = new GenericRecordBuilder(schema)
  //    (key, grb.build())
  //  }).to(outputTopic)
  //
  //  val streams: KafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration)
  //  streams.start()
}



//package link.lcz.kbookdemo
//
//import link.lcz.kbookdemo.Dag
//import net.liftweb.json.{JArray, JObject}
//import scalax.collection.Graph
//import scalax.collection.edge.LDiEdge
//import scalax.collection.io.json._
//
//object KBookDemo extends App {
//  val (source, transformer, sink) = (
//    Dag.LogicNode(
//      Dag.MetaInfo(uuid = "1", name = "x", `type` = "source", clazz = "a.b.c"),
//      config = JObject(),
//      inbounds = List(Dag.BoundObject(config = JObject())),
//      outbounds = List()),
//    Dag.LogicNode(Dag.MetaInfo(uuid = "2",
//      name = "y",
//      `type` = "transformer",
//      clazz = "a.b.d"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List()),
//    Dag.LogicNode(
//      Dag.MetaInfo(uuid = "3", name = "z", `type` = "sink", clazz = "a.b.e"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List())
//  )
//
//  val dag = Graph[Dag.LogicNode, LDiEdge](
//    LDiEdge(source, transformer)(Dag.BoundLabel(0, 0)),
//    LDiEdge(transformer, sink)(Dag.BoundLabel(0, 0)))
//  println(dag.toJson(DAG.Parser.descriptor))
//}


//package link.lcz.kbookdemo
//
//import java.time.Duration
//import java.util.Properties
//
//import ch.qos.logback.classic.Level
//import org.apache.kafka.streams.kstream.Materialized
//import org.apache.kafka.streams.scala.ImplicitConversions._
//import org.apache.kafka.streams.scala._
//import org.apache.kafka.streams.scala.kstream._
//import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
//
//object KBookDemo extends App {
//  org.slf4j.LoggerFactory.getLogger("org.apache.kafka").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)
//
//  import Serdes._
//
//  val props: Properties = {
//    val p = new Properties()
//    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
//    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
//    p
//  }
//
//  val builder: StreamsBuilder = new StreamsBuilder
//  val textLines: KStream[String, String] = builder.stream[String, String]("TextLinesTopic")
//  val wordCounts: KTable[String, Long] = textLines
//    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+"))
//    .groupBy((_, word) => word)
//    .count()(Materialized.as("counts-store"))
//  wordCounts.toStream.to("WordsWithCountsTopic")
//
//  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
//  streams.start()
//
//  sys.ShutdownHookThread {
//    streams.close(Duration.ofSeconds(10))
//  }
//}