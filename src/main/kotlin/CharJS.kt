
private val opcodes = setOf(
    '+', '-', '*', '/', '%',
    '=', '!', '&', '|',
    '^', '~', '?', ':',
    '.', '(', ')', '[', ']', '{', '}',
    ';', ','
)

fun Char.isOpcode(): Boolean {
    return opcodes.contains(this)
}

fun Char.isStringLeader(): Boolean {
    return this == '"' || this == '\''
}