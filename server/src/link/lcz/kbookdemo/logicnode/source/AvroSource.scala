package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.logicnode.{BaseNode, Source}
import org.apache.avro.generic.GenericRecord

class AvroSource(env: Source.Environment) extends Source[AvroSource.Config](env) {
  //  import env.ctx.Serdes.conversions._
  //  import env.ctx.Serdes.simpleSerdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes._

  override def outbound(idx: Int) =
    BaseNode.SchemaBound(
      env.ctx.stream[String, GenericRecord](config.topicName),
      env.ctx.getValueSchema(config.topicName)
    )

}

object AvroSource {

  case class Config(topicName: String)

}
