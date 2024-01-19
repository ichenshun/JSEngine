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

    // 定义一个函数，解析JavaScript语法树
    private fun parseProgram(): Node {
        val statements = mutableListOf<Statement>()
        while (lexer.currentToken.type != TokenType.EOF) {
            statements.add(parseStatement())
        }
        return Program(statements)
    }

    private fun parseStatement(): Statement {
        return when (lexer.currentToken.type) {
            TokenType.OPEN_BRACE -> parseBlockStatement()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.SEMICOLON -> EmptyStatement(lexer.currentToken)
            TokenType.KEYWORD_FUNCTION -> parseFunctionDeclaration()
            TokenType.KEYWORD_IF -> parseIfStatement()
            TokenType.KEYWORD_DO -> parseDoStatement()
            TokenType.KEYWORD_WHILE -> parseWhileStatement()
            TokenType.KEYWORD_FOR -> parseForIterationStatement()
            TokenType.KEYWORD_RETURN -> parseReturnStatement()
            else -> {
                if (lexer.currentToken.type != TokenType.OPEN_BRACE) {
                    parseExpressionStatement()
                } else {
                    throw IllegalStateException("Unexpected token ${lexer.currentToken}")
                }
            }
        }
    }

    private fun parseBlockStatement(): Block {
        requireToken(TokenType.OPEN_BRACE)
        val statements = mutableListOf<Statement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement())
        }
        requireToken(TokenType.CLOSE_BRACE)
        return Block(statements)
    }

    private fun parseVariableStatement(): VariableStatement {
        requireToken(TokenType.KEYWORD_VAR)
        return VariableStatement(parseVariableDeclarationList())
    }

    private fun parseVariableDeclarationList(): VariableDeclarationList {
        val variableDeclarations  = mutableListOf<VariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (lexer.currentToken.type != TokenType.EOF
            && lexer.currentToken.type != TokenType.SEMICOLON
            && lexer.currentToken.type != TokenType.CLOSE_BRACE
        ) {
            requireToken(TokenType.COMMA)
            variableDeclarations.add(parseVariableDeclaration())
        }
        return VariableDeclarationList(variableDeclarations)
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
        val statements = mutableListOf<Statement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement())
        }
        requireToken(TokenType.CLOSE_BRACE)
        return FunctionBody(statements)
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
        TODO("Not yet implemented")
    }

    private fun parseSingleExpression(): SingleExpression {
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

    private fun requireToken(tokenType: TokenType): Token {
        val currentToken = lexer.currentToken
        if (currentToken.type == tokenType) {
            lexer.nextToken()
            return currentToken
        } else {
            throw IllegalStateException("Expected token type $tokenType but got ${lexer.currentToken.type}")
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