/**
 * expression
 *  : expression ('*' | '/') expression
 *  | expression ('+' | '-') expression
 *  | expression ('||') expression
 *  | expression ('&&') expression
 *  | '!' expression
 *  | '(' expression ')'
 *  | identifier
 *  | literal
 *  ;
 *
 *  expression
 *  : multiplicative_expression
 *  | additive_expression
 *  | logical_or_expression
 *  | logical_and_expression
 *  | unary_expression
 *  | paren_expression
 *  | identifier
 *  | literal
 *  ;
 *
 *  logical_or_expression
 *  : expression ('||') expression
 *
 *  logical_or_expression
 *  : multiplicative_expression ('||') expression
 *  | additive_expression  ('||') expression
 *  | logical_and_expression ('||') expression
 *  | logical_or_expression ('||') expression
 *  | unary_expression ('||') expression
 *  | paren_expression ('||') expression
 *  | identifier ('||') expression
 *  | literal ('||') expression
 *  ;
 *
 */

open class Expression
data class AtomExpression(val token: Token) : Expression()
data class MultiplicativeExpression(val left: Expression, val op: Token, val right: Expression) : Expression()
data class AdditiveExpression(val left: Expression, val op: Token, val right: Expression) : Expression()
data class LogicalAndExpression(val left: Expression, val op: Token, val right: Expression) : Expression()
data class LogicalOrExpression(val left: Expression, val op: Token, val right: Expression) : Expression()
data class UnaryExpression(val op: Token, val expr: Expression) : Expression()
data class ParenExpression(val expr: Expression) : Expression()

data class Token(val text: String, val type: TokenType)

enum class TokenType {
    IDENTIFIER,
    LITERAL,
    OPERATOR,
    OPERATOR_MUL,
    OPERATOR_DIV,
    OPERATOR_ADD,
    OPERATOR_SUB,
    LEFT_PAREN,
    RIGHT_PAREN,
    LOGICAL_NOT,
    LOGICAL_AND,
    LOGICAL_OR,
    EOF
}

open class TokenStream(val tokens: List<Token>) {
    private var index = 0

    fun nextToken() : Token? {
        if (index >= tokens.size - 1) {
            index++
            return null
        }
        return tokens[++index]
    }

    fun currentToken() : Token {
        if (index >= tokens.size){
           return Token("", TokenType.EOF)
        }
        return tokens[index]
    }

    fun hasNext(): Boolean {
        return index < tokens.size - 1
    }

    override fun toString(): String {
        return "index:$index, tokens: $tokens"
    }
}

class ExpressionLexer(private val input: String) {
    fun tokens() : TokenStream {
        val terms = input.trim().split(" ")
        val tokens = terms.map {
            Token(it, getTokenType(it))
        }
        return TokenStream(tokens)
    }

    fun getTokenType(token: String): TokenType {
        if (token.first().isDigit()) {
            return TokenType.LITERAL
        }
        if (token.first().isLetter()){
            return TokenType.IDENTIFIER
        }
        if (token == "(") {
            return TokenType.LEFT_PAREN
        }
        if  (token == ")") {
            return TokenType.RIGHT_PAREN
        }
        if (token == "!") {
            return TokenType.LOGICAL_NOT
        }
        if (token == "/") {
            return TokenType.OPERATOR_DIV
        }
        if (token == "*") {
            return TokenType.OPERATOR_MUL
        }
        if (token == "+") {
            return TokenType.OPERATOR_ADD
        }
        if (token == "-") {
            return TokenType.OPERATOR_SUB
        }
        if (token == "&&") {
            return TokenType.LOGICAL_AND
        }
        if (token == "||") {
            return TokenType.LOGICAL_OR
        }
        throw IllegalArgumentException("Invalid token: $token")
    }
}

class ExpressionParser(private val tokenStream: TokenStream) {
    fun parseExpression() : Expression {
        when (tokenStream.currentToken().type) {
            TokenType.LEFT_PAREN -> {
                return parseParenExpression()
            }
            TokenType.LOGICAL_NOT -> {
                return UnaryExpression(requireToken(TokenType.LOGICAL_NOT), parseExpression())
            }
            TokenType.IDENTIFIER, TokenType.LITERAL -> {
                val expression = AtomExpression(tokenStream.currentToken())
                tokenStream.nextToken()
                return expression
            }
            else -> {
                val leftExp = parseExpression()
                val operator = tokenStream.currentToken()
                tokenStream.nextToken()
                val rightExp = parseExpression()
                when (operator.type) {
                    TokenType.OPERATOR_ADD, TokenType.OPERATOR_SUB -> {
                        return AdditiveExpression(leftExp, operator, rightExp)
                    }

                    TokenType.OPERATOR_DIV, TokenType.OPERATOR_MUL -> {
                        return MultiplicativeExpression(leftExp, operator, rightExp)
                    }

                    TokenType.LOGICAL_AND -> {
                        return LogicalAndExpression(leftExp, operator, rightExp)
                    }

                    TokenType.LOGICAL_OR -> {
                        return LogicalOrExpression(leftExp, operator, rightExp)
                    }

                    else -> {
                        throw IllegalStateException("Unexpected token: ${tokenStream.currentToken()}")
                    }
                }
            }
        }
    }

    fun parseExpression2(): Expression {
        if (!tokenStream.hasNext()) {
            return Expression()
        }
        val leftExp = parseAtomExpression()
        val operator = tokenStream.currentToken()
        tokenStream.nextToken()
        val rightExp = parseExpression2()
        when (operator.type) {
            TokenType.OPERATOR_ADD, TokenType.OPERATOR_SUB -> {
                return AdditiveExpression(leftExp, operator, rightExp)
            }

            TokenType.OPERATOR_DIV, TokenType.OPERATOR_MUL -> {
                return MultiplicativeExpression(leftExp, operator, rightExp)
            }

            TokenType.LOGICAL_AND -> {
                return LogicalAndExpression(leftExp, operator, rightExp)
            }

            TokenType.LOGICAL_OR -> {
                return LogicalOrExpression(leftExp, operator, rightExp)
            }

            else -> {
                throw IllegalStateException("Unexpected token: ${tokenStream.currentToken()}")
            }
        }
    }

    fun parseExpression3(): Expression {
        if (!tokenStream.hasNext()) {
            return Expression()
        }
        return parseAdditiveExpression()

    }

    private fun parseAtomExpression(): Expression {
        when (tokenStream.currentToken().type) {
            TokenType.LEFT_PAREN -> {
                return parseParenExpression()
            }

            TokenType.LOGICAL_NOT -> {
                return UnaryExpression(requireToken(TokenType.LOGICAL_NOT), parseExpression())
            }

            TokenType.IDENTIFIER, TokenType.LITERAL -> {
                val expression = AtomExpression(tokenStream.currentToken())
                tokenStream.nextToken()
                return expression
            }
        }
        throw IllegalStateException("Unexpected token: ${tokenStream.currentToken()}")
    }

    private fun parseLogicalOrExpression(): Expression {
        var leftExp = parseLogicalAndExpression()

        while (tokenStream.currentToken().type == TokenType.LOGICAL_OR) {
            val operator = requireToken(TokenType.LOGICAL_OR)
            val rightExp = parseLogicalAndExpression()
            leftExp = LogicalAndExpression(leftExp, operator, rightExp)
        }
        return leftExp
    }

    private fun parseLogicalAndExpression(): Expression {
        var leftExp = parseAdditiveExpression()

        if (tokenStream.currentToken().type == TokenType.LOGICAL_AND) {
            val operator = requireToken(TokenType.LOGICAL_AND)
            val rightExp = parseAdditiveExpression()
            leftExp = LogicalAndExpression(leftExp, operator, rightExp)
        }
        return leftExp
    }

    private fun parseAdditiveExpression(): Expression {
        var leftExp = parseMultiplicativeExpression()

        if (tokenStream.currentToken().type in listOf(TokenType.OPERATOR_ADD, TokenType.OPERATOR_SUB)) {
            val operator = tokenStream.currentToken()
            tokenStream.nextToken()
            val rightExp = parseMultiplicativeExpression()
            leftExp = AdditiveExpression(leftExp, operator, rightExp)
        }

        return leftExp
    }

    private fun parseMultiplicativeExpression(): Expression {
        var leftExp = parseAtomExpression()

        if (tokenStream.currentToken().type in listOf(TokenType.OPERATOR_MUL, TokenType.OPERATOR_DIV)) {
            val operator = tokenStream.currentToken()
            tokenStream.nextToken()
            val rightExp = parseAtomExpression()
            leftExp = MultiplicativeExpression(leftExp, operator, rightExp)
        }

        return leftExp
    }

    private fun parseParenExpression(): ParenExpression {
        requireToken(TokenType.LEFT_PAREN)
        val expr = parseExpression3()
        requireToken(TokenType.RIGHT_PAREN)
        return ParenExpression(expr)
    }

    fun requireToken(tokenType: TokenType): Token {
        val token = tokenStream.currentToken()

        if (token.type != tokenType) {
            throw IllegalArgumentException("expected $tokenType, actual ${tokenStream.currentToken()}")
        }
        tokenStream.nextToken()
        return token
    }
}

fun main() {
    val expr = "10 + 20 * 30 + ( 40 - 50 || d ) && a || b && ! c"
    val expr2 = "10 + 20 * 30"
    val expr3 = "10 * 20 + 30"
    val expr4 = "10 * 20 + 30 * 40"
    val expr5 = "10 * ( 20 + 30 )"
    val expr6 = "10 * ( 20 + 30 ) + 40"
    val expr7 = "10 * 20 * 30 + 50"
    val lexer = ExpressionLexer(expr7)
    val tokens = lexer.tokens()
    println(tokens)
    val parser = ExpressionParser(tokens)
    val tree = parser.parseExpression3()
    println(tree)
}

