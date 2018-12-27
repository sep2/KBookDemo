package link.lcz.kbookdemo.logicnode

trait Predecessor {
  def outbound(idx: Int): BaseNode.SchemaBound
}

object Predecessor {
}