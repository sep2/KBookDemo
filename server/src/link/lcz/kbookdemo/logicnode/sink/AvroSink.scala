package link.lcz.kbookdemo.logicnode.sink

import link.lcz.kbookdemo.logicnode.Sink

class AvroSink(env: Sink.Environment) extends Sink[AvroSink.Config](env) {
//  import env.ctx.Serdes.conversions._
//  import env.ctx.Serdes.simpleSerdes._

  import org.apache.kafka.streams.scala.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  env.inbounds.foreach(_.bound.to(config.topicName))
}

object AvroSink {

  case class Config(topicName: String)

}
