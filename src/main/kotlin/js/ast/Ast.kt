package js.ast

import kotlin.Boolean
import kotlin.String

// 定义抽象语法树的节点
sealed class Node

data class Program(
    val statements: List<Statement>
) : Node()

open class Statement: Node()

data class FunctionDeclaration(
    val functionName: Token,
    val parameters: List<Token>,
    val body: FunctionBody
) : Statement()

data class FunctionBody(
    val statements: List<Statement>
): Node()

data class EmptyStatement(
    val token: Token
) : Statement()

data class Block(
    val statements: List<Statement>
) : Statement()

data class VariableStatement(
    val variableDeclarationList: VariableDeclarationList
) : Statement()

data class VariableDeclaration(
    val variableName: Token,
    val initializer: Node?
) : Statement()

data class VariableDeclarationList(
    val variableDeclaration: List<VariableDeclaration>
) : Statement()

data class IfStatement(
    val condition: ExpressionStatement,
    val trueStatement: Statement,
    val falseStatement: Statement?
) : Statement()

open class IterationStatement() : Statement()

data class DoStatement(
    val statement: Statement,
    val condition: ExpressionStatement
) : IterationStatement()

data class WhileStatement(
    val condition: ExpressionStatement,
    val statement: Statement
) : IterationStatement()

data class ForStatement(
    val initializer: Node?, // maybe ExpressionSequence or VariableDeclarationList
    val condition: ExpressionSequence?,
    val incrementer: ExpressionSequence?,
    val statement: Statement
): IterationStatement()

data class ForVarStatement(
    val variableDeclaration: VariableDeclaration,
    val condition: ExpressionSequence?,
    val incrementer: ExpressionSequence?,
    val statement: Statement
) : IterationStatement()

data class ForInStatement(
    val singleExpressionOrVariableDeclarationList: Node,
    val expressionSequence: ExpressionSequence,
    val statement: Statement
) : IterationStatement()

data class ForOfStatement(
    val await: Boolean, // default is false
    val singleExpressionOrVariableDeclarationList: Node,
    val expressionSequence: ExpressionSequence,
    val statement: Statement
) : IterationStatement()

data class ExpressionStatement(
    val expressionSequence: ExpressionSequence
) : Statement()

open class SingleExpression : Node()

data class ExpressionSequence(
    val expressions: List<SingleExpression>
) : SingleExpression() {

    fun isSingleExpression(): Boolean {
        return expressions.size == 1
    }

    fun asSingleExpression(): SingleExpression {
        if  (isSingleExpression()) {
            return  expressions[0]
        } else {
            throw IllegalStateException("ExpressionSequence is not a single expression")
        }
    }
}

data class FunctionExpression(
    val name: Token?,
    val parameters: List<Token>,
    val body: FunctionBody
) : SingleExpression()

data class NewExpression(
    val expression: SingleExpression,
    val arguments: List<Node>
) : SingleExpression()

data class DeleteExpression(
    val expression: SingleExpression
) : SingleExpression()

data class VoidExpression(
    val expression: SingleExpression
) : SingleExpression()

data class TypeofExpression(
    val expression: SingleExpression
) : SingleExpression()

data class PreIncrementExpression(
    val expression: SingleExpression
) : SingleExpression()

data class PreDecreaseExpression(
    val expression: SingleExpression
) : SingleExpression()

data class PostIncrementExpression(
    val expression: SingleExpression
) : SingleExpression()

data class PostDecreaseExpression(
    val expression: SingleExpression
) : SingleExpression()

data class UnaryPlusExpression(
    val expression: SingleExpression
) : SingleExpression()

data class UnaryMinusExpression(
    val expression: SingleExpression
) : SingleExpression()

data class BitNotExpression(
    val expression: SingleExpression
) : SingleExpression()

data class NotExpression(
    val expression: SingleExpression
) : SingleExpression()

class ThisExpression : SingleExpression()

data class IdentifierExpression(
    val name: Token
): SingleExpression()

data class ParenthesizedExpression(
    val expressionSequence: ExpressionSequence
) : SingleExpression()

open class LiteralExpression : SingleExpression()

class NullLiteral : LiteralExpression()

class RegExpLiteral : LiteralExpression()

data class BooleanLiteralExpression(
    val value: Boolean
) : SingleExpression()

data class StringLiteralExpression(
    val value: String
) : SingleExpression()

data class NumericLiteralExpression(
    val value: Double
) : SingleExpression()

data class ArrayAccessExpression(
    val array: SingleExpression,
    val index: SingleExpression
) : SingleExpression()

data class CommaExpression(
    val expressions: List<SingleExpression>
) : SingleExpression()

data class AssignmentExpression(
    val lhs: SingleExpression,
    val op: Token,
    val rhs: SingleExpression
) : SingleExpression()

data class ArrayLiteralExpression(
    val elements: List<Node>
) : SingleExpression()

data class TildeExpression(
    val expression: SingleExpression
) : SingleExpression()

data class CallExpression(val callee: Node, val arguments: List<Node>) : Node()

data class ReturnStatement(val expression: Node?) : Node()

data class AssignmentStatement(val variableName: String, val expression: Node) : Node()

data class Call(val name: String, val arguments: List<Node>) : Node()
data class If(val condition: Node, val ifTrue: Node, val ifFalse: Node?) : Node()
data class While(val condition: Node, val body: Node) : Node()

data class Return(val value: Node?) : Node()
data class String(val value: String) : Node()
//data class Boolean(val value: Boolean) : Node()
data class Null(val value:Any?=null) : Node()
data class Undefined(val value:Any?=null) : Node()
data class Array(val elements: List<Node>) : Node()
data class Object(val properties: List<Property>) : Node()
data class Property(val name: String, val value: Node) : Node()
data class ObjectGetter(val jsobject: Node, val property: String) : Node()
data class ObjectSetter(val jsobject: Node, val property: String, val value: Node) : Node()
data class New(val constructor: Node, val arguments: List<Node>) : Node()
data class This(val value: Any?=null) : Node()
data class ObjectGet(val jsobject: Node, val property: String) : Node()
data class ObjectSet(val jsobject: Node, val property: String, val value: Node) : Node()
data class UndefinedLiteral(val value: Any?=null) : Node()
data class For(val initializer: Node?, val condition: Node?, val iterator: Node?, val body: Node) : Node()
data class ForIn(val variable: String, val iterable: Node, val body: Node) : Node()
data class PropertyAccess(val jsobject: Node, val property: String) : Node()
data class StringLiteral(val value: String) : Node()
data class BooleanLiteral(val value: Boolean) : Node()
data class NumberLiteral(val value: Double) : Node()
data class ObjectLiteral(val properties: List<Property>) : Node()
data class ArrayLiteral(val elements: List<Node>) : Node()
data class UnaryOperation(val operator: String, val operand: Node) : Node()
data class VariableAssignment(val name: String, val value: Node) : Node()
data class PropertyAssignment(val jsobject: Node, val name: String, val value: Node) : Node()
data class Number(val value: Double) : Node()
data class BinaryOperation(val operator: String, val left: Node, val right: Node) : Node()
data class Variable(val name: String) : Node()
data class Assignment(val variable: Variable, val value: Node) : Node()
data class FunctionCall(val functionName: String, val arguments: List<Node>) : Node()

