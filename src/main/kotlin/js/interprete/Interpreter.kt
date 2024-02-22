package js.interprete

import js.ast.*
import kotlin.math.pow


class Interpreter {
    // 全局的英文
    fun evaluate(context: ExecutionContext, node: Node): JsValue {
        return when (node) {
            is Program -> return evaluateProgram(context, node)
            else -> JsUndefined
        }
    }

    private fun evaluateProgram(context: ExecutionContext, program: Program): JsValue {
        return evaluateStatementList(context, program.statementList)
    }

    private fun evaluateStatementList(context: ExecutionContext, statementList: StatementList): JsValue {
        var value: JsValue = JsUndefined
        val statementGroup = statementList.statements.groupBy {
            when (it) {
                is FunctionDeclaration -> "function"
                else -> "other"
            }
        }

        statementGroup["function"]?.forEach {
            evaluateFunctionDeclaration(context, it as FunctionDeclaration)
        }

        for (statement in statementGroup["other"]?: emptyList()) {
            value = evaluateStatement(context, statement)
            if (statement is ReturnStatement) {
                break
            }
        }
        return value
    }

    private fun evaluateStatement(context: ExecutionContext, statement: Statement): JsValue {
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

    private fun evaluateDebuggerStatement(context: ExecutionContext, statement: DebuggerStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTryStatement(context: ExecutionContext, statement: TryStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThrowStatement(context: ExecutionContext, statement: ThrowStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateSwitchStatement(context: ExecutionContext, statement: SwitchStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLabelledStatement(context: ExecutionContext, statement: LabelledStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateWithStatement(context: ExecutionContext, statement: WithStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldStatement(context: ExecutionContext, statement: YieldStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateReturnStatement(context: ExecutionContext, statement: ReturnStatement): JsValue {
        if (statement.expression != null) {
            return statement.expression.evaluate(context)
        }
        return JsUndefined
    }

    private fun evaluateBreakStatement(context: ExecutionContext, statement: BreakStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateContinueStatement(context: ExecutionContext, statement: ContinueStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIterationStatement(context: ExecutionContext, statement: IterationStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateIfStatement(context: ExecutionContext, statement: IfStatement): JsValue {
        val value = evaluateExpressionStatement(context, statement.condition)
        if  (value.asBoolean()) {
            return evaluateStatement(context, statement.trueStatement)
        } else if (statement.falseStatement != null) {
            return evaluateStatement(context, statement.falseStatement)
        }
        return JsUndefined
    }

    private fun evaluateFunctionDeclaration(context: ExecutionContext, functionDeclaration: FunctionDeclaration): JsValue {
        val value = JsFunctionCustom(functionDeclaration.parameters, functionDeclaration.functionBody)
        context.setVariable(functionDeclaration.functionName.value, value)
        return value
    }

    private fun evaluateExpressionStatement(context: ExecutionContext, statement: ExpressionStatement): JsValue {
        return evaluateExpressionSequence(context, statement.expressionSequence)
    }

    private fun evaluateExpressionSequence(context: ExecutionContext, expressionSequence: ExpressionSequence): JsValue {
        var value: JsValue = JsUndefined
        expressionSequence.expressions.forEach { value = it.evaluate(context) }
        return value
    }

    private fun evaluateClassDeclaration(context: ExecutionContext, statement: ClassDeclaration): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEmptyStatement(context: ExecutionContext, statement: EmptyStatement): JsValue {
        return JsUndefined
    }

    private fun evaluateExportStatement(context: ExecutionContext, statement: ExportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportStatement(context: ExecutionContext, statement: ImportStatement): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVariableStatement(context: ExecutionContext, statement: VariableStatement): JsValue {
        statement.variableDeclarationList.variableDeclarations.forEach {
            context.setVariable(it.variableName.value, it.initializer?.evaluate(context) ?: JsUndefined)
        }
        return JsUndefined
    }

    private fun SingleExpression.evaluate(context: ExecutionContext): JsValue {
        when (this) {
            is ExpressionSequence -> return evaluateExpressionSequence(context, this)
            is AnonymousFunctionExpression -> return evaluateAnonymousFunctionExpression(context, this)
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
            is IdentifierExpression -> return evaluateIdentifierExpression(context, this)
            is SuperExpression ->  return evaluateSuperExpression(context, this)
            is LiteralExpression ->  return evaluateLiteralExpression(context, this)
            is StringLiteralExpression -> return JsString(value.value)
            is TemplateStringLiteral -> return evaluateTemplateStringLiteral(context, this)
            is NumericLiteralExpression -> return JsNumber(value.value.toDouble())
            is BooleanLiteralExpression -> return JsBoolean(value.value == "true")
            is ArrayLiteralExpression -> return evaluateArrayLiteralExpression(context, this)
            is ObjectLiteralExpression -> return evaluateObjectLiteralExpression(context, this)
            is ParenthesizedExpression -> return evaluateParenthesizedExpression(context, this)
            else -> throw RuntimeException("Unsupported expression: $this")
        }
    }

    private fun evaluateTemplateStringLiteral(
        context: ExecutionContext,
        templateStringLiteral: TemplateStringLiteral
    ): JsValue {
        val sb = StringBuilder()
        var index = 0
        templateStringLiteral.expressionParts.forEach {
            sb.appendRange(templateStringLiteral.templateString, index, it.first.first)
            index = it.first.last + 1
            sb.append(it.second.evaluate(context).asString())
        }
        if (index < templateStringLiteral.templateString.length) {
            sb.append(templateStringLiteral.templateString.substring(index))
        }
        return JsString(sb.toString())
    }

    private fun evaluateIdentifierExpression(context: ExecutionContext, identifierExpression: IdentifierExpression): JsValue {
        return context.getVariable(identifierExpression.name.value)
    }

    private fun evaluateAnonymousFunctionExpression(
        context: ExecutionContext,
        anonymousFunctionExpression: AnonymousFunctionExpression
    ): JsValue {
        return JsFunctionCustom(anonymousFunctionExpression.parameters,
            anonymousFunctionExpression.functionBody)
    }

    private fun evaluateOptionalChainExpression(context: ExecutionContext, optionalChainExpression: OptionalChainExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberIndexExpression(context: ExecutionContext, memberIndexExpression: MemberIndexExpression): JsValue {
        return getVariableReference(context, memberIndexExpression).getValue()
    }

    private fun evaluateMemberDotExpression(context: ExecutionContext, memberDotExpression: MemberDotExpression): JsValue {
        return getVariableReference(context, memberDotExpression).getValue()
    }

    private fun evaluateNewExpression(context: ExecutionContext, newExpression: NewExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArgumentsExpression(context: ExecutionContext, argumentsExpression: ArgumentsExpression): JsValue {
        val argumentList = argumentsExpression.argumentList.arguments.map { it.expression.evaluate(context) }
        val value = argumentsExpression.expression.evaluate(context)
        if (value !is JsFunction) {
            throw RuntimeException("Cannot call non-function: $value")
        }
        // 执行函数调用
        return value.call(context,this, argumentList)
    }

    private fun evaluatePostIncrementExpression(context: ExecutionContext, postIncrementExpression: PostIncrementExpression): JsValue {
        val variableReference = getVariableReference(context, postIncrementExpression.expression)
        val value = variableReference.getValue()
        variableReference.setValue(JsNumber(value.asNumber() + 1))
        return value
    }

    private fun evaluatePostDecreaseExpression(context: ExecutionContext, postDecreaseExpression: PostDecreaseExpression): JsValue {
        val variableReference = getVariableReference(context, postDecreaseExpression.expression)
        val value = variableReference.getValue()
        variableReference.setValue(JsNumber(value.asNumber() - 1))
        return value
    }

    private fun evaluatePreIncrementExpression(context: ExecutionContext, preIncrementExpression: PreIncrementExpression): JsValue {
        val variableReference = getVariableReference(context, preIncrementExpression.expression)
        val value = variableReference.getValue()
        val incrementValue = JsNumber(value.asNumber() + 1)
        variableReference.setValue(incrementValue)
        return incrementValue
    }

    private fun evaluatePreDecreaseExpression(context: ExecutionContext, preDecreaseExpression: PreDecreaseExpression): JsValue {
        val variableReference = getVariableReference(context, preDecreaseExpression.expression)
        val value = variableReference.getValue()
        val decreaseValue = JsNumber(value.asNumber() - 1)
        variableReference.setValue(decreaseValue)
        return decreaseValue
    }

    private fun evaluateDeleteExpression(context: ExecutionContext, deleteExpression: DeleteExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateVoidExpression(context: ExecutionContext, voidExpression: VoidExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTypeofExpression(context: ExecutionContext, typeofExpression: TypeofExpression): JsValue {
        TODO("Not yet implemented")
    }


    private fun evaluateUnaryPlusExpression(context: ExecutionContext, unaryPlusExpression: UnaryPlusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryMinusExpression(context: ExecutionContext, unaryMinusExpression: UnaryMinusExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitNotExpression(context: ExecutionContext, bitNotExpression: BitNotExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateNotExpression(context: ExecutionContext, notExpression: NotExpression): JsValue {
        val value = notExpression.expression.evaluate(context)
        return JsBoolean(!value.asBoolean())
    }

    private fun evaluateAwaitExpression(context: ExecutionContext, awaitExpression: AwaitExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluatePowerExpression(context: ExecutionContext, powerExpression: PowerExpression): JsValue {
        val leftValue = powerExpression.leftExpression.evaluate(context)
        val rightValue = powerExpression.rightExpression.evaluate(context)
        return when (powerExpression.operator.type) {
            TokenType.OPERATOR_POWER -> JsNumber(leftValue.asNumber().pow(rightValue.asNumber()))
            else -> throw IllegalArgumentException("Invalid operator: ${powerExpression.operator.type}")
        }
    }

    private fun evaluateMultiplicativeExpression(context: ExecutionContext, multiplicativeExpression: MultiplicativeExpression): JsValue {
        val leftValue = multiplicativeExpression.leftExpression.evaluate(context)
        val rightValue = multiplicativeExpression.rightExpression.evaluate(context)
        return when (multiplicativeExpression.operator.type) {
            TokenType.OPERATOR_MULTIPLY -> JsNumber(leftValue.asNumber() * rightValue.asNumber())
            TokenType.OPERATOR_DIVIDE -> JsNumber(leftValue.asNumber() / rightValue.asNumber())
            else -> throw IllegalArgumentException("Invalid operator: ${multiplicativeExpression.operator.type}")
        }
    }

    private fun evaluateAdditiveExpression(context: ExecutionContext, additiveExpression: AdditiveExpression): JsValue {
        val leftValue = additiveExpression.leftExpression.evaluate(context)
        val rightValue = additiveExpression.rightExpression.evaluate(context)
        return when (additiveExpression.operator.type) {
            TokenType.OPERATOR_PLUS -> {
                if (leftValue is JsString || rightValue is JsString) {
                    JsString(leftValue.asString() + rightValue.asString())
                } else {
                    JsNumber(leftValue.asNumber() + rightValue.asNumber())
                }
            }
            TokenType.OPERATOR_MINUS -> JsNumber(leftValue.asNumber() - rightValue.asNumber())
            else -> throw IllegalArgumentException("Invalid operator: ${additiveExpression.operator.type}")
        }
    }

    private fun evaluateCoalesceExpression(context: ExecutionContext, coalesceExpression: CoalesceExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitShiftExpression(context: ExecutionContext, bitShiftExpression: BitShiftExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateRelationalExpression(context: ExecutionContext, relationalExpression: RelationalExpression): JsValue {
        val leftValue = relationalExpression.leftExpression.evaluate(context)
        val rightValue = relationalExpression.rightExpression.evaluate(context)
        return when (relationalExpression.operator.type) {
            TokenType.OPERATOR_LESS_THAN ->
                JsBoolean(leftValue.asNumber() < rightValue.asNumber())
            TokenType.OPERATOR_MORE_THAN ->
                JsBoolean(leftValue.asNumber() > rightValue.asNumber())
            TokenType.OPERATOR_LESS_THAN_EQUALS ->
                JsBoolean(leftValue.asNumber() >= rightValue.asNumber())
            TokenType.OPERATOR_MORE_THAN_EQUALS ->
                JsBoolean(leftValue.asNumber() <= rightValue.asNumber())
            else -> throw IllegalArgumentException("Invalid operator: ${relationalExpression.operator.type}")
        }
    }

    private fun evaluateInstanceOfExpression(context: ExecutionContext, instanceOfExpression: InstanceOfExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateInExpression(context: ExecutionContext, inExpression: InExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateEqualityExpression(context: ExecutionContext, equalityExpression: EqualityExpression): JsValue {
        val leftValue = equalityExpression.leftExpression.evaluate(context)
        val rightValue = equalityExpression.rightExpression.evaluate(context)
        return when (equalityExpression.operator.type) {
            TokenType.OPERATOR_EQUAL -> JsBoolean(leftValue == rightValue)
            TokenType.OPERATOR_NOT_EQUAL -> JsBoolean(leftValue != rightValue)
            // TODO: 处理对象相等
            TokenType.OPERATOR_IDENTITY_EQUAL -> JsBoolean(leftValue === rightValue)
            TokenType.OPERATOR_IDENTITY_NOT_EQUAL -> JsBoolean(leftValue !== rightValue)
            else -> throw IllegalArgumentException("Invalid operator: ${equalityExpression.operator.type}")
        }
    }

    private fun evaluateBitAndExpression(context: ExecutionContext, bitAndExpression: BitAndExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitXorExpression(context: ExecutionContext, bitXorExpression: BitXorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateBitOrExpression(context: ExecutionContext, bitOrExpression: BitOrExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalAndExpression(context: ExecutionContext, logicalAndExpression: LogicalAndExpression): JsValue {
        val left = logicalAndExpression.leftExpression.evaluate(context)
        if (!left.asBoolean()) {
            return left
        }
        return logicalAndExpression.rightExpression.evaluate(context)
    }

    private fun evaluateLogicalOrExpression(context: ExecutionContext, logicalOrExpression: LogicalOrExpression): JsValue {
        val left = logicalOrExpression.leftExpression.evaluate(context)
        if (left.asBoolean()) {
            return left
        }
        return logicalOrExpression.rightExpression.evaluate(context)
    }

    private fun evaluateTernaryExpression(context: ExecutionContext, ternaryExpression: TernaryExpression): JsValue {
        val condition = ternaryExpression.conditionExpression.evaluate(context)
        return if (condition.asBoolean()) {
            ternaryExpression.leftExpression.evaluate(context)
        } else {
            ternaryExpression.rightExpression.evaluate(context)
        }
    }

    private fun getVariableReference(context: ExecutionContext, expression: SingleExpression): VariableReference {
        when (expression) {
            is IdentifierExpression -> {
                return IdentifierVariableReference(context, expression.name.value)
            }
            is MemberDotExpression -> {
                val jsObject = expression.expression.evaluate(context)
                if (jsObject !is JsObject) {
                    throw RuntimeException("Unsupported left expression type: $expression")
                }
                return MemberVariableReference(context, jsObject, expression.identifier.value)
            }
            is MemberIndexExpression -> {
                val jsObject = expression.expression.evaluate(context)
                if (jsObject !is JsObject) {
                    throw RuntimeException("Unsupported left expression type: $expression")
                }
                val index = expression.indexExpression.evaluate(context)
                return MemberVariableReference(context, jsObject, index.asString())
            }
            else ->
                throw RuntimeException("Unsupported left expression type: $expression")
        }
    }

    private fun evaluateAssignmentExpression(context: ExecutionContext, assignmentExpression: AssignmentExpression): JsValue {
        val variableReference = getVariableReference(context, assignmentExpression.leftExpression)
        val value = assignmentExpression.rightExpression.evaluate(context)
        variableReference.setValue(value)
        return value
    }

    private fun evaluateAssignmentOperatorExpression(context: ExecutionContext, assignmentOperatorExpression: AssignmentOperatorExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateImportExpression(context: ExecutionContext, importExpression: ImportExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateTemplateStringExpression(context: ExecutionContext, templateStringExpression: TemplateStringExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldExpression(context: ExecutionContext, yieldExpression: YieldExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateThisExpression(context: ExecutionContext, thisExpression: ThisExpression): JsValue {
        try {
            return context.getVariable("this")
        } catch (e: RuntimeException) {
            // 如果找不到变量"this"，则创建一个新的JsObject对象并将其赋值给变量"this"
            val value = JsObject()
            context.setVariable("this", value)
            return value
        }
    }

    private fun evaluateSuperExpression(context: ExecutionContext, superExpression: SuperExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateLiteralExpression(context: ExecutionContext, literalExpression: LiteralExpression): JsValue {
        TODO("Not yet implemented")
    }

    private fun evaluateArrayLiteralExpression(context: ExecutionContext, arrayLiteralExpression: ArrayLiteralExpression): JsValue {
        val jsArray = JsArray()
        arrayLiteralExpression.elements.forEachIndexed { index, element ->
            jsArray.setProperty(index.toString(), element.expression.evaluate(context))
        }
        return jsArray
    }

    private fun evaluateObjectLiteralExpression(context: ExecutionContext, objectLiteralExpression: ObjectLiteralExpression): JsValue {
        val jsObject = JsObject()
        for (property in objectLiteralExpression.propertyAssignments) {

            when (property) {
                is PropertyExpressionAssignment -> {
                    val name = when (property.propertyName) {
                        is IdentifierPropertyName -> {
                            property.propertyName.name.value
                        }
                        is StringLiteralPropertyName -> {
                            property.propertyName.name.value
                        }
                        is NumericLiteralPropertyName -> {
                            property.propertyName.name.value
                        }
                        is ComputedPropertyName -> {
                            property.propertyName.expression.evaluate(context).asString()
                        }
                        else ->
                            throw RuntimeException("Unsupported property name type ${property.propertyName}")
                    }
                    jsObject.setProperty(name, property.propertyValue.evaluate(context))
                }
            }
        }
        return jsObject
    }

    private fun evaluateParenthesizedExpression(context: ExecutionContext, parenthesizedExpression: ParenthesizedExpression): JsValue {
        return parenthesizedExpression.expressionSequence.evaluate(context)
    }

    private fun evaluateBlock(context: ExecutionContext, statement: Block): JsValue {
        return evaluateStatementList(context, statement.statementList)
    }

    private fun evaluateWhileStatement(context: ExecutionContext, statement: WhileStatement) {
        TODO("Not yet implemented")
    }

    fun evaluateFunctionBody(context: ExecutionContext, functionBody: FunctionBody): JsValue {
        return evaluateStatementList(context, functionBody.statementList)
    }
}