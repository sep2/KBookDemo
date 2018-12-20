package link.lcz.kbookdemo

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import link.lcz.kbookdemo.logicnode.LogicNode
import org.apache.kafka.streams.scala.kstream.{Consumed, KStream}
import org.apache.kafka.streams.{KafkaStreams, Topology}

class KBook(ctx: KBook.Context) extends LazyLogging {
  val uuid: String = ctx.config.meta.uuid

  val streamsConfiguration: Properties = {
    val p = new Properties()
    import org.apache.kafka.streams.StreamsConfig
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, s"kbook-$uuid")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ctx.config.meta.bootstrapServers)

    //p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ctx.schemaRegistry)
    //p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }

  val streams: KafkaStreams = {
    logger.info(ctx.topology.describe().toString)
    new KafkaStreams(ctx.topology, streamsConfiguration)
  }

  def start(): Unit = streams.start()

  def stop(): Unit = streams.close()
}

object KBook extends LazyLogging {

  def apply(config: KBookConfig): KBook = {
    logger.info(s"KBookConfig: $config")

    val ctx = KBook.Context(config)

    val linearized = config.dag.dag
      .ensuring(_.isAcyclic, throw new RuntimeException("The graph is not acyclic"))
      .ensuring(_.isDirected, throw new RuntimeException("The graph is directed"))
      .ensuring(_.isConnected, throw new RuntimeException("The graph is connected"))
      .topologicalSort.right.getOrElse(throw new RuntimeException("The graph has no node"))

    // Since side effect is performed in the constructor of each LogicNode
    // we do not have to capture the result of this operation (unlike Spark)
    linearized.foldLeft(List[LogicNode]())((constructed, current) => {
      val inbounds = current.incoming.toSeq
        .map { edge => constructed.find(_.uuid == edge.from.toOuter.meta.uuid) -> edge.label.asInstanceOf[Dag.EdgeLabel] }
        .collect { case (Some(node), edge) => node -> edge }
        .sortBy(_._2.toPort)
        .foldLeft(LogicNode.Bounds()) { (ibs, x) =>
          ibs :+ x._1.asInstanceOf[logicnode.Predecessor].outbound(x._2.fromPort)
        }

      val nodeConfig = current.toOuter

      val logicNode = nodeConfig.meta.`type` match {
        case Dag.NodeType.Source => logicnode.Source(ctx, nodeConfig)
        case Dag.NodeType.Transformer => logicnode.Transformer(ctx, nodeConfig, inbounds)
        case Dag.NodeType.Sink => logicnode.Sink(ctx, nodeConfig, inbounds)
      }

      constructed :+ logicNode
    })

    new KBook(ctx)
  }

  case class Context(private[KBook] val config: KBookConfig) {
    private val builder = new org.apache.kafka.streams.scala.StreamsBuilder

    private[KBook] lazy val topology: Topology = builder.build()

    def stream[K, V](topic: String)(implicit consumed: Consumed[K, V]): KStream[K, V] = builder.stream[K, V](topic)
    def stream[K, V](topics: Set[String])(implicit consumed: Consumed[K, V]): KStream[K, V] = builder.stream[K, V](topics)

    // FIXME: too many import needed
    object Serdes {
      implicit val simpleSerdes = org.apache.kafka.streams.scala.Serdes
      implicit val conversions = org.apache.kafka.streams.scala.ImplicitConversions

      import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde

      implicit val genericAvroValueSerde: GenericAvroSerde = {
        val gas = new GenericAvroSerde

        import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
        gas.configure(java.util.Collections.singletonMap(
          AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
          config.meta.schemaRegistry),
          false)
        gas
      }
    }

  }

}
