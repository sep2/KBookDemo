package link.lcz.kbookdemo

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object KBookRunner extends LazyLogging {
  def props: Props = Props[KBookRunner]

  sealed trait Command

  case class PostBook(bookDefinition: String) extends Command

  case class PlayBook(uuid: String) extends Command

  case class StopBook(uuid: String) extends Command

  case class DeleteBook(uuid: String) extends Command

}

class KBookRunner extends Actor with LazyLogging {

  import KBookRunner._

  private val store = mutable.Set[KBook]()

  override def receive = {
    case PostBook(bd) => {
      logger.info(s"post")
      val kbc = KBookConfig.parse(bd)
      if (store.exists(_.uuid == kbc.meta.uuid)) {
        logger.error(s"[${kbc.meta.uuid}] already exists")
      } else {
        store.add(KBook(kbc))
      }
    }
    case PlayBook(uuid) =>
      logger.info(s"play")
      store.find(_.uuid == uuid) match {
        case Some(kBook) => kBook.start()
        case None => logger.error(s"[$uuid] not found")
      }
    case StopBook(uuid) =>
      logger.info(s"stop")
      store.find(_.uuid == uuid) match {
        case Some(kBook) => kBook.stop()
        case None => logger.error(s"[$uuid] not found")
      }
    case DeleteBook(uuid) =>
      logger.info(s"delete")
      store.retain(_.uuid != uuid)
  }
}
