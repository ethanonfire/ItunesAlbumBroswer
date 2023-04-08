package com.touchtune.myapplication.utilities

import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.utilities.Utils.extractReleaseYears
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class UtilsTest {
    @Test
    fun testProcessAlbumList_bad_releaseDate_inputs() {
        val input = mutableListOf(
            Album(
                "metadata",
                0, null, null,
                null, null, null, null, null, null
            ),
            Album(
                "album",
                12345, null, null,
                null, null, null, null, null, null
            ),
            Album(
                "album",
                345345, null, null,
                null, null, null, null, "asdfasdfasdf", null
            ),
            Album(
                "album",
                666666, null, null,
                null, null, null, null, "", null
            )

        )
        val expected = mutableListOf(
            Album(
                "album",
                12345, null, null,
                null, null, null, null, "0", null
            ),
            Album(
                "album",
                345345, null, null,
                null, null, null, null, "0", null
            ),
            Album(
                "album",
                666666, null, null,
                null, null, null, null, "0", null
            )

        )

        assertThat(Utils.processAlbumList(input), `is`(expected))
    }

    @Test
    fun testProcessAlbumList_input_of_size_1() {
        val input = mutableListOf(
            Album(
                "metadata",
                0, null, null,
                null, null, null, null, null, null
            )
        )
        val expected = mutableListOf<Album>()

        assertThat(Utils.processAlbumList(input), `is`(expected))

    }

    @Test
    fun testProcessAlbumList_input_of_size_4() {
        val input = mutableListOf(
            Album(
                "metadata",
                0,  null, null,
                null, null, null, null, null, null
            ),
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2023-04-03T10:30:00+05:30", null
            ),
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2022-04-03T10:30:00+05:30", null
            ),
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2021-04-03T10:30:00+05:30", null
            ),
        )
        val expected = mutableListOf<Album>(
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2023", null
            ),
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2022", null
            ),
            Album(
                "album",
                0, null, null,
                null, null, null, null, "2021", null
            )
        )

        assertThat(Utils.processAlbumList(input), `is`(expected))
    }


    @Test
    fun testJavaDateTimeConverter_valid_input() {
        val inputDateTimeString = "2023-04-03T10:30:00+05:30"
        val expectedLocalDateTime = LocalDateTime.of(2023, 4, 3, 10, 30)
        val actualLocalDateTime = Utils.javaDateTimeConverter(inputDateTimeString)
        assertThat(actualLocalDateTime, `is`(expectedLocalDateTime))
    }

    @Test
    fun testJavaDateTimeConverter_null_input() {
        val inputDateTimeString = null
        val actualLocalDateTime = Utils.javaDateTimeConverter(inputDateTimeString)
        assertThat(actualLocalDateTime, `is`(nullValue()))
    }

    @Test
    fun testJavaDateTimeConverter_bad_input() {
        val inputDateTimeString = "2023-044-103T10:3zzz320:00+05:30"
        val actualLocalDateTime = Utils.javaDateTimeConverter(inputDateTimeString)
        assertThat(actualLocalDateTime, `is`(nullValue()))
    }

    @Test
    fun `test extractReleaseYears with Success UiState`() {
        val albums = listOf(
            Album(releaseDate = "1995"),
            Album(releaseDate = "2001"),
            Album(releaseDate = "1995"),
            Album(releaseDate = "1998"),
            Album(releaseDate = "2020"))

        val uiState = UiState.Success(albums,UiState.DataType.ALBUM_SEARCH)
        val expectedYears = listOf(1995, 1998, 2001,2020)

        val result = extractReleaseYears(uiState)

        assertEquals(expectedYears, result)
    }

    @Test
    fun `test extractReleaseYears with Error UiState`() {
        val uiState = UiState.Error("error message") as UiState

        val result = extractReleaseYears(uiState)

        assertNull(result)
    }
}