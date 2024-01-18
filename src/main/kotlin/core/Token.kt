package core


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

    // 加号
    PLUS,

    // 减号
    MINUS,

    // 乘号
    MULTIPLY,

    // 除号
    DIVIDE,

    // 等号
    EQUAL,

    // 左括号
    OPEN_PAREN,

    // 右括号
    CLOSE_PAREN,

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
}