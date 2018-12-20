package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook

abstract class Transformer(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds)
  extends LogicNode(ctx, nd) with Predecessor {
  override def outbounds: LogicNode.Bounds
}


object Transformer {
  def apply(ctx: KBook.Context, nd: LogicNode.NodeDef, inbounds: LogicNode.Bounds): Transformer =
    LogicNode.reflect[Transformer](nd.meta.clazz)(ctx, nd, inbounds)
}