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
import androidx.navigation.fragment.NavHostFragment
import com.touchtune.myapplication.adapters.ArtistRecyclerViewAdapter
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.databinding.FragmentArtistSearchesBinding
import com.touchtune.myapplication.viewmodels.ArtistSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistSearchFragment : Fragment() {

    val viewModel: ArtistSearchViewModel by viewModel()

    lateinit var binding: FragmentArtistSearchesBinding

    private var listAdapter = ArtistRecyclerViewAdapter {
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

        binding = FragmentArtistSearchesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = listAdapter

        binding.rvArtistSearch.apply {
            adapter = listAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun clearRecyclerViewData(){
        listAdapter.submitList(mutableListOf())
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