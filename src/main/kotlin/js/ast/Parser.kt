package js.ast

/**
 *  解析器
 *  https://github.com/antlr/grammars-v4/blob/master/javascript/ecmascript/JavaScript/ECMAScript.g4
 *  参考antlr4的JavaScript语法定义
 */
class Parser(private val lexer: Lexer) {
    fun parse(): Node {
        return parseProgram()
    }

    // 定义一个函数，解析JavaScript语法树
    private fun parseProgram(): Node {
        val elements = mutableListOf<SourceElement>()
        while (lexer.currentToken.type != TokenType.EOF) {
            elements.add(parseSourceElement())
        }
        return Program(elements)
    }

    private fun parseSourceElement(): SourceElement {
        return if (lexer.currentToken.type == TokenType.KEYWORD_FUNCTION) {
            parseFunctionDeclaration()
        } else {
            parseStatement()
        }
    }

    private fun parseFunctionDeclaration(): FunctionDeclaration {
        // 解析函数名
        requireToken(TokenType.KEYWORD_FUNCTION)
        val functionName = requireToken(TokenType.IDENTIFIER)

        // 解析参数列表
        requireToken(TokenType.OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.CLOSE_PAREN)

        // 解析函数体
        requireToken(TokenType.OPEN_BRACE)
        val body = parseFunctionBody()
        requireToken(TokenType.CLOSE_BRACE)

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

    private fun parseFunctionBody(): List<SourceElement> {
        val sourceElements = mutableListOf<SourceElement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            sourceElements.add(parseSourceElement())
        }
        return sourceElements
    }

    private fun parseStatement(): Statement {
        return when (lexer.currentToken.type) {
            TokenType.OPEN_BRACE -> parseBlockStatement()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.SEMICOLON -> EmptyStatement(lexer.currentToken)
            TokenType.KEYWORD_IF -> parseIfStatement()
            TokenType.KEYWORD_DO -> parseDoStatement()
            TokenType.KEYWORD_WHILE -> parseWhileStatement()
            TokenType.KEYWORD_FOR -> parseForStatement()
            TokenType.KEYWORD_RETURN -> parseReturnStatement()
            TokenType.IDENTIFIER -> parseExpressionStatement()
            else -> {
                if (lexer.currentToken.type != TokenType.OPEN_BRACE) {
                    parseExpressionStatement()
                } else {
                    throw IllegalStateException("Unexpected token ${lexer.currentToken}")
                }
            }
        }
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

    private fun parseForStatement(): ForStatement {
        requireToken(TokenType.KEYWORD_FOR)
        requireToken(TokenType.OPEN_PAREN)

        val initializer = parseExpressionSequence()
        requireToken(TokenType.SEMICOLON)
        val condition = parseExpressionSequence()
        requireToken(TokenType.SEMICOLON)
        val increment = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        val statement = parseStatement()
        return ForStatement(initializer, condition, increment, statement)
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


    private fun parseVariableStatement(): Statement {
        requireToken(TokenType.KEYWORD_VAR)
        val variableDeclarations  = mutableListOf<VariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (lexer.currentToken.type != TokenType.EOF
            && lexer.currentToken.type != TokenType.SEMICOLON
            && lexer.currentToken.type != TokenType.CLOSE_BRACE
        ) {
            requireToken(TokenType.COMMA)
            variableDeclarations.add(parseVariableDeclaration())
        }
        return VariableStatement(variableDeclarations)
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
}