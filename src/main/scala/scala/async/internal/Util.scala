package scala.async.internal

trait Util {
  self: JoinMacro =>
  import c.universe._
  import scala.async.internal.Debug

  // Returns the only type argument of a symbol.
  // For example: the only type argument of "Observable[Int]" is "Int". Calling 
  // the method with a symbol that has no type argument will and 
  // should cause a error.
  def typeArgumentOf(sym: Symbol) = 
    sym.typeSignature.asInstanceOf[TypeRefApi].args.head

  // Generates a TermName with a fresh name in the context
  def fresh(name: String): TermName = TermName(c.freshName(name))

 // Generates a fresh name (a unique identifier in a TermName tree) for every element in a traversable
  def freshNames[A](t: Traversable[A], prefix: String): Map[A, TermName] = t.map(e => (e, fresh(prefix))).toMap

  // Replaces every occurence of a symbol in a tree called "block" with the tree stored in
  // the "trees" list. The symbols are mapped onto the trees by means of their list-indices. 
  // Therefore, the two lists "symbols", and "trees" are required to have the same size.
  def replaceSymbolsWithTrees(symbols: List[Symbol], trees: List[c.Tree], block: c.Tree) = {
    // We reuse functionality which was implemented in the compiler internal SymbolTable,
    // and thus have to cast all trees, and symbols to the internal types.
    val symtable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]
    val symsToReplace = symbols.asInstanceOf[List[symtable.Symbol]]
    val treesToInsert = trees.asInstanceOf[List[symtable.Tree]]
    val substituter = new symtable.TreeSubstituter(symsToReplace, treesToInsert)
    val transformedBody = substituter.transform(block.asInstanceOf[symtable.Tree])
    c.untypecheck(transformedBody.asInstanceOf[c.universe.Tree]) 
  }
}