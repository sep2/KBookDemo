package link.lcz.kbookdemo

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder

class Book(val dag: KBookConfig, ks: KafkaStreams) {
  val uuid: String = dag.meta.uuid

  def start(): Unit = ks.start()

  def stop(): Unit = ks.close()
}

object Book {

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
