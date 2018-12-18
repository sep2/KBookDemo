package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.Book


abstract class Transformer(ctx: Book.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds)
  extends LogicNode(ctx, nd) {
  def outbounds(): LogicNode.Bounds
}


object Transformer {
  def apply(ctx: Book.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds): Transformer =
    LogicNode.reflect[Transformer](nd.meta.clazz)(ctx, nd, inbounds)
}