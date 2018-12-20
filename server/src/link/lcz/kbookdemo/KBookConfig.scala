package link.lcz.kbookdemo

import com.typesafe.scalalogging.LazyLogging
import link.lcz.kbookdemo.logicnode.LogicNode
import net.liftweb.json._
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge

import scala.util.Try

class KBookConfig(val meta: KBookConfig.KBookMeta, val dag: Dag)
  extends LazyLogging {
  def construct(): Try[Book] = Try {
    val ctx = Book.Context("schema")

    val sorted = dag.dag
      .ensuring(_.isAcyclic, throw new RuntimeException("The graph is not acyclic"))
      .ensuring(_.isDirected, throw new RuntimeException("The graph is directed"))
      .ensuring(_.isConnected, throw new RuntimeException("The graph is connected"))
      .topologicalSort.right.getOrElse(throw new RuntimeException("The graph has no node"))
      .foldLeft(List[LogicNode]())((r, n) => {
        r
      })
    new Book(this, null)
  }

  override def toString: String = KBookConfig.render(this)
}

object KBookConfig {
  private[KBookConfig] implicit val formats = {
    val jsonGraph = scalax.collection.io.json.JsonGraphCoreCompanion(Graph)
    implicit val graphConfig = jsonGraph.companion.defaultConfig

    class DagSerializer extends CustomSerializer[Dag](format => ( {
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

  def render(kc: KBookConfig): String = {
    net.liftweb.json.Serialization.write(kc)
  }

  def parse(jsonText: String): KBookConfig = {
    net.liftweb.json.Serialization.read[KBookConfig](jsonText)
  }

  case class KBookMeta(uuid: String, name: String)

}