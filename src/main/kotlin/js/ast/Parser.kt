package js.ast

/**
 *  解析器
 *
 *  参考antlr4的JavaScript语法定义
 *  https://github.com/antlr/grammars-v4/blob/master/javascript/javascript/JavaScriptLexer.g4
 *  https://github.com/antlr/grammars-v4/blob/master/javascript/javascript/JavaScriptParser.g4
 */
class Parser(private val lexer: Lexer) {
    fun parse(): Node {
        return parseProgram()
    }

    private fun parseProgram(): Program {
        val statements = mutableListOf<Statement>()
        while (lexer.currentToken.type != TokenType.EOF) {
            statements.add(parseStatement())
        }
        return Program(statements)
    }

    private fun parseStatement(): Statement {
        return when (lexer.currentToken.type) {
            TokenType.OPEN_BRACE -> parseBlock()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.KEYWORD_IMPORT -> parseImportStatement()
            TokenType.KEYWORD_EXPORT -> parseExportStatement()
            TokenType.SEMICOLON -> parseEmptyStatement()
            TokenType.KEYWORD_FUNCTION -> parseFunctionDeclaration()
            TokenType.KEYWORD_IF -> parseIfStatement()
            TokenType.KEYWORD_DO -> parseDoStatement()
            TokenType.KEYWORD_WHILE -> parseWhileStatement()
            TokenType.KEYWORD_FOR -> parseForIterationStatement()
            TokenType.KEYWORD_CONTINUE -> parseContinueStatement()
            TokenType.KEYWORD_BREAK -> parseBreakStatement()
            TokenType.KEYWORD_RETURN -> parseReturnStatement()
            TokenType.KEYWORD_YIELD -> parseYieldStatement()
            TokenType.KEYWORD_WITH -> parseWithStatement()
            TokenType.KEYWORD_SWITCH -> parseSwitchStatement()
            TokenType.KEYWORD_LABELLED -> parseLabelledStatement() // 需要重新识别
            TokenType.KEYWORD_THROW -> parseThrowStatement()
            TokenType.KEYWORD_TRY -> parseTryStatement()
            TokenType.KEYWORD_DEBUGGER -> parseDebuggerStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseBlock(): Block {
        requireToken(TokenType.OPEN_BRACE)
        val statements = parseStatementList()
        requireToken(TokenType.CLOSE_BRACE)
        return Block(statements)
    }

    private fun parseVariableStatement(): VariableStatement {
        return VariableStatement(parseVariableDeclarationList())
    }

    private fun parseVariableDeclarationList(): VariableDeclarationList {
        val varModifier = parseVariableModifier()
        val variableDeclarations  = mutableListOf<VariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (lexer.currentToken.type != TokenType.EOF
            && lexer.currentToken.type != TokenType.SEMICOLON
            && lexer.currentToken.type != TokenType.CLOSE_BRACE
        ) {
            requireToken(TokenType.COMMA)
            variableDeclarations.add(parseVariableDeclaration())
        }
        return VariableDeclarationList(varModifier, variableDeclarations)
    }

    private fun parseVariableModifier(): VarModifier {
        return VarModifier(requireToken(TokenType.KEYWORD_VAR))
    }

    private fun parseVariableDeclaration(): VariableDeclaration {
        val variableName = requireToken(TokenType.IDENTIFIER)
        var initializer: SingleExpression? = null
        if (lexer.currentToken.type == TokenType.EQUAL) {
            eatToken(TokenType.EQUAL)
            initializer = parseSingleExpression()
        }
        return VariableDeclaration(variableName, initializer)
    }

    private fun parseImportStatement(): ImportStatement {
        TODO("Not yet implemented")
    }

    private fun parseExportStatement(): ExportStatement {
        TODO("Not yet implemented")
    }

    private fun parseEmptyStatement(): EmptyStatement {
        return EmptyStatement(requireToken(TokenType.SEMICOLON))
    }

    private fun parseTryStatement(): TryStatement {
        requireToken(TokenType.KEYWORD_TRY)
        val tryBlock = parseBlock()
        val catchProduction = if (lexer.currentToken.type == TokenType.KEYWORD_CATCH) {
            parseCatchProduction()
        } else {
            null
        }
        val finallyProduction = if (lexer.currentToken.type == TokenType.KEYWORD_FINALLY) {
            parseFinallyProduction()
        } else {
            null
        }
        return TryStatement(tryBlock, catchProduction, finallyProduction)
    }

    private fun parseCatchProduction(): CatchProduction {
        val catchToken = requireToken(TokenType.KEYWORD_CATCH)
        var catchAssignable: Assignable? = null
        if (lexer.currentToken.type == TokenType.OPEN_PAREN) {
            if (lexer.currentToken.type != TokenType.CLOSE_PAREN) {
                catchAssignable = parseAssignable()
            }
        }
        val catchBlock = parseBlock()
        return CatchProduction(catchToken, catchAssignable, catchBlock)
    }

    private fun parseAssignable(): Assignable {
        when (lexer.currentToken.type) {
            TokenType.IDENTIFIER,
            TokenType.ARRAY_LITERAL,
            TokenType.OBJECT_LITERAL
            -> return Assignable(lexer.currentToken)
            else ->
                throw IllegalStateException("Expected assignable, found ${lexer.currentToken}")
        }
    }

    private fun parseFinallyProduction(): FinallyProduction {
        return FinallyProduction(requireToken(TokenType.KEYWORD_FINALLY), parseBlock())
    }

    private fun parseDebuggerStatement(): DebuggerStatement {
        return DebuggerStatement(requireToken(TokenType.KEYWORD_DEBUGGER))
    }

    private fun parseSwitchStatement(): SwitchStatement {
        val switchToken = requireToken(TokenType.KEYWORD_SWITCH)
        requireToken(TokenType.OPEN_PAREN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        val caseBlock = parseCaseBlock()
        return SwitchStatement(switchToken, expressionSequence, caseBlock)
    }

    private fun parseCaseBlock(): CaseBlock {
        requireToken(TokenType.OPEN_BRACE)
        var caseClauses: CaseClauses? =null
        if (lexer.currentToken.type != TokenType.KEYWORD_CASE) {
            caseClauses = parseCaseClauses()
        }
        var defaultClause: DefaultClause? = null
        if (lexer.currentToken.type == TokenType.KEYWORD_DEFAULT) {
            defaultClause = parseDefaultClause()
        }

        var caseClauses2: CaseClauses? =null
        if (lexer.currentToken.type != TokenType.KEYWORD_CASE) {
            caseClauses2 = parseCaseClauses()
        }

        requireToken(TokenType.CLOSE_BRACE)
        return CaseBlock(caseClauses, defaultClause, caseClauses2)
    }

    private fun parseCaseClauses(): CaseClauses {
        val caseClauses = mutableListOf<CaseClause>()
        while (lexer.currentToken.type == TokenType.KEYWORD_CASE) {
            caseClauses.add(parseCaseClause())
        }
        return CaseClauses(caseClauses)
    }

    private fun parseCaseClause(): CaseClause {
        val caseToken = requireToken(TokenType.KEYWORD_CASE)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.COLON)
        val statementList = parseStatementList()
        return CaseClause(caseToken, expressionSequence, statementList)
    }

    private fun parseDefaultClause(): DefaultClause {
        val defaultToken = requireToken(TokenType.KEYWORD_DEFAULT)
        requireToken(TokenType.COLON)
        val statementList = parseStatementList()
        return DefaultClause(defaultToken, statementList)
    }

    private fun parseLabelledStatement(): LabelledStatement {
        val label = requireToken(TokenType.IDENTIFIER)
        requireToken(TokenType.COLON)
        return LabelledStatement(label, parseStatement())
    }

    private fun parseThrowStatement(): ThrowStatement {
        return ThrowStatement(
            requireToken(TokenType.KEYWORD_THROW),
            parseExpressionSequence())
    }

    private fun parseWithStatement(): WithStatement {
        val withToken = requireToken(TokenType.KEYWORD_WITH)
        requireToken(TokenType.OPEN_PAREN)
        val expression = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        return WithStatement(withToken, expression, parseStatement())
    }

    private fun parseYieldStatement(): YieldStatement {
        val yieldToken = requireToken(TokenType.KEYWORD_YIELD)
        var expressionSequence: ExpressionSequence? = null
        if (isExpressionLeaderToken(lexer.currentToken.type)) {
            expressionSequence = parseExpressionSequence()
        }
        return YieldStatement(yieldToken, expressionSequence)
    }

    private fun parseBreakStatement(): BreakStatement {
        val breakToken = requireToken(TokenType.KEYWORD_BREAK)
        var label: Token? = null
        if (lexer.currentToken.type == TokenType.IDENTIFIER) {
            label = requireToken(TokenType.IDENTIFIER)
        }
        return BreakStatement(breakToken, label)
    }

    private fun parseContinueStatement(): ContinueStatement {
        val continueToken = requireToken(TokenType.KEYWORD_CONTINUE)
        var label: Token? = null
        if (lexer.currentToken.type == TokenType.IDENTIFIER) {
            label = requireToken(TokenType.IDENTIFIER)
        }
        return ContinueStatement(continueToken, label)
    }


    private fun parseFunctionDeclaration(): FunctionDeclaration {
        // 解析函数名
        requireToken(TokenType.KEYWORD_FUNCTION)
        val functionName = requireToken(TokenType.IDENTIFIER)

        // 解析参数列表
        requireToken(TokenType.OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.CLOSE_PAREN)

        val body = parseFunctionBody()

        return FunctionDeclaration(functionName, parameters, body)
    }

    private fun parseFormalParameterList(): List<Token> {
        val parameters = mutableListOf<Token>()
        parameters.add(requireToken(TokenType.IDENTIFIER))
        while (lexer.currentToken.type != TokenType.CLOSE_PAREN) {
            requireToken(TokenType.COMMA)
            parameters.add(requireToken(TokenType.IDENTIFIER))
        }
        return parameters
    }

    private fun parseFunctionBody(): FunctionBody {
        // 解析函数体
        requireToken(TokenType.OPEN_BRACE)
        val statements = parseStatementList()
        requireToken(TokenType.CLOSE_BRACE)
        return FunctionBody(statements)
    }

    private fun parseStatementList(): StatementList {
        val statements = mutableListOf<Statement>()
        while (isStatementLeaderToken(lexer.currentToken.type)) {
            statements.add(parseStatement())
        }
        return StatementList(statements)
    }

    private fun parseDoStatement(): DoStatement {
        requireToken(TokenType.KEYWORD_DO)
        val statement = parseStatement()
        requireToken(TokenType.KEYWORD_WHILE)
        requireToken(TokenType.OPEN_PAREN)
        val condition = parseExpressionStatement()
        requireToken(TokenType.CLOSE_PAREN)
        return DoStatement(statement, condition)
    }

    private fun parseWhileStatement(): WhileStatement {
        requireToken(TokenType.KEYWORD_WHILE)
        requireToken(TokenType.OPEN_PAREN)
        val condition = parseExpressionStatement()
        requireToken(TokenType.CLOSE_PAREN)
        val statement = parseStatement()
        return WhileStatement(condition, statement)
    }

    private fun parseForIterationStatement(): IterationStatement {
        requireToken(TokenType.KEYWORD_FOR)
        // 解析是否有await关键字
        val await = lexer.currentToken.type == TokenType.KEYWORD_AWAIT
        if (await) {
            lexer.nextToken()
        }
        requireToken(TokenType.OPEN_PAREN)

        var expressionSequence: ExpressionSequence? = null
        var variableDeclarationList: VariableDeclarationList? = null
        if (isExpressionLeaderToken(lexer.currentToken.type)) {
            expressionSequence = parseExpressionSequence()
        } else if (lexer.currentToken.type == TokenType.KEYWORD_VAR) {
            variableDeclarationList = parseVariableDeclarationList()
        }

        if (lexer.currentToken.type == TokenType.SEMICOLON) {
            if (await) {
                throw IllegalStateException("Unexpected 'await' keyword before ';'")
            }
            return parseForStatement(expressionSequence ?: variableDeclarationList)
        } else {
            if (expressionSequence == null && variableDeclarationList == null) {
                throw IllegalStateException("For-in/of statement requires a single expression or variable declaration list")
            }
            if (expressionSequence != null && !expressionSequence.isSingleExpression()) {
                throw IllegalStateException("For-in/of statement requires a single expression")
            }
            if (lexer.currentToken.type == TokenType.KEYWORD_IN) {
                if (await) {
                    throw IllegalStateException("Unexpected 'await' keyword before keyword 'in'")
                }
                if (expressionSequence != null) {
                    return parseForInStatement(expressionSequence.asSingleExpression())
                } else if (variableDeclarationList != null) {
                    return parseForInStatement(variableDeclarationList)
                } else {
                    throw IllegalStateException("For-in/of statement requires a single expression or variable declaration list")
                }
            } else if (lexer.currentToken.type == TokenType.KEYWORD_OF) {
                if (expressionSequence != null) {
                    return parseForOfStatement(await, expressionSequence.asSingleExpression())
                } else if (variableDeclarationList != null) {
                    return parseForOfStatement(await, variableDeclarationList)
                } else {
                    throw IllegalStateException("For-in/of statement requires a single expression or variable declaration list")
                }
            } else {
                throw IllegalStateException("For-in/of statement requires 'in' or 'of' keyword")
            }
        }
    }

    private fun parseForStatement(initializer: Node?): ForStatement {
        var condition: ExpressionSequence? = null
        if (lexer.currentToken.type == TokenType.SEMICOLON) {
            lexer.nextToken()
        } else {
            condition = parseExpressionSequence()
            requireToken(TokenType.SEMICOLON)
        }

        var increment: ExpressionSequence? = null
        if (lexer.currentToken.type == TokenType.CLOSE_PAREN) {
            lexer.nextToken()
        } else {
            increment = parseExpressionSequence()
            requireToken(TokenType.CLOSE_PAREN)
        }
        val statement = parseStatement()
        return ForStatement(initializer, condition, increment, statement)
    }

    private fun parseForInStatement(singleExpressionOrVariableDeclarationList: Node): ForInStatement {
        requireToken(TokenType.KEYWORD_IN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        val statement = parseStatement()
        return ForInStatement(singleExpressionOrVariableDeclarationList, expressionSequence, statement)
    }

    private fun parseForOfStatement(await: Boolean, singleExpressionOrVariableDeclarationList: Node): ForOfStatement {
        requireToken(TokenType.KEYWORD_IN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        val statement = parseStatement()
        return ForOfStatement(false, singleExpressionOrVariableDeclarationList, expressionSequence, statement)
    }

    private fun parseExpressionStatement(): ExpressionStatement {
        return ExpressionStatement(parseExpressionSequence())
    }

    private fun parseIfStatement(): Statement {
        requireToken(TokenType.KEYWORD_IF)
        requireToken(TokenType.OPEN_PAREN)
        val condition = ExpressionStatement(parseExpressionSequence())
        requireToken(TokenType.CLOSE_PAREN)
        val trueStatement = parseStatement()
        var falseStatement: Statement? = null
        if (lexer.currentToken.type == TokenType.KEYWORD_ELSE) {
            requireToken(TokenType.KEYWORD_ELSE)
            falseStatement = parseStatement()
        }
        return IfStatement(condition, trueStatement, falseStatement)
    }

    private fun parseReturnStatement(): Statement {
        val returnToken = requireToken(TokenType.KEYWORD_RETURN)
        var expressionSequence: ExpressionSequence? = null
        if (isExpressionLeaderToken(lexer.currentToken.type)) {
            expressionSequence = parseExpressionSequence()
        }
        return ReturnStatement(returnToken, expressionSequence)
    }

    private fun parseSingleExpression(): SingleExpression {
        return parseTemplateStringExpression()
    }

    private fun parseTemplateStringExpression(): SingleExpression {
        var leftExpression = parseAssignmentOperatorExpression()
        while (lexer.currentToken.type == TokenType.TEMPLATE_STRING_START) {
            val template = parseTemplateStringLiteral()
            leftExpression = TemplateStringExpression(leftExpression, template)
        }
        return leftExpression
    }

    private fun parseTemplateStringLiteral(): TemplateStringLiteral {
        TODO("Not yet implemented")
    }

    private fun parseAssignmentOperatorExpression(): SingleExpression {
        var leftExpression = parseAssignmentExpression()
        while (lexer.currentToken.type == TokenType.ASSIGNMENT_OPERATOR) {
            val operatorToken = requireToken(TokenType.ASSIGNMENT_OPERATOR)
            val rightExpression = parseAssignmentExpression()
            leftExpression = AssignmentOperatorExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseAssignmentExpression(): SingleExpression {
        var leftExpression = parseTernaryExpression()
        while (lexer.currentToken.type == TokenType.EQUAL) {
            val operatorToken = requireToken(TokenType.EQUAL)
            val rightExpression = parseTernaryExpression()
            leftExpression = AssignmentExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseTernaryExpression(): SingleExpression {
        var leftExpression = parseLogicalOrExpression()
        if (lexer.currentToken.type == TokenType.QUESTION_MARK) {
            val questionToken = requireToken(TokenType.QUESTION_MARK)
            val middleExpression = parseTernaryExpression()
            val colonToken = requireToken(TokenType.COLON)
            val rightExpression = parseTernaryExpression()
            leftExpression =
                TernaryExpression(leftExpression, questionToken, middleExpression, colonToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseLogicalOrExpression(): SingleExpression {
        var leftExpression = parseLogicalAndExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_OR) {
            val operatorToken = requireToken(TokenType.OPERATOR_OR)
            val rightExpression = parseLogicalAndExpression()
            leftExpression = LogicalOrExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseLogicalAndExpression(): SingleExpression {
        var leftExpression = parseBitOrExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_AND) {
            val operatorToken = requireToken(TokenType.OPERATOR_AND)
            val rightExpression = parseBitOrExpression()
            leftExpression = LogicalAndExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitOrExpression(): SingleExpression {
        var leftExpression = parseBitXorExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_BIT_OR) {
            val operatorToken = requireToken(TokenType.OPERATOR_BIT_OR)
            val rightExpression = parseBitXorExpression()
            leftExpression = BitOrExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitXorExpression(): SingleExpression {
        var leftExpression = parseBitAndExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_BIT_XOR) {
            val operatorToken = requireToken(TokenType.OPERATOR_BIT_XOR)
            val rightExpression = parseBitAndExpression()
            leftExpression = BitXorExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitAndExpression(): SingleExpression {
        var leftExpression = parseEqualityExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_BIT_AND) {
            val operatorToken = requireToken(TokenType.OPERATOR_BIT_AND)
            val rightExpression = parseEqualityExpression()
            leftExpression = BitAndExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseEqualityExpression(): SingleExpression {
        var leftExpression = parseInExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_EQUAL ||
            lexer.currentToken.type == TokenType.OPERATOR_NOT_EQUAL) {
            val operatorToken = requireToken(TokenType.OPERATOR_EQUAL, TokenType.OPERATOR_NOT_EQUAL)
            val rightExpression = parseInExpression()
            leftExpression = EqualityExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseInExpression(): SingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseAtomSingleExpression(): SingleExpression {
        when (lexer.currentToken.type) {
            TokenType.KEYWORD_FUNCTION -> return parseFunctionExpression()
            TokenType.KEYWORD_NEW -> return parseNewExpression()
            TokenType.KEYWORD_DELETE -> return parseDeleteExpression()
            TokenType.KEYWORD_VOID -> return parseVoidExpression()
            TokenType.KEYWORD_TYPEOF -> return parseTypeofExpression()
            TokenType.OPERATOR_INCREMENT -> return parsePreIncrementExpression()
            TokenType.OPERATOR_DECREMENT -> return parsePreDecreaseExpression()
            TokenType.OPERATOR_PLUS -> return parseUnaryPlusExpression()
            TokenType.OPERATOR_MINUS -> return parseUnaryMinusExpression()
            TokenType.OPERATOR_BIT_NOT -> return parseBitNotExpression()
            TokenType.OPERATOR_NOT -> return parseNotExpression()
            TokenType.KEYWORD_THIS -> return parseThisExpression()
            TokenType.IDENTIFIER -> return parseIdentifierExpression()
            TokenType.OPEN_PAREN -> return parseParenthesizedExpression()
            TokenType.NULL_LITERAL -> return parseNullLiteralExpression()
            TokenType.BOOLEAN_LITERAL -> return parseBooleanLiteralExpression()
            TokenType.STRING_LITERAL -> return parseStringLiteralExpression()
            TokenType.NUMBER_LITERAL -> return parseNumericLiteralExpression()
            TokenType.REGEX_LITERAL -> return parseRegularExpressionLiteralExpression()
            TokenType.OPEN_BRACKET -> return parseArrayLiteralExpression()
            TokenType.OPEN_BRACE -> return parseObjectLiteralExpression()
            else -> throw IllegalStateException("Unexpected token: " + lexer.currentToken)
        }
    }

    private fun parseObjectLiteralExpression(): SingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseArrayLiteralExpression(): SingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseRegularExpressionLiteralExpression(): RegExpLiteral {
        TODO("Not yet implemented")
    }

    private fun parseNumericLiteralExpression(): NumericLiteralExpression {
        TODO("Not yet implemented")
    }

    private fun parseStringLiteralExpression(): StringLiteralExpression {
        val token = requireToken(TokenType.STRING_LITERAL)
        return StringLiteralExpression(token.value)
    }

    private fun parseBooleanLiteralExpression(): BooleanLiteralExpression {
        requireToken(TokenType.BOOLEAN_LITERAL)
        return BooleanLiteralExpression(lexer.currentToken.value == "true")
    }

    private fun parseNullLiteralExpression(): NullLiteral {
        requireToken(TokenType.NULL_LITERAL)
        return NullLiteral()
    }

    private fun parseParenthesizedExpression(): ParenthesizedExpression {
        requireToken(TokenType.OPEN_PAREN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        return ParenthesizedExpression(expressionSequence)
    }

    private fun parseFunctionExpression(): FunctionExpression {
        requireToken(TokenType.KEYWORD_FUNCTION)
        var functionName: Token? = null
        if (lexer.currentToken.type == TokenType.IDENTIFIER) {
            functionName = lexer.currentToken
        }

        // 解析参数列表
        requireToken(TokenType.OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.CLOSE_PAREN)

        // 解析函数体
        requireToken(TokenType.OPEN_BRACE)
        val body = parseFunctionBody()
        requireToken(TokenType.CLOSE_BRACE)

        return FunctionExpression(functionName, parameters, body)
    }

    private fun parseNewExpression(): NewExpression {
        requireToken(TokenType.KEYWORD_NEW)
        val expression = parseSingleExpression()
        val arguments = parseArguments()
        return NewExpression(expression, arguments)
    }

    private fun parseDeleteExpression(): DeleteExpression {
        requireToken(TokenType.KEYWORD_DELETE)
        val expression = parseSingleExpression()
        return DeleteExpression(expression)
    }

    private fun parseVoidExpression(): VoidExpression {
        requireToken(TokenType.KEYWORD_VOID)
        val expression = parseSingleExpression()
        return VoidExpression(expression)
    }

    private fun parseTypeofExpression(): TypeofExpression {
        requireToken(TokenType.KEYWORD_TYPEOF)
        val expression = parseSingleExpression()
        return TypeofExpression(expression)
    }

    private fun parsePreIncrementExpression(): PreIncrementExpression {
        requireToken(TokenType.OPERATOR_INCREMENT)
        val expression = parseSingleExpression()
        return PreIncrementExpression(expression)
    }

    private fun parsePreDecreaseExpression(): PreDecreaseExpression {
        requireToken(TokenType.OPERATOR_DECREMENT)
        val expression = parseSingleExpression()
        return PreDecreaseExpression(expression)
    }

    private fun parseUnaryPlusExpression(): UnaryPlusExpression {
        requireToken(TokenType.OPERATOR_PLUS)
        val expression = parseSingleExpression()
        return UnaryPlusExpression(expression)
    }

    private fun parseUnaryMinusExpression(): UnaryMinusExpression {
        requireToken(TokenType.OPERATOR_MINUS)
        val expression = parseSingleExpression()
        return UnaryMinusExpression(expression)
    }

    private fun parseBitNotExpression(): BitNotExpression {
        requireToken(TokenType.OPERATOR_BIT_NOT)
        val expression = parseSingleExpression()
        return BitNotExpression(expression)
    }

    private fun parseNotExpression(): NotExpression {
        requireToken(TokenType.OPERATOR_NOT)
        val expression = parseSingleExpression()
        return NotExpression(expression)
    }

    private fun parseThisExpression(): ThisExpression {
        requireToken(TokenType.KEYWORD_THIS)
        return ThisExpression()
    }

    private fun parseIdentifierExpression(): IdentifierExpression {
        return IdentifierExpression(requireToken(TokenType.IDENTIFIER))
    }

    private fun parseArguments(): List<Node> {
        TODO("Not yet implemented")
    }

    private fun parseExpressionSequence(): ExpressionSequence {
        val expressions = mutableListOf<SingleExpression>()
        expressions.add(parseSingleExpression())
        while (lexer.currentToken.type == TokenType.COMMA) {
            expressions.add(parseSingleExpression())
        }
        return ExpressionSequence(expressions)
    }

    private fun eatToken(tokenType: TokenType) {
        if (lexer.currentToken.type == tokenType) {
            lexer.nextToken()
        }
    }

    private fun requireToken(vararg tokenTypes: TokenType): Token {
        val currentToken = lexer.currentToken
        if (currentToken.type in tokenTypes) {
            lexer.nextToken()
            return currentToken
        } else {
            throw IllegalStateException("Expected token type $tokenTypes but got ${lexer.currentToken.type}")
        }
    }

    private fun isStatementLeaderToken(tokenType: TokenType): Boolean {
        when (tokenType) {
            TokenType.OPEN_BRACE,
            TokenType.KEYWORD_VAR,
            TokenType.KEYWORD_IMPORT,
            TokenType.KEYWORD_EXPORT,
            TokenType.SEMICOLON,
            TokenType.KEYWORD_CLASS,
            TokenType.KEYWORD_FUNCTION,
            TokenType.KEYWORD_IF,
            TokenType.KEYWORD_DO,
            TokenType.KEYWORD_WHILE,
            TokenType.KEYWORD_FOR,
            TokenType.KEYWORD_CONTINUE,
            TokenType.KEYWORD_BREAK,
            TokenType.KEYWORD_RETURN,
            TokenType.KEYWORD_YIELD,
            TokenType.KEYWORD_WITH,
            TokenType.IDENTIFIER,
            TokenType.KEYWORD_SWITCH,
            TokenType.KEYWORD_THROW,
            TokenType.KEYWORD_TRY,
            TokenType.KEYWORD_DEBUGGER -> return true
            else -> return isExpressionLeaderToken(tokenType)
        }
    }

    private fun isExpressionLeaderToken(tokenType: TokenType): Boolean {
        when (tokenType) {
            TokenType.KEYWORD_ASYNC,
            TokenType.KEYWORD_FUNCTION,
            TokenType.KEYWORD_CLASS,
            TokenType.KEYWORD_DELETE,
            TokenType.KEYWORD_VOID,
            TokenType.KEYWORD_TYPEOF,
            TokenType.OPERATOR_INCREMENT,
            TokenType.OPERATOR_DECREMENT,
            TokenType.OPERATOR_PLUS,
            TokenType.OPERATOR_MINUS,
            TokenType.OPERATOR_BIT_NOT,
            TokenType.OPERATOR_NOT,
            TokenType.KEYWORD_AWAIT,
            TokenType.KEYWORD_IMPORT,
            TokenType.KEYWORD_YIELD,
            TokenType.KEYWORD_THIS,
            TokenType.IDENTIFIER,
            TokenType.KEYWORD_SUPER,
            TokenType.LITERAL,
            TokenType.OPEN_BRACKET,
            TokenType.OPEN_BRACE,
            TokenType.OPEN_PAREN -> return true
            else -> return false
        }
    }
}