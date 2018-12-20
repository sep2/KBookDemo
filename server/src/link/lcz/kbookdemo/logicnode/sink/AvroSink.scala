package link.lcz.kbookdemo.logicnode.sink

import link.lcz.kbookdemo.KBook
import link.lcz.kbookdemo.logicnode.{LogicNode, Sink}

class AvroSink(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds) extends Sink(ctx, nd, inbounds) {
  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  implicit val gas = ctx.Serdes.genericAvroValueSerde
  implicit val formats = net.liftweb.json.DefaultFormats

  inbounds.foreach(_.to((nd.config \ "topic.name").extract[String]))
}
