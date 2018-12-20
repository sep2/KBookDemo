package link.lcz.kbookdemo

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object KBookRunner extends LazyLogging {
  def props: Props = Props[KBookRunner]

  case class PostBook(bookDefinition: String)
  case class PlayBook(bookUuid: String)
  case class StopBook(bookUuid: String)
  case class DeleteBook(bookUuid: String)
}

class KBookRunner extends Actor with LazyLogging {
  import KBookRunner._

  private val store = mutable.Set[KBook]()

  override def receive = {
    case PostBook(bd)     => {
      logger.info("not implemented")
      //store.add(Dag.parse(bd).construct())
    }
    case PlayBook(uuid)   => store.find(_.uuid == uuid)
    case StopBook(uuid)   => store.find(_.uuid == uuid)
    case DeleteBook(uuid) => store.find(_.uuid == uuid)
  }
}
