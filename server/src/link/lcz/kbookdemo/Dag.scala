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
  val descriptor: Descriptor[NodeDef] = {
    val logicNodeDescriptor =
      new NodeDescriptor[NodeDef](
        customSerializers = Seq(new NodeType.Serializer)) {
        override def id(node: Any): String = node match {
          case NodeDef(NodeMeta(uuid, _, _, _), _, _, _) => uuid
        }
      }

    class BoundedEdgeSerializer(labelSerializers: Serializer[EdgeLabel]*)
      extends LSerializer[EdgeLabel](labelSerializers: _*)
        with Serializer[LEdgeParameters[EdgeLabel]] {

      override def deserialize(implicit format: Formats) = {
        case (TypeInfo(cls, _), json) =>
          json match {
            case JObject(
            JField("fromNode", JString(n1)) ::
              JField("toNode", JString(n2)) ::
              JField("uuid", JString(uuid)) ::
              JField("fromPort", JInt(fromPort)) ::
              JField("toPort", JInt(toPort)) ::
              Nil) =>
              new LEdgeParameters[EdgeLabel](n1, n2, EdgeLabel(uuid, fromPort.intValue(), toPort.intValue()))
            case x =>
              throw new MappingException(
                "Can't convert " + x + " to " + cls.getSimpleName)
          }
      }

      override def serialize(implicit format: Formats) = {
        case LEdgeParameters((nId_1, nId_2), label) =>
          JObject(
            JField("fromNode", JString(nId_1)) ::
              JField("toNode", JString(nId_2)) ::
              JField("uuid", JString(label.asInstanceOf[EdgeLabel].uuid)) ::
              JField("fromPort", JInt(label.asInstanceOf[EdgeLabel].fromPort)) ::
              JField("toPort", JInt(label.asInstanceOf[EdgeLabel].toPort)) ::
              Nil)
      }
    }

    val boundedEdgeDescriptor =
      LDi.descriptor[NodeDef, EdgeLabel](
        EdgeLabel("", 0, 0),
        Some(new BoundedEdgeSerializer))

    new Descriptor[Dag.NodeDef](
      defaultNodeDescriptor = logicNodeDescriptor,
      defaultEdgeDescriptor = boundedEdgeDescriptor
    )
  }

  sealed trait NodeType

  trait NodeConfig

  case class EdgeLabel(uuid: String, fromPort: Int, toPort: Int)

  case class BoundObject(config: JObject)

  case class NodeDef(meta: NodeMeta,
                     config: JObject,
                     inbounds: List[BoundObject],
                     outbounds: List[BoundObject])

  case class NodeMeta(uuid: String,
                      name: String,
                      clazz: String,
                      `type`: NodeType)

  object NodeConfig {
  }

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