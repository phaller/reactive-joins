package scala.async.internal.imports.nondeterministic

sealed trait MatchResult
case object Resolved extends MatchResult
case object NoMatch extends MatchResult
case object Retry extends MatchResult