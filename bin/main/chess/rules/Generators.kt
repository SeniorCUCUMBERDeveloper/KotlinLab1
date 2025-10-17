package chess.rules

import chess.core.Board
import chess.model.*
import kotlin.math.abs

/**
 * @brief Контекст генерации ходов.
 * @param board Текущее состояние доски.
 * @param side Сторона, для которой генерируются ходы.
 * @param enPassant Состояние EP (если доступно на этот ход).
 */
data class MoveContext(
    val board: Board,
    val side: Color,
    val enPassant: EnPassant?
)

/**
 * @brief Универсальный интерфейс генератора ходов для любой фигуры.
 * @details Специальная логика фигур (шаблоны ходов) инкапсулируется тут.
 */
interface AnyMoveGenerator {
    /**
     * @brief Сгенерировать псевдолегальные ходы фигуры.
     * @param ctx Контекст (доска, сторона, EP).
     * @param from Клетка фигуры.
     * @param piece Сама фигура.
     * @return Список ходов (без учёта шаха/короля — в нашей пешечной версии короля нет).
     */
    fun generate(ctx: MoveContext, from: Position, piece: Piece): List<Move>
}

/**
 * @brief Генератор ходов для пешки (включая взятие на проходе).
 */
class PawnMoveGenerator : AnyMoveGenerator {
    override fun generate(ctx: MoveContext, from: Position, piece: Piece): List<Move> {
        val pawn = piece as Pawn
        val b = ctx.board
        val side = pawn.color
        val opp = side.opponent()
        val dir = side.dir()
        val startRow = side.startRow()
        val moves = mutableListOf<Move>()

        fun empty(pos: Position) = pos.inBounds() && b.pieceAt(pos) == null
        fun enemy(pos: Position) = pos.inBounds() && (b.pieceAt(pos) as? Piece)?.color == opp

        val f1 = Position(from.row + dir, from.col)
        if (empty(f1)) moves += Move(from, f1)

        val mid = Position(from.row + dir, from.col)
        val f2 = Position(from.row + 2 * dir, from.col)
        if (from.row == startRow && empty(mid) && empty(f2)) moves += Move(from, f2)

        val dl = Position(from.row + dir, from.col - 1)
        val dr = Position(from.row + dir, from.col + 1)
        if (enemy(dl)) moves += Move(from, dl)
        if (enemy(dr)) moves += Move(from, dr)

        val ep = ctx.enPassant
        if (ep != null) {
            if (dl.inBounds() && dl == ep.target &&
                ep.victimColor == opp && ep.victimPos.row == from.row &&
                abs(ep.victimPos.col - from.col) == 1
            ) moves += Move(from, dl)
            if (dr.inBounds() && dr == ep.target &&
                ep.victimColor == opp && ep.victimPos.row == from.row &&
                abs(ep.victimPos.col - from.col) == 1
            ) moves += Move(from, dr)
        }

        return moves
    }
}
