package link.lcz.kbookdemo.logicnode

import link.lcz.kbookdemo.Book

abstract class Source(ctx: Book.Context, nd: LogicNode.NodeDef) extends LogicNode(ctx, nd) {
  def outbounds(): LogicNode.Bounds
}

object Source {
  def apply(ctx: Book.Context, nd: LogicNode.NodeDef): Source =
    LogicNode.reflect[Source](nd.meta.clazz)(ctx, nd)
}
