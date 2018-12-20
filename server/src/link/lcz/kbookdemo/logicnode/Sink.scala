package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook

abstract class Sink(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds)
    extends LogicNode(ctx, nd) {}

object Sink {
  def apply(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds): Sink =
    LogicNode.reflect[Sink](nd.meta.clazz)(ctx, nd, inbounds)
}
