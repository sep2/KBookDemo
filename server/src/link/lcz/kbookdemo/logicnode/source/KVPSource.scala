package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.logicnode.{BaseNode, Source}
import net.liftweb.json._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}

import scala.util.Try

class KVPSource(env: Source.Environment) extends Source[KVPSource.Config](env) {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.Serdes._

  private val schema = new Schema.Parser().parse(net.liftweb.json.compactRender(JObject(
    JField("type", JString("record")),
    JField("name", JString(env.nd.meta.uuid)),
    JField("fields", config.fields)
  )))

  logger.info(s"${env.nd.meta.name} schema: $schema")

  private val recordMaker = (raw: String) => makeRecord(schema, raw)

  override def outbound(idx: Int): BaseNode.Bound =
    env.ctx.stream[String, String](config.topicNames)
      .map[String, GenericRecord]((k, raw) => k -> recordMaker(raw))

  private def makeRecord(schema: Schema, raw: String): GenericRecord = {
    val r = new GenericData.Record(schema)
    raw
      .split(',').toSeq
      .map(_.split('='))
      .ensuring(_.length == 2, throw new RuntimeException(s"bad kvp $raw"))
      .map(kvp => kvp(0) -> kvp(1))
      .foreach { case (k, v) =>
        Try {
          Option(schema.getField(k)).map {
            _.schema().getType match {
              case Schema.Type.STRING => v.asInstanceOf[AnyRef]
              case Schema.Type.DOUBLE => v.toDouble.asInstanceOf[AnyRef]
              case Schema.Type.FLOAT => v.toFloat.asInstanceOf[AnyRef]
              case Schema.Type.INT => v.toInt.asInstanceOf[AnyRef]
              case Schema.Type.LONG => v.toLong.asInstanceOf[AnyRef]
              case unknown =>
                throw new RuntimeException(s"unknown type: $unknown")
            }
          }
        }.toOption.flatten.foreach(r.put(k, _))
      }
    r
  }
}

object KVPSource {

  case class Config(topicNames: Set[String], fields: JArray)

}
