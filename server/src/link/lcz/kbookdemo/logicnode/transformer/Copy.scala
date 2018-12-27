package link.lcz.kbookdemo.logicnode.transformer

import link.lcz.kbookdemo.logicnode.BaseNode.Bound
import link.lcz.kbookdemo.logicnode.Transformer

class Copy(env: Transformer.Environment) extends Transformer[Copy.Config](env) {
  override def outbound(idx: Int): Bound = env.inbounds(0)
}

object Copy {

  case class Config()

}