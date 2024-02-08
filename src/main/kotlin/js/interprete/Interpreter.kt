package js.interprete

import js.ast.*
import kotlin.math.pow


class Interpreter {
    private val builtInJsObject = BuiltInJsObject()
    private val variablesMap = mutableMapOf<String, JsValue>()

    fun evaluate(node: Node): JsValue {
        return when (node) {
            is Program -> return evaluateProgram(node)
            else -> JsValue.UNDEFINED
        }
    }

    private fun evaluateProgram(program: Program): JsValue {
        return evaluateStatementList(program.statementList)
    }

    private fun evaluateStatementList(statement: StatementList): JsValue {
        var value = JsValue.UNDEFINED
        statement.statements.forEach { value = evaluateStatement(it) }
        return value
    }

    private fun evaluateStatement(statement: Statement): JsValue {
        return when (statement) {
            is Block -> evaluateBlock(statement)
            is VariableStatement -> evaluateVariableStatement(statement)
            is ImportStatement -> evaluateImportStatement(statement)
            is ExportStatement -> evaluateExportStatement(statement)
            is EmptyStatement -> evaluateEmptyStatement(statement)
            is ClassDeclaration -> evaluateClassDeclaration(statement)
            is FunctionDeclaration -> evaluateFunctionDeclaration(statement)
            is ExpressionStatement -> evaluateExpressionStatement(statement)
            is IfStatement -> evaluateIfStatement(statement)
            is IterationStatement -> evaluateIterationStatement(statement)
            is ContinueStatement -> evaluateContinueStatement(statement)
            is BreakStatement -> evaluateBreakStatement(statement)
            is ReturnStatement -> evaluateReturnStatement(statement)
            is YieldStatement -> evaluateYieldStatement(statement)
            is WithStatement -> evaluateWithStatement(statement)
            is LabelledStatement -> evaluateLabelledStatement(statement)
            is SwitchStatement -> evaluateSwitchStatement(statement)
            is ThrowStatement -> evaluateThrowStatement(statement)
            is TryStatement -> evaluateTryStatement(statement)
            is DebuggerStatement -> evaluateDebuggerStatement(statement)
            else -> throw IllegalArgumentException("Unsupported statement type: ${statement::class}")
        }
    }

    private fun evaluateDebuggerStatement(statement: DebuggerStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTryStatement(statement: TryStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThrowStatement(statement: ThrowStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateSwitchStatement(statement: SwitchStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLabelledStatement(statement: LabelledStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateWithStatement(statement: WithStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldStatement(statement: YieldStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateReturnStatement(statement: ReturnStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBreakStatement(statement: BreakStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateContinueStatement(statement: ContinueStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIterationStatement(statement: IterationStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIfStatement(statement: IfStatement): JsValue {
        val value = evaluateExpressionStatement(statement.condition)
        if  (value.toBoolean()) {
            return evaluateStatement(statement.trueStatement)
        } else if (statement.falseStatement != null) {
            return evaluateStatement(statement.falseStatement)
        }
        return JsValue.UNDEFINED
    }

    private fun evaluateFunctionDeclaration(node: FunctionDeclaration): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateExpressionStatement(statement: ExpressionStatement): JsValue {
        return evaluateExpressionSequence(statement.expressionSequence)
    }

    private fun evaluateExpressionSequence(expressionSequence: ExpressionSequence): JsValue {
        var value = JsValue.UNDEFINED
        expressionSequence.expressions.forEach { value = it.evaluate() }
        return value
    }

    private fun evaluateClassDeclaration(statement: ClassDeclaration): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEmptyStatement(statement: EmptyStatement): JsValue {
        return JsValue.UNDEFINED
    }

    private fun evaluateExportStatement(statement: ExportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportStatement(statement: ImportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVariableStatement(statement: VariableStatement): JsValue {
        statement.variableDeclarationList.variableDeclarations.forEach {
            // TODO 同名的变量覆盖问题
            variablesMap[it.variableName.value] = it.initializer?.evaluate() ?: JsValue.UNDEFINED
        }
        return JsValue.UNDEFINED
    }

    private fun SingleExpression.evaluate(): JsValue {
        when (this) {
            is FunctionExpression -> return evaluateFunctionExpression(this)
            is OptionalChainExpression -> return evaluateOptionalChainExpression(this)
            is MemberIndexExpression -> return evaluateMemberIndexExpression(this)
            is MemberDotExpression -> return evaluateMemberDotExpression(this)
            is NewExpression -> return evaluateNewExpression(this)
            is ArgumentsExpression -> return evaluateArgumentsExpression(this)
            is PostIncrementExpression -> return evaluatePostIncrementExpression(this)
            is PostDecreaseExpression -> return evaluatePostDecreaseExpression(this)
            is DeleteExpression -> return evaluateDeleteExpression(this)
            is VoidExpression -> return evaluateVoidExpression(this)
            is TypeofExpression -> return evaluateTypeofExpression(this)
            is PreIncrementExpression -> return evaluatePreIncrementExpression(this)
            is PreDecreaseExpression -> return evaluatePreDecreaseExpression(this)
            is UnaryPlusExpression -> return evaluateUnaryPlusExpression(this)
            is UnaryMinusExpression -> return evaluateUnaryMinusExpression(this)
            is BitNotExpression -> return evaluateBitNotExpression(this)
            is NotExpression -> return evaluateNotExpression(this)
            is AwaitExpression -> return evaluateAwaitExpression(this)
            is PowerExpression -> return evaluatePowerExpression(this)
            is MultiplicativeExpression -> return evaluateMultiplicativeExpression(this)
            is AdditiveExpression -> return evaluateAdditiveExpression(this)
            is CoalesceExpression -> return evaluateCoalesceExpression(this)
            is BitShiftExpression -> return evaluateBitShiftExpression(this)
            is RelationalExpression -> return evaluateRelationalExpression(this)
            is InstanceOfExpression -> return evaluateInstanceOfExpression(this)
            is InExpression -> return evaluateInExpression(this)
            is EqualityExpression -> return evaluateEqualityExpression(this)
            is BitAndExpression -> return evaluateBitAndExpression(this)
            is BitXorExpression -> return evaluateBitXorExpression(this)
            is BitOrExpression -> return evaluateBitOrExpression(this)
            is LogicalAndExpression -> return evaluateLogicalAndExpression(this)
            is LogicalOrExpression -> return evaluateLogicalOrExpression(this)
            is TernaryExpression -> return evaluateTernaryExpression(this)
            is AssignmentExpression -> return evaluateAssignmentExpression(this)
            is AssignmentOperatorExpression -> return evaluateAssignmentOperatorExpression(this)
            is ImportExpression -> return evaluateImportExpression(this)
            is TemplateStringExpression -> return evaluateTemplateStringExpression(this)
            is YieldExpression -> return evaluateYieldExpression(this)
            is ThisExpression -> return evaluateThisExpression(this)
            // TODO 同名的变量覆盖问题
            is IdentifierExpression -> return evaluateIdentifierExpression(this)
            is SuperExpression ->  return evaluateSuperExpression(this)
            is LiteralExpression ->  return evaluateLiteralExpression(this)
            is StringLiteralExpression -> return JsValue(ValueType.STRING, value.value)
            is NumericLiteralExpression -> return JsValue(ValueType.NUMBER, value.value.toDouble())
            is BooleanLiteralExpression -> return JsValue(ValueType.BOOLEAN, value.toDouble())
            is ArrayLiteralExpression -> return evaluateArrayLiteralExpression(this)
            is ObjectLiteralExpression -> return evaluateObjectLiteralExpression(this)
            is ParenthesizedExpression -> return evaluateParenthesizedExpression(this)
            else -> throw RuntimeException("Unsupported expression: $this")
        }
    }

    private fun evaluateIdentifierExpression(identifierExpression: IdentifierExpression): JsValue {
        val builtInJsObject = builtInJsObject.get(identifierExpression.name.value)
        if (builtInJsObject != null) {
            return JsValue(ValueType.OBJECT, builtInJsObject)
        }
        return variablesMap[identifierExpression.name.value] ?: JsValue.UNDEFINED
    }

    private fun evaluateFunctionExpression(functionExpression: FunctionExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateOptionalChainExpression(optionalChainExpression: OptionalChainExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberIndexExpression(memberIndexExpression: MemberIndexExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberDotExpression(memberDotExpression: MemberDotExpression): JsValue {
        val value = memberDotExpression.expression.evaluate()
        if (value.valueType != ValueType.OBJECT) {
            throw RuntimeException("Member access on non-object: $value")
        }
        val jsObject = value.value as JsObject
        // 执行属性访问
        // 查找对象表，找到属性对应的值
        // 返回属性对应的值
        return jsObject.get(memberDotExpression.identifier.value)
    }

    private fun evaluateNewExpression(newExpression: NewExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArgumentsExpression(argumentsExpression: ArgumentsExpression): JsValue {
        val value = argumentsExpression.expression.evaluate()
        if (value.valueType != ValueType.FUNCTION) {
            throw RuntimeException("Cannot call non-function: $value")
        }
        // 执行函数调用
        val argumentList = argumentsExpression.argumentList.arguments.map { it.expression.evaluate() }
        val jsFunction = value.value as JsFunction
        return jsFunction.call(argumentList)
    }

    private fun evaluatePostIncrementExpression(postIncrementExpression: PostIncrementExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePostDecreaseExpression(postDecreaseExpression: PostDecreaseExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateDeleteExpression(deleteExpression: DeleteExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVoidExpression(voidExpression: VoidExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTypeofExpression(typeofExpression: TypeofExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePreIncrementExpression(preIncrementExpression: PreIncrementExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePreDecreaseExpression(preDecreaseExpression: PreDecreaseExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryPlusExpression(unaryPlusExpression: UnaryPlusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryMinusExpression(unaryMinusExpression: UnaryMinusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitNotExpression(bitNotExpression: BitNotExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateNotExpression(notExpression: NotExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAwaitExpression(awaitExpression: AwaitExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePowerExpression(powerExpression: PowerExpression): JsValue {
        val leftValue = powerExpression.leftExpression.evaluate()
        val rightValue = powerExpression.rightExpression.evaluate()
        println("left=${leftValue.value}, right=${rightValue.value}")
        return when (powerExpression.operator.type) {
            TokenType.OPERATOR_POWER -> JsValue(ValueType.NUMBER, leftValue.toDouble().pow(rightValue.toDouble()))
            else -> throw IllegalArgumentException("Invalid operator: ${powerExpression.operator.type}")
        }
    }

    private fun evaluateMultiplicativeExpression(multiplicativeExpression: MultiplicativeExpression): JsValue {
        val leftValue = multiplicativeExpression.leftExpression.evaluate()
        val rightValue = multiplicativeExpression.rightExpression.evaluate()
        return when (multiplicativeExpression.operator.type) {
            TokenType.OPERATOR_MULTIPLY -> JsValue(ValueType.NUMBER, leftValue.toDouble() * rightValue.toDouble())
            TokenType.OPERATOR_DIVIDE -> JsValue(ValueType.NUMBER, leftValue.toDouble() / rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${multiplicativeExpression.operator.type}")
        }
    }

    private fun evaluateAdditiveExpression(additiveExpression: AdditiveExpression): JsValue {
        val leftValue = additiveExpression.leftExpression.evaluate()
        val rightValue = additiveExpression.rightExpression.evaluate()
        return when (additiveExpression.operator.type) {
            TokenType.OPERATOR_PLUS -> JsValue(ValueType.NUMBER, leftValue.toDouble() + rightValue.toDouble())
            TokenType.OPERATOR_MINUS -> JsValue(ValueType.NUMBER, leftValue.toDouble() - rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${additiveExpression.operator.type}")
        }
    }

    private fun evaluateCoalesceExpression(coalesceExpression: CoalesceExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitShiftExpression(bitShiftExpression: BitShiftExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateRelationalExpression(relationalExpression: RelationalExpression): JsValue {
        val leftValue = relationalExpression.leftExpression.evaluate()
        val rightValue = relationalExpression.rightExpression.evaluate()
        return when (relationalExpression.operator.type) {
            TokenType.OPERATOR_LESS_THAN ->
                JsValue(ValueType.BOOLEAN, leftValue.toDouble() < rightValue.toDouble())
            TokenType.OPERATOR_MORE_THAN ->
                JsValue(ValueType.BOOLEAN, leftValue.toDouble() > rightValue.toDouble())
            TokenType.OPERATOR_LESS_THAN_EQUALS ->
                JsValue(ValueType.BOOLEAN, leftValue.toDouble() >= rightValue.toDouble())
            TokenType.OPERATOR_MORE_THAN_EQUALS ->
                JsValue(ValueType.BOOLEAN, leftValue.toDouble() <= rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${relationalExpression.operator.type}")
        }
    }

    private fun evaluateInstanceOfExpression(instanceOfExpression: InstanceOfExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateInExpression(inExpression: InExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEqualityExpression(equalityExpression: EqualityExpression): JsValue {
        val leftValue = equalityExpression.leftExpression.evaluate()
        val rightValue = equalityExpression.rightExpression.evaluate()
        return when (equalityExpression.operator.type) {
            TokenType.OPERATOR_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue == rightValue)
            TokenType.OPERATOR_NOT_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue != rightValue)
            // TODO: 处理对象想等
            TokenType.OPERATOR_IDENTITY_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue === rightValue)
            TokenType.OPERATOR_IDENTITY_NOT_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue !== rightValue)
            else -> throw IllegalArgumentException("Invalid operator: ${equalityExpression.operator.type}")
        }
    }

    private fun evaluateBitAndExpression(bitAndExpression: BitAndExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitXorExpression(bitXorExpression: BitXorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitOrExpression(bitOrExpression: BitOrExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalAndExpression(logicalAndExpression: LogicalAndExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalOrExpression(logicalOrExpression: LogicalOrExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTernaryExpression(ternaryExpression: TernaryExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAssignmentExpression(assignmentExpression: AssignmentExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAssignmentOperatorExpression(assignmentOperatorExpression: AssignmentOperatorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportExpression(importExpression: ImportExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTemplateStringExpression(templateStringExpression: TemplateStringExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldExpression(yieldExpression: YieldExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThisExpression(thisExpression: ThisExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateSuperExpression(superExpression: SuperExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLiteralExpression(literalExpression: LiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArrayLiteralExpression(arrayLiteralExpression: ArrayLiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateObjectLiteralExpression(objectLiteralExpression: ObjectLiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateParenthesizedExpression(parenthesizedExpression: ParenthesizedExpression): JsValue {
        return evaluateExpressionSequence(parenthesizedExpression.expressionSequence)
    }

    private fun evaluateBlock(statement: Block): JsValue {
        return evaluateStatementList(statement.statementList)
    }

    private fun evaluateWhileStatement(statement: WhileStatement) {
        TODO("Not yet implemented")
    }

    private fun Boolean.toDouble() : Double {
        return if (this) 1.0 else 0.0
    }
}