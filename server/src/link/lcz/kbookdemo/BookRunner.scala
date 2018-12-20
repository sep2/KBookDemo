package link.lcz.kbookdemo

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object BookRunner extends LazyLogging {
  def props: Props = Props[BookRunner]

  case class PostBook(bookDefinition: String)
  case class PlayBook(bookUuid: String)
  case class StopBook(bookUuid: String)
  case class DeleteBook(bookUuid: String)
}

class BookRunner extends Actor with LazyLogging {
  import BookRunner._

  private val store = mutable.Set[Book]()

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
