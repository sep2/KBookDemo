import mill._
import mill.scalajslib._
import mill.scalalib._

import coursier.maven.MavenRepository

object server extends ScalaModule {
  def scalaVersion = "2.12.8"

//  def compileIvyDeps = Agg(ivy"com.lihaoyi::acyclic:0.1.7")
//
//  def scalacOptions = Seq("-P:acyclic:force")
//
//  def scalacPluginIvyDeps = Agg(ivy"com.lihaoyi::acyclic:0.1.7")

  def repositories = super.repositories ++ Seq(
    MavenRepository("https://packages.confluent.io/maven")
  )

  def ivyDeps = Agg(
    ivy"ch.qos.logback:logback-classic:1.2.3",
    ivy"com.typesafe.scala-logging::scala-logging:3.9.0",
    ivy"com.typesafe.akka::akka-actor:2.5.19",
    ivy"com.typesafe.akka::akka-stream:2.5.19",
    ivy"com.typesafe.akka::akka-http:10.1.5",
    //ivy"com.lihaoyi::ujson:0.7.1",
    //ivy"com.lihaoyi::upickle:0.7.1",
    ivy"org.apache.avro:avro:1.8.2",
    ivy"org.apache.kafka::kafka-streams-scala:2.1.0",
    ivy"io.confluent:kafka-streams-avro-serde:5.1.0",
    ivy"org.scala-graph::graph-core:1.12.5",
    ivy"org.scala-graph::graph-constrained:1.12.3",
    ivy"org.scala-graph::graph-json:1.12.1",
    ivy"net.liftweb::lift-json:3.0.1"
  )
}
