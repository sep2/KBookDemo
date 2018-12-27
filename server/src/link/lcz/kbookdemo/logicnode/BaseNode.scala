package link.lcz.kbookdemo.logicnode

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import link.lcz.kbookdemo.{Dag, KBook}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.scala.kstream.KStream

abstract class BaseNode(val env: BaseNode.Environment) extends LazyLogging {
  implicit protected val genericAvroSerde: GenericAvroSerde = env.ctx.Serdes.genericAvroValueSerde
  logger.info(s"[${env.nd.meta.name}] gets constructed.")
}

object BaseNode {

  type NodeDef = Dag.NodeDef
  type Bound = KStream[String, GenericRecord]
  type Bounds = IndexedSeq[Bound]

  def reflect[A](clazz: String)(args: AnyRef*): A = {
    import scala.reflect.runtime.{universe => ru}
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val cs = m.classSymbol(Class.forName(clazz))
    val cm = m.reflectClass(cs)
    val ctor = cm.reflectConstructor(cm.symbol.info.member(ru.termNames.CONSTRUCTOR).asMethod)
    ctor(args: _*).asInstanceOf[A]
  }

  abstract class Environment(val ctx: KBook.Context, val nd: BaseNode.NodeDef) {
    val uuid: String = nd.meta.uuid
  }

  object Bounds {
    def apply(xs: Bound*): Bounds = IndexedSeq(xs: _*)
  }

}
