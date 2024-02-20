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
            TokenType.KEYWORD_VAR, TokenType.KEYWORD_LET, TokenType.KEYWORD_CONST -> parseVariableStatement()
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
        return VarModifier(requireToken(TokenType.KEYWORD_VAR, TokenType.KEYWORD_LET, TokenType.KEYWORD_CONST))
    }

    private fun parseVariableDeclaration(): VariableDeclaration {
        val variableName = requireToken(TokenType.IDENTIFIER)
        var initializer: SingleExpression? = null
        if (isToken(TokenType.OPERATOR_ASSIGN)) {
            eatToken()
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
        val catchProduction = if (isToken(TokenType.KEYWORD_CATCH)) {
            parseCatchProduction()
        } else {
            null
        }
        val finallyProduction = if (isToken(TokenType.KEYWORD_FINALLY)) {
            parseFinallyProduction()
        } else {
            null
        }
        return TryStatement(tryBlock, catchProduction, finallyProduction)
    }

    private fun parseCatchProduction(): CatchProduction {
        val catchToken = requireToken(TokenType.KEYWORD_CATCH)
        var catchAssignable: Assignable? = null
        if (isToken(TokenType.OPERATOR_OPEN_PAREN)) {
            if (!isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
                catchAssignable = parseAssignable()
            }
        }
        val catchBlock = parseBlock()
        return CatchProduction(catchToken, catchAssignable, catchBlock)
    }

    private fun parseAssignable(): Assignable {
        when (lexer.currentToken.type) {
            TokenType.IDENTIFIER, TokenType.ARRAY_LITERAL, TokenType.OBJECT_LITERAL ->
                return Assignable(eatToken())
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
        if (!isToken(TokenType.KEYWORD_CASE)) {
            caseClauses = parseCaseClauses()
        }
        var defaultClause: DefaultClause? = null
        if (isToken(TokenType.KEYWORD_DEFAULT)) {
            defaultClause = parseDefaultClause()
        }

        var caseClauses2: CaseClauses? = null
        if (!isToken(TokenType.KEYWORD_CASE)) {
            caseClauses2 = parseCaseClauses()
        }

        requireToken(TokenType.OPERATOR_CLOSE_BRACE)
        return CaseBlock(caseClauses, defaultClause, caseClauses2)
    }

    private fun parseCaseClauses(): CaseClauses {
        val caseClauses = mutableListOf<CaseClause>()
        while (isToken(TokenType.KEYWORD_CASE)) {
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
        val label = optionalToken(TokenType.IDENTIFIER)
        return BreakStatement(breakToken, label)
    }

    private fun parseContinueStatement(): ContinueStatement {
        val continueToken = requireToken(TokenType.KEYWORD_CONTINUE)
        var label: Token? = null
        if (isToken(TokenType.IDENTIFIER)) {
            label = eatToken()
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

        val functionBody = parseFunctionBody()

        return FunctionDeclaration(
            asyncToken,
            functionToken,
            asteriskToken,
            functionName,
            openParen,
            parameters,
            closeParen,
            functionBody)
    }

    private fun parseFormalParameterList(): List<Token> {
        // TODO 解析 ECMAScript 6: Rest Parameter
        val parameters = mutableListOf<Token>()
        if (!isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
            parameters.add(requireToken(TokenType.IDENTIFIER))
            while (!isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
                requireToken(TokenType.OPERATOR_COMMA)
                parameters.add(requireToken(TokenType.IDENTIFIER))
            }
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
        val await = isToken(TokenType.KEYWORD_AWAIT)
        if (await) {
            eatToken()
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
                throw ParseException("Unexpected 'await' keyword before ';'")
            }
            return parseForStatement(expressionSequence ?: variableDeclarationList)
        } else {
            if (expressionSequence == null && variableDeclarationList == null) {
                throw ParseException("For-in/of statement requires a single expression or variable declaration list")
            }
            if (expressionSequence != null && !expressionSequence.isSingleExpression()) {
                throw ParseException("For-in/of statement requires a single expression")
            }
            if (lexer.currentToken.type == TokenType.KEYWORD_IN) {
                if (await) {
                    throw ParseException("Unexpected 'await' keyword before keyword 'in'")
                }
                if (expressionSequence != null) {
                    return parseForInStatement(expressionSequence.asSingleExpression())
                } else if (variableDeclarationList != null) {
                    return parseForInStatement(variableDeclarationList)
                } else {
                    throw ParseException("For-in/of statement requires a single expression or variable declaration list")
                }
            } else if (lexer.currentToken.type == TokenType.KEYWORD_OF) {
                if (expressionSequence != null) {
                    return parseForOfStatement(await, expressionSequence.asSingleExpression())
                } else if (variableDeclarationList != null) {
                    return parseForOfStatement(await, variableDeclarationList)
                } else {
                    throw ParseException("For-in/of statement requires a single expression or variable declaration list")
                }
            } else {
                throw ParseException("For-in/of statement requires 'in' or 'of' keyword")
            }
        }
    }

    private fun parseForStatement(initializer: Node?): ForStatement {
        var condition: ExpressionSequence? = null
        if (isToken(TokenType.OPERATOR_SEMICOLON)) {
            eatToken()
        } else {
            condition = parseExpressionSequence()
            requireToken(TokenType.OPERATOR_SEMICOLON)
        }

        var increment: ExpressionSequence? = null
        if (isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
            eatToken()
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
        while (isToken(TokenType.TEMPLATE_STRING_LITERAL)) {
            val template = parseTemplateStringLiteral()
            leftExpression = TemplateStringExpression(leftExpression, template)
        }
        return leftExpression
    }

    private fun parseTemplateStringLiteral(): TemplateStringLiteral {
        val templateString = eatToken().value
        val expressionParts = mutableListOf<Pair<IntRange, SingleExpression>>()
        var fromIndex = 0
        while (true) {
            val expressionStartIndex = templateString.indexOf("$" + "{", fromIndex)
            if (expressionStartIndex == -1) {
                break
            }
            val expressionEndIndex = templateString.indexOf("}", expressionStartIndex + 2)
            if (expressionEndIndex == -1) {
                throw ParseException("Unclosed template string expression")
            }
            val expressionString = templateString.substring(expressionStartIndex + 2, expressionEndIndex)
            val parser = Parser(Lexer(CharStream(expressionString)))
            val expression = parser.parseSingleExpression()
            if (!parser.isToken(TokenType.EOF)) {
                throw ParseException("Expected end of expression, got ${parser.lexer.currentToken}")
            }
            expressionParts.add(Pair(IntRange(expressionStartIndex, expressionEndIndex), expression))
            fromIndex = expressionEndIndex + 1
        }
        return TemplateStringLiteral(templateString, expressionParts)
    }

    private fun parseAssignmentOperatorExpression(): SingleExpression {
        var leftExpression = parseAssignmentExpression()
        while (isToken(TokenType.OPERATOR_ASSIGN)) {
            val operatorToken = eatToken()
            val rightExpression = parseAssignmentExpression()
            leftExpression = AssignmentOperatorExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseAssignmentExpression(): SingleExpression {
        var leftExpression = parseTernaryExpression()
        while (isToken(TokenType.OPERATOR_ASSIGN)) {
            val operatorToken = eatToken()
            val rightExpression = parseTernaryExpression()
            leftExpression = AssignmentExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseTernaryExpression(): SingleExpression {
        var conditionExpression = parseLogicalOrExpression()
        if (isToken(TokenType.OPERATOR_QUESTION_MARK)) {
            val questionToken = requireToken(TokenType.OPERATOR_QUESTION_MARK)
            val leftExpression = parseTernaryExpression()
            val colonToken = requireToken(TokenType.OPERATOR_COLON)
            val rightExpression = parseTernaryExpression()
            conditionExpression =
                TernaryExpression(conditionExpression, questionToken, leftExpression, colonToken, rightExpression)
        }
        return conditionExpression
    }

    private fun parseLogicalOrExpression(): SingleExpression {
        var leftExpression = parseLogicalAndExpression()
        while (isToken(TokenType.OPERATOR_OR)) {
            val operatorToken = eatToken()
            val rightExpression = parseLogicalAndExpression()
            leftExpression = LogicalOrExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseLogicalAndExpression(): SingleExpression {
        var leftExpression = parseBitOrExpression()
        while (isToken(TokenType.OPERATOR_AND)) {
            val operatorToken = eatToken()
            val rightExpression = parseBitOrExpression()
            leftExpression = LogicalAndExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitOrExpression(): SingleExpression {
        var leftExpression = parseBitXorExpression()
        while (isToken(TokenType.OPERATOR_BIT_OR)) {
            val operatorToken = eatToken()
            val rightExpression = parseBitXorExpression()
            leftExpression = BitOrExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitXorExpression(): SingleExpression {
        var leftExpression = parseBitAndExpression()
        while (isToken(TokenType.OPERATOR_BIT_XOR)) {
            val operatorToken = eatToken()
            val rightExpression = parseBitAndExpression()
            leftExpression = BitXorExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseBitAndExpression(): SingleExpression {
        var leftExpression = parseEqualityExpression()
        while (isToken(TokenType.OPERATOR_BIT_AND)) {
            val operatorToken = eatToken()
            val rightExpression = parseEqualityExpression()
            leftExpression = BitAndExpression(leftExpression, operatorToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseEqualityExpression(): SingleExpression {
        var leftExpression = parseInExpression()
        while (isToken(TokenType.OPERATOR_EQUAL,
                TokenType.OPERATOR_NOT_EQUAL,
                TokenType.OPERATOR_IDENTITY_EQUAL,
                TokenType.OPERATOR_IDENTITY_NOT_EQUAL)
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
        while (isToken(TokenType.KEYWORD_IN)) {
            val inToken = eatToken()
            val rightExpression = parseInstanceOfExpression()
            leftExpression = InExpression(leftExpression, inToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseInstanceOfExpression(): SingleExpression {
        var leftExpression = parseRelationalExpression()
        while (isToken(TokenType.KEYWORD_INSTANCEOF)) {
            val inToken = requireToken(TokenType.KEYWORD_INSTANCEOF)
            val rightExpression = parseRelationalExpression()
            leftExpression = InstanceOfExpression(leftExpression, inToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseRelationalExpression(): SingleExpression {
        var leftExpression = parseBitShiftExpression()
        while (isToken(TokenType.OPERATOR_LESS_THAN,
                TokenType.OPERATOR_MORE_THAN,
                TokenType.OPERATOR_LESS_THAN_EQUALS,
                TokenType.OPERATOR_MORE_THAN_EQUALS)
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
        while (isToken(TokenType.OPERATOR_LEFT_SHIFT_ARITHMETIC,
                TokenType.OPERATOR_RIGHT_SHIFT_ARITHMETIC,
                TokenType.OPERATOR_RIGHT_SHIFT_LOGICAL)
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
        while (isToken(TokenType.OPERATOR_NULL_COALESCE)) {
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
        while (isToken(TokenType.OPERATOR_MULTIPLY, TokenType.OPERATOR_DIVIDE, TokenType.OPERATOR_MOD)) {
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
        var leftExpression = parseMemberIndexExpression()
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
                eatToken()
                if (!isToken(TokenType.OPERATOR_CLOSE_PAREN)) {
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
        val ellipse = optionalToken(TokenType.OPERATOR_ELLIPSIS)
        return Argument(ellipse, parseSingleExpression())
    }

    private fun parseMemberDotExpression(): SingleExpression {
        var leftExpression = parseOptionalChainExpression()
        while (isToken(TokenType.OPERATOR_QUESTION_MARK_DOT, TokenType.OPERATOR_DOT)) {
            val dotToken = eatToken()
            val hashtagToken = optionalToken(TokenType.OPERATOR_HASHTAG)
            val identifier = requireToken(identifierName)
            leftExpression = MemberDotExpression(leftExpression, dotToken, hashtagToken, identifier)
        }
        return leftExpression
    }

    private fun parseMemberIndexExpression(): SingleExpression {
        var leftExpression = parseMemberDotExpression()
        while (isToken(TokenType.OPERATOR_QUESTION_MARK_DOT, TokenType.OPERATOR_OPEN_BRACKET)) {
            val questionDotToken = optionalToken(TokenType.OPERATOR_QUESTION_MARK_DOT)
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
            val questionDotToken = eatToken()
            val rightExpression = parseOptionalChainExpression()
            leftExpression = OptionalChainExpression(leftExpression, questionDotToken, rightExpression)
        }
        return leftExpression
    }

    private fun parseAtomSingleExpression(): SingleExpression {
        when (lexer.currentToken.type) {
            TokenType.KEYWORD_FUNCTION -> return parseAnonymousFunctionExpression()
            TokenType.KEYWORD_NEW -> return parseNewExpression()
            TokenType.KEYWORD_DELETE -> return parseDeleteExpression()
            TokenType.KEYWORD_VOID -> return parseVoidExpression()
            TokenType.KEYWORD_TYPEOF -> return parseTypeofExpression()
            TokenType.OPERATOR_PLUS_PLUS -> return parsePreIncrementExpression()
            TokenType.OPERATOR_MINUS_MINUS -> return parsePreDecreaseExpression()
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
            TokenType.TEMPLATE_STRING_LITERAL -> return parseTemplateStringLiteral()
            TokenType.NUMBER_LITERAL -> return parseNumericLiteralExpression()
            TokenType.REGEX_LITERAL -> return parseRegularExpressionLiteralExpression()
            TokenType.OPERATOR_OPEN_BRACKET -> return parseArrayLiteralExpression()
            TokenType.OPERATOR_OPEN_BRACE -> return parseObjectLiteralExpression()
            else -> throw ParseException("Unexpected token: " + lexer.currentToken)
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
            eatToken()
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
            isToken(identifierName) -> IdentifierPropertyName(eatToken())
            isToken(TokenType.STRING_LITERAL) -> StringLiteralPropertyName(eatToken())
            isToken(TokenType.NUMBER_LITERAL) -> NumericLiteralPropertyName(eatToken())
            isToken(TokenType.OPERATOR_OPEN_BRACKET) -> parseComputedPropertyName()
            else -> throw ParseException("Unexpected token: " + lexer.currentToken)
        }
    }

    private fun parseComputedPropertyName(): ComputedPropertyName {
        val openBracketToken = requireToken(TokenType.OPERATOR_OPEN_BRACKET)
        val expression = parseSingleExpression()
        val closeBracketToken = requireToken(TokenType.OPERATOR_CLOSE_BRACKET)
        return ComputedPropertyName(openBracketToken, expression, closeBracketToken)
    }

    private fun parseArrayLiteralExpression(): SingleExpression {
        val openBracketToken = requireToken(TokenType.OPERATOR_OPEN_BRACKET)
        val elements = mutableListOf<ArrayElement>()
        while (!isToken(TokenType.OPERATOR_CLOSE_BRACKET) && !isToken(TokenType.EOF)) {
            if (isToken(TokenType.OPERATOR_COMMA)) {
                // 添加一个空的ArrayElement，表示元素之间的逗号
                elements.add(ArrayElement.Empty)
            } else {
                elements.add(parseArrayElement())
                if (isToken(TokenType.OPERATOR_COMMA)) {
                    eatToken()
                }
            }
        }
        val closeBracketToken = requireToken(TokenType.OPERATOR_CLOSE_BRACKET)
        return ArrayLiteralExpression(openBracketToken, elements, closeBracketToken)
    }

    private fun parseArrayElement(): ArrayElement {
        val ellipsisToken = if (isToken(TokenType.OPERATOR_ELLIPSIS)) {
             requireToken(TokenType.OPERATOR_ELLIPSIS)
        } else {
            null
        }
        val expression = parseSingleExpression()
        return ArrayElement(ellipsisToken, expression)
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
        return BooleanLiteralExpression(requireToken(TokenType.BOOLEAN_LITERAL))
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

    private fun parseAnonymousFunctionExpression(): AnonymousFunctionExpression {
        val asyncToken = optionalToken(TokenType.KEYWORD_ASYNC)
        val functionToken = requireToken(TokenType.KEYWORD_FUNCTION)
        val asteriskToken = optionalToken(TokenType.OPERATOR_MULTIPLY)

        // 解析参数列表
        val openParenToken = requireToken(TokenType.OPERATOR_OPEN_PAREN)
        val parameters = parseFormalParameterList()
        val closeParenToken = requireToken(TokenType.OPERATOR_CLOSE_PAREN)

        // 解析函数体
        val functionBody = parseFunctionBody()

        return AnonymousFunctionExpression(
            asyncToken,
            functionToken,
            asteriskToken,
            openParenToken,
            parameters,
            closeParenToken,
            functionBody
        )
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
        requireToken(TokenType.OPERATOR_PLUS_PLUS)
        val expression = parseSingleExpression()
        return PreIncrementExpression(expression)
    }

    private fun parsePreDecreaseExpression(): PreDecreaseExpression {
        requireToken(TokenType.OPERATOR_MINUS_MINUS)
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

        return ThisExpression(requireToken(TokenType.KEYWORD_THIS))
    }

    private fun parseIdentifierExpression(): IdentifierExpression {
        return IdentifierExpression(requireToken(TokenType.IDENTIFIER))
    }

    private fun parseExpressionSequence(): ExpressionSequence {
        val expressions = mutableListOf<SingleExpression>()
        expressions.add(parseSingleExpression())
        while (isToken(TokenType.OPERATOR_COMMA)) {
            eatToken()
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
            throw ParseException("Expected token type ${tokenTypes.contentToString()} but got ${lexer.currentToken.type}")
        }
    }

    private fun requireToken(filter: (TokenType) -> Boolean ): Token {
        val currentToken = lexer.currentToken
        if (filter(currentToken.type)) {
            lexer.nextToken()
            return currentToken
        } else {
            throw ParseException("Unexpected token type ${lexer.currentToken.type}")
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

    private fun isToken(filter: (TokenType) -> Boolean): Boolean {
        return filter(lexer.currentToken.type)
    }

    private fun eatToken(): Token {
        val token = lexer.currentToken
        lexer.nextToken()
        return token
    }

    private fun isStatementLeaderToken(tokenType: TokenType): Boolean {
        return statementLeaderTokens.contains(tokenType) || isExpressionLeaderToken(tokenType)
    }

    private val statementLeaderTokens = mutableSetOf(
        TokenType.OPERATOR_OPEN_BRACE,
        TokenType.KEYWORD_VAR,
        TokenType.KEYWORD_LET,
        TokenType.KEYWORD_CONST,
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
        TokenType.KEYWORD_DEBUGGER
    )

    private val expressionLeaderTokens = mutableSetOf(
        TokenType.KEYWORD_ASYNC,
        TokenType.KEYWORD_FUNCTION,
        TokenType.KEYWORD_CLASS,
        TokenType.KEYWORD_DELETE,
        TokenType.KEYWORD_VOID,
        TokenType.KEYWORD_TYPEOF,
        TokenType.OPERATOR_PLUS_PLUS,
        TokenType.OPERATOR_MINUS_MINUS,
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
        TokenType.TEMPLATE_STRING_LITERAL,
        TokenType.OPERATOR_OPEN_BRACKET,
        TokenType.OPERATOR_OPEN_BRACE,
        TokenType.OPERATOR_OPEN_PAREN
    )

    private fun isExpressionLeaderToken(tokenType: TokenType): Boolean {
        return expressionLeaderTokens.contains(tokenType)
    }

    private val identifierNameTokens = mutableSetOf(
        TokenType.KEYWORD_FUNCTION,

        // 关键字if
        TokenType.KEYWORD_IF,

        // 关键字while
        TokenType.KEYWORD_WHILE,

        // 关键字return
        TokenType.KEYWORD_RETURN,

        // 关键字var
        TokenType.KEYWORD_VAR,

        // 关键字new
        TokenType.KEYWORD_NEW,

        // 关键字delete
        TokenType.KEYWORD_DELETE,

        // 关键字void
        TokenType.KEYWORD_VOID,

        // 关键字typeof
        TokenType.KEYWORD_TYPEOF,

        // 关键字this
        TokenType.KEYWORD_THIS,

        // 关键字else
        TokenType.KEYWORD_ELSE,

        // 关键字for
        TokenType.KEYWORD_FOR,

        // 关键字do
        TokenType.KEYWORD_DO,

        // 关键字in
        TokenType.KEYWORD_IN,
        TokenType.KEYWORD_CLASS,
        TokenType.KEYWORD_AWAIT,
        TokenType.KEYWORD_IMPORT,
        TokenType.KEYWORD_YIELD,
        TokenType.KEYWORD_SUPER,
        TokenType.KEYWORD_ASYNC,
        TokenType.KEYWORD_OF,
        TokenType.KEYWORD_EXPORT,
        TokenType.KEYWORD_CONTINUE,
        TokenType.KEYWORD_BREAK,
        TokenType.KEYWORD_WITH,
        TokenType.KEYWORD_SWITCH,
        TokenType.KEYWORD_LABELLED,
        TokenType.KEYWORD_THROW,
        TokenType.KEYWORD_TRY,
        TokenType.KEYWORD_DEBUGGER,
        TokenType.KEYWORD_CATCH,
        TokenType.KEYWORD_FINALLY,
        TokenType.KEYWORD_INSTANCEOF,
        TokenType.KEYWORD_CASE,
        TokenType.KEYWORD_DEFAULT,
        TokenType.KEYWORD_AS,
        TokenType.KEYWORD_FROM,
        TokenType.KEYWORD_ENUM,
        TokenType.KEYWORD_EXTENDS,
        TokenType.KEYWORD_CONST,
        TokenType.KEYWORD_IMPLEMENTS,
        TokenType.KEYWORD_LET,
        TokenType.KEYWORD_PRIVATE,
        TokenType.KEYWORD_PUBLIC,
        TokenType.KEYWORD_INTERFACE,
        TokenType.KEYWORD_PACKAGE,
        TokenType.KEYWORD_PROTECTED,
        TokenType.KEYWORD_STATIC,
        TokenType.NULL_LITERAL,
        TokenType.BOOLEAN_LITERAL,
        TokenType.IDENTIFIER,
    )

    private val identifierName: (tokenType: TokenType) -> Boolean = {
        it -> identifierNameTokens.contains(it)
    }
}