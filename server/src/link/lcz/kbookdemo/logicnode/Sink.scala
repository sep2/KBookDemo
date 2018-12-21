package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook
import scala.reflect.runtime.universe.TypeTag

abstract class Sink[A: TypeTag](env: Sink.Environment)
    extends ConfiguredNode[A](env)

object Sink {

  def apply(clazz: String, env: Sink.Environment): Sink[_] =
    BaseNode.reflect[Sink[_]](clazz)(env)

  case class Environment(
                          override val ctx: KBook.Context,
                          override val nd: BaseNode.NodeDef,
                          inbounds: BaseNode.Bounds
  ) extends BaseNode.Environment(ctx, nd)

}
