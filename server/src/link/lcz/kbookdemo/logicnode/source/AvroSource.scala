package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.Book
import link.lcz.kbookdemo.logicnode.LogicNode.Bounds
import link.lcz.kbookdemo.logicnode.{LogicNode, Source}
import org.apache.avro.generic.GenericRecord

class AvroSource(ctx: Book.Context, nd: LogicNode.NodeDef)
  extends Source(ctx, nd) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  implicit val gas = ctx.Serdes.genericAvroValueSerde
  implicit val formats = net.liftweb.json.DefaultFormats

  override def outbounds(): Bounds = Bounds(
    ctx.builder
      .stream[String, GenericRecord]((nd.config \ "topic.name").extract[String])
  )
}
