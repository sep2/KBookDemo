package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.Book

abstract class Sink(ctx: Book.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds)
    extends LogicNode(ctx, nd) {}

object Sink {
  def apply(ctx: Book.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds): Sink =
    LogicNode.reflect[Sink](nd.meta.clazz)(ctx, nd, inbounds)
}
