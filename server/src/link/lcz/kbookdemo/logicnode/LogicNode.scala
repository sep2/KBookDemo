package link.lcz.kbookdemo.logicnode

import com.typesafe.scalalogging.LazyLogging
import link.lcz.kbookdemo.{Book, Dag}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.scala.kstream.KStream

abstract class LogicNode(ctx: Book.Context, nd: LogicNode.NodeDef) extends LazyLogging {
}

object LogicNode {
  type NodeDef = Dag.NodeDef
  type Bound = KStream[String, GenericRecord]
  type Bounds = IndexedSeq[Bound]

  def reflect[A](clazz: String)(args: AnyRef*): A =
    Class.forName(clazz).getConstructors.head.newInstance(args: _*).asInstanceOf[A]

  object Bounds {
    def apply(xs: Bound*): Bounds = IndexedSeq(xs: _*)
  }

}
