package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.Book
import link.lcz.kbookdemo.logicnode.LogicNode.Bounds
import link.lcz.kbookdemo.logicnode.{LogicNode, Source}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

class CSVSource(ctx: Book.Context, nd: LogicNode.NodeDef)
  extends Source(ctx, nd) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._

  implicit val gavs = ctx.Serdes.genericAvroValueSerde

  val schema = new Schema.Parser()
    .parse(net.liftweb.json.compactRender(nd.config \ "node.schema"))
  logger.info(s"${nd.meta.name} schema: $schema")

  override def outbounds(): Bounds =
    Bounds(
      ctx.builder
        .stream[String, String]("")
        .map[String, GenericRecord] { (k, raw) =>
        k -> LogicNode.makeRecord(schema,
          raw
            .split(',')
            .toSeq
            .map(_.split('='))
            .map(kvp => kvp(0) -> kvp(1)): _*)
      }
    )
}
