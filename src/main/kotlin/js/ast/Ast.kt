package js.ast

import kotlin.Boolean
import kotlin.String

// 定义抽象语法树的节点
sealed class Node

data class Program(
    val statementList: StatementList
) : Node()

open class Statement: Node()

data class StatementList(
    val statements: List<Statement>
) : Node()

data class Block(
    val statementList: StatementList
) : Statement()


data class ImportStatement(
    val importToken: Token,
    val importFromBlock: ImportFromBlock,
) : Statement()

open class ImportFromBlock: Node()

data class ImportFrom(
    val moduleSpecifier: Token,
    val namedImports: List<NamedImport>,
    val defaultImport: DefaultImport?
) : ImportFromBlock()

data class NamedImport(
    val identifier: Token,
    val isTypeOnly: Boolean
) : Node()

data class DefaultImport(
    val identifier: Token,
    val isTypeOnly: Boolean
) : Node()

data class ExportStatement(
    val declaration: Node
) : Statement()

class ClassDeclaration(): Statement()

data class FunctionDeclaration(
    val asyncToken: Token?,
    val functionKeyword: Token,
    val asteriskToken: Token?,
    val functionName: Token,
    val openParenToken: Token,
    val parameters: List<Token>,
    val closeParenToken: Token,
    val body: FunctionBody
) : Statement()

data class FunctionBody(
    val openBraceToken: Token,
    val statementList: StatementList,
    val closeBracketToken: Token
): Node()

data class EmptyStatement(
    val token: Token
) : Statement()


data class VariableStatement(
    val variableDeclarationList: VariableDeclarationList
) : Statement()

data class VariableDeclarationList(
    val varModifier: VarModifier,
    val variableDeclarations: List<VariableDeclaration>
) : Statement()

data class VarModifier(
    val token: Token
) : Node()

data class VariableDeclaration(
    val variableName: Token,
    val initializer: SingleExpression?
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

data class ContinueStatement(
    val continueToken: Token,
    val label: Token?
) : Statement()

data class BreakStatement(
    val breakToken: Token,
    val label: Token? // identifier
) : Statement()

data class ReturnStatement(
    val returnToken: Token,
    val expression: ExpressionSequence?
) : Statement()

data class WithStatement(
    val withToken: Token,
    val expressionSequence: ExpressionSequence,
    val statement: Statement
) : Statement()

data class LabelledStatement(
    val identifier: Token,
    val statement: Statement
) : Statement()

data class ThrowStatement(
    val throwToken: Token,
    val expression: ExpressionSequence
) : Statement()

data class SwitchStatement(
    val switchToken: Token,
    val expression: ExpressionSequence,
    val caseBlock: CaseBlock
) : Statement()

data class CaseBlock(
    val caseClauses: CaseClauses?,
    val defaultClause: DefaultClause?,
    val caseClauses2: CaseClauses?
) : Node()

data class CaseClauses(
    val caseClauses: List<CaseClause>
) : Node()

data class CaseClause(
    val caseToken: Token,
    val expression: ExpressionSequence,
    val statementList: StatementList?
) : Node()

data class DefaultClause(
    val defaultToken: Token,
    val statementList: StatementList?
) : Node()

data class YieldStatement(
    val yieldToken: Token,
    val expression: ExpressionSequence?
) : Statement()

data class TryStatement(
    val tryBlock: Block,
    val catchProduction: CatchProduction?,
    val finallyProduction: FinallyProduction?
) : Statement()

data class CatchProduction(
    val catchToken: Token,
    val catchParameter: Assignable?,
    val catchBlock: Block
) : Node()

data class Assignable(
    val identifier: Token // identifier, array literal, object literal
)

data class FinallyProduction(
    val finallyToken: Token,
    val finallyBlock: Block
): Node()

data class DebuggerStatement(val token: Token) : Statement()

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
    val newToken: Token,
    val expression: SingleExpression,
    val arguments: ArgumentList?
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
    val expression: SingleExpression,
    val operator: Token
) : SingleExpression()

data class PostDecreaseExpression(
    val expression: SingleExpression,
    val operator: Token
) : SingleExpression()

data class ArgumentsExpression(
    val expression: SingleExpression,
    val argumentList: ArgumentList
) : SingleExpression()

data class ArgumentList(
    val arguments: List<Argument>
)

data class Argument(
    val ellipse: Token?,
    val expression: SingleExpression
)

data class MemberDotExpression(
    val expression: SingleExpression,
    val questionToken: Token?,
    val dotToken: Token,
    val hashtagToken: Token?,
    val identifier: Token
) : SingleExpression()

data class MemberIndexExpression(
    val leftExpression: SingleExpression,
    val questionDotToken: Token?,
    val openBracketToken: Token,
    val expressionSequence: ExpressionSequence,
    val closeBracketToken: Token,
) : SingleExpression()

data class OptionalChainExpression(
    val leftExpression: SingleExpression,
    val questionDotToken: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class UnaryPlusExpression(
    val expression: SingleExpression
) : SingleExpression()

class ImportExpression() : SingleExpression()

data class YieldExpression(
    val statement: YieldStatement
) : SingleExpression()

class SuperExpression() : SingleExpression()

data class UnaryMinusExpression(
    val expression: SingleExpression
) : SingleExpression()

data class BitNotExpression(
    val expression: SingleExpression
) : SingleExpression()

data class NotExpression(
    val expression: SingleExpression
) : SingleExpression()

data class AwaitExpression(
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
    val value: Token
) : SingleExpression()

data class NumericLiteralExpression(
    val value: Token
) : SingleExpression()

data class ObjectLiteralExpression(
    val openBracketToken: Token,
    val propertyAssignments: List<PropertyAssignment>,
    val closeBracketToken: Token
): SingleExpression()

open class PropertyAssignment : Node()

data class PropertyExpressionAssignment(
    val propertyName: PropertyName,
    val colonToken: Token,
    val propertyValue: SingleExpression
) : PropertyAssignment()

data class PropertyShorthandAssignment(
    val name: PropertyName
) : PropertyAssignment()

data class SpreadAssignment(
    val expression: SingleExpression
) : PropertyAssignment()

data class ComputedPropertyNameAssignment(
    val name: PropertyName,
    val expression: SingleExpression
) : PropertyAssignment()

// PropertyName
open class PropertyName : Node()

data class IdentifierPropertyName(
    val name: Token
) : PropertyName()

data class StringLiteralPropertyName(
    val value: Token
) : PropertyName()

data class NumericLiteralPropertyName(
    val value: Token
) : PropertyName()

data class ComputedPropertyName(
    val openBracketToken: Token,
    val expression: SingleExpression,
    val closeBracketToken: Token
) : PropertyName()

data class AssignmentExpression(
    val leftExpression: SingleExpression,
    val assignOperator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class TernaryExpression(
    val leftExpression: SingleExpression,
    val questionToken: Token,
    val middleExpression: SingleExpression,
    val colonToken: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class LogicalOrExpression(
    val left: SingleExpression,
    val op: Token,
    val right: SingleExpression
) : SingleExpression()

data class ArrayLiteralExpression(
    val elements: List<Node>
) : SingleExpression()

data class TemplateStringExpression(
    val expression: SingleExpression,
    val templateStringLiteral: TemplateStringLiteral
) : SingleExpression()

data class TemplateStringLiteral(
    val parts: List<String>
) : Node()

data class AssignmentOperatorExpression(
    val leftExpression: SingleExpression,
    val assignmentOperator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class LogicalAndExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class BitOrExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class BitXorExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class BitAndExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class EqualityExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class InExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class RelationalExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class InstanceOfExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class BitShiftExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class CoalesceExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class AdditiveExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class MultiplicativeExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()

data class PowerExpression(
    val leftExpression: SingleExpression,
    val operator: Token,
    val rightExpression: SingleExpression
) : SingleExpression()
