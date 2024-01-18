package core

// 定义抽象语法树的节点
sealed class JsNode

data class JsProgram(
    val elements: List<JsSourceElement>
) : JsNode()

open class JsSourceElement : JsNode()

data class JsFunctionDeclaration(
    val functionName: Token,
    val parameters: List<Token>,
    val body: List<JsSourceElement>
) : JsSourceElement()

open class JsStatement: JsSourceElement()

data class JsBlock(
    val statements: List<JsStatement>
) : JsStatement()

data class JsVariableStatement(
    val variableDeclaration: List<JsVariableDeclaration>
) : JsStatement()

data class JsVariableDeclaration(
    val variableName: Token,
    val initializer: JsNode?
) : JsStatement()

data class JsExpressionStatement(
    val expression: List<JsSingleExpression>
) : JsNode()

open class JsSingleExpression : JsNode()

data class JsFunctionExpression(
    val name: Token?,
    val parameters: List<Token>,
    val body: List<JsSourceElement>
) : JsSingleExpression()

data class JsNewExpression(
    val expression: JsSingleExpression,
    val arguments: List<JsNode>
) : JsSingleExpression()

data class JsDeleteExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsVoidExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsTypeofExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsPreIncrementExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsPreDecreaseExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsPostIncrementExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsPostDecreaseExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsUnaryPlusExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsUnaryMinusExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsBitNotExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsNotExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

class JsThisExpression : JsSingleExpression()

data class JsIdentifierExpression(
    val name: Token
): JsSingleExpression()

data class JsParenthesizedExpression(
    val expressions: List<JsSingleExpression>
) : JsSingleExpression()

open class LiteralExpression : JsSingleExpression()

class JsNullLiteral : LiteralExpression()

class JsRegExpLiteral : LiteralExpression()

data class JsBooleanLiteralExpression(
    val value: Boolean
) : JsSingleExpression()

data class JsStringLiteralExpression(
    val value: String
) : JsSingleExpression()

data class JsNumericLiteralExpression(
    val value: Double
) : JsSingleExpression()

data class JsArrayAccessExpression(
    val array: JsSingleExpression,
    val index: JsSingleExpression
) : JsSingleExpression()

data class JsCommaExpression(
    val expressions: List<JsSingleExpression>
) : JsSingleExpression()

data class JsAssignmentExpression(
    val lhs: JsSingleExpression,
    val op: Token,
    val rhs: JsSingleExpression
) : JsSingleExpression()

data class JsArrayLiteralExpression(
    val elements: List<JsNode>
) : JsSingleExpression()

data class JsTildeExpression(
    val expression: JsSingleExpression
) : JsSingleExpression()

data class JsCallExpression(val callee: JsNode, val arguments: List<JsNode>) : JsNode()

data class JsReturnStatement(val expression: JsNode?) : JsNode()

data class JsAssignmentStatement(val variableName: String, val expression: JsNode) : JsNode()

data class JsCall(val name: String, val arguments: List<JsNode>) : JsNode()
data class JsIf(val condition: JsNode, val ifTrue: JsNode, val ifFalse: JsNode?) : JsNode()
data class JsWhile(val condition: JsNode, val body: JsNode) : JsNode()

data class JsReturn(val value: JsNode?) : JsNode()
data class JsString(val value: String) : JsNode()
data class JsBoolean(val value: Boolean) : JsNode()
data class JsNull(val value:Any?=null) : JsNode()
data class JsUndefined(val value:Any?=null) : JsNode()
data class JsArray(val elements: List<JsNode>) : JsNode()
data class JsObject(val properties: List<JsProperty>) : JsNode()
data class JsProperty(val name: String, val value: JsNode) : JsNode()
data class JsObjectGetter(val jsobject: JsNode, val property: String) : JsNode()
data class JsObjectSetter(val jsobject: JsNode, val property: String, val value: JsNode) : JsNode()
data class JsNew(val constructor: JsNode, val arguments: List<JsNode>) : JsNode()
data class JsThis(val value: Any?=null) : JsNode()
data class JsObjectGet(val jsobject: JsNode, val property: String) : JsNode()
data class JsObjectSet(val jsobject: JsNode, val property: String, val value: JsNode) : JsNode()
data class JsUndefinedLiteral(val value: Any?=null) : JsNode()
data class JsFor(val initializer: JsNode?, val condition: JsNode?, val iterator: JsNode?, val body: JsNode) : JsNode()
data class JsForIn(val variable: String, val iterable: JsNode, val body: JsNode) : JsNode()
data class JsPropertyAccess(val jsobject: JsNode, val property: String) : JsNode()
data class JsStringLiteral(val value: String) : JsNode()
data class JsBooleanLiteral(val value: Boolean) : JsNode()
data class JsNumberLiteral(val value: Double) : JsNode()
data class JsObjectLiteral(val properties: List<JsProperty>) : JsNode()
data class JsArrayLiteral(val elements: List<JsNode>) : JsNode()
data class JsUnaryOperation(val operator: String, val operand: JsNode) : JsNode()
data class JsVariableAssignment(val name: String, val value: JsNode) : JsNode()
data class JsPropertyAssignment(val jsobject: JsNode, val name: String, val value: JsNode) : JsNode()
data class JsNumber(val value: Double) : JsNode()
data class JsBinaryOperation(val operator: String, val left: JsNode, val right: JsNode) : JsNode()
data class JsVariable(val name: String) : JsNode()
data class JsAssignment(val variable: JsVariable, val value: JsNode) : JsNode()
data class JsFunctionCall(val functionName: String, val arguments: List<JsNode>) : JsNode()
