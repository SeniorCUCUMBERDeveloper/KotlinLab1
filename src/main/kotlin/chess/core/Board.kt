package chess.core

import chess.model.*

/**
 * @brief Игровая доска 8×8.
 * @details Хранит расположение фигур и предоставляет базовые операции.
 */
class Board {
    private val grid: Array<Array<Piece?>> = Array(8) { arrayOfNulls<Piece?>(8) }

    /** @return Фигура в позиции или null. */
    fun pieceAt(pos: Position): Piece? = grid[pos.row][pos.col]

    /** @brief Установить фигуру (или null) в позицию. */
    fun setPiece(pos: Position, piece: Piece?) { grid[pos.row][pos.col] = piece }

    /**
     * @brief Переместить фигуру согласно ходу (без проверок).
     * @param move Ход.
     * @note Ответственность за корректность — на вызывающей стороне (Rules).
     */
    fun movePiece(move: Move) {
        val p = pieceAt(move.from)
        setPiece(move.to, p)
        setPiece(move.from, null)
    }

    /** Снять фигуру с клетки. */
    fun removePiece(pos: Position) = setPiece(pos, null)

    /** @brief Начальная расстановка для «пешечной» версии. */
    fun setupInitial() {
        for (c in 0..7) {
            setPiece(Position(6, c), Pawn(Color.WHITE))
            setPiece(Position(1, c), Pawn(Color.BLACK))
        }
    }

    /** @return Количество пешек заданного цвета. */
    fun countPawns(color: Color): Int =
        grid.sumOf { row -> row.count { it is Pawn && it.color == color } }

    /**
     * @brief Все клетки с фигурами указанного цвета.
     * @note Универсально для расширения до классики.
     */
    fun allPiecePositions(color: Color): List<Position> {
        val result = mutableListOf<Position>()
        for (r in 0..7) for (c in 0..7) {
            val p = grid[r][c]
            if (p != null && p.color == color) result.add(Position(r, c))
        }
        return result
    }

    /** @brief Утилита печати текстовой доски. */
    fun printBoard() {
        fun border() = println("  +---+---+---+---+---+---+---+---+")
        for (rankRow in 0..7) {
            val rank = 8 - rankRow
            border()
            val sb = StringBuilder()
            sb.append(rank).append(" |")
            for (c in 0..7) {
                val p = grid[rankRow][c]
                sb.append(' ')
                sb.append(p?.symbol ?: ' ')
                sb.append(" |")
            }
            println(sb.toString())
        }
        border()
        println("    a   b   c   d   e   f   g   h")
    }
}
