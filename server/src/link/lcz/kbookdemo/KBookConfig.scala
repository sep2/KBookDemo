package link.lcz.kbookdemo

import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json._
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge

class KBookConfig(val meta: KBookConfig.KBookMeta, val dag: Dag)
  extends LazyLogging {
  override def toString: String = KBookConfig.render(this)
}

object KBookConfig {
  private[KBookConfig] implicit val formats: Formats = {
    val jsonGraph = scalax.collection.io.json.JsonGraphCoreCompanion(Graph)
    implicit val graphConfig = jsonGraph.companion.defaultConfig

    class DagSerializer extends CustomSerializer[Dag](_ => ( {
      case jo: JObject => Dag(jsonGraph.fromJson[Dag.NodeDef, LDiEdge](jo, Dag.descriptor))
    }, {
      case dag: Dag =>
        val export = new scalax.collection.io.json.exp.Export(dag.dag, Dag.descriptor)
        import export._
        val (nodesToExport, edgesToExport) = (jsonASTNodes, jsonASTEdges)
        jsonAST(List(nodesToExport, edgesToExport))
    }))

    Serialization.formats(NoTypeHints) + new DagSerializer
  }

  def apply(jsonText: String): KBookConfig = parse(jsonText)

  def parse(jsonText: String): KBookConfig = {
    net.liftweb.json.Serialization.read[KBookConfig](jsonText)
  }

  def render(kc: KBookConfig): String = {
    net.liftweb.json.Serialization.write(kc)
  }

  case class KBookMeta(uuid: String, name: String, schemaRegistry: String, bootstrapServers: String)

}