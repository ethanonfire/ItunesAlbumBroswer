package com.touchtune.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.touchtune.myapplication.adapters.AlbumRecyclerViewAdapter
import com.touchtune.myapplication.adapters.MarginItemDecoration
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.databinding.DialogInfoBinding
import com.touchtune.myapplication.databinding.FragmentAlbumListBinding
import com.touchtune.myapplication.viewmodels.AlbumListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AlbumListFragment : Fragment() {

    private lateinit var binding: FragmentAlbumListBinding
    private lateinit var albumRecyclerViewAdapter: AlbumRecyclerViewAdapter

//    private val viewModel by viewModels<AlbumListViewModel> {
//        AlbumListViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
//    }

//    private val viewModel by viewModels<AlbumListViewModel> {
//        AlbumListViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
//    }

    private val viewModel by viewModel<AlbumListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AlbumListFragment", "onCreateView")

//        viewModel =
//            AlbumListViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
//                .create(AlbumListViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_album_list, container,
            false
        )
        albumRecyclerViewAdapter =
            AlbumRecyclerViewAdapter {
                showInfoDialog(
                    it.primaryGenreName,
                    it.collectionPrice,
                    it.currency,
                    it.copyright
                )
            }
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.albumSearchAdapter = albumRecyclerViewAdapter

        binding.rvAlbumList.apply {
            addItemDecoration(MarginItemDecoration(30))
            adapter = albumRecyclerViewAdapter
        }

        //(activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnClickListener {
            findNavController(this)
                .navigate(R.id.action_albumListFragment_to_recentSearchFragment)
        }
        findNavController(this).currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("key")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                //clear current recyclerview first

                albumRecyclerViewAdapter.submitList(mutableListOf<Album>())
                job?.cancel()
                job = lifecycleScope.launch {
                    viewModel.searchAlbumsById(result)
                }
            }

        return binding.root
    }

    var job: Job? = null
    var isFirst = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AlbumListFragment", "onViewCreated")
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        Log.d("AlbumListFragment", "onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.d("AlbumListFragment", "onStart")

        if (isFirst) {
            job = lifecycleScope.launch {
                viewModel.searchAlbumsById(159260351)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AlbumListFragment", "onDestroy")
    }

    private fun showInfoDialog(
        genre: String?,
        price: Double?,
        currency: String?,
        copyright: String?
    ) {
        val binding = DialogInfoBinding.inflate(layoutInflater)
        binding.genreText.text = "Genre Name: $genre"
        binding.priceText.text = "Price: $price"
        binding.currencyText.text = "Currency: $currency"
        binding.copyrightText.text = "Copyright: $copyright"
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(binding.root)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }
}