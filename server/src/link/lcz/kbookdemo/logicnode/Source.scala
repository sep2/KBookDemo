package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook
import scala.reflect.runtime.universe.TypeTag

abstract class Source[A: TypeTag](env: Source.Environment)
  extends ConfiguredNode[A](env)
    with Predecessor

object Source {

  def apply(clazz: String, env: Source.Environment): Source[_] =
    BaseNode.reflect[Source[_]](clazz)(env)

  case class Environment(
                          override val ctx: KBook.Context,
                          override val nd: BaseNode.NodeDef
                        ) extends BaseNode.Environment(ctx, nd)

}
