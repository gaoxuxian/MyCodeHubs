package com.xx.kotlindemo.study

// int[]
fun getIntArray(): IntArray {
    val array = IntArray(2) {
        System.out.println(it.toString())
        4
    }
    return array
}

// String[]
fun getStringArray(): Array<String> {
    return arrayOf("h","e","l","l","o")
}

fun getMutableListIntArray(): MutableList<Int> {
   return mutableListOf(1, 3, 4)
}

fun getArrayListIntArray(): ArrayList<Int> {
    return arrayListOf(9, 8, 7)
}

fun main() {
//    getIntArray().apply {
//        for (i in 0 until size) {
//            System.out.println(this[i].toString())
//        }
//    }

//    getStringArray().apply {
//        for (i in this) {
//            print(i)
//        }
//    }

//    getMutableListIntArray().apply {
//        this[0] = 10
//        for (i in this) {
//            print(i)
//        }
//    }

    val array = getArrayListIntArray().apply {
        this[0] = 0
        for (i in this) {
            print(i)
        }
    }
}