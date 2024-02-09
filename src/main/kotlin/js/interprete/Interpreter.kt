package js.interprete

import js.ast.*
import kotlin.math.pow


class Interpreter {
    // 全局的英文
    fun evaluate(context: JsExecutionContext, node: Node): JsValue {
        return when (node) {
            is Program -> return evaluateProgram(context, node)
            else -> JsValue.UNDEFINED
        }
    }

    private fun evaluateProgram(context: JsExecutionContext, program: Program): JsValue {
        return evaluateStatementList(context, program.statementList)
    }

    private fun evaluateStatementList(context: JsExecutionContext, statement: StatementList): JsValue {
        var value = JsValue.UNDEFINED
        statement.statements.forEach { value = evaluateStatement(context, it) }
        return value
    }

    private fun evaluateStatement(context: JsExecutionContext, statement: Statement): JsValue {
        return when (statement) {
            is Block -> evaluateBlock(context, statement)
            is VariableStatement -> evaluateVariableStatement(context, statement)
            is ImportStatement -> evaluateImportStatement(context, statement)
            is ExportStatement -> evaluateExportStatement(context, statement)
            is EmptyStatement -> evaluateEmptyStatement(context, statement)
            is ClassDeclaration -> evaluateClassDeclaration(context, statement)
            is FunctionDeclaration -> evaluateFunctionDeclaration(context, statement)
            is ExpressionStatement -> evaluateExpressionStatement(context, statement)
            is IfStatement -> evaluateIfStatement(context, statement)
            is IterationStatement -> evaluateIterationStatement(context, statement)
            is ContinueStatement -> evaluateContinueStatement(context, statement)
            is BreakStatement -> evaluateBreakStatement(context, statement)
            is ReturnStatement -> evaluateReturnStatement(context, statement)
            is YieldStatement -> evaluateYieldStatement(context, statement)
            is WithStatement -> evaluateWithStatement(context, statement)
            is LabelledStatement -> evaluateLabelledStatement(context, statement)
            is SwitchStatement -> evaluateSwitchStatement(context, statement)
            is ThrowStatement -> evaluateThrowStatement(context, statement)
            is TryStatement -> evaluateTryStatement(context, statement)
            is DebuggerStatement -> evaluateDebuggerStatement(context, statement)
            else -> throw IllegalArgumentException("Unsupported statement type: ${statement::class}")
        }
    }

    private fun evaluateDebuggerStatement(context: JsExecutionContext, statement: DebuggerStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTryStatement(context: JsExecutionContext, statement: TryStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThrowStatement(context: JsExecutionContext, statement: ThrowStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateSwitchStatement(context: JsExecutionContext, statement: SwitchStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLabelledStatement(context: JsExecutionContext, statement: LabelledStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateWithStatement(context: JsExecutionContext, statement: WithStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldStatement(context: JsExecutionContext, statement: YieldStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateReturnStatement(context: JsExecutionContext, statement: ReturnStatement): JsValue {
        if (statement.expression != null) {
            return statement.expression.evaluate(context)
        }
        return JsValue.UNDEFINED
    }

    private fun evaluateBreakStatement(context: JsExecutionContext, statement: BreakStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateContinueStatement(context: JsExecutionContext, statement: ContinueStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIterationStatement(context: JsExecutionContext, statement: IterationStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIfStatement(context: JsExecutionContext, statement: IfStatement): JsValue {
        val value = evaluateExpressionStatement(context, statement.condition)
        if  (value.toBoolean()) {
            return evaluateStatement(context, statement.trueStatement)
        } else if (statement.falseStatement != null) {
            return evaluateStatement(context, statement.falseStatement)
        }
        return JsValue.UNDEFINED
    }

    private fun evaluateFunctionDeclaration(context: JsExecutionContext, functionDeclaration: FunctionDeclaration): JsValue {
        println(functionDeclaration)
        val value = JsValue(ValueType.FUNCTION, JsFunctionCustom(functionDeclaration))
        context.variables[functionDeclaration.functionName.value] = value
        return value
    }

    private fun evaluateExpressionStatement(context: JsExecutionContext, statement: ExpressionStatement): JsValue {
        return evaluateExpressionSequence(context, statement.expressionSequence)
    }

    private fun evaluateExpressionSequence(context: JsExecutionContext, expressionSequence: ExpressionSequence): JsValue {
        var value = JsValue.UNDEFINED
        expressionSequence.expressions.forEach { value = it.evaluate(context) }
        return value
    }

    private fun evaluateClassDeclaration(context: JsExecutionContext, statement: ClassDeclaration): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEmptyStatement(context: JsExecutionContext, statement: EmptyStatement): JsValue {
        return JsValue.UNDEFINED
    }

    private fun evaluateExportStatement(context: JsExecutionContext, statement: ExportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportStatement(context: JsExecutionContext, statement: ImportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVariableStatement(context: JsExecutionContext, statement: VariableStatement): JsValue {
        statement.variableDeclarationList.variableDeclarations.forEach {
            // TODO 同名的变量覆盖问题
            context.variables[it.variableName.value] = it.initializer?.evaluate(context) ?: JsValue.UNDEFINED
        }
        return JsValue.UNDEFINED
    }

    private fun SingleExpression.evaluate(context: JsExecutionContext): JsValue {
        when (this) {
            is ExpressionSequence -> return evaluateExpressionSequence(context, this)
            is FunctionExpression -> return evaluateFunctionExpression(context, this)
            is OptionalChainExpression -> return evaluateOptionalChainExpression(context, this)
            is MemberIndexExpression -> return evaluateMemberIndexExpression(context, this)
            is MemberDotExpression -> return evaluateMemberDotExpression(context, this)
            is NewExpression -> return evaluateNewExpression(context, this)
            is ArgumentsExpression -> return evaluateArgumentsExpression(context, this)
            is PostIncrementExpression -> return evaluatePostIncrementExpression(context, this)
            is PostDecreaseExpression -> return evaluatePostDecreaseExpression(context, this)
            is DeleteExpression -> return evaluateDeleteExpression(context, this)
            is VoidExpression -> return evaluateVoidExpression(context, this)
            is TypeofExpression -> return evaluateTypeofExpression(context, this)
            is PreIncrementExpression -> return evaluatePreIncrementExpression(context, this)
            is PreDecreaseExpression -> return evaluatePreDecreaseExpression(context, this)
            is UnaryPlusExpression -> return evaluateUnaryPlusExpression(context, this)
            is UnaryMinusExpression -> return evaluateUnaryMinusExpression(context, this)
            is BitNotExpression -> return evaluateBitNotExpression(context, this)
            is NotExpression -> return evaluateNotExpression(context, this)
            is AwaitExpression -> return evaluateAwaitExpression(context, this)
            is PowerExpression -> return evaluatePowerExpression(context, this)
            is MultiplicativeExpression -> return evaluateMultiplicativeExpression(context, this)
            is AdditiveExpression -> return evaluateAdditiveExpression(context, this)
            is CoalesceExpression -> return evaluateCoalesceExpression(context, this)
            is BitShiftExpression -> return evaluateBitShiftExpression(context, this)
            is RelationalExpression -> return evaluateRelationalExpression(context, this)
            is InstanceOfExpression -> return evaluateInstanceOfExpression(context, this)
            is InExpression -> return evaluateInExpression(context, this)
            is EqualityExpression -> return evaluateEqualityExpression(context, this)
            is BitAndExpression -> return evaluateBitAndExpression(context, this)
            is BitXorExpression -> return evaluateBitXorExpression(context, this)
            is BitOrExpression -> return evaluateBitOrExpression(context, this)
            is LogicalAndExpression -> return evaluateLogicalAndExpression(context, this)
            is LogicalOrExpression -> return evaluateLogicalOrExpression(context, this)
            is TernaryExpression -> return evaluateTernaryExpression(context, this)
            is AssignmentExpression -> return evaluateAssignmentExpression(context, this)
            is AssignmentOperatorExpression -> return evaluateAssignmentOperatorExpression(context, this)
            is ImportExpression -> return evaluateImportExpression(context, this)
            is TemplateStringExpression -> return evaluateTemplateStringExpression(context, this)
            is YieldExpression -> return evaluateYieldExpression(context, this)
            is ThisExpression -> return evaluateThisExpression(context, this)
            // TODO 同名的变量覆盖问题
            is IdentifierExpression -> return evaluateIdentifierExpression(context, this)
            is SuperExpression ->  return evaluateSuperExpression(context, this)
            is LiteralExpression ->  return evaluateLiteralExpression(context, this)
            is StringLiteralExpression -> return JsValue(ValueType.STRING, value.value)
            is NumericLiteralExpression -> return JsValue(ValueType.NUMBER, value.value.toDouble())
            is BooleanLiteralExpression -> return JsValue(ValueType.BOOLEAN, value)
            is ArrayLiteralExpression -> return evaluateArrayLiteralExpression(context, this)
            is ObjectLiteralExpression -> return evaluateObjectLiteralExpression(context, this)
            is ParenthesizedExpression -> return evaluateParenthesizedExpression(context, this)
            else -> throw RuntimeException("Unsupported expression: $this")
        }
    }

    private fun evaluateIdentifierExpression(context: JsExecutionContext, identifierExpression: IdentifierExpression): JsValue {
        return context.variables[identifierExpression.name.value] ?: JsValue.UNDEFINED
    }

    private fun evaluateFunctionExpression(context: JsExecutionContext, functionExpression: FunctionExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateOptionalChainExpression(context: JsExecutionContext, optionalChainExpression: OptionalChainExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberIndexExpression(context: JsExecutionContext, memberIndexExpression: MemberIndexExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberDotExpression(context: JsExecutionContext, memberDotExpression: MemberDotExpression): JsValue {
        val value = memberDotExpression.expression.evaluate(context)
        if (value.valueType != ValueType.OBJECT) {
            throw RuntimeException("Member access on non-object: $value")
        }
        val jsObject = value.value as JsObject
        // 执行属性访问
        // 查找对象表，找到属性对应的值
        // 返回属性对应的值
        return jsObject.get(memberDotExpression.identifier.value)
    }

    private fun evaluateNewExpression(context: JsExecutionContext, newExpression: NewExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArgumentsExpression(context: JsExecutionContext, argumentsExpression: ArgumentsExpression): JsValue {
        val value = argumentsExpression.expression.evaluate(context)
        if (value.valueType != ValueType.FUNCTION) {
            throw RuntimeException("Cannot call non-function: $value")
        }
        // 执行函数调用
        val argumentList = argumentsExpression.argumentList.arguments.map { it.expression.evaluate(context) }
        val jsFunction = value.value as JsFunction
        return jsFunction.call(this, argumentList)
    }

    private fun evaluatePostIncrementExpression(context: JsExecutionContext, postIncrementExpression: PostIncrementExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePostDecreaseExpression(context: JsExecutionContext, postDecreaseExpression: PostDecreaseExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateDeleteExpression(context: JsExecutionContext, deleteExpression: DeleteExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVoidExpression(context: JsExecutionContext, voidExpression: VoidExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTypeofExpression(context: JsExecutionContext, typeofExpression: TypeofExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePreIncrementExpression(context: JsExecutionContext, preIncrementExpression: PreIncrementExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePreDecreaseExpression(context: JsExecutionContext, preDecreaseExpression: PreDecreaseExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryPlusExpression(context: JsExecutionContext, unaryPlusExpression: UnaryPlusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryMinusExpression(context: JsExecutionContext, unaryMinusExpression: UnaryMinusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitNotExpression(context: JsExecutionContext, bitNotExpression: BitNotExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateNotExpression(context: JsExecutionContext, notExpression: NotExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAwaitExpression(context: JsExecutionContext, awaitExpression: AwaitExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePowerExpression(context: JsExecutionContext, powerExpression: PowerExpression): JsValue {
        val leftValue = powerExpression.leftExpression.evaluate(context)
        val rightValue = powerExpression.rightExpression.evaluate(context)
        println("left=${leftValue.value}, right=${rightValue.value}")
        return when (powerExpression.operator.type) {
            TokenType.OPERATOR_POWER -> JsValue(ValueType.NUMBER, leftValue.toDouble().pow(rightValue.toDouble()))
            else -> throw IllegalArgumentException("Invalid operator: ${powerExpression.operator.type}")
        }
    }

    private fun evaluateMultiplicativeExpression(context: JsExecutionContext, multiplicativeExpression: MultiplicativeExpression): JsValue {
        val leftValue = multiplicativeExpression.leftExpression.evaluate(context)
        val rightValue = multiplicativeExpression.rightExpression.evaluate(context)
        return when (multiplicativeExpression.operator.type) {
            TokenType.OPERATOR_MULTIPLY -> JsValue(ValueType.NUMBER, leftValue.toDouble() * rightValue.toDouble())
            TokenType.OPERATOR_DIVIDE -> JsValue(ValueType.NUMBER, leftValue.toDouble() / rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${multiplicativeExpression.operator.type}")
        }
    }

    private fun evaluateAdditiveExpression(context: JsExecutionContext, additiveExpression: AdditiveExpression): JsValue {
        val leftValue = additiveExpression.leftExpression.evaluate(context)
        val rightValue = additiveExpression.rightExpression.evaluate(context)
        return when (additiveExpression.operator.type) {
            TokenType.OPERATOR_PLUS -> JsValue(ValueType.NUMBER, leftValue.toDouble() + rightValue.toDouble())
            TokenType.OPERATOR_MINUS -> JsValue(ValueType.NUMBER, leftValue.toDouble() - rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${additiveExpression.operator.type}")
        }
    }

    private fun evaluateCoalesceExpression(context: JsExecutionContext, coalesceExpression: CoalesceExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitShiftExpression(context: JsExecutionContext, bitShiftExpression: BitShiftExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateRelationalExpression(context: JsExecutionContext, relationalExpression: RelationalExpression): JsValue {
        val leftValue = relationalExpression.leftExpression.evaluate(context)
        val rightValue = relationalExpression.rightExpression.evaluate(context)
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

    private fun evaluateInstanceOfExpression(context: JsExecutionContext, instanceOfExpression: InstanceOfExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateInExpression(context: JsExecutionContext, inExpression: InExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEqualityExpression(context: JsExecutionContext, equalityExpression: EqualityExpression): JsValue {
        val leftValue = equalityExpression.leftExpression.evaluate(context)
        val rightValue = equalityExpression.rightExpression.evaluate(context)
        return when (equalityExpression.operator.type) {
            TokenType.OPERATOR_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue == rightValue)
            TokenType.OPERATOR_NOT_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue != rightValue)
            // TODO: 处理对象想等
            TokenType.OPERATOR_IDENTITY_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue === rightValue)
            TokenType.OPERATOR_IDENTITY_NOT_EQUAL -> JsValue(ValueType.BOOLEAN, leftValue !== rightValue)
            else -> throw IllegalArgumentException("Invalid operator: ${equalityExpression.operator.type}")
        }
    }

    private fun evaluateBitAndExpression(context: JsExecutionContext, bitAndExpression: BitAndExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitXorExpression(context: JsExecutionContext, bitXorExpression: BitXorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitOrExpression(context: JsExecutionContext, bitOrExpression: BitOrExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalAndExpression(context: JsExecutionContext, logicalAndExpression: LogicalAndExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalOrExpression(context: JsExecutionContext, logicalOrExpression: LogicalOrExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTernaryExpression(context: JsExecutionContext, ternaryExpression: TernaryExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAssignmentExpression(context: JsExecutionContext, assignmentExpression: AssignmentExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateAssignmentOperatorExpression(context: JsExecutionContext, assignmentOperatorExpression: AssignmentOperatorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportExpression(context: JsExecutionContext, importExpression: ImportExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTemplateStringExpression(context: JsExecutionContext, templateStringExpression: TemplateStringExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldExpression(context: JsExecutionContext, yieldExpression: YieldExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThisExpression(context: JsExecutionContext, thisExpression: ThisExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateSuperExpression(context: JsExecutionContext, superExpression: SuperExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLiteralExpression(context: JsExecutionContext, literalExpression: LiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArrayLiteralExpression(context: JsExecutionContext, arrayLiteralExpression: ArrayLiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateObjectLiteralExpression(context: JsExecutionContext, objectLiteralExpression: ObjectLiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateParenthesizedExpression(context: JsExecutionContext, parenthesizedExpression: ParenthesizedExpression): JsValue {
        return parenthesizedExpression.expressionSequence.evaluate(context)
    }

    private fun evaluateBlock(context: JsExecutionContext, statement: Block): JsValue {
        return evaluateStatementList(context, statement.statementList)
    }

    private fun evaluateWhileStatement(context: JsExecutionContext, statement: WhileStatement) {
        TODO("Not yet implemented")
    }

    fun evaluateFunctionBody(context: JsExecutionContext, functionBody: FunctionBody): JsValue {
        return evaluateStatementList(context, functionBody.statementList)
    }
}