package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.logicnode.{BaseNode, Source}
import net.liftweb.json.JObject
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

class AvroSource(env: Source.Environment) extends Source[AvroSource.Config](env) {
  //  import env.ctx.Serdes.conversions._
  //  import env.ctx.Serdes.simpleSerdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes._

  // FIXME: need avro schema name
  private val schema = new Schema.Parser().parse(net.liftweb.json.compactRender(config.schema))

  override def outbound(idx: Int) = BaseNode.SchemaBound(env.ctx.stream[String, GenericRecord](config.topicName), schema)

}

object AvroSource {

  case class Config(topicName: String, schema: JObject)

}