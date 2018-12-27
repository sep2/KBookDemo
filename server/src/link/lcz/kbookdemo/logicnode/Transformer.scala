package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook
import scala.reflect.runtime.universe.TypeTag

abstract class Transformer[A: TypeTag](env: Transformer.Environment)
    extends ConfiguredNode[A](env)
    with Predecessor

object Transformer {

  def apply(clazz: String, env: Transformer.Environment): Transformer[_] =
    BaseNode.reflect[Transformer[_]](clazz)(env)

  case class Environment(
                          override val ctx: KBook.Context,
                          override val nd: BaseNode.NodeDef,
                          inbounds: BaseNode.SchemaBounds
  ) extends BaseNode.Environment(ctx, nd)

}
