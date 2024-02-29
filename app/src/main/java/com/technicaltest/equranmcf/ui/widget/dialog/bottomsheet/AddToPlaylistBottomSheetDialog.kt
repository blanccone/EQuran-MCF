package com.blanccone.mimecloud.ui.widget.dialog.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.blanccone.mimecloud.R
import com.blanccone.mimecloud.databinding.BottomSheetDialogAddToPlaylistBinding
import com.blanccone.mimecloud.model.remote.response.AnimeData
import com.blanccone.mimecloud.ui.viewmodel.HomeViewModel
import com.blanccone.mimecloud.ui.widget.adapter.AddToPlaylistAdapter
import com.blanccone.mimecloud.ui.widget.dialog.popup.CreatePlaylistDialog
import com.technicaltest.core.ui.widget.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToPlaylistBottomSheetDialog : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: BottomSheetDialogAddToPlaylistBinding
    private val adapter by lazy { AddToPlaylistAdapter() }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogAddToPlaylistBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeightState()
        setRecyclerView()
        setEvent()
        fetchFromLocal()
        setObserves()
    }

    private fun fetchFromLocal() {
        viewModel.getAnimePlaylistNames()
    }

    private fun setObserves() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            it?.let { isLoading ->
                showLoadingDialog(isLoading)
            }
        }

        viewModel.animePlaylistNames.observe(viewLifecycleOwner) {
            adapter.submitData(it)
        }

        viewModel.insertAnimePlaylistNameSuccessful.observe(viewLifecycleOwner) {
            if (it.insertSuccessful) {
                it.newPlaylist?.let { newPlaylist ->
                    adapter.addData(newPlaylist)
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.rvAddToPlaylist.adapter = adapter
    }

    private fun setEvent() {
        with(binding) {
            tvCreatePlaylist.setOnClickListener {
                showCreatePlaylistDialog()
            }

            with(adapter) {
                setOnCheckListener {
                    animeData?.let { data ->
                        viewModel.insertAnimeToPlaylist(data, it.playlistName)
                    }
                }
                setOnUncheckListener {
                    animeData?.let { data ->
                        viewModel.deleteAnimeFromPlaylist(data, it.playlistName)
                    }
                }
            }

            cslFooter.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun showCreatePlaylistDialog() {
        CreatePlaylistDialog.showDialog(childFragmentManager).apply {
            setOnCreatePlaylistListener {
                viewModel.insertAnimePlaylistName(it)
            }
        }
    }

    private fun showLoadingDialog(isLoading: Boolean) {
        with(LoadingDialog) {
            if (isLoading) {
                showDialog(childFragmentManager)
            } else {
                dismissDialog(childFragmentManager)
            }
        }
    }

    private fun setHeightState() {
        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) as FrameLayout
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        private const val TAG = "BOTTOM_SHEET_DIALOG"
        private var animeData: AnimeData? = null

        fun showDialog(
            fragmentManager: FragmentManager,
            animeData: AnimeData
        ) : AddToPlaylistBottomSheetDialog {
            this.animeData = animeData
            val dialog = AddToPlaylistBottomSheetDialog()
            dialog.show(fragmentManager, TAG)
            return dialog
        }
    }
}