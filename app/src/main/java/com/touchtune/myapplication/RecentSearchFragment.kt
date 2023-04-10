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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.touchtune.myapplication.adapters.RecentSearchRecyclerViewAdapter
import com.touchtune.myapplication.databinding.FragmentRecentSearchesBinding
import com.touchtune.myapplication.viewmodels.RecentSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentSearchFragment : Fragment(){

    val viewModel: RecentSearchViewModel by viewModel()

    lateinit var binding: FragmentRecentSearchesBinding
    private var recentSearchAdapter =
        RecentSearchRecyclerViewAdapter {
            Log.d("RecentSearchFragment", "RecentSearchFragment")
            val parentFragment = parentFragment as SearchFragmentMain?
            parentFragment?.binding?.svSearch?.setQuery(it.artistName, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecentSearchesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = recentSearchAdapter
        binding.rvRecentSearches.apply {
            adapter = recentSearchAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // binding.svSearch.isIconified = false
        printFragmentBackStack()
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