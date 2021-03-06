package sigmastate.utxo

import sigmastate._
import sigmastate.interpreter.Interpreter


class UtxoInterpreter(override val maxCost: Int = CostTable.ScriptLimit) extends Interpreter {
  override type StateT = StateTree
  override type CTX = UtxoContext

  override def specificTransformations(context: UtxoContext): PartialFunction[SigmaStateTree, SigmaStateTree] = {
    case Inputs => ConcreteCollection(context.boxesToSpend.map(BoxConstant.apply))

    case Outputs => ConcreteCollection(context.spendingTransaction.newBoxes
      .zipWithIndex
      .map { case (b, i) => BoxWithMetadata(b, BoxMetadata(context.currentHeight, i.toShort)) }
      .map(BoxConstant.apply))

    case Self => BoxConstant(context.self)

    case t: Transformer[_, _] if t.transformationReady => t.function()

    case Height => IntConstant(context.currentHeight)

    case LastBlockUtxoRootHash => AvlTreeConstant(context.lastBlockUtxoRoot)

    case inst: Transformer[SBox.type, _]
      if inst.input.isInstanceOf[BoxConstant] =>

      inst.function(inst.input.asInstanceOf[BoxConstant])
  }
}