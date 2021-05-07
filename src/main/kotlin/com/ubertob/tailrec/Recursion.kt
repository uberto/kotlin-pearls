package com.ubertob.tailrec

class Recursion {
}

tailrec fun sumAll(acc: Int, x: Int):Int = when {
    x <= 1 -> acc
    else -> sumAll(acc+x, x-1)
}


fun sumAllNTR(acc: Int, x: Int):Int = when {
    x <= 1 -> acc
    else -> sumAllNTR(acc+x, x-1)
}

tailrec fun sumNumbers(nums: List<Int>): Int =  //not a tail rec!
    if (nums.size == 2)
        nums[0] + nums[1]
    else
        nums[0] + sumNumbers(nums.drop(1))


fun fibonacciNR(n: Int): Int {
    var prev1 = 0
    var prev2 = 1
    var new = prev1 + prev2
    for(i in 1..n){
        new = prev1 + prev2
        prev2 = prev1
        prev1 = new
    }
    return new
}

fun fibonacciR(n: Int): Int = when(n){
    1 -> 1
    2 -> 1
    else -> fibonacciR(n-1) + fibonacciR(n-2)
}

fun fibonacci(n: Int): Int = fibTail(n, 0, 1)

tailrec fun fibTail(n: Int, prev2: Int, prev1: Int): Int =
    when(n){
        1 -> prev1
        else -> fibTail(n - 1, prev1, prev1 + prev2)
    }

fun gcdNR(n1: Int, n2: Int): Int {
    var a = n1
    var b = n2
    while (a != b) {
        if (a > b) a = a - b
        else b = b - a
    }
    return a
}

tailrec fun gcd(n1: Int, n2: Int): Int =
    when {
        n1 == n2 -> n1
        n1 > n2 -> gcd(n1 - n2, n2)
        else -> gcd(n1, n2-n1)
    }


fun main(){
    (1..10).joinToString(separator = ",") { fibonacci(it).toString() }.also { println(it) }

    println(gcd(30,20))
//    println(sumNumbers((0 .. 100000).toList()))
//    println(sumAll(0,100000))
//    println(sumAllNTR(0,100000))
}
