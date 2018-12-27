package link.lcz.kbookdemo.logicnode.transformer

import link.lcz.kbookdemo.logicnode.{BaseNode, Transformer}

class Copy(env: Transformer.Environment) extends Transformer[Copy.Config](env) {
  override def outbound(idx: Int): BaseNode.SchemaBound = env.inbounds(0)

}

object Copy {

  case class Config()

}