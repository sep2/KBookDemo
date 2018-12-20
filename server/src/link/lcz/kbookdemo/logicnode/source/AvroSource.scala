package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.KBook
import link.lcz.kbookdemo.logicnode.{LogicNode, Source}
import org.apache.avro.generic.GenericRecord

class AvroSource(ctx: KBook.Context, nd: LogicNode.NodeDef) extends Source(ctx, nd) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  override def outbound(idx: Int): LogicNode.Bound =
    ctx.stream[String, GenericRecord](config[AvroSource.Config].topicName)

}

object AvroSource {

  case class Config(topicName: String)

}