package chess.app

import chess.core.Board
import chess.model.Color
import chess.model.Player
import chess.rules.MoveResult
import chess.rules.Rules
import chess.util.formatMove
import java.util.Locale

/**
 * @brief Консольное приложение «Pawns-Only Chess» с меню выбора хода.
 * @note Меню строится из генерации допустимых ходов Rules.generateLegalMoves().
 */
fun main() {
    Locale.setDefault(Locale.US)

    val white = Player(name = "White", color = Color.WHITE)
    val black = Player(name = "Black", color = Color.BLACK)

    val board = Board().apply { setupInitial() }
    val rules = Rules()

    println(" Pawns-Only Chess")
    board.printBoard()

    var side = Color.WHITE

    game@ while (true) {
        val legalMoves = rules.generateLegalMoves(board, side)
        if (legalMoves.isEmpty()) {
            println("Stalemate!")
            break
        }

        val currentName = if (side == Color.WHITE) white.name else black.name
        println("$currentName to move — выберите номер хода (или 'exit' для выхода):")
        legalMoves.forEachIndexed { idx, mv ->
            val finishMark = if (mv.to.row == side.lastRow()) " *" else ""
            println("${idx + 1}. ${formatMove(mv)}$finishMark")
        }

        val idx = readChoice(legalMoves.size) ?: return
        val chosenMove = legalMoves[idx]

        when (rules.validateAndApply(board, side, chosenMove).result) {
            MoveResult.OK -> {
                board.printBoard()

                if (chosenMove.to.row == side.lastRow() ||
                    board.countPawns(side.opponent()) == 0
                ) {
                    println("$currentName wins!")
                    break@game
                }
                side = side.opponent()
            }
            else -> println("Что-то пошло не так: ход отклонён.")
        }
    }
}

/** @brief Ввод номера хода с повтором до корректного значения или 'exit'. */
private fun readChoice(max: Int): Int? {
    while (true) {
        print("Ваш выбор: ")
        val line = readLine()?.trim() ?: return null
        if (line.equals("exit", ignoreCase = true)) {
            println("Bye!")
            return null
        }
        val num = line.toIntOrNull()
        if (num != null && num in 1..max) return num - 1
        println("Введите число от 1 до $max или 'exit'.")
    }
}
