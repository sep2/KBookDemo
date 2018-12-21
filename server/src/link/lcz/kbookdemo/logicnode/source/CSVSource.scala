package link.lcz.kbookdemo.logicnode.source

import link.lcz.kbookdemo.logicnode.{BaseNode, Source}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

class CSVSource(env: Source.Environment) extends Source(env) {

  import env.ctx.Serdes.conversions._
  import env.ctx.Serdes.simpleSerdes._

  val schema = new Schema.Parser()
    .parse(net.liftweb.json.compactRender(env.nd.config \ "node.schema"))
  logger.info(s"${env.nd.meta.name} schema: $schema")

  override def outbound(idx: Int): BaseNode.Bound =
    env.ctx
      .stream[String, String](Set(""))
      .map[String, GenericRecord] { (k, raw) =>
      k -> BaseNode.makeRecord(schema,
        raw
          .split(',')
          .toSeq
          .map(_.split('='))
          .map(kvp => kvp(0) -> kvp(1)): _*)
    }

}
