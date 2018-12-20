package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.KBook

abstract class Source(ctx: KBook.Context, nd: LogicNode.NodeDef) extends LogicNode(ctx, nd) with Predecessor {
  override def outbounds: LogicNode.Bounds
}

object Source {
  def apply(ctx: KBook.Context, nd: LogicNode.NodeDef): Source =
    LogicNode.reflect[Source](nd.meta.clazz)(ctx, nd)
}
