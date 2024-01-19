package core

/**
 *  解析器
 *  https://github.com/antlr/grammars-v4/blob/master/javascript/ecmascript/JavaScript/ECMAScript.g4
 *  参考antlr4的JavaScript语法定义
 */
class Parser(private val lexer: Lexer) {
    fun parse(): JsNode {
        return parseProgram()
    }

    // 定义一个函数，解析JavaScript语法树
    private fun parseProgram(): JsNode {
        val elements = mutableListOf<JsSourceElement>()
        while (lexer.currentToken.type != TokenType.EOF) {
            elements.add(parseSourceElement())
        }
        return JsProgram(elements)
    }

    private fun parseSourceElement(): JsSourceElement {
        return if (lexer.currentToken.type == TokenType.KEYWORD_FUNCTION) {
            parseFunctionDeclaration()
        } else {
            parseStatement()
        }
    }

    private fun parseFunctionDeclaration(): JsFunctionDeclaration {
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

        return JsFunctionDeclaration(functionName, parameters, body)
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

    private fun parseFunctionBody(): List<JsSourceElement> {
        val sourceElements = mutableListOf<JsSourceElement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            sourceElements.add(parseSourceElement())
        }
        return sourceElements
    }

    private fun parseBlockStatement(): JsBlock {
        requireToken(TokenType.OPEN_BRACE)
        val statements = mutableListOf<JsStatement>()
        while (lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement())
        }
        requireToken(TokenType.CLOSE_BRACE)
        return JsBlock(statements)
    }

    private fun parseExpressionStatement(): JsExpressionStatement {
        return JsExpressionStatement(parseExpressionSequence())
    }

    private fun parseIfStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseWhileStatement(): JsStatement {
        TODO("Not yet implemented")
    }

    private fun parseReturnStatement(): JsStatement {
        TODO("Not yet implemented")
    }


    private fun parseVariableStatement(): JsStatement {
        requireToken(TokenType.KEYWORD_VAR)
        val variableDeclarations  = mutableListOf<JsVariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (lexer.currentToken.type != TokenType.EOF
            && lexer.currentToken.type != TokenType.SEMICOLON
            && lexer.currentToken.type != TokenType.CLOSE_BRACE) {
            requireToken(TokenType.COMMA)
            variableDeclarations.add(parseVariableDeclaration())
        }
        return JsVariableStatement(variableDeclarations)
    }

    private fun parseVariableDeclaration(): JsVariableDeclaration {
        val variableName = requireToken(TokenType.IDENTIFIER)
        var initializer: JsSingleExpression? = null
        if (lexer.currentToken.type == TokenType.EQUAL) {
            eatToken(TokenType.EQUAL)
            initializer = parseSingleExpression()
        }
        return JsVariableDeclaration(variableName, initializer)
    }

    private fun parseSingleExpression(): JsSingleExpression {
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

    private fun parseObjectLiteralExpression(): JsSingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseArrayLiteralExpression(): JsSingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseRegularExpressionLiteralExpression(): JsRegExpLiteral {
        TODO("Not yet implemented")
    }

    private fun parseNumericLiteralExpression(): JsNumericLiteralExpression {
        TODO("Not yet implemented")
    }

    private fun parseStringLiteralExpression(): JsStringLiteralExpression {
        val token = requireToken(TokenType.STRING_LITERAL)
        return JsStringLiteralExpression(token.value)
    }

    private fun parseBooleanLiteralExpression(): JsBooleanLiteralExpression {
        requireToken(TokenType.BOOLEAN_LITERAL)
        return JsBooleanLiteralExpression(lexer.currentToken.value == "true")
    }

    private fun parseNullLiteralExpression(): JsNullLiteral {
        requireToken(TokenType.NULL_LITERAL)
        return JsNullLiteral()
    }

    private fun parseParenthesizedExpression(): JsParenthesizedExpression {
        requireToken(TokenType.OPEN_PAREN)
        val expressions = parseExpressionSequence()
        requireToken(TokenType.CLOSE_PAREN)
        return JsParenthesizedExpression(parseExpressionSequence())
    }

    private fun parseFunctionExpression(): JsFunctionExpression {
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

        return JsFunctionExpression(functionName, parameters, body)
    }

    private fun parseNewExpression(): JsNewExpression {
        requireToken(TokenType.KEYWORD_NEW)
        val expression = parseSingleExpression()
        val arguments = parseArguments()
        return JsNewExpression(expression, arguments)
    }

    private fun parseDeleteExpression(): JsDeleteExpression {
        requireToken(TokenType.KEYWORD_DELETE)
        val expression = parseSingleExpression()
        return JsDeleteExpression(expression)
    }

    private fun parseVoidExpression(): JsVoidExpression {
        requireToken(TokenType.KEYWORD_VOID)
        val expression = parseSingleExpression()
        return JsVoidExpression(expression)
    }

    private fun parseTypeofExpression(): JsTypeofExpression {
        requireToken(TokenType.KEYWORD_TYPEOF)
        val expression = parseSingleExpression()
        return JsTypeofExpression(expression)
    }

    private fun parsePreIncrementExpression(): JsPreIncrementExpression {
        requireToken(TokenType.OPERATOR_INCREMENT)
        val expression = parseSingleExpression()
        return JsPreIncrementExpression(expression)
    }

    private fun parsePreDecreaseExpression(): JsPreDecreaseExpression {
        requireToken(TokenType.OPERATOR_DECREMENT)
        val expression = parseSingleExpression()
        return JsPreDecreaseExpression(expression)
    }

    private fun parseUnaryPlusExpression(): JsUnaryPlusExpression {
        requireToken(TokenType.OPERATOR_PLUS)
        val expression = parseSingleExpression()
        return JsUnaryPlusExpression(expression)
    }

    private fun parseUnaryMinusExpression(): JsUnaryMinusExpression {
        requireToken(TokenType.OPERATOR_MINUS)
        val expression = parseSingleExpression()
        return JsUnaryMinusExpression(expression)
    }

    private fun parseBitNotExpression(): JsBitNotExpression {
        requireToken(TokenType.OPERATOR_BIT_NOT)
        val expression = parseSingleExpression()
        return JsBitNotExpression(expression)
    }

    private fun parseNotExpression(): JsNotExpression {
        requireToken(TokenType.OPERATOR_NOT)
        val expression = parseSingleExpression()
        return JsNotExpression(expression)
    }

    private fun parseThisExpression(): JsThisExpression {
        requireToken(TokenType.KEYWORD_THIS)
        return JsThisExpression()
    }

    private fun parseIdentifierExpression(): JsIdentifierExpression {
        return JsIdentifierExpression(requireToken(TokenType.IDENTIFIER))
    }

    private fun parseArguments(): List<JsNode> {
        TODO("Not yet implemented")
    }

    private fun parseExpressionSequence(): List<JsSingleExpression> {
        val expressions = mutableListOf<JsSingleExpression>()
        expressions.add(parseSingleExpression())
        while (lexer.currentToken.type == TokenType.COMMA) {
            expressions.add(parseSingleExpression())
        }
        return expressions
    }

    private fun parseStatement(): JsStatement {
        return when (lexer.currentToken.type) {
            TokenType.OPEN_BRACE -> parseBlockStatement()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.SEMICOLON -> JsEmptyStatement(lexer.currentToken)
            TokenType.KEYWORD_IF -> parseIfStatement()
            TokenType.KEYWORD_WHILE -> parseWhileStatement()
            TokenType.KEYWORD_RETURN -> parseReturnStatement()
            TokenType.IDENTIFIER -> parseExpressionStatement()
            else -> throw IllegalStateException("Unexpected token ${lexer.currentToken}")
        }
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