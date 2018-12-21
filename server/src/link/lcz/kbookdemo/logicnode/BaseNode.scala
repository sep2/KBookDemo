package link.lcz.kbookdemo.logicnode

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import link.lcz.kbookdemo.{Dag, KBook}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.streams.scala.kstream.KStream

import scala.util.Try

abstract class BaseNode(env: BaseNode.Environment) extends LazyLogging {
  val uuid: String = env.nd.meta.uuid

  implicit val genericAvroSerde: GenericAvroSerde = env.ctx.Serdes.genericAvroValueSerde

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

  def makeRecord(schema: Schema, xs: (String, String)*): GenericRecord = {
    val r = new GenericData.Record(schema)
    xs.foreach { x =>
      Try[AnyRef] {
        schema.getField(x._1).schema().getType match {
          case Schema.Type.STRING => x._2.asInstanceOf[AnyRef]
          case Schema.Type.DOUBLE => x._2.toDouble.asInstanceOf[AnyRef]
          case Schema.Type.FLOAT => x._2.toFloat.asInstanceOf[AnyRef]
          case Schema.Type.INT => x._2.toInt.asInstanceOf[AnyRef]
          case Schema.Type.LONG => x._2.toLong.asInstanceOf[AnyRef]
          case unknown => throw new RuntimeException(s"unknown type: $unknown")
        }
      }.foreach(r.put(x._1, _))
    }
    r
  }

  abstract class Environment(val ctx: KBook.Context, val nd: BaseNode.NodeDef)

  object Bounds {
    def apply(xs: Bound*): Bounds = IndexedSeq(xs: _*)
  }

}
