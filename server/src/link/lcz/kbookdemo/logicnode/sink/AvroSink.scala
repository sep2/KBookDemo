package link.lcz.kbookdemo.logicnode.sink

import link.lcz.kbookdemo.Book
import link.lcz.kbookdemo.logicnode.{LogicNode, Sink}

class AvroSink(ctx: Book.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds) extends Sink(ctx, nd, inbounds) {
  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._
  implicit val gas = ctx.Serdes.genericAvroValueSerde

  inbounds.foreach(_.to(""))
}
