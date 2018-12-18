package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.Book
import link.lcz.kbookdemo.logicnode.LogicNode.Bounds
import link.lcz.kbookdemo.logicnode.{LogicNode, Source}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}

class CSVSource(ctx: Book.Context, nd: LogicNode.NodeDef)
  extends Source(ctx, nd) {

  import ctx.Serdes.conversions._
  import ctx.Serdes.simpleSerdes._
  implicit val gavs = ctx.Serdes.genericAvroValueSerde

  val schema = new Schema.Parser().parse("")

  override def outbounds(): Bounds =
    Bounds(
      ctx.builder
        .stream[String, String]("")
        .map[String, GenericRecord] { (k, v) =>
        k -> new GenericData.Record(schema)
      })
}
