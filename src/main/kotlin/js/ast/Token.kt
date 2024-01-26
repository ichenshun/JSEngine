package js.ast

import kotlin.String


data class Token(val type: TokenType, val value: String)


/**
 * kotlin枚举类型的变量，用于描述Token的类型，这些类型描述了Javascript的单词类型，并为每个成员变量添加注释说明
 */
enum class TokenType {
    // "关键字"
    KEYWORD,

    KEYWORD_FUNCTION,

    // "标识符"
    IDENTIFIER,

    // 字面量
    LITERAL,

    // 数字字面量
    NUMBER_LITERAL,

    // 字符串字面量
    STRING_LITERAL,

    // 布尔字面量
    BOOLEAN_LITERAL,

    // 空字面量
    NULL_LITERAL,

    // 正则表达式字面量
    REGEX_LITERAL,

    // 运算符
    OPERATOR,

    // 分隔符
    SEPARATOR,

    // 注释
    COMMENT,

    // 错误
    ERROR,

    // 空
    NONE,

    // 数值
    NUMBER,

    // 赋值
    ASSIGN,

    // 等号
    EQUAL,

    // 左括号
    OPEN_PAREN,

    // 右括号
    CLOSE_PAREN,

    // 左中括号
    OPEN_BRACKET,

    // 右中括号
    CLOSE_BRACKET,

    // 左大括号
    OPEN_BRACE,

    // 右大括号
    CLOSE_BRACE,

    // 分号
    SEMICOLON,

    // 逗号
    COMMA,

    // 结束符
    EOF,

    // 关键字if
    KEYWORD_IF,

    // 关键字while
    KEYWORD_WHILE,

    // 关键字return
    KEYWORD_RETURN,

    // 关键字var
    KEYWORD_VAR,

    // 关键字new
    KEYWORD_NEW,

    // 关键字delete
    KEYWORD_DELETE,

    // 关键字void
    KEYWORD_VOID,

    // 关键字typeof
    KEYWORD_TYPEOF,

    // 关键字this
    KEYWORD_THIS,

    // ++
    OPERATOR_INCREMENT,

    // --
    OPERATOR_DECREMENT,

    // +
    OPERATOR_PLUS,

    // -
    OPERATOR_MINUS,

    // *
    OPERATOR_MULTIPLY,

    // /
    OPERATOR_DIVIDE,

    // %
    OPERATOR_MODULO,

    // ==
    OPERATOR_EQUAL,

    // !=
    OPERATOR_NOT_EQUAL,

    // >
    OPERATOR_GREATER,

    // <
    OPERATOR_LESS,

    // >=
    OPERATOR_GREATER_EQUAL,

    // ~
    OPERATOR_BIT_NOT,

    // !
    OPERATOR_NOT,

    // 关键字else
    KEYWORD_ELSE,

    // 关键字for
    KEYWORD_FOR,

    // 关键字do
    KEYWORD_DO,

    // 关键字in
    KEYWORD_IN,
    KEYWORD_CLASS,
    KEYWORD_AWAIT,
    KEYWORD_IMPORT,
    KEYWORD_YIELD,
    KEYWORD_SUPER,
    KEYWORD_ASYNC,
    KEYWORD_OF,
    KEYWORD_EXPORT,
    KEYWORD_CONTINUE,
    KEYWORD_BREAK,
    KEYWORD_WITH,
    KEYWORD_SWITCH,
    KEYWORD_LABELLED,
    KEYWORD_THROW,
    KEYWORD_TRY,
    KEYWORD_DEBUGGER,
    KEYWORD_CATCH,
    KEYWORD_FINALLY,
    ARRAY_LITERAL,
    OBJECT_LITERAL,
    COLON,
    KEYWORD_CASE,
    KEYWORD_DEFAULT,
    TEMPLATE_STRING_START,
    ASSIGNMENT_OPERATOR,
    QUESTION_MARK,
    OPERATOR_OR,
    OPERATOR_AND,
    OPERATOR_BIT_OR,
    OPERATOR_BIT_XOR,
    OPERATOR_BIT_AND,
    OPERATOR_IDENTITY_EQUAL,
    OPERATOR_IDENTITY_NOT_EQUAL,
    KEYWORD_INSTANCEOF,
    OPERATOR_LESS_THAN,
    OPERATOR_MORE_THAN,
    OPERATOR_LESS_THAN_EQUALS,
    OPERATOR_MORE_THAN_EQUALS,
    OPERATOR_LEFT_SHIFT_ARITHMETIC,
    OPERATOR_RIGHT_SHIFT_ARITHMETIC,
    OPERATOR_RIGHT_SHIFT_LOGICAL,
    OPERATOR_NULL_COALESCE,
    OPERATOR_MOD,
    OPERATOR_POWER,
    OPERATOR_MINUS_MINUS,
    OPERATOR_PLUS_PLUS,
    ELLIPSIS,
    DOT,
    HASHTAG,
    QUESTION_MARK_DOT,
}