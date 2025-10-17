package chess.rules

import chess.core.Board
import chess.model.*
import chess.util.formatMove
import kotlin.reflect.KClass

/**
 * @brief Результат применения хода.
 */
enum class MoveResult { OK, INVALID_INPUT, INVALID_MOVE }

/** @brief Обёртка результата. */
data class MoveOutcome(val result: MoveResult)

/**
 * @brief Правила игры и «движок» ходов.
 * @details
 *  - Генерация ходов выполняется стратегиями (per-piece) через реестр генераторов.
 *  - Применение хода — тут (обновление EP, снятие фигур и т.п.).
 *  - Для расширения до классики добавляйте генераторы фигур и дополняйте
 *    логику применения (например, рокировка, превращение).
 */
class Rules {

    /** @brief Текущее окно для взятия на проходе (если доступно). */
    var enPassant: EnPassant? = null
        private set

    /** @brief Реестр генераторов ходов по типам фигур. */
    private val generators: MutableMap<KClass<out Piece>, AnyMoveGenerator> = mutableMapOf(
        Pawn::class to PawnMoveGenerator()
    )

    /**
     * @brief Зарегистрировать/заменить генератор для типа фигуры.
     * @param pieceKlass KClass типа фигуры.
     * @param generator Реализация генератора.
     */
    fun registerGenerator(pieceKlass: KClass<out Piece>, generator: AnyMoveGenerator) {
        generators[pieceKlass] = generator
    }

    /** @brief Сбросить состояние EP. */
    fun resetEnPassant() { enPassant = null }

    /**
     * @brief Сгенерировать ВСЕ допустимые ходы для стороны side.
     * @details Использует реестр генераторов; если генератор для фигуры отсутствует — фигура игнорируется.
     */
    fun generateLegalMoves(board: Board, side: Color): List<Move> {
        val ctx = MoveContext(board, side, enPassant)
        val moves = mutableListOf<Move>()
        for (from in board.allPiecePositions(side)) {
            val piece = board.pieceAt(from) ?: continue
            val gen = generators[piece::class] ?: continue
            moves += gen.generate(ctx, from, piece)
        }
        return moves.sortedBy { formatMove(it) }
    }

    /**
     * @brief Проверить и ПРИМЕНИТЬ ход (если допустим); обновляет EP.
     * @note Уникальные кейсы (EP, рокировка, промоушен) обрабатываются тут.
     */
    fun validateAndApply(board: Board, side: Color, move: Move): MoveOutcome {
        val from = move.from
        val to = move.to
        if (!from.inBounds() || !to.inBounds() || (from == to)) return MoveOutcome(MoveResult.INVALID_INPUT)

        val piece = board.pieceAt(from) ?: return MoveOutcome(MoveResult.INVALID_MOVE)
        if (piece.color != side) return MoveOutcome(MoveResult.INVALID_MOVE)

        val legal = generateLegalMoves(board, side)
        if (move !in legal) return MoveOutcome(MoveResult.INVALID_MOVE)

        when (piece) {
            is Pawn -> applyPawnMove(board, side, move)
            else -> {
                board.movePiece(move)
                enPassant = null
            }
        }
        return MoveOutcome(MoveResult.OK)
    }

    /** @brief Есть ли у стороны хотя бы один ход (для пата). */
    fun hasAnyLegalMove(board: Board, side: Color): Boolean =
        generateLegalMoves(board, side).isNotEmpty()


    private fun applyPawnMove(board: Board, side: Color, move: Move) {
        val dir = side.dir()
        val from = move.from
        val to = move.to
        val target = board.pieceAt(to)

        val ep = enPassant
        val isEnPassantCapture =
            target == null && ep != null && ep.target == to && ep.victimColor == side.opponent()

        if (isEnPassantCapture) {
            board.removePiece(ep!!.victimPos) // снимаем «жертву»
        }

        board.movePiece(move)

        val isDoublePush = (to.col == from.col) && (to.row - from.row == 2 * dir)
        enPassant = if (isDoublePush) {
            EnPassant(
                victimPos = to,
                target = Position(to.row - dir, to.col),
                victimColor = side
            )
        } else null
    }
}
