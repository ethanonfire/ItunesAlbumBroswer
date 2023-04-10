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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.databinding.FragmentSearchesMainBinding
import com.touchtune.myapplication.viewmodels.RecentSearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragmentMain : Fragment() {

    val viewModel: RecentSearchViewModel by viewModel()

    lateinit var binding: FragmentSearchesMainBinding

    val recentSearchFragment = RecentSearchFragment()
    val artistSearchFragment = ArtistSearchFragment()
    var currentFragment: Fragment = recentSearchFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchesMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(name: String?): Boolean {
                    Log.d("onQuery_submit", "query: $name")

                    val imm: InputMethodManager =
                        requireContext().getSystemService(InputMethodService.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.svSearch.windowToken, 0)

                    if (!name.isNullOrEmpty()) {
                        recentSearchFragment.viewModel.insertRecentSearch(
                            RecentArtistSearch(
                                0,
                                name,
                                System.currentTimeMillis()
                            )
                        )
                    }

                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d("onQuery_change", "query: $query")
                    artistSearchJob?.cancel()
                    query?.let {
                        if (it.isNotEmpty()) {

                            if (currentFragment != artistSearchFragment) {
                                childFragmentManager.commit {
                                    hide(recentSearchFragment)
                                    show(artistSearchFragment)
                                    currentFragment = artistSearchFragment
                                }
                            }
                            artistSearchFragment.clearRecyclerViewData()
                            artistSearchJob = lifecycleScope.launch {
                                artistSearchFragment.binding.viewModel?.searchArtistByName(query)
                            }
                        } else {
                            if (currentFragment != recentSearchFragment) {
                                childFragmentManager.commit {
                                    hide(artistSearchFragment)
                                    show(recentSearchFragment)
                                    currentFragment = recentSearchFragment
                                }
                            }
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

        childFragmentManager.commit {

            add(R.id.child_fragment_container, recentSearchFragment)
            add(R.id.child_fragment_container, artistSearchFragment)
            hide(artistSearchFragment)
            currentFragment = recentSearchFragment
        }
        lifecycleScope.launch {
            delay(2000)
            Log.d("SearchFragmentMain", childFragmentManager.fragments.toString())

        }
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

    fun printFragmentBackStack() {
        val fragmentManager: FragmentManager = getParentFragmentManager()
        val count: Int = fragmentManager.getBackStackEntryCount()
        Log.d("BackStack", "Current Fragment Back Stack count: $count")
        for (i in 0 until count) {
            val entry: FragmentManager.BackStackEntry = fragmentManager.getBackStackEntryAt(i)
            Log.d("BackStack", "BackStackEntry " + i + ": " + entry.getName())
        }
    }
}