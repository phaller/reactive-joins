package scala.async.internal.imports

sealed trait MatchResult
case object Matched extends MatchResult
case object NoMatch extends MatchResult
case object Retry extends MatchResult