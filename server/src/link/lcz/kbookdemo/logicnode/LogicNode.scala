package link.lcz.kbookdemo.logicnode

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import link.lcz.kbookdemo.{Dag, KBook}
import net.liftweb.json.DefaultFormats
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.streams.scala.kstream.KStream

import scala.util.Try

abstract class LogicNode(ctx: KBook.Context, nd: LogicNode.NodeDef) extends LazyLogging {
  val uuid: String = nd.meta.uuid

  implicit val genericAvroSerde: GenericAvroSerde = ctx.Serdes.genericAvroValueSerde
  implicit val jsonFormats: DefaultFormats = net.liftweb.json.DefaultFormats

  def config[A](implicit mf: scala.reflect.Manifest[A]): A = nd.config.extract[A]
}

object LogicNode {

  type NodeDef = Dag.NodeDef
  type Bound = KStream[String, GenericRecord]
  type Bounds = IndexedSeq[Bound]

  def reflect[A](clazz: String)(args: AnyRef*): A =
    Class
      .forName(clazz)
      .getConstructors.ensuring(_.length == 1, s"Too many constructors for $clazz")
      .head
      .newInstance(args: _*)
      .asInstanceOf[A]

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

  object Bounds {
    def apply(xs: Bound*): Bounds = IndexedSeq(xs: _*)
  }

}
