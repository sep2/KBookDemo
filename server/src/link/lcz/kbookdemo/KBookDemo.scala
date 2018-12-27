package link.lcz.kbookdemo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.qos.logback.classic.Level
import com.typesafe.scalalogging.LazyLogging
import net.liftweb.json.JsonAST.{JBool, JField, JObject, JString}

import scala.concurrent.duration.{Duration, SECONDS}
import scala.io.StdIn
import scala.util.{Failure, Success}

object KBookDemo extends App with LazyLogging {
  org.slf4j.LoggerFactory
    .getLogger("org.apache.kafka")
    .asInstanceOf[ch.qos.logback.classic.Logger]
    .setLevel(Level.WARN)
  org.slf4j.LoggerFactory
    .getLogger("io.confluent.kafka")
    .asInstanceOf[ch.qos.logback.classic.Logger]
    .setLevel(Level.WARN)

  implicit val system: ActorSystem = ActorSystem("KBookDemo")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  def completeWithFail(error: String) = complete(HttpEntity(ContentTypes.`application/json`, responseFail(error)))

  def responseFail(error: String) = {
    implicit val jsonFormats = net.liftweb.json.DefaultFormats
    net.liftweb.json.Serialization.write(JObject(JField("status", JBool(false)), JField("message", JString(error))))
  }

  val responseOk = {
    implicit val jsonFormats = net.liftweb.json.DefaultFormats
    net.liftweb.json.Serialization.write(JObject(JField("status", JBool(true))))
  }
  val completeWithOk = complete(HttpEntity(ContentTypes.`application/json`, responseOk))
  val kBookRunner = system.actorOf(KBookRunner.props, "kBookRunner")
  val route =
    (post & path("book" / "post") & entity(as[String])) { body =>
      kBookRunner ! KBookRunner.PostBook(body)
      completeWithOk
    } ~ ((post | get) & parameter('uuid)) { uuid =>
      path("book" / "play") {
        kBookRunner ! KBookRunner.PlayBook(uuid)
        completeWithOk
      } ~ path("book" / "stop") {
        kBookRunner ! KBookRunner.StopBook(uuid)
        completeWithOk
      } ~ path("book" / "delete") {
        kBookRunner ! KBookRunner.DeleteBook(uuid)
        completeWithOk
      } ~ path("book" / "show") {
        onComplete(kBookRunner.ask(KBookRunner.ShowBook(uuid))(Timeout(Duration(3, SECONDS)))) {
          case Success(j) => complete(HttpEntity(ContentTypes.`application/json`, j.asInstanceOf[String]))
          case Failure(ex) => completeWithFail(ex.getMessage)
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9999)

  println(s"Server online at http://localhost:9999/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}

//object KBookDemo extends App {
//  org.slf4j.LoggerFactory
//    .getLogger("org.apache.kafka")
//    .asInstanceOf[ch.qos.logback.classic.Logger]
//    .setLevel(Level.WARN)
//  org.slf4j.LoggerFactory
//    .getLogger("io.confluent.kafka")
//    .asInstanceOf[ch.qos.logback.classic.Logger]
//    .setLevel(Level.WARN)
//
//  println(Source.fromFile("sample.json").mkString)
//
//  //  println(link.lcz.kbookdemo.logicnode.sink.AvroSink.Config.getClass.getConstructors.head.getParameterCount)
//  //
//  //  println(Class
//  //    .forName("link.lcz.kbookdemo.logicnode.sink.AvroSink$Config")
//  //    .getConstructors
//  //    .head
//  //    .newInstance("x")
//  //    .asInstanceOf[link.lcz.kbookdemo.logicnode.sink.AvroSink.Config].topicName)
//
//  val (sink1, source, transformer1, transformer2, sink2) = (
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "2",
//        name = "Avro Sink 1",
//        `type` = Dag.NodeType.Sink,
//        clazz = "link.lcz.kbookdemo.logicnode.sink.AvroSink"),
//      config = JObject(JField("topicName", JString("outtopic1"))),
//      inbounds = List(),
//      outbounds = List()
//    ),
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "1",
//        name = "Avro Source",
//        `type` = Dag.NodeType.Source,
//        clazz = "link.lcz.kbookdemo.logicnode.source.AvroSource"),
//      config = JObject(JField("topicName", JString("intopic"))),
//      inbounds = List(),
//      outbounds = List()
//    ),
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "3",
//        name = "Copy 1",
//        `type` = Dag.NodeType.Transformer,
//        clazz = "link.lcz.kbookdemo.logicnode.transformer.Copy"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List()
//    ),
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "5",
//        name = "Copy 2",
//        `type` = Dag.NodeType.Transformer,
//        clazz = "link.lcz.kbookdemo.logicnode.transformer.Copy"),
//      config = JObject(),
//      inbounds = List(),
//      outbounds = List()
//    ),
//    Dag.NodeDef(
//      Dag.NodeMeta(uuid = "4",
//        name = "Avro Sink 2",
//        `type` = Dag.NodeType.Sink,
//        clazz = "link.lcz.kbookdemo.logicnode.sink.AvroSink"),
//      config = JObject(JField("topicName", JString("outtopic3"))),
//      inbounds = List(),
//      outbounds = List()
//    ),
//  )
//
//  val dag = Dag(
//    Graph[Dag.NodeDef, LDiEdge](
//      LDiEdge(source, transformer1)(Dag.EdgeLabel("a", 0, 0)),
//      LDiEdge(transformer1, sink1)(Dag.EdgeLabel("b", 0, 0)),
//      LDiEdge(transformer1, sink2)(Dag.EdgeLabel("c", 1, 0)),
//      LDiEdge(transformer1, transformer2)(Dag.EdgeLabel("d", 2, 0)),
//      LDiEdge(transformer2, sink2)(Dag.EdgeLabel("e", 0, 1))
//    ))
//
//  val kBookConfig = new KBookConfig(
//    KBookConfig.KBookMeta("namehere", "uuidhere", "schema", "localhost:9092"),
//    dag)
//  val kBook = KBook(KBookConfig.parse(KBookConfig.render(kBookConfig)))
//  //  val rendered = KBookConfig.render(new KBookConfig(KBookConfig.KBookMeta("1", "2"), dag))
//
//  //  println(rendered)
//  //  println(KBookConfig.render(KBookConfig.parse(rendered)))
//
//}
