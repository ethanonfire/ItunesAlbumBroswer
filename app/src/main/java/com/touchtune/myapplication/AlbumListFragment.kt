package com.touchtune.myapplication

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    private val albumListViewModel by viewModel<AlbumListViewModel>()

//    private lateinit var albumListViewModel: AlbumListViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {

            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> false
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AlbumListFragment", "onCreateView")

        albumRecyclerViewAdapter =
            AlbumRecyclerViewAdapter {
                showInfoDialog(
                    it.primaryGenreName,
                    it.collectionPrice,
                    it.currency,
                    it.copyright
                )
            }

        binding = FragmentAlbumListBinding.inflate(inflater, container, false).apply {
            viewModel = albumListViewModel
            Log.d("BindingAdapter", "setViewModel")
            lifecycleOwner = viewLifecycleOwner
            albumSearchAdapter = albumRecyclerViewAdapter
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)

        binding.rvAlbumList.apply {
            addItemDecoration(MarginItemDecoration(30))
            adapter = albumRecyclerViewAdapter
        }

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
                Log.d("AlbumListFragment", "search new albums")
                job = lifecycleScope.launch {
                    albumListViewModel.searchAlbumsById(result)
                }
            }
        setHasOptionsMenu(true)
        job?.cancel()
        job = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if(isFirst) {
                    Log.d("AlbumListFragment", "searchAlbumsById")
                    albumListViewModel.searchAlbumsById(159260351)
                    isFirst = false
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AlbumListFragment", "onViewCreated")

    }

    var job: Job? = null
    var isFirst = true

    override fun onStop() {
        super.onStop()
        job?.cancel()
        Log.d("AlbumListFragment", "onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.d("AlbumListFragment", "onstart")
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

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            albumListViewModel.years.value?.forEach { year ->
                this.menu.add(Menu.NONE, year, Menu.NONE, year.toString())
            }
            setOnMenuItemClickListener {
                albumListViewModel.setFiltering(it.itemId)
                true
            }
            show()
        }
    }
}