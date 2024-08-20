package com.rabross.clockclock.ui.models

typealias PartClock = Pair<Float, Float>
typealias SixPartClock = Array<PartClock>

sealed class Number(val partClocks: SixPartClock) {

    object Blank : Number(arrayOf(
        BLANK, BLANK,
        BLANK, BLANK,
        BLANK, BLANK))
    object Zero : Number(arrayOf(
        SECOND_QUAD, THIRD_QUAD,
        VERTICAL, VERTICAL,
        FIRST_QUAD, FOURTH_QUAD))
    object One : Number(arrayOf(
        BLANK, BOTTOM_VERTICAL,
        BLANK, VERTICAL,
        BLANK, TOP_VERTICAL))
    object Two : Number(arrayOf(
        END_HORIZONTAL, THIRD_QUAD,
        SECOND_QUAD, FOURTH_QUAD,
        FIRST_QUAD, START_HORIZONTAL))
    object Three : Number(arrayOf(
        END_HORIZONTAL, THIRD_QUAD,
        END_HORIZONTAL, FOURTH_QUAD,
        END_HORIZONTAL, FOURTH_QUAD))
    object Four : Number(arrayOf(
        BOTTOM_VERTICAL, BOTTOM_VERTICAL,
        FIRST_QUAD, VERTICAL,
        BLANK, TOP_VERTICAL))
    object Five : Number(arrayOf(
        SECOND_QUAD, START_HORIZONTAL,
        FIRST_QUAD, THIRD_QUAD,
        END_HORIZONTAL, FOURTH_QUAD))
    object Six : Number(arrayOf(
        SECOND_QUAD, START_HORIZONTAL,
       VERTICAL, THIRD_QUAD,
        FIRST_QUAD, FOURTH_QUAD))
    object Seven : Number(arrayOf(
        END_HORIZONTAL, THIRD_QUAD,
        BLANK, VERTICAL,
        BLANK, TOP_VERTICAL))
    object Eight : Number(arrayOf(
        SECOND_QUAD, THIRD_QUAD,
        FIRST_QUAD, FOURTH_QUAD,
        FIRST_QUAD, FOURTH_QUAD))
    object Nine : Number(arrayOf(
        SECOND_QUAD, THIRD_QUAD,
        FIRST_QUAD, VERTICAL,
        END_HORIZONTAL, FOURTH_QUAD))

    companion object {
        private val BLANK = 225f to 225f
        private val FIRST_QUAD = 0f to 90f
        private val SECOND_QUAD = 90f to 180f
        private val THIRD_QUAD = 180f to 270f
        private val FOURTH_QUAD = 270f to 0f
        private val VERTICAL = 0f to 180f
        private val HORIZONTAL = 90f to 270f
        private val TOP_VERTICAL = 0f to 0f
        private val BOTTOM_VERTICAL = 180f to 180f
        private val START_HORIZONTAL = 270f to 270f
        private val END_HORIZONTAL = 90f to 90f
        private val FIRST_QUAD_INVERSE = 90f to 0f
        private val SECOND_QUAD_INVERSE = 180f to 90f
        private val THIRD_QUAD_INVERSE = 270f to 180f
        private val FOURTH_QUAD_INVERSE = 0f to 270f
        private val VERTICAL_INVERSE = 180f to 0f
        private val HORIZONTAL_INVERSE = 270f to 90f

        fun map(number: Int): Number {
            return when(number){
                1 -> One
                2 -> Two
                3 -> Three
                4 -> Four
                5 -> Five
                6 -> Six
                7 -> Seven
                8 -> Eight
                9 -> Nine
                0 -> Zero
                else -> Blank
            }
        }
    }
}