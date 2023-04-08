package com.touchtune.myapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.runner.RunWith

@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()
@RunWith(AndroidJUnit4::class)
class AlbumListViewModelTest {



//    private val testDispatcher = TestCoroutineDispatcher()
//
//    private var repository  = FakeRepository()

   // @Mock
  //  private lateinit var yearsObserver: Observer<List<Int>?>

//    private lateinit var viewModel: AlbumListViewModel
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
//        viewModel = AlbumListViewModel(repository)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//        testDispatcher.cleanupTestCoroutines()
//    }
//
//    @Test
//    fun `searchAlbumsById_should_update_album_and_years_with_fetched_data`() = runBlocking {
//        val repository  = FakeRepository()
//
//        val viewModel = AlbumListViewModel(repository)
//
////        // Given
//        val artistId = 1L
//        val albums = listOf(
//            Album(artistId = 1L,releaseDate = "2001"),
//            Album(artistId = 1L, releaseDate = "2002")
//        )
//        repository.addAlbums(artistId, albums)
//       // val uiState = UiState.Success(albums, UiState.DataType.ALBUM_SEARCH)
////
////        // When
////        val albumValues = mutableListOf<UiState?>()
//        val job = GlobalScope.launch(Dispatchers.IO) {
//
//            viewModel.searchAlbumsById(1L)
////            viewModel.forTesting().collect{
////                Log.d("ANDROIDTEST", it.releaseDate.toString())
////            }
//
//        }
//        viewModel.albums.take(2)
//        job.cancel()
//      //  Log.d("TESTING", viewModel.album.value.toString())
//
//      //  advanceUntilIdle()
//
//        // Then
////        assert(viewModel.album.value.contains(uiState))
////        assert(viewModel.years.value == listOf(2020, 2021))
//
//       // job.cancel()
//    }

//    @Test
//    fun `setFiltering should filter albums by year`() = testDispatcher.runBlockingTest {
//        // Given
//        val albums = listOf(
//            Album(releaseDate = "2020"),
//            Album(releaseDate = "2021")
//        )
//        val uiState = UiState.Success(albums, UiState.DataType.ALBUM_SEARCH)
//        viewModel._album.value = uiState
//        viewModel.currentAlbums = albums
//
//        // When
//        viewModel.setFiltering(2021)
//
//        // Then
//        val filteredUiState = UiState.Success(listOf(albums[1]), UiState.DataType.ALBUM_SEARCH)
//        assert(viewModel.album.value == filteredUiState)
//    }
}
