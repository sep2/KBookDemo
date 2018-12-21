package link.lcz.kbookdemo.logicnode.sink

import link.lcz.kbookdemo.logicnode.Sink

class AvroSink(env: Sink.Environment) extends Sink[AvroSink.Config](env) {
  import env.ctx.Serdes.conversions._
  import env.ctx.Serdes.simpleSerdes._

  env.inbounds.foreach(_.to(config.topicName))
}

object AvroSink {

  case class Config(topicName: String)

}
