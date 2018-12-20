package link.lcz.kbookdemo

import net.liftweb.json.JObject
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge

object KBookDemo extends App {
  val (source, transformer, sink) = (
    Dag.NodeDef(
      Dag.NodeMeta(uuid = "1", name = "x", `type` = Dag.NodeType.Source, clazz = "a.b.c"),
      config = JObject(),
      inbounds = List(Dag.BoundObject(config = JObject())),
      outbounds = List()),
    Dag.NodeDef(Dag.NodeMeta(uuid = "2",
      name = "y",
      `type` = Dag.NodeType.Transformer,
      clazz = "a.b.d"),
      config = JObject(),
      inbounds = List(),
      outbounds = List()),
    Dag.NodeDef(
      Dag.NodeMeta(uuid = "3", name = "z", `type` = Dag.NodeType.Sink, clazz = "a.b.e"),
      config = JObject(),
      inbounds = List(),
      outbounds = List())
  )

  val dag = Dag(Graph[Dag.NodeDef, LDiEdge](
    LDiEdge(source, transformer)(Dag.EdgeMeta("xxx", 1, 0)),
    LDiEdge(transformer, sink)(Dag.EdgeMeta("yyy", 1, 2))
  ))

  val rendered = KBookConfig.render(new KBookConfig(KBookConfig.KBookMeta("1", "2"), dag))

  println(rendered)
  println(KBookConfig.render(KBookConfig.parse(rendered)))

}

//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import scala.io.StdIn
//
//object KBookDemo extends App {
//  implicit val system = ActorSystem("KBookDemo")
//  implicit val materializer = ActorMaterializer()
//  // needed for the future flatMap/onComplete in the end
//  implicit val executionContext = system.dispatcher
//
//  val bookRunner = system.actorOf(BookRunner.props, "bookRunner")
//
//  val route =
//    path("book") {
//      post {
//        complete(
//          HttpEntity(ContentTypes.`text/html(UTF-8)`,
//                     "<h1>Say hello to akka-http</h1>"))
//      }
//    }
//
//  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
//
//  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
//  StdIn.readLine() // let it run until user presses return
//  bindingFuture
//    .flatMap(_.unbind()) // trigger unbinding from the port
//    .onComplete(_ => system.terminate()) // and shutdown when done
//}
