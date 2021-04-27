package com.ninja.android.lib

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//class ExampleUnitTest {
//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
//}


fun main() {
    println(isPalindrome("20200202"))
}

fun isPalindrome(s: String): Boolean {
    if (s == null || s.length == 0) {
        return false
    }
    val length = s.length
   for (i in 0..s.length){
       if(s[i]!= s[length-1-i]){
           return false
       }
   }
    return true;
}