package com.rabross.clockclock.ui.models

typealias PartClock = Pair<Float, Float>
typealias SixPartClock = Array<PartClock>

sealed class Number(val partClocks: SixPartClock) {

    object Blank : Number(arrayOf(
        BLANK, BLANK,
        BLANK, BLANK,
        BLANK, BLANK))
    object Zero : Number(arrayOf(
        90f to 180f, 270f to 180f,
        0f to 180f, 180f to 0f,
        0f to 90f, 0f to 270f))
    object One : Number(arrayOf(
        BLANK, 180f to 180f,
        BLANK, 0f to 180f,
        BLANK, 0f to 0f))
    object Two : Number(arrayOf(
        90f to 90f, 270f to 180f,
        180f to 90f, 0f to 270f,
        0f to 90f, 270f to 270f))
    object Three : Number(arrayOf(
        90f to 90f, 270f to 180f,
        90f to 90f, 0f to 270f,
        90f to 90f, 0f to 270f))
    object Four : Number(arrayOf(
        180f to 180f, 180f to 180f,
        0f to 90f, 0f to 180f,
        BLANK, 0f to 0f))
    object Five : Number(arrayOf(
        90f to 180f, 270f to 270f,
        0f to 90f, 180f to 270f,
        90f to 90f, 0f to 270f))
    object Six : Number(arrayOf(
        180f to 90f, 270f to 270f,
        0f to 180f, 270f to 180f,
        0f to 90f, 0f to 270f))
    object Seven : Number(arrayOf(
        90f to 90f, 270f to 180f,
        BLANK, 180f to 0f,
        BLANK, 0f to 0f))
    object Eight : Number(arrayOf(
        90f to 180f, 180f to 270f,
        0f to 90f, 270f to 0f,
        0f to 90f, 270f to 0f))
    object Nine : Number(arrayOf(
        90f to 180f, 270f to 180f,
        0f to 90f, 0f to 180f,
        90f to 90f, 270f to 0f))

    companion object {
        private val BLANK = 225f to 225f

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