package com.atomist.tree.content.text.microgrammar

import com.atomist.rug.kind.core.FileArtifactBackedMutableView
import com.atomist.rug.kind.dynamic.{ChildResolver, MutableContainerTreeNodeMutableView, MutableTreeNodeUpdater}
import com.atomist.rug.spi.{MutableView, TypeProvider, TypeRegistry, Typed}
import com.atomist.tree.content.text.MutableContainerTreeNode
import com.atomist.tree.content.text.grammar.MatchListener

/**
  * Type information for the results of evaluating a microgrammar.
  * It will have the name of the microgrammar, and can be
  * evaluated against any file
  *
  * @param microgrammar microgrammar to evaluate
  */
class MicrogrammarTypeProvider(microgrammar: Microgrammar)
  extends TypeProvider(classOf[MutableContainerTreeNodeMutableView])
    with ChildResolver {

  override def name: String = microgrammar.name

  override def description: String = s"Microgrammar type for [$name]"

  override def resolvesFromNodeTypes: Set[String] = Set("file")

  override def findAllIn(context: MutableView[_]): Option[Seq[MutableView[_]]] = context match {
    case f: FileArtifactBackedMutableView =>
      val l: Option[MatchListener] = None
      val container = microgrammar.matchesInContainer(f.content, l)
      val views = container.childNodes collect {
        case moo: MutableContainerTreeNode =>
          new MutableContainerTreeNodeMutableView(moo, f)
      }
      f.registerUpdater(new MutableTreeNodeUpdater(container))
      Some(views)
    case _ => None
  }
}


/**
  * Decorating TypeRegistry
  * Try to resolve from the new types first, then fallback to the shared delegate.
  * One of these is instantiated per usage.
  *
  * @param delegate
  * @param newTypes
  */
class UsageSpecificTypeRegistry(delegate: TypeRegistry,
                                newTypes: Seq[Typed]) extends TypeRegistry {

  override def findByName(kind: String): Option[Typed] =
    newTypes.find(t => kind.equals(t.name)).orElse(delegate.findByName(kind))

  override def kindNames: Traversable[String] =
    newTypes.map(_.name) ++ delegate.kindNames

  override def kinds: Seq[Typed] =
    newTypes ++ kinds
}