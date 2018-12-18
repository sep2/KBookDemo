package link.lcz.kbookdemo

import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json._
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.edge.LEdgeParameters
import scalax.collection.io.json.descriptor.predefined.LDi
import scalax.collection.io.json.serializer.LSerializer
import scalax.collection.io.json.{Descriptor, NodeDescriptor}

import scala.util.Try

case class Dag(meta: Dag.DagMeta, graph: Graph[Dag.NodeDef, LDiEdge])
  extends LazyLogging {
  def construct(): Try[Book] = Try {
    logger.warn("not implemented")
    require(graph.isAcyclic, throw new RuntimeException("The graph is not acyclic"))
    require(graph.isDirected, throw new RuntimeException("The graph is directed"))
    require(graph.isConnected, throw new RuntimeException("The graph is connected"))

    val ctx = Book.Context("schema")

    graph.topologicalSort.flatMap(???) // bad
    new Book(this, null)
  }
}

object Dag {

  val descriptor = {
    val logicNodeDescriptor =
      new NodeDescriptor[Dag.NodeDef]() {
        override def id(node: Any): String = node match {
          case Dag.NodeDef(Dag.NodeMeta(uuid, _, _, _), _, _, _) => uuid
        }
      }

    class BoundedEdgeSerializer[L: Manifest](labelSerializers: Serializer[L]*)
      extends LSerializer[L](labelSerializers: _*)
        with Serializer[LEdgeParameters[L]] {
      private val clazz = classOf[LEdgeParameters[_]]

      override def deserialize(implicit format: Formats) = {
        case (TypeInfo(clazz, _), json) =>
          json match {
            case JObject(JField("node",
            JObject(
            JField("from", JString(n1)) :: JField(
            "to",
            JString(n2)) :: Nil)) :: JField(
            "meta",
            jsonLabel) :: Nil) =>
              new LEdgeParameters[L](n1,
                n2,
                LabelSerialization.extract(jsonLabel))
            case x =>
              throw new MappingException(
                "Can't convert " + x + " to " + clazz.getSimpleName)
          }
      }

      override def serialize(implicit format: Formats) = {
        case LEdgeParameters((nId_1, nId_2), label) =>
          JObject(
            JField("node",
              JObject(
                JField("from", JString(nId_1)) :: JField(
                  "to",
                  JString(nId_2)) :: Nil)) :: JField(
              "meta",
              LabelSerialization.decompose(label.asInstanceOf[L])) :: Nil)
      }
    }

    val boundedEdgeDescriptor =
      LDi.descriptor[Dag.NodeDef, EdgeMeta](
        EdgeMeta("", 0, 0),
        Some(new BoundedEdgeSerializer[EdgeMeta]()))

    new Descriptor[Dag.NodeDef](
      defaultNodeDescriptor = logicNodeDescriptor,
      defaultEdgeDescriptor = boundedEdgeDescriptor
    )
  }

  def parse(jsonText: String): Dag = {
    implicit val formats = net.liftweb.json.DefaultFormats
    import scalax.collection.io.json.JsonGraphCoreCompanion

    val jsonAST = net.liftweb.json.parse(jsonText)
    Dag(meta = (jsonAST \ "meta").extract[Dag.DagMeta],
      graph = JsonGraphCoreCompanion(Graph).fromJson(jsonText, descriptor))
  }

  case class EdgeMeta(uuid: String, fromPort: Int, toPort: Int)

  case class BoundObject(config: JObject)

  case class NodeDef(meta: NodeMeta,
                     config: JObject,
                     inbounds: List[BoundObject],
                     outbounds: List[BoundObject])

  case class NodeMeta(uuid: String, name: String, clazz: String, `type`: String)

  case class DagMeta(uuid: String, name: String)

}

//package link.lcz.kbookdemo
//
//import com.typesafe.scalalogging.LazyLogging
//import net.liftweb.json._
//import scalax.collection.Graph
//import scalax.collection.edge.LDiEdge
//import scalax.collection.io.edge.LEdgeParameters
//import scalax.collection.io.json.descriptor.predefined.LDi
//import scalax.collection.io.json.serializer.LSerializer
//import scalax.collection.io.json.{Descriptor, NodeDescriptor}
//
//case class Dag(meta: Dag.DagMeta, graph: Graph[Dag.LogicNode, LDiEdge])
//  extends LazyLogging {
//  def construct(): Book = {
//    logger.warn("not implemented")
//    new Book(this, null)
//  }
//}
//
//object Dag {
//
//  case class BoundPort(from: Int, to: Int)
//
//  case class BoundObject(config: JObject)
//
//  case class LogicNode(meta: NodeMeta,
//                       config: JObject,
//                       inbounds: List[BoundObject],
//                       outbounds: List[BoundObject])
//
//  case class NodeMeta(uuid: String, name: String, clazz: String, `type`: String)
//
//  case class DagMeta(uuid: String, name: String)
//
//  val descriptor = {
//    val logicNodeDescriptor =
//      new NodeDescriptor[Dag.LogicNode]() {
//        override def id(node: Any): String = node match {
//          case Dag.LogicNode(Dag.NodeMeta(uuid, _, _, _), _, _, _) => uuid
//        }
//      }
//
//    class BoundedEdgeSerializer[L: Manifest](labelSerializers: Serializer[L]*)
//      extends LSerializer[L](labelSerializers: _*)
//        with Serializer[LEdgeParameters[L]] {
//      private val clazz = classOf[LEdgeParameters[_]]
//
//      override def deserialize(implicit format: Formats) = {
//        case (TypeInfo(clazz, _), json) =>
//          json match {
//            case JObject(JField("node",
//            JObject(
//            JField("from", JString(n1)) :: JField(
//            "to",
//            JString(n2)) :: Nil)) :: JField(
//            "port",
//            jsonLabel) :: Nil) =>
//              new LEdgeParameters[L](n1,
//                n2,
//                LabelSerialization.extract(jsonLabel))
//            case x =>
//              throw new MappingException(
//                "Can't convert " + x + " to " + clazz.getSimpleName)
//          }
//      }
//
//      override def serialize(implicit format: Formats) = {
//        case LEdgeParameters((nId_1, nId_2), label) =>
//          JObject(
//            JField("node",
//              JObject(
//                JField("from", JString(nId_1)) :: JField(
//                  "to",
//                  JString(nId_2)) :: Nil)) :: JField(
//              "port",
//              LabelSerialization.decompose(label.asInstanceOf[L])) :: Nil)
//      }
//    }
//
//    val boundedEdgeDescriptor =
//      LDi.descriptor[Dag.LogicNode, BoundPort](
//        BoundPort(0, 0),
//        Some(new BoundedEdgeSerializer[BoundPort]()))
//
//    new Descriptor[Dag.LogicNode](
//      defaultNodeDescriptor = logicNodeDescriptor,
//      defaultEdgeDescriptor = boundedEdgeDescriptor
//    )
//  }
//
//  def parse(jsonText: String): Dag = {
//    implicit val formats = net.liftweb.json.DefaultFormats
//    import scalax.collection.io.json.JsonGraphCoreCompanion
//
//    val jsonAST = net.liftweb.json.parse(jsonText)
//    Dag(meta = (jsonAST \ "meta").extract[Dag.DagMeta],
//      graph = JsonGraphCoreCompanion(Graph)
//        .fromJson[Dag.LogicNode, LDiEdge](jsonText, descriptor))
//  }
//}
