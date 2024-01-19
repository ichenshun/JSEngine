package js.ast

import isOpcode
import isStringLeader


class Lexer(private val stream: CharStream) {
    var currentToken: Token = nextToken()

    fun nextToken(): Token {
        stream.mark()
        val char = stream.nextChar() ?: return Token(TokenType.EOF, "")
        if (char.isLetter()) {
            currentToken = parseIdentifierToken()
        } else if (char.isOpcode()) {
            currentToken = parseOperatorToken()
        } else if (char.isStringLeader()) {
            currentToken = parseStringLiteralToken(char)
        } else if (char.isDigit()) {
            currentToken = parseNumberLiteralToken()
        } else if (char.isWhitespace()) {
            skipWhitespaceChars()
            currentToken = nextToken()
        } else {
            throw IllegalArgumentException("Invalid character: $char")
        }
        return currentToken
    }

    private fun parseNumberLiteralToken(): Token {
        while (stream.nextChar()?.isDigit() == true) {}
        stream.seekBack()
        return Token(TokenType.NUMBER_LITERAL, stream.substring())
    }

    private fun parseIdentifierToken(): Token {
        while (stream.nextChar()?.isLetterOrDigit() == true) {}
        stream.seekBack()
        return Token(TokenType.IDENTIFIER, stream.substring())
    }

    private fun parseOperatorToken(): Token {
        while (stream.nextChar()?.isOpcode() == true) {}
        stream.seekBack()
        return Token(TokenType.OPERATOR, stream.substring())
    }

    private fun parseStringLiteralToken(leadChar: Char): Token {
        while (true) {
            var char = stream.nextChar()
            if (char == leadChar) {
                return Token(TokenType.STRING_LITERAL, stream.substring())
            }
            if (char == null || char == '\n') {
                // 需要返回词法分析错误
            }
            // 需要考虑连接行
            // 需要考虑转义字符
        }
        return Token(TokenType.STRING_LITERAL, stream.substring())
    }

    private fun skipWhitespaceChars() {
        while (stream.nextChar()?.isWhitespace() == true) {}
        stream.seekBack()
    }
}