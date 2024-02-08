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
        return Program(parseStatementList())
    }

    private fun parseStatementList(): StatementList {
        val statements = mutableListOf<Statement>()
        while (isStatementLeaderToken(lexer.currentToken.type)) {
            statements.add(parseStatement())
        }
        return StatementList(statements)
    }

    private fun parseStatement(): Statement {
        return when (lexer.currentToken.type) {
            TokenType.OPERATOR_OPEN_BRACE -> parseBlock()
            TokenType.KEYWORD_VAR -> parseVariableStatement()
            TokenType.KEYWORD_IMPORT -> parseImportStatement()
            TokenType.KEYWORD_EXPORT -> parseExportStatement()
            TokenType.OPERATOR_SEMICOLON -> parseEmptyStatement()
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
        requireToken(TokenType.OPERATOR_OPEN_BRACE)
        val statements = parseStatementList()
        requireToken(TokenType.OPERATOR_CLOSE_BRACE)
        return Block(statements)
    }

    private fun parseVariableStatement(): VariableStatement {
        return VariableStatement(parseVariableDeclarationList())
    }

    private fun parseVariableDeclarationList(): VariableDeclarationList {
        val varModifier = parseVariableModifier()
        val variableDeclarations = mutableListOf<VariableDeclaration>()
        variableDeclarations.add(parseVariableDeclaration())
        while (isToken(TokenType.OPERATOR_COMMA)) {
            requireToken(TokenType.OPERATOR_COMMA)
            variableDeclarations.add(parseVariableDeclaration())
            if (isToken(TokenType.EOF, TokenType.OPERATOR_SEMICOLON, TokenType.OPERATOR_CLOSE_BRACE)) {
                break
            }
        }
        return VariableDeclarationList(varModifier, variableDeclarations)
    }

    private fun parseVariableModifier(): VarModifier {
        return VarModifier(requireToken(TokenType.KEYWORD_VAR))
    }

    private fun parseVariableDeclaration(): VariableDeclaration {
        val variableName = requireToken(TokenType.IDENTIFIER)
        var initializer: SingleExpression? = null
        if (lexer.currentToken.type == TokenType.OPERATOR_ASSIGN) {
            requireToken(TokenType.OPERATOR_ASSIGN)
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
        return EmptyStatement(requireToken(TokenType.OPERATOR_SEMICOLON))
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
        if (lexer.currentToken.type == TokenType.OPERATOR_OPEN_PAREN) {
            if (lexer.currentToken.type != TokenType.OPERATOR_CLOSE_PAREN) {
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
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        val caseBlock = parseCaseBlock()
        return SwitchStatement(switchToken, expressionSequence, caseBlock)
    }

    private fun parseCaseBlock(): CaseBlock {
        requireToken(TokenType.OPERATOR_OPEN_BRACE)
        var caseClauses: CaseClauses? = null
        if (lexer.currentToken.type != TokenType.KEYWORD_CASE) {
            caseClauses = parseCaseClauses()
        }
        var defaultClause: DefaultClause? = null
        if (lexer.currentToken.type == TokenType.KEYWORD_DEFAULT) {
            defaultClause = parseDefaultClause()
        }

        var caseClauses2: CaseClauses? = null
        if (lexer.currentToken.type != TokenType.KEYWORD_CASE) {
            caseClauses2 = parseCaseClauses()
        }

        requireToken(TokenType.OPERATOR_CLOSE_BRACE)
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
        requireToken(TokenType.OPERATOR_COLON)
        val statementList = parseStatementList()
        return CaseClause(caseToken, expressionSequence, statementList)
    }

    private fun parseDefaultClause(): DefaultClause {
        val defaultToken = requireToken(TokenType.KEYWORD_DEFAULT)
        requireToken(TokenType.OPERATOR_COLON)
        val statementList = parseStatementList()
        return DefaultClause(defaultToken, statementList)
    }

    private fun parseLabelledStatement(): LabelledStatement {
        val label = requireToken(TokenType.IDENTIFIER)
        requireToken(TokenType.OPERATOR_COLON)
        return LabelledStatement(label, parseStatement())
    }

    private fun parseThrowStatement(): ThrowStatement {
        return ThrowStatement(
            requireToken(TokenType.KEYWORD_THROW),
            parseExpressionSequence()
        )
    }

    private fun parseWithStatement(): WithStatement {
        val withToken = requireToken(TokenType.KEYWORD_WITH)
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val expression = parseExpressionSequence()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
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
        val asyncToken = optionalToken(TokenType.KEYWORD_ASYNC)
        val functionToken = requireToken(TokenType.KEYWORD_FUNCTION)
        val functionName = requireToken(TokenType.IDENTIFIER)
        val asteriskToken = optionalToken(TokenType.OPERATOR_MULTIPLY)

        val openParen  = requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val parameters = parseFormalParameterList()
        val closeParen = requireToken(TokenType.OPERATOR_CLOSE_PAREN)

        val body = parseFunctionBody()

        return FunctionDeclaration(
            asyncToken,
            functionToken,
            asteriskToken,
            functionName,
            openParen,
            parameters,
            closeParen,
            body)
    }

    private fun parseFormalParameterList(): List<Token> {
        // TODO 解析 ECMAScript 6: Rest Parameter
        val parameters = mutableListOf<Token>()
        parameters.add(requireToken(TokenType.IDENTIFIER))
        while (lexer.currentToken.type != TokenType.OPERATOR_CLOSE_PAREN) {
            requireToken(TokenType.OPERATOR_COMMA)
            parameters.add(requireToken(TokenType.IDENTIFIER))
        }
        return parameters
    }

    private fun parseFunctionBody(): FunctionBody {
        // 解析函数体
        val openBraceToken = requireToken(TokenType.OPERATOR_OPEN_BRACE)
        val statements = parseStatementList()
        val closeBraceToken = requireToken(TokenType.OPERATOR_CLOSE_BRACE)
        return FunctionBody(openBraceToken, statements, closeBraceToken)
    }

    private fun parseDoStatement(): DoStatement {
        requireToken(TokenType.KEYWORD_DO)
        val statement = parseStatement()
        requireToken(TokenType.KEYWORD_WHILE)
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val condition = parseExpressionStatement()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        return DoStatement(statement, condition)
    }

    private fun parseWhileStatement(): WhileStatement {
        requireToken(TokenType.KEYWORD_WHILE)
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val condition = parseExpressionStatement()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
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
        requireToken(TokenType.OPERATOR_OPEN_PAREN)

        var expressionSequence: ExpressionSequence? = null
        var variableDeclarationList: VariableDeclarationList? = null
        if (isExpressionLeaderToken(lexer.currentToken.type)) {
            expressionSequence = parseExpressionSequence()
        } else if (lexer.currentToken.type == TokenType.KEYWORD_VAR) {
            variableDeclarationList = parseVariableDeclarationList()
        }

        if (lexer.currentToken.type == TokenType.OPERATOR_SEMICOLON) {
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
        if (lexer.currentToken.type == TokenType.OPERATOR_SEMICOLON) {
            lexer.nextToken()
        } else {
            condition = parseExpressionSequence()
            requireToken(TokenType.OPERATOR_SEMICOLON)
        }

        var increment: ExpressionSequence? = null
        if (lexer.currentToken.type == TokenType.OPERATOR_CLOSE_PAREN) {
            lexer.nextToken()
        } else {
            increment = parseExpressionSequence()
            requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        }
        val statement = parseStatement()
        return ForStatement(initializer, condition, increment, statement)
    }

    private fun parseForInStatement(singleExpressionOrVariableDeclarationList: Node): ForInStatement {
        requireToken(TokenType.KEYWORD_IN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        val statement = parseStatement()
        return ForInStatement(singleExpressionOrVariableDeclarationList, expressionSequence, statement)
    }

    private fun parseForOfStatement(await: Boolean, singleExpressionOrVariableDeclarationList: Node): ForOfStatement {
        requireToken(TokenType.KEYWORD_IN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        val statement = parseStatement()
        return ForOfStatement(false, singleExpressionOrVariableDeclarationList, expressionSequence, statement)
    }

    private fun parseExpressionStatement(): ExpressionStatement {
        return ExpressionStatement(parseExpressionSequence())
    }

    private fun parseIfStatement(): Statement {
        requireToken(TokenType.KEYWORD_IF)
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val condition = ExpressionStatement(parseExpressionSequence())
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
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
        while (lexer.currentToken.type == TokenType.OPERATOR_ASSIGN) {
            val operatorToken = requireToken(TokenType.OPERATOR_ASSIGN)
            val rightExpression = parseAssignmentExpression()
            leftExpression = AssignmentOperatorExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseAssignmentExpression(): SingleExpression {
        var leftExpression = parseTernaryExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_ASSIGN) {
            val operatorToken = requireToken(TokenType.OPERATOR_ASSIGN)
            val rightExpression = parseTernaryExpression()
            leftExpression = AssignmentExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseTernaryExpression(): SingleExpression {
        var leftExpression = parseLogicalOrExpression()
        if (lexer.currentToken.type == TokenType.OPERATOR_QUESTION_MARK) {
            val questionToken = requireToken(TokenType.OPERATOR_QUESTION_MARK)
            val middleExpression = parseTernaryExpression()
            val colonToken = requireToken(TokenType.OPERATOR_COLON)
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
            lexer.currentToken.type == TokenType.OPERATOR_NOT_EQUAL ||
            lexer.currentToken.type == TokenType.OPERATOR_IDENTITY_EQUAL ||
            lexer.currentToken.type == TokenType.OPERATOR_IDENTITY_NOT_EQUAL
        ) {
            val operatorToken = requireToken(
                TokenType.OPERATOR_EQUAL,
                TokenType.OPERATOR_NOT_EQUAL,
                TokenType.OPERATOR_IDENTITY_EQUAL,
                TokenType.OPERATOR_IDENTITY_NOT_EQUAL
            )
            val rightExpression = parseInExpression()
            leftExpression = EqualityExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseInExpression(): SingleExpression {
        var leftExpression = parseInstanceOfExpression()
        while (lexer.currentToken.type == TokenType.KEYWORD_IN) {
            val inToken = requireToken(TokenType.KEYWORD_IN)
            val rightExpression = parseInstanceOfExpression()
            leftExpression = InExpression(leftExpression, inToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseInstanceOfExpression(): SingleExpression {
        var leftExpression = parseRelationalExpression()
        while (lexer.currentToken.type == TokenType.KEYWORD_INSTANCEOF) {
            val inToken = requireToken(TokenType.KEYWORD_INSTANCEOF)
            val rightExpression = parseRelationalExpression()
            leftExpression = InstanceOfExpression(leftExpression, inToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseRelationalExpression(): SingleExpression {
        var leftExpression = parseBitShiftExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_LESS_THAN ||
            lexer.currentToken.type == TokenType.OPERATOR_MORE_THAN ||
            lexer.currentToken.type == TokenType.OPERATOR_LESS_THAN_EQUALS ||
            lexer.currentToken.type == TokenType.OPERATOR_MORE_THAN_EQUALS
        ) {
            val operator = requireToken(
                TokenType.OPERATOR_LESS_THAN,
                TokenType.OPERATOR_MORE_THAN,
                TokenType.OPERATOR_LESS_THAN_EQUALS,
                TokenType.OPERATOR_MORE_THAN_EQUALS
            )
            val rightExpression = parseBitShiftExpression()
            leftExpression = RelationalExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitShiftExpression(): SingleExpression {
        var leftExpression = parseCoalesceExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_LEFT_SHIFT_ARITHMETIC ||
            lexer.currentToken.type == TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC ||
            lexer.currentToken.type == TokenType.OPERATOR_RIGHT_SHIFT_LOGICAL
        ) {
            val operator = requireToken(
                TokenType.OPERATOR_LEFT_SHIFT_ARITHMETIC,
                TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC,
                TokenType.OPERATOR_RIGHT_SHIFT_LOGICAL
            )
            val rightExpression = parseCoalesceExpression()
            leftExpression = BitShiftExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parseCoalesceExpression(): SingleExpression {
        var leftExpression = parseAdditiveExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_NULL_COALESCE) {
            val operator = requireToken(TokenType.OPERATOR_NULL_COALESCE)
            val rightExpression = parseAdditiveExpression()
            leftExpression = CoalesceExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parseAdditiveExpression(): SingleExpression {
        var leftExpression = parseMultiplicativeExpression()
        while (isToken(TokenType.OPERATOR_PLUS, TokenType.OPERATOR_MINUS)) {
            val operator = requireToken(TokenType.OPERATOR_PLUS, TokenType.OPERATOR_MINUS)
            val rightExpression = parseMultiplicativeExpression()
            leftExpression = AdditiveExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parseMultiplicativeExpression(): SingleExpression {
        var leftExpression = parsePowerExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_MULTIPLY
            || lexer.currentToken.type == TokenType.OPERATOR_DIVIDE
            || lexer.currentToken.type == TokenType.OPERATOR_MOD
        ) {
            val operator = requireToken(
                TokenType.OPERATOR_MULTIPLY,
                TokenType.OPERATOR_DIVIDE,
                TokenType.OPERATOR_MOD
            )
            val rightExpression = parsePowerExpression()
            leftExpression = MultiplicativeExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parsePowerExpression(): SingleExpression {
        var leftExpression = parsePostDecreaseExpression()
        while (lexer.currentToken.type == TokenType.OPERATOR_POWER) {
            val operator = requireToken(TokenType.OPERATOR_POWER)
            val rightExpression = parsePostDecreaseExpression()
            leftExpression = PowerExpression(leftExpression, operator, rightExpression)
        }
        return leftExpression
    }

    private fun parsePostDecreaseExpression(): SingleExpression {
        var leftExpression = parsePostIncrementExpression()
        while (isToken(TokenType.OPERATOR_MINUS_MINUS)) {
            val operator = requireToken(TokenType.OPERATOR_MINUS_MINUS)
            leftExpression = PostDecreaseExpression(leftExpression, operator)
        }
        return leftExpression
    }

    private fun parsePostIncrementExpression(): SingleExpression {
        var leftExpression = parseArgumentsExpression()
        while (isToken(TokenType.OPERATOR_PLUS_PLUS)) {
            val operator = requireToken(TokenType.OPERATOR_PLUS_PLUS)
            leftExpression = PostIncrementExpression(leftExpression, operator)
        }
        return leftExpression
    }

    private fun parseArgumentsExpression(): SingleExpression {
        var leftExpression = parseMemberDotExpression()
        while (isToken(TokenType.OPERATOR_OPEN_PAREN)) {
            leftExpression = ArgumentsExpression(leftExpression, parseArgumentList())
        }
        return leftExpression
    }

    private fun parseArgumentList(): ArgumentList {
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val arguments = mutableListOf<Argument>()
        if (!isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
            arguments.add(parseArgument())
            while (isToken(TokenType.OPERATOR_COMMA)) {
                requireToken(TokenType.OPERATOR_COMMA)
                if (lexer.currentToken.type != TokenType.OPERATOR_CLOSE_PAREN) {
                    arguments.add(parseArgument())
                } else {
                    break
                }
            }
        }
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        return ArgumentList(arguments)
    }

    private fun parseArgument(): Argument {
        val ellipse = if (isToken(TokenType.OPERATOR_ELLIPSIS)) lexer.currentToken else null
        return Argument(ellipse, parseSingleExpression())
    }

    private fun parseMemberDotExpression(): SingleExpression {
        var leftExpression = parseMemberIndexExpression()
        while (isToken(TokenType.OPERATOR_QUESTION_MARK, TokenType.OPERATOR_DOT)) {
            val questionToke =
                if (isToken(TokenType.OPERATOR_QUESTION_MARK)) requireToken(TokenType.OPERATOR_QUESTION_MARK) else null
            val dotToken = requireToken(TokenType.OPERATOR_DOT)
            val hastTagToken =
                if (isToken(TokenType.OPERATOR_HASHTAG)) requireToken(TokenType.OPERATOR_HASHTAG) else null
            val identifier = requireToken(TokenType.IDENTIFIER)
            leftExpression = MemberDotExpression(leftExpression, questionToke, dotToken, hastTagToken, identifier)
        }
        return leftExpression
    }

    private fun parseMemberIndexExpression(): SingleExpression {
        var leftExpression = parseOptionalChainExpression()
        while (isToken(TokenType.OPERATOR_QUESTION_MARK_DOT, TokenType.OPERATOR_OPEN_BRACKET)) {
            val questionDotToken = if (lexer.currentToken.type == TokenType.OPERATOR_QUESTION_MARK_DOT)
                requireToken(TokenType.OPERATOR_QUESTION_MARK_DOT) else null
            val openBracket = requireToken(TokenType.OPERATOR_OPEN_BRACKET)
            val expressionSequence = parseExpressionSequence()
            val closeBracket = requireToken(TokenType.OPERATOR_CLOSE_BRACKET)
            leftExpression = MemberIndexExpression(
                leftExpression,
                questionDotToken,
                openBracket,
                expressionSequence,
                closeBracket
            )
        }
        return leftExpression
    }

    private fun parseOptionalChainExpression(): SingleExpression {
        var leftExpression = parseAtomSingleExpression()
        while (isToken(TokenType.OPERATOR_QUESTION_MARK_DOT)) {
            val questionDotToken = requireToken(TokenType.OPERATOR_QUESTION_MARK_DOT)
            val rightExpression = parseAtomSingleExpression()
            leftExpression = OptionalChainExpression(leftExpression, questionDotToken, rightExpression)
        }
        return leftExpression
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
            TokenType.KEYWORD_AWAIT -> return parseAwaitExpression()
            TokenType.KEYWORD_THIS -> return parseThisExpression()
            TokenType.IDENTIFIER -> return parseIdentifierExpression()
            TokenType.OPERATOR_OPEN_PAREN -> return parseParenthesizedExpression()
            TokenType.NULL_LITERAL -> return parseNullLiteralExpression()
            TokenType.BOOLEAN_LITERAL -> return parseBooleanLiteralExpression()
            TokenType.STRING_LITERAL -> return parseStringLiteralExpression()
            TokenType.NUMBER_LITERAL -> return parseNumericLiteralExpression()
            TokenType.REGEX_LITERAL -> return parseRegularExpressionLiteralExpression()
            TokenType.OPERATOR_OPEN_BRACKET -> return parseArrayLiteralExpression()
            TokenType.OPERATOR_OPEN_BRACE -> return parseObjectLiteralExpression()
            else -> throw IllegalStateException("Unexpected token: " + lexer.currentToken)
        }
    }

    private fun parseAwaitExpression(): SingleExpression {
        requireToken(TokenType.KEYWORD_AWAIT)
        return AwaitExpression(parseSingleExpression())
    }

    private fun parseObjectLiteralExpression(): ObjectLiteralExpression {
        val openBraceToken = requireToken(TokenType.OPERATOR_OPEN_BRACE)
        val properties = mutableListOf<PropertyAssignment>()
        if (!isToken(TokenType.OPERATOR_CLOSE_BRACE)) {
            properties.add(parsePropertyAssignment())
        }
        while (isToken(TokenType.OPERATOR_COMMA)) {
            requireToken(TokenType.OPERATOR_COMMA)
            if (!isToken(TokenType.OPERATOR_CLOSE_BRACE)) {
                properties.add(parsePropertyAssignment())
            }
        }
        val closeBraceToken = requireToken(TokenType.OPERATOR_CLOSE_BRACE)
        return ObjectLiteralExpression(openBraceToken, properties, closeBraceToken)
    }

    private fun parsePropertyAssignment(): PropertyAssignment {
        val propertyName = parsePropertyName()
        val colonToken = requireToken(TokenType.OPERATOR_COLON)
        val propertyValue = parseSingleExpression()
        // TODO 解析其他类型的属性赋值
        return PropertyExpressionAssignment(propertyName, colonToken, propertyValue)
    }

    private fun parsePropertyName(): PropertyName {
        return when {
            isToken(TokenType.STRING_LITERAL) -> StringLiteralPropertyName(eatToken())
            isToken(TokenType.NUMBER_LITERAL) -> NumericLiteralPropertyName(eatToken())
            isToken(TokenType.IDENTIFIER,
                TokenType.NULL_LITERAL,
                TokenType.BOOLEAN_LITERAL
            ) -> {
                IdentifierPropertyName(eatToken())
            }
            isToken(TokenType.OPERATOR_OPEN_BRACKET) -> parseComputedPropertyName()
            isKeyword(lexer.currentToken.type) -> IdentifierPropertyName(eatToken())
            else -> throw IllegalStateException("Unexpected token: " + lexer.currentToken)
        }
    }

    private fun parseComputedPropertyName(): ComputedPropertyName {
        val openBracketToken = requireToken(TokenType.OPERATOR_OPEN_BRACKET)
        val expression = parseSingleExpression()
        val closeBracketToken = requireToken(TokenType.OPERATOR_CLOSE_BRACKET)
        return ComputedPropertyName(openBracketToken, expression, closeBracketToken)
    }

    private fun isKeyword(tokenType: TokenType): Boolean {
        when (tokenType) {
            TokenType.KEYWORD_FUNCTION,
            TokenType.KEYWORD_IF,
            TokenType.KEYWORD_ELSE,
            TokenType.KEYWORD_WHILE,
            TokenType.KEYWORD_FOR,
            TokenType.KEYWORD_DO,
            TokenType.KEYWORD_TRY,
            TokenType.KEYWORD_CATCH,
            TokenType.KEYWORD_FINALLY,
            TokenType.KEYWORD_THROW,
            TokenType.KEYWORD_SWITCH,
            TokenType.KEYWORD_CASE,
            TokenType.KEYWORD_DEFAULT,
            TokenType.KEYWORD_BREAK,
            TokenType.KEYWORD_CONTINUE,
            TokenType.KEYWORD_RETURN,
            TokenType.KEYWORD_VAR,
            TokenType.KEYWORD_LET,
            TokenType.KEYWORD_CONST,
            TokenType.KEYWORD_CLASS,
            TokenType.KEYWORD_EXTENDS,
            TokenType.KEYWORD_IMPLEMENTS,
            TokenType.KEYWORD_INTERFACE,
            TokenType.KEYWORD_NEW,
            TokenType.KEYWORD_THIS,
            TokenType.KEYWORD_SUPER,
            TokenType.KEYWORD_YIELD,
            TokenType.KEYWORD_WITH,
            TokenType.KEYWORD_AS,
            TokenType.KEYWORD_IN,
            TokenType.KEYWORD_OF,
            TokenType.KEYWORD_TYPEOF,
            TokenType.KEYWORD_INSTANCEOF,
            TokenType.KEYWORD_IMPORT,
            TokenType.KEYWORD_FROM,
            TokenType.KEYWORD_EXPORT,
            TokenType.KEYWORD_DELETE,
            TokenType.KEYWORD_AWAIT,
            TokenType.KEYWORD_VOID,
            TokenType.KEYWORD_DEBUGGER -> return true
            else -> return false
        }
    }

    private fun parseArrayLiteralExpression(): SingleExpression {
        TODO("Not yet implemented")
    }

    private fun parseRegularExpressionLiteralExpression(): RegExpLiteral {
        TODO("Not yet implemented")
    }

    private fun parseNumericLiteralExpression(): NumericLiteralExpression {
        val token = requireToken(TokenType.NUMBER_LITERAL)
        return NumericLiteralExpression(token)
    }

    private fun parseStringLiteralExpression(): StringLiteralExpression {
        val token = requireToken(TokenType.STRING_LITERAL)
        return StringLiteralExpression(token)
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
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val expressionSequence = parseExpressionSequence()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)
        return ParenthesizedExpression(expressionSequence)
    }

    private fun parseFunctionExpression(): FunctionExpression {
        requireToken(TokenType.KEYWORD_FUNCTION)
        var functionName: Token? = null
        if (lexer.currentToken.type == TokenType.IDENTIFIER) {
            functionName = lexer.currentToken
        }

        // 解析参数列表
        requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val parameters = parseFormalParameterList()
        requireToken(TokenType.OPERATOR_CLOSE_PAREN)

        // 解析函数体
        val body = parseFunctionBody()

        return FunctionExpression(functionName, parameters, body)
    }

    private fun parseNewExpression(): NewExpression {
        val newToken = requireToken(TokenType.KEYWORD_NEW)
        val expression = parseSingleExpression()
        val arguments = if (isToken(TokenType.OPERATOR_OPEN_PAREN)) {
             parseArgumentList()
        } else {
            null
        }
        return NewExpression(newToken, expression, arguments)
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

    private fun parseExpressionSequence(): ExpressionSequence {
        val expressions = mutableListOf<SingleExpression>()
        expressions.add(parseSingleExpression())
        while (lexer.currentToken.type == TokenType.OPERATOR_COMMA) {
            expressions.add(parseSingleExpression())
        }
        return ExpressionSequence(expressions)
    }

    private fun requireToken(vararg tokenTypes: TokenType): Token {
        val currentToken = lexer.currentToken
        if (currentToken.type in tokenTypes) {
            lexer.nextToken()
            return currentToken
        } else {
            throw IllegalStateException("Expected token type ${tokenTypes.contentToString()} but got ${lexer.currentToken.type}")
        }
    }

    private fun optionalToken(vararg tokenTypes: TokenType): Token? {
        if (lexer.currentToken.type in tokenTypes) {
            return eatToken()
        }
        return null
    }

    private fun isToken(vararg tokenType: TokenType): Boolean {
        return lexer.currentToken.type in tokenType
    }

    private fun eatToken(): Token {
        val token = lexer.currentToken
        lexer.nextToken()
        return token
    }

    private fun isStatementLeaderToken(tokenType: TokenType): Boolean {
        when (tokenType) {
            TokenType.OPERATOR_OPEN_BRACE,
            TokenType.KEYWORD_VAR,
            TokenType.KEYWORD_IMPORT,
            TokenType.KEYWORD_EXPORT,
            TokenType.OPERATOR_SEMICOLON,
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
            TokenType.NULL_LITERAL,
            TokenType.BOOLEAN_LITERAL,
            TokenType.NUMBER_LITERAL,
            TokenType.STRING_LITERAL,
            TokenType.OPERATOR_OPEN_BRACKET,
            TokenType.OPERATOR_OPEN_BRACE,
            TokenType.OPERATOR_OPEN_PAREN -> return true
            else -> return false
        }
    }
}