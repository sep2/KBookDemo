package link.lcz.kbookdemo.logicnode

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.typeTag
import scala.reflect.{ClassTag, ManifestFactory}

abstract class ConfiguredNode[A: TypeTag](env: BaseNode.Environment)
    extends BaseNode(env) {
  private implicit val jsonFormats = net.liftweb.json.DefaultFormats

  private implicit def toManifest[T: TypeTag]: Manifest[T] = {
    val t = typeTag[T]
    val mirror = t.mirror
    def toManifestRec(t: Type): Manifest[_] = {
      val clazz = ClassTag[T](mirror.runtimeClass(t)).runtimeClass
      if (t.typeArgs.length == 1) {
        val arg = toManifestRec(t.typeArgs.head)
        ManifestFactory.classType(clazz, arg)
      } else if (t.typeArgs.length > 1) {
        val args = t.typeArgs.map(x => toManifestRec(x))
        ManifestFactory.classType(clazz, args.head, args.tail: _*)
      } else {
        ManifestFactory.classType(clazz)
      }
    }
    toManifestRec(t.tpe).asInstanceOf[Manifest[T]]
  }

  lazy val config: A = env.nd.config.extract[A]
}
