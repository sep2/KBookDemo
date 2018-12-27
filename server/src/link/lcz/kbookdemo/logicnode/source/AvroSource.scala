package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.logicnode.{BaseNode, Source}
import org.apache.avro.generic.GenericRecord

class AvroSource(env: Source.Environment) extends Source[AvroSource.Config](env) {
//  import env.ctx.Serdes.conversions._
//  import env.ctx.Serdes.simpleSerdes._
  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  override def outbound(idx: Int): BaseNode.Bound =
    env.ctx.stream[String, GenericRecord](config.topicName)
}

object AvroSource {
  case class Config(topicName: String)
}