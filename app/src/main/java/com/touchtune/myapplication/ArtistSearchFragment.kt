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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.touchtune.myapplication.adapters.ArtistRecyclerViewAdapter
import com.touchtune.myapplication.adapters.RecentSearchRecyclerViewAdapter
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.databinding.FragmentRecentSearchesBinding
import com.touchtune.myapplication.utilities.setGone
import com.touchtune.myapplication.utilities.setVisible
import com.touchtune.myapplication.viewmodels.ArtistSearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ArtistSearchFragment : Fragment() {

    val viewModel: ArtistSearchViewModel by viewModel()

    lateinit var binding: FragmentRecentSearchesBinding
    private var recentSearchAdapter =
        RecentSearchRecyclerViewAdapter {
            artistRecyclerViewAdapter.submitList(mutableListOf())
            binding.svSearch.setQuery(it.artistName, false)
        }

    private var artistRecyclerViewAdapter = ArtistRecyclerViewAdapter {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        viewModel = ViewModelProvider(requireActivity(),
//            SharedViewModel.MapViewModelFactory(
//                (requireContext().applicationContext as MainApplication).appRepository)).get(SharedViewModel::class.java)

//        viewModel =
//            RecentArtistSearchViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
//                .create(RecentArtistSearchViewModel::class.java)


        binding = FragmentRecentSearchesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.rvRecentSearches.apply {
            adapter = recentSearchAdapter
        }

//        artistRecyclerViewAdapter = ArtistRecyclerViewAdapter {
//            NavHostFragment.findNavController(requireParentFragment())
//                .previousBackStackEntry?.savedStateHandle?.set(
//                    "key",
//                    it.artistId
//                )
//
//            viewModel.insertRecentSearch(
//                RecentArtistSearch(0, it.artistName!!, System.currentTimeMillis())
//            )
//            NavHostFragment.findNavController(requireParentFragment()).popBackStack()
//        }


        binding.artistSearchAdapter = artistRecyclerViewAdapter
        binding.recentSearchAdapter = recentSearchAdapter

        binding.svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(name: String?): Boolean {
                    Log.d("onQueryTextSubmit", "query: $name")
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
                    //  Log.d("onQueryTextChange", "query: $query")
                    Log.d("ArtistSearchFragment", "onQueryTextChange: " + query)
                    artistSearchJob?.cancel()
                    if (query.isNullOrEmpty()) {
                        binding.tvRecentSearch.setVisible()
                        binding.progressBar.setGone()
                        binding.tvAlbumListEmpty.setGone()
                        //show recent search history recyclerview....
                        binding.rvRecentSearches.adapter = recentSearchAdapter
                    } else {
                        binding.tvRecentSearch.setGone()
                        binding.rvRecentSearches.adapter = artistRecyclerViewAdapter
                        artistRecyclerViewAdapter.submitList(mutableListOf())
                        artistSearchJob = lifecycleScope.launch {
                            viewModel.searchArtistByName(query)
                        }
                    }

                    query?.let {
                        artistSearchJob?.cancel()
                        if (it.isNotEmpty()) {
                            binding.tvRecentSearch.setGone()
                            binding.rvRecentSearches.adapter = artistRecyclerViewAdapter
                            artistRecyclerViewAdapter.submitList(mutableListOf())
                            artistSearchJob = lifecycleScope.launch {
                                viewModel.searchArtistByName(query)
                            }

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

}