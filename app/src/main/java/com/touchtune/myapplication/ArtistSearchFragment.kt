package com.touchtune.myapplication

import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.touchtune.myapplication.adapters.ArtistRecyclerViewAdapter
import com.touchtune.myapplication.adapters.RecentSearchRecyclerViewAdapter
import com.touchtune.myapplication.data.Artist
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.databinding.FragmentRecentSearchesBinding
import com.touchtune.myapplication.utilities.setGone
import com.touchtune.myapplication.utilities.setVisible
import com.touchtune.myapplication.viewmodels.RecentArtistSearchViewModel
import kotlinx.coroutines.Job

class ArtistSearchFragment : Fragment() {

    lateinit var binding: FragmentRecentSearchesBinding
    private lateinit var recentSearchAdapter: RecentSearchRecyclerViewAdapter
    private lateinit var artistRecyclerViewAdapter: ArtistRecyclerViewAdapter

    private val viewModel by viewModels<RecentArtistSearchViewModel> {
        RecentArtistSearchViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecentSearchesBinding.inflate(inflater)

        binding.rvRecentSearches.apply {
            recentSearchAdapter =
                RecentSearchRecyclerViewAdapter {
                    artistRecyclerViewAdapter.submitList(mutableListOf())
                    binding.svSearch.setQuery(it.artistName, false)
                }
            adapter = recentSearchAdapter
        }

        artistRecyclerViewAdapter = ArtistRecyclerViewAdapter {
            NavHostFragment.findNavController(requireParentFragment())
                .previousBackStackEntry?.savedStateHandle?.set(
                    "key",
                    it.artistId
                )

            viewModel.insertRecentSearch(
                RecentArtistSearch(0, it.artistName!!, System.currentTimeMillis())
            )
            NavHostFragment.findNavController(requireParentFragment()).popBackStack()
        }

        //observe recent search list change
        viewModel.recentSearchFlow.observe(viewLifecycleOwner) { it ->
            render(it)
        }
        //observe artist search list change
        viewModel.artistSearch.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(name: String?): Boolean {
                    Log.d("onQueryTextSubmit", "query: " + name)
                    val imm: InputMethodManager =
                        requireContext().getSystemService(InputMethodService.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.svSearch.windowToken, 0)
                    name?.let {
                        if (name.isNotEmpty()) {
                            viewModel.insertRecentSearch(
                                RecentArtistSearch(
                                    0,
                                    name,
                                    System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d("onQueryTextChange", "query: $query")
                    query?.let {
                        artistSearchJob?.cancel()
                        if (it.isNotEmpty()) {
                            binding.tvRecentSearch.setGone()
                            binding.rvRecentSearches.adapter = artistRecyclerViewAdapter
                            artistRecyclerViewAdapter.submitList(mutableListOf())
                            artistSearchJob = viewModel.searchArtistByName(query)
                        } else {
                            binding.tvRecentSearch.setVisible()
                            binding.progressBar.setGone()
                            binding.tvAlbumListEmpty.setGone()
                            //show recent search history recyclerview....
                            binding.rvRecentSearches.adapter = recentSearchAdapter
                        }
                    }
                    return true
                }
            })

        binding.btCancel.setOnClickListener {
            parentFragmentManager.setFragmentResult("FROMCANCEL", Bundle())
            NavHostFragment.findNavController(this).popBackStack()
        }
        return binding.root
    }

    var artistSearchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.svSearch.isIconified = false
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (enter && nextAnim != 0) {
            val animation: Animation = AnimationUtils.loadAnimation(context, nextAnim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    val imm: InputMethodManager =
                        requireContext().getSystemService(InputMethodService.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            return animation
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    @Suppress("UNCHECKED_CAST")
    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                binding.progressBar.bringToFront()
            }
            is UiState.Success<*> -> {
                binding.progressBar.setGone()
                val isDataRecentSearch: Boolean = uiState.items.any {
                    it is RecentArtistSearch
                }
                if (isDataRecentSearch) {
                    val list = uiState.items as MutableList<RecentArtistSearch>
                    recentSearchAdapter.submitList(list.sortedByDescending {
                        it.timeAdded
                    })
                } else {
                    binding.tvRecentSearch.setGone()
                    binding.rvRecentSearches.setVisible()
                    binding.rvRecentSearches.adapter = artistRecyclerViewAdapter
                    val lists = uiState.items as MutableList<Artist>
                    if (lists.isEmpty()) {
                        binding.tvAlbumListEmpty.setVisible()
                    } else {
                        binding.tvAlbumListEmpty.setGone()
                    }
                    artistRecyclerViewAdapter.submitList(lists)
                }
            }
            is UiState.Error -> {
                binding.progressBar.setGone()
                binding.tvAlbumListEmpty.setGone()
            }
        }
    }
}