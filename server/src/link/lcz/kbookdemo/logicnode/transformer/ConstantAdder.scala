package link.lcz.kbookdemo.logicnode.transformer

import link.lcz.kbookdemo.logicnode.{BaseNode, Transformer}
import org.apache.avro.Schema

class ConstantAdder(env: Transformer.Environment)
    extends Transformer[ConstantAdder.Config](env) {

  this.ensuring(
    env.inbounds.forall(
      _.schema.getField(config.field).schema().getType == Schema.Type.INT),
    throw new RuntimeException(s"${config.field} is not Int type"))

  override def outbound(idx: Int): BaseNode.SchemaBound =
    env.inbounds
      .map { sb =>
        BaseNode.SchemaBound(
          sb.bound.map(
            (k, v) => {
              v.put(config.field,
                    v.get(config.field).asInstanceOf[Int] + config.constant)
              k -> v
            }
          ),
          sb.schema)
      }
      .apply(idx)
}

object ConstantAdder {

  case class Config(field: String, constant: Int)

}
