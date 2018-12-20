package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.KBook
import link.lcz.kbookdemo.logicnode.{LogicNode, Source}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

class CSVSource(ctx: KBook.Context, nd: LogicNode.NodeDef)
  extends Source(ctx, nd) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  val schema = new Schema.Parser()
    .parse(net.liftweb.json.compactRender(nd.config \ "node.schema"))
  logger.info(s"${nd.meta.name} schema: $schema")

  override def outbound(idx: Int): LogicNode.Bound =
    ctx.stream[String, String](Set(""))
      .map[String, GenericRecord] { (k, raw) =>
      k -> LogicNode.makeRecord(schema,
        raw
          .split(',')
          .toSeq
          .map(_.split('='))
          .map(kvp => kvp(0) -> kvp(1)): _*)
    }

}
