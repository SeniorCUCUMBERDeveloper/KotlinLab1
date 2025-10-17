package chess.model

/**
 * @brief Цвет игрока/фигур.
 * @details Даёт также направление движения пешек и удобные помощники.
 */
enum class Color {
    WHITE, BLACK;

    /** @return Смещение по ряду: -1 для белых, +1 для чёрных. */
    fun dir(): Int = if (this == WHITE) -1 else +1

    /** @return Стартовая линия пешек: 6 для белых, 1 для чёрных. */
    fun startRow(): Int = if (this == WHITE) 6 else 1

    /** @return Последняя линия соперника (финиш для пешек). */
    fun lastRow(): Int = if (this == WHITE) 0 else 7

    /** @return Человеко-читаемое имя цвета. */
    fun pretty(): String = if (this == WHITE) "White" else "Black"

    /** @return Цвет соперника. */
    fun opponent(): Color = if (this == WHITE) BLACK else WHITE
}

/**
 * @brief Игрок.
 * @param name Имя игрока.
 * @param color Цвет игрока.
 */
data class Player(val name: String, val color: Color)

/**
 * @brief Клетка доски в индексации [0..7]×[0..7].
 * @param row Номер ряда (0 — верхняя, rank 8).
 * @param col Номер колонки (0 — file a).
 */
data class Position(val row: Int, val col: Int) {
    /** @return true, если позиция внутри доски. */
    fun inBounds(): Boolean = row in 0..7 && col in 0..7
}

/**
 * @brief Базовый интерфейс фигуры.
 * @note Специальные свойства/поведение реализуются генераторами ходов и логикой применения.
 */
interface Piece {
    /** Цвет фигуры. */
    val color: Color
    /** Символ для печати на текстовой доске. */
    val symbol: Char
}

/**
 * @brief Пешка.
 * @param color Цвет пешки.
 */
class Pawn(override val color: Color) : Piece {
    override val symbol: Char = if (color == Color.WHITE) 'W' else 'B'
}

/**
 * @brief Ход: из клетки from в клетку to.
 */
data class Move(val from: Position, val to: Position)

/**
 * @brief Состояние для взятия на проходе.
 * @param victimPos Позиция пешки, совершившей двойной шаг на прошлом ходу.
 * @param target «Проскоченная» клетка (куда бьёт соперник при EP).
 * @param victimColor Цвет пешки-жертвы (которая ходила на 2).
 */
data class EnPassant(
    val victimPos: Position,
    val target: Position,
    val victimColor: Color
)
