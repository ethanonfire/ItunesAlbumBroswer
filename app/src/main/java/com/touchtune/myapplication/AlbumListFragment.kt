package com.touchtune.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.touchtune.myapplication.adapters.AlbumRecyclerViewAdapter
import com.touchtune.myapplication.adapters.MarginItemDecoration
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.databinding.DialogInfoBinding
import com.touchtune.myapplication.databinding.FragmentAlbumListBinding
import com.touchtune.myapplication.utilities.setGone
import com.touchtune.myapplication.utilities.setVisible
import com.touchtune.myapplication.viewmodels.AlbumListViewModel
import kotlinx.coroutines.Job

class AlbumListFragment : Fragment() {

    private lateinit var binding: FragmentAlbumListBinding
    private lateinit var albumRecyclerViewAdapter: AlbumRecyclerViewAdapter

    private val viewModel by viewModels<AlbumListViewModel> {
        AlbumListViewModel.MapViewModelFactory((requireContext().applicationContext as MainApplication).appRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumListBinding.inflate(inflater)

        albumRecyclerViewAdapter =
            AlbumRecyclerViewAdapter {
                showInfoDialog(
                    it.primaryGenreName,
                    it.collectionPrice,
                    it.currency,
                    it.copyright
                )
            }

        binding.rvAlbumList.apply {
            addItemDecoration(MarginItemDecoration(30))
            adapter = albumRecyclerViewAdapter
        }

        subscribeUi()

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnClickListener {
            findNavController(this)
                .navigate(R.id.action_albumListFragment_to_recentSearchFragment)
        }
        return binding.root
    }

    private fun subscribeUi() {
        viewModel.albums.observe(viewLifecycleOwner) { uiState ->
            render(uiState, albumRecyclerViewAdapter)
        }
    }

    var job: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController(this).currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("key")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                //clear current recyclerview first
                albumRecyclerViewAdapter.submitList(mutableListOf<Album>())
                job?.cancel()
                job = viewModel.getAlbumById(result!!)
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun render(uiState: UiState, adapter: AlbumRecyclerViewAdapter) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
            }
            is UiState.Success<*> -> {
                //drop the first one, it is only metadata
                val list = (uiState.items as List<Album>).drop(1)
                if (list.isEmpty()) {
                    binding.tvAlbumListEmpty.setVisible()
                } else {
                    binding.tvAlbumListEmpty.setGone()
                }
                adapter.submitList(list)
                binding.progressBar.setGone()
            }
            is UiState.Error -> {
                binding.progressBar.setGone()
                binding.tvAlbumListEmpty.setGone()
            }
        }
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