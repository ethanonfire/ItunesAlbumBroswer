package com.touchtune.myapplication

import com.touchtune.myapplication.utilities.Utils.javaDateTimeConverter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testJavaDateTimeConverter() {
        // Define input and expected output
        val inputDateTimeString = "2023-04-03T10:30:00+05:30"
        val expectedLocalDateTime = LocalDateTime.of(2023, 4, 3, 10, 30)

        // Call the method with the input
        val actualLocalDateTime = javaDateTimeConverter(inputDateTimeString)

        // Assert that the output matches the expected value
        assertEquals(expectedLocalDateTime, actualLocalDateTime)
    }
}