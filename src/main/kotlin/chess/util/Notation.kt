package chess.util

import chess.model.Move
import chess.model.Position

/** @brief Форматирование клетки в алгебраической нотации (например, e4). */
fun formatSquare(pos: Position): String {
    val file = ('a'.code + pos.col).toChar()
    val rank = 8 - pos.row
    return "$file$rank"
}

/** @brief Форматирование хода в коротком виде (e2e4). */
fun formatMove(m: Move): String = formatSquare(m.from) + formatSquare(m.to)
