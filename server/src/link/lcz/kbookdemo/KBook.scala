package link.lcz.kbookdemo

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import link.lcz.kbookdemo.Dag.NodeType
import link.lcz.kbookdemo.logicnode.LogicNode
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

class KBook(val config: KBookConfig, ctx: KBook.Context) {
  val uuid: String = config.meta.uuid

  val streamsConfiguration: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "generic-avro-scala-integration-test")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ctx.schemaRegistry)
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val streams: KafkaStreams = {
    val topology = ctx.builder.build()
    println(topology.describe())
    new KafkaStreams(topology, streamsConfiguration)
  }

  def start(): Unit = streams.start()

  def stop(): Unit = streams.close()
}

object KBook extends LazyLogging {

  def apply(config: KBookConfig): KBook = {
    val ctx = KBook.Context("schema")

    val linearized = config.dag.dag
      .ensuring(_.isAcyclic, throw new RuntimeException("The graph is not acyclic"))
      .ensuring(_.isDirected, throw new RuntimeException("The graph is directed"))
      .ensuring(_.isConnected, throw new RuntimeException("The graph is connected"))
      .topologicalSort.right.getOrElse(throw new RuntimeException("The graph has no node"))

    linearized.foldLeft(List[LogicNode]())((r, n) => {
      val inbounds = n.incoming.toSeq
        .map { edge => r.find(_.uuid == edge.from.toOuter.meta.uuid) -> edge.label.asInstanceOf[Dag.EdgeMeta] }
        .collect { case (Some(node), edge) => node -> edge }
        .sortBy(_._2.toPort)
        .foldLeft(LogicNode.Bounds()) { (ib, x) =>
          ib :+ x._1.asInstanceOf[logicnode.Predecessor].outbounds(x._2.fromPort)
        }

      val nodeConfig = n.toOuter

      val logicNode = nodeConfig.meta.`type` match {
        case NodeType.Source => logicnode.Source(ctx, nodeConfig)
        case NodeType.Transformer => logicnode.Transformer(ctx, nodeConfig, inbounds)
        case NodeType.Sink => logicnode.Sink(ctx, nodeConfig, inbounds)
      }

      r :+ logicNode
    })

    new KBook(config, ctx)
  }

  case class Context(schemaRegistry: String) {
    val builder = new StreamsBuilder

    object Serdes {
      implicit val simpleSerdes = org.apache.kafka.streams.scala.Serdes
      implicit val conversions =
        org.apache.kafka.streams.scala.ImplicitConversions

      implicit val genericAvroValueSerde: GenericAvroSerde = {
        val gas = new GenericAvroSerde
        gas.configure(java.util.Collections.singletonMap(
          AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
          schemaRegistry),
          false)
        gas
      }
    }

  }

}
