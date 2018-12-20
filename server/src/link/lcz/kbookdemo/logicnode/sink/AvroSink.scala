package link.lcz.kbookdemo.logicnode.sink

import link.lcz.kbookdemo.KBook
import link.lcz.kbookdemo.logicnode.{LogicNode, Sink}

class AvroSink(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds) extends Sink(ctx, nd, inbounds) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  inbounds.foreach(_.to(config[AvroSink.Config].topicName))
}

object AvroSink {

  case class Config(topicName: String)

}