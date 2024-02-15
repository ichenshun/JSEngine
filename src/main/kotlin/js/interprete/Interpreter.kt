package js.interprete

import js.ast.*
import kotlin.math.pow


class Interpreter {
    // 全局的英文
    fun evaluate(context: ExecutionContext, node: Node): Value {
        return when (node) {
            is Program -> return evaluateProgram(context, node)
            else -> Value.UNDEFINED
        }
    }

    private fun evaluateProgram(context: ExecutionContext, program: Program): Value {
        return evaluateStatementList(context, program.statementList)
    }

    private fun evaluateStatementList(context: ExecutionContext, statementList: StatementList): Value {
        var value = Value.UNDEFINED
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

    private fun evaluateStatement(context: ExecutionContext, statement: Statement): Value {
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

    private fun evaluateDebuggerStatement(context: ExecutionContext, statement: DebuggerStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateTryStatement(context: ExecutionContext, statement: TryStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateThrowStatement(context: ExecutionContext, statement: ThrowStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateSwitchStatement(context: ExecutionContext, statement: SwitchStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateLabelledStatement(context: ExecutionContext, statement: LabelledStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateWithStatement(context: ExecutionContext, statement: WithStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldStatement(context: ExecutionContext, statement: YieldStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateReturnStatement(context: ExecutionContext, statement: ReturnStatement): Value {
        if (statement.expression != null) {
            return statement.expression.evaluate(context)
        }
        return Value.UNDEFINED
    }

    private fun evaluateBreakStatement(context: ExecutionContext, statement: BreakStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateContinueStatement(context: ExecutionContext, statement: ContinueStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateIterationStatement(context: ExecutionContext, statement: IterationStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateIfStatement(context: ExecutionContext, statement: IfStatement): Value {
        val value = evaluateExpressionStatement(context, statement.condition)
        if  (value.toBoolean()) {
            return evaluateStatement(context, statement.trueStatement)
        } else if (statement.falseStatement != null) {
            return evaluateStatement(context, statement.falseStatement)
        }
        return Value.UNDEFINED
    }

    private fun evaluateFunctionDeclaration(context: ExecutionContext, functionDeclaration: FunctionDeclaration): Value {
        val value = Value(ValueType.FUNCTION, FunctionCustom(functionDeclaration))
        context.setVariable(functionDeclaration.functionName.value, value)
        return value
    }

    private fun evaluateExpressionStatement(context: ExecutionContext, statement: ExpressionStatement): Value {
        return evaluateExpressionSequence(context, statement.expressionSequence)
    }

    private fun evaluateExpressionSequence(context: ExecutionContext, expressionSequence: ExpressionSequence): Value {
        var value = Value.UNDEFINED
        expressionSequence.expressions.forEach { value = it.evaluate(context) }
        return value
    }

    private fun evaluateClassDeclaration(context: ExecutionContext, statement: ClassDeclaration): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateEmptyStatement(context: ExecutionContext, statement: EmptyStatement): Value {
        return Value.UNDEFINED
    }

    private fun evaluateExportStatement(context: ExecutionContext, statement: ExportStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateImportStatement(context: ExecutionContext, statement: ImportStatement): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateVariableStatement(context: ExecutionContext, statement: VariableStatement): Value {
        statement.variableDeclarationList.variableDeclarations.forEach {
            context.setVariable(it.variableName.value, it.initializer?.evaluate(context) ?: Value.UNDEFINED)
        }
        return Value.UNDEFINED
    }

    private fun SingleExpression.evaluate(context: ExecutionContext): Value {
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
            is StringLiteralExpression -> return Value(ValueType.STRING, value.value)
            is NumericLiteralExpression -> return Value(ValueType.NUMBER, value.value.toDouble())
            is BooleanLiteralExpression -> return Value(ValueType.BOOLEAN, value.value == "true")
            is ArrayLiteralExpression -> return evaluateArrayLiteralExpression(context, this)
            is ObjectLiteralExpression -> return evaluateObjectLiteralExpression(context, this)
            is ParenthesizedExpression -> return evaluateParenthesizedExpression(context, this)
            else -> throw RuntimeException("Unsupported expression: $this")
        }
    }

    private fun evaluateIdentifierExpression(context: ExecutionContext, identifierExpression: IdentifierExpression): Value {
        return context.getVariable(identifierExpression.name.value)
    }

    private fun evaluateFunctionExpression(context: ExecutionContext, functionExpression: FunctionExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateOptionalChainExpression(context: ExecutionContext, optionalChainExpression: OptionalChainExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberIndexExpression(context: ExecutionContext, memberIndexExpression: MemberIndexExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateMemberDotExpression(context: ExecutionContext, memberDotExpression: MemberDotExpression): Value {
        val value = memberDotExpression.expression.evaluate(context)
        if (value.valueType != ValueType.OBJECT) {
            throw RuntimeException("Member access on non-object: $value")
        }
        val jsObject = value.value as Object
        // 执行属性访问
        // 查找对象表，找到属性对应的值
        // 返回属性对应的值
        return jsObject.getProperty(memberDotExpression.identifier.value)
    }

    private fun evaluateNewExpression(context: ExecutionContext, newExpression: NewExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateArgumentsExpression(context: ExecutionContext, argumentsExpression: ArgumentsExpression): Value {
        val value = argumentsExpression.expression.evaluate(context)
        if (value.valueType != ValueType.FUNCTION) {
            throw RuntimeException("Cannot call non-function: $value")
        }
        // 执行函数调用
        val argumentList = argumentsExpression.argumentList.arguments.map { it.expression.evaluate(context) }
        val function = value.value as Function
        return function.call(context,this, argumentList)
    }

    private fun evaluatePostIncrementExpression(context: ExecutionContext, postIncrementExpression: PostIncrementExpression): Value {
        when (postIncrementExpression.expression) {
            is IdentifierExpression -> {
                val value = postIncrementExpression.expression.evaluate(context)
                context.setVariable(postIncrementExpression.expression.name.value, Value(ValueType.NUMBER, value.toDouble() + 1))
                return value
            }
            else ->
                throw RuntimeException("Cannot increment non-variable: ${postIncrementExpression.expression}")
        }

    }

    private fun evaluatePostDecreaseExpression(context: ExecutionContext, postDecreaseExpression: PostDecreaseExpression): Value {
        when (postDecreaseExpression.expression) {
            is IdentifierExpression -> {
                val value = postDecreaseExpression.expression.evaluate(context)
                context.setVariable(postDecreaseExpression.expression.name.value, Value(ValueType.NUMBER, value.toDouble() - 1))
                return value
            }
            else ->
                throw RuntimeException("Cannot increment non-variable: ${postDecreaseExpression.expression}")
        }
    }

    private fun evaluatePreIncrementExpression(context: ExecutionContext, preIncrementExpression: PreIncrementExpression): Value {
        when (preIncrementExpression.expression) {
            is IdentifierExpression -> {
                val value = preIncrementExpression.expression.evaluate(context)
                val incrementValue = Value(ValueType.NUMBER, value.toDouble() + 1)
                context.setVariable(preIncrementExpression.expression.name.value, incrementValue)
                return incrementValue
            }
            else ->
                throw RuntimeException("Cannot increment non-variable: ${preIncrementExpression.expression}")
        }
    }

    private fun evaluatePreDecreaseExpression(context: ExecutionContext, preDecreaseExpression: PreDecreaseExpression): Value {
        when (preDecreaseExpression.expression) {
            is IdentifierExpression -> {
                val value = preDecreaseExpression.expression.evaluate(context)
                val incrementValue = Value(ValueType.NUMBER, value.toDouble() - 1)
                context.setVariable(preDecreaseExpression.expression.name.value, incrementValue)
                return incrementValue
            }
            else ->
                throw RuntimeException("Cannot increment non-variable: ${preDecreaseExpression.expression}")
        }
    }

    private fun evaluateDeleteExpression(context: ExecutionContext, deleteExpression: DeleteExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateVoidExpression(context: ExecutionContext, voidExpression: VoidExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateTypeofExpression(context: ExecutionContext, typeofExpression: TypeofExpression): Value {
        TODO("Not yet implemented")
    }


    private fun evaluateUnaryPlusExpression(context: ExecutionContext, unaryPlusExpression: UnaryPlusExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateUnaryMinusExpression(context: ExecutionContext, unaryMinusExpression: UnaryMinusExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateBitNotExpression(context: ExecutionContext, bitNotExpression: BitNotExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateNotExpression(context: ExecutionContext, notExpression: NotExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateAwaitExpression(context: ExecutionContext, awaitExpression: AwaitExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluatePowerExpression(context: ExecutionContext, powerExpression: PowerExpression): Value {
        val leftValue = powerExpression.leftExpression.evaluate(context)
        val rightValue = powerExpression.rightExpression.evaluate(context)
        println("left=${leftValue.value}, right=${rightValue.value}")
        return when (powerExpression.operator.type) {
            TokenType.OPERATOR_POWER -> Value(ValueType.NUMBER, leftValue.toDouble().pow(rightValue.toDouble()))
            else -> throw IllegalArgumentException("Invalid operator: ${powerExpression.operator.type}")
        }
    }

    private fun evaluateMultiplicativeExpression(context: ExecutionContext, multiplicativeExpression: MultiplicativeExpression): Value {
        val leftValue = multiplicativeExpression.leftExpression.evaluate(context)
        val rightValue = multiplicativeExpression.rightExpression.evaluate(context)
        return when (multiplicativeExpression.operator.type) {
            TokenType.OPERATOR_MULTIPLY -> Value(ValueType.NUMBER, leftValue.toDouble() * rightValue.toDouble())
            TokenType.OPERATOR_DIVIDE -> Value(ValueType.NUMBER, leftValue.toDouble() / rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${multiplicativeExpression.operator.type}")
        }
    }

    private fun evaluateAdditiveExpression(context: ExecutionContext, additiveExpression: AdditiveExpression): Value {
        val leftValue = additiveExpression.leftExpression.evaluate(context)
        val rightValue = additiveExpression.rightExpression.evaluate(context)
        return when (additiveExpression.operator.type) {
            TokenType.OPERATOR_PLUS -> {
                if (leftValue.valueType == ValueType.STRING || rightValue.valueType == ValueType.STRING) {
                    Value(ValueType.STRING, leftValue.asString() + rightValue.asString())
                } else {
                    Value(ValueType.NUMBER, leftValue.toDouble() + rightValue.toDouble())
                }
            }
            TokenType.OPERATOR_MINUS -> Value(ValueType.NUMBER, leftValue.toDouble() - rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${additiveExpression.operator.type}")
        }
    }

    private fun evaluateCoalesceExpression(context: ExecutionContext, coalesceExpression: CoalesceExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateBitShiftExpression(context: ExecutionContext, bitShiftExpression: BitShiftExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateRelationalExpression(context: ExecutionContext, relationalExpression: RelationalExpression): Value {
        val leftValue = relationalExpression.leftExpression.evaluate(context)
        val rightValue = relationalExpression.rightExpression.evaluate(context)
        return when (relationalExpression.operator.type) {
            TokenType.OPERATOR_LESS_THAN ->
                Value(ValueType.BOOLEAN, leftValue.toDouble() < rightValue.toDouble())
            TokenType.OPERATOR_MORE_THAN ->
                Value(ValueType.BOOLEAN, leftValue.toDouble() > rightValue.toDouble())
            TokenType.OPERATOR_LESS_THAN_EQUALS ->
                Value(ValueType.BOOLEAN, leftValue.toDouble() >= rightValue.toDouble())
            TokenType.OPERATOR_MORE_THAN_EQUALS ->
                Value(ValueType.BOOLEAN, leftValue.toDouble() <= rightValue.toDouble())
            else -> throw IllegalArgumentException("Invalid operator: ${relationalExpression.operator.type}")
        }
    }

    private fun evaluateInstanceOfExpression(context: ExecutionContext, instanceOfExpression: InstanceOfExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateInExpression(context: ExecutionContext, inExpression: InExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateEqualityExpression(context: ExecutionContext, equalityExpression: EqualityExpression): Value {
        val leftValue = equalityExpression.leftExpression.evaluate(context)
        val rightValue = equalityExpression.rightExpression.evaluate(context)
        return when (equalityExpression.operator.type) {
            TokenType.OPERATOR_EQUAL -> Value(ValueType.BOOLEAN, leftValue == rightValue)
            TokenType.OPERATOR_NOT_EQUAL -> Value(ValueType.BOOLEAN, leftValue != rightValue)
            // TODO: 处理对象想等
            TokenType.OPERATOR_IDENTITY_EQUAL -> Value(ValueType.BOOLEAN, leftValue === rightValue)
            TokenType.OPERATOR_IDENTITY_NOT_EQUAL -> Value(ValueType.BOOLEAN, leftValue !== rightValue)
            else -> throw IllegalArgumentException("Invalid operator: ${equalityExpression.operator.type}")
        }
    }

    private fun evaluateBitAndExpression(context: ExecutionContext, bitAndExpression: BitAndExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateBitXorExpression(context: ExecutionContext, bitXorExpression: BitXorExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateBitOrExpression(context: ExecutionContext, bitOrExpression: BitOrExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalAndExpression(context: ExecutionContext, logicalAndExpression: LogicalAndExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateLogicalOrExpression(context: ExecutionContext, logicalOrExpression: LogicalOrExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateTernaryExpression(context: ExecutionContext, ternaryExpression: TernaryExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateAssignmentExpression(context: ExecutionContext, assignmentExpression: AssignmentExpression): Value {
        when (assignmentExpression.leftExpression) {
            is IdentifierExpression -> {
                val value = assignmentExpression.rightExpression.evaluate(context)
                context.setVariable(
                    assignmentExpression.leftExpression.name.value,
                    value
                )
                return value
            }
            else ->
                throw RuntimeException("Unsupported left expression type: ${assignmentExpression.leftExpression::class.simpleName}")
        }
    }

    private fun evaluateAssignmentOperatorExpression(context: ExecutionContext, assignmentOperatorExpression: AssignmentOperatorExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateImportExpression(context: ExecutionContext, importExpression: ImportExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateTemplateStringExpression(context: ExecutionContext, templateStringExpression: TemplateStringExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateYieldExpression(context: ExecutionContext, yieldExpression: YieldExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateThisExpression(context: ExecutionContext, thisExpression: ThisExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateSuperExpression(context: ExecutionContext, superExpression: SuperExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateLiteralExpression(context: ExecutionContext, literalExpression: LiteralExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateArrayLiteralExpression(context: ExecutionContext, arrayLiteralExpression: ArrayLiteralExpression): Value {
        TODO("Not yet implemented")
    }

    private fun evaluateObjectLiteralExpression(context: ExecutionContext, objectLiteralExpression: ObjectLiteralExpression): Value {
        val jsObject = Object()
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
        return Value(ValueType.OBJECT, jsObject)
    }

    private fun evaluateParenthesizedExpression(context: ExecutionContext, parenthesizedExpression: ParenthesizedExpression): Value {
        return parenthesizedExpression.expressionSequence.evaluate(context)
    }

    private fun evaluateBlock(context: ExecutionContext, statement: Block): Value {
        return evaluateStatementList(context, statement.statementList)
    }

    private fun evaluateWhileStatement(context: ExecutionContext, statement: WhileStatement) {
        TODO("Not yet implemented")
    }

    fun evaluateFunctionBody(context: ExecutionContext, functionBody: FunctionBody): Value {
        return evaluateStatementList(context, functionBody.statementList)
    }
}