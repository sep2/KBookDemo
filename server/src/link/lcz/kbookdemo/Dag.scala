package link.lcz.kbookdemo

import net.liftweb.json.JsonAST.JString
import net.liftweb.json._
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.edge.LEdgeParameters
import scalax.collection.io.json.descriptor.predefined.LDi
import scalax.collection.io.json.serializer.LSerializer
import scalax.collection.io.json.{Descriptor, NodeDescriptor}

case class Dag(dag: Graph[Dag.NodeDef, LDiEdge])

object Dag {
  val descriptor = {
    val logicNodeDescriptor =
      new NodeDescriptor[NodeDef](
        customSerializers = Seq(new NodeType.Serializer)) {
        override def id(node: Any): String = node match {
          case NodeDef(NodeMeta(uuid, _, _, _), _, _, _) => uuid
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
      LDi.descriptor[NodeDef, EdgeMeta](
        EdgeMeta("", 0, 0),
        Some(new BoundedEdgeSerializer[EdgeMeta]()))

    new Descriptor[Dag.NodeDef](
      defaultNodeDescriptor = logicNodeDescriptor,
      defaultEdgeDescriptor = boundedEdgeDescriptor
    )
  }

  sealed trait NodeType

  case class EdgeMeta(uuid: String, fromPort: Int, toPort: Int)

  case class BoundObject(config: JObject)

  case class NodeDef(meta: NodeMeta,
                     config: JObject,
                     inbounds: List[BoundObject],
                     outbounds: List[BoundObject])

  case class NodeMeta(uuid: String,
                      name: String,
                      clazz: String,
                      `type`: NodeType)

  object NodeType {

    private[Dag] class Serializer
      extends CustomSerializer[NodeType](_ =>
        ( {
          case JString("source") =>
            NodeType.Source
          case JString("transformer") =>
            NodeType.Transformer
          case JString("sink") =>
            NodeType.Sink
        }, {
          case _: NodeType.Source.type => JString("source")
          case _: NodeType.Transformer.type => JString("transformer")
          case _: NodeType.Sink.type => JString("sink")
        }))

    object Source extends NodeType

    object Transformer extends NodeType

    object Sink extends NodeType

  }

}
