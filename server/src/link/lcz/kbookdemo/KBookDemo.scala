package link.lcz.kbookdemo

import ch.qos.logback.classic.Level
import net.liftweb.json.JObject
import net.liftweb.json.JsonAST.{JField, JString}
import scalax.collection.Graph
import scalax.collection.edge.LDiEdge

object KBookDemo extends App {
  org.slf4j.LoggerFactory.getLogger("org.apache.kafka").asInstanceOf[ch.qos.logback.classic.Logger].setLevel(Level.WARN)

  val (sink, source) = (
  Dag.NodeDef(
    Dag.NodeMeta(uuid = "2", name = "w", `type` = Dag.NodeType.Sink, clazz = "link.lcz.kbookdemo.logicnode.sink.AvroSink"),
    config = JObject(JField("topic.name", JString("outtopic"))),
    inbounds = List(),
    outbounds = List()),
    Dag.NodeDef(
      Dag.NodeMeta(uuid = "1", name = "x", `type` = Dag.NodeType.Source, clazz = "link.lcz.kbookdemo.logicnode.source.AvroSource"),
      config = JObject(JField("topic.name", JString("intopic"))),
      inbounds = List(Dag.BoundObject(config = JObject())),
      outbounds = List())
  )

  val dag = Dag(Graph[Dag.NodeDef, LDiEdge](
    LDiEdge(source, sink)(Dag.EdgeMeta("zzz", 0, 0))
  ))

  val kBook = KBook(new KBookConfig(KBookConfig.KBookMeta("namehere", "uuidhere"), dag))


  //  val rendered = KBookConfig.render(new KBookConfig(KBookConfig.KBookMeta("1", "2"), dag))

  //  println(rendered)
  //  println(KBookConfig.render(KBookConfig.parse(rendered)))

}

//object KBookDemo extends App {
//  val (source1, transformer, source2, sink) = (
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "1", name = "x", `type` = Dag.NodeType.Source, clazz = "a.b.c"),
//      config = JObject(),
//      inbounds = List(Dag.BoundObject(config = JObject())),
//      outbounds = List()),
//    Dag.NodeDef(Dag.NodeMeta(uuid = "2",
//      name = "y",
//      `type` = Dag.NodeType.Transformer,
//      clazz = "a.b.d"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List()), Dag.NodeDef(Dag.NodeMeta(uuid = "3",
//    name = "z",
//    `type` = Dag.NodeType.Source,
//    clazz = "a.b.d"),
//    config = JObject(),
//    inbounds = List(),
//    outbounds = List()),
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "4", name = "w", `type` = Dag.NodeType.Sink, clazz = "a.b.e"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List())
//  )
//
//  val dag = Dag(Graph[Dag.NodeDef, LDiEdge](
//    LDiEdge(source1, transformer)(Dag.EdgeMeta("xxx", 0, 0)),
//    LDiEdge(transformer, sink)(Dag.EdgeMeta("yyy", 0, 0)),
//    LDiEdge(source1, sink)(Dag.EdgeMeta("zzz", 0, 1))
//  ))
//
//  val kBook = KBook(new KBookConfig(KBookConfig.KBookMeta("1", "2"), dag))
//
//
//  //  val rendered = KBookConfig.render(new KBookConfig(KBookConfig.KBookMeta("1", "2"), dag))
//
//  //  println(rendered)
//  //  println(KBookConfig.render(KBookConfig.parse(rendered)))
//
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
