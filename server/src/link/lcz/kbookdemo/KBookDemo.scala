package link.lcz.kbookdemo

import net.liftweb.json.JObject
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge
import scalax.collection.io.json._

object KBookDemo extends App {
  val (source, transformer, sink) = (
    Dag.NodeDef(
      Dag.NodeMeta(uuid = "1", name = "x", `type` = "source", clazz = "a.b.c"),
      config = JObject(),
      inbounds = List(Dag.BoundObject(config = JObject())),
      outbounds = List()),
    Dag.NodeDef(Dag.NodeMeta(uuid = "2",
      name = "y",
      `type` = "transformer",
      clazz = "a.b.d"),
      config = JObject(),
      inbounds = List(),
      outbounds = List()),
    Dag.NodeDef(
      Dag.NodeMeta(uuid = "3", name = "z", `type` = "sink", clazz = "a.b.e"),
      config = JObject(),
      inbounds = List(),
      outbounds = List())
  )
  //
  val dag = Graph[Dag.NodeDef, LDiEdge](
    LDiEdge(source, transformer)(Dag.EdgeMeta("xxx", 1, 0)),
    LDiEdge(transformer, sink)(Dag.EdgeMeta("yyy", 1, 2))
  )
  println(dag.toJson(Dag.descriptor))

  println(dag)
  println(
    JsonGraphCoreCompanion(Graph)
      .fromJson[Dag.NodeDef, LDiEdge](dag.toJson(Dag.descriptor),
      Dag.descriptor)
      .toJson(Dag.descriptor))
}

//package link.lcz.kbookdemo
//
//import net.liftweb.json.JObject
//import scalax.collection.Graph
//import scalax.collection.edge.LDiEdge
//import scalax.collection.io.json._
//
//object KBookDemo extends App {
//  val (source, transformer, sink) = (
//    Dag.LogicNode(Dag.NodeMeta(uuid = "1",
//                                   name = "x",
//                                   `type` = "source",
//                                   clazz = "a.b.c"),
//                  config = JObject(),
//                  inbounds = List(Dag.BoundObject(config = JObject())),
//                  outbounds = List()),
//    Dag.LogicNode(Dag.NodeMeta(uuid = "2",
//                                   name = "y",
//                                   `type` = "transformer",
//                                   clazz = "a.b.d"),
//                  config = JObject(),
//                  inbounds = List(),
//                  outbounds = List()),
//    Dag.LogicNode(Dag.NodeMeta(uuid = "3",
//                                   name = "z",
//                                   `type` = "sink",
//                                   clazz = "a.b.e"),
//                  config = JObject(),
//                  inbounds = List(),
//                  outbounds = List())
//  )
//  //
//  val dag = Graph[Dag.LogicNode, LDiEdge](
//    LDiEdge(source, transformer)(Dag.BoundPort(1, 0)),
//    LDiEdge(transformer, sink)(Dag.BoundPort(1, 2))
//  )
//  println(dag.toJson(Dag.descriptor))
//
//  println(dag)
//  println(JsonGraphCoreCompanion(Graph).fromJson[Dag.LogicNode, LDiEdge](dag.toJson(Dag.descriptor), Dag.descriptor).toJson(Dag.descriptor))
//}

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
