package com.blanccone.mimecloud.ui.widget.dialog.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blanccone.mimecloud.R
import com.blanccone.mimecloud.databinding.DialogCreatePlaylistBinding
import com.blanccone.mimecloud.model.local.AnimePlaylist
import com.blanccone.mimecloud.util.Utils
import com.blanccone.mimecloud.util.Utils.generateUniqueId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreatePlaylistDialog : DialogFragment() {

    private lateinit var binding: DialogCreatePlaylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.RoundedAlertDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            etPlaylistName.doAfterTextChanged {
                if (it.toString().isBlank()) {
                    tvCreate.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.blue_transparent_60)
                    )
                } else {
                    tvCreate.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.blue_primary)
                    )
                }
            }

            tvCreate.setOnClickListener {
                if (etPlaylistName.text.toString().isBlank()) return@setOnClickListener
                val playlistName = etPlaylistName.text.toString()
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentTime = Calendar.getInstance().time
                val currentTimeFormatted = formatter.format(currentTime)
                val animePlaylist = AnimePlaylist(
                    id = "${generateUniqueId(playlistName)}",
                    playlistName = playlistName,
                    createdAt = currentTimeFormatted,
                    updatedAt = currentTimeFormatted
                )
                onCreatePlaylistListener?.let { clicked ->
                    clicked(animePlaylist)
                }
            }

            tvCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private var onCreatePlaylistListener : ((AnimePlaylist) -> Unit)? = null
    fun setOnCreatePlaylistListener(listener: ((AnimePlaylist) -> Unit)) {
        onCreatePlaylistListener = listener
    }

    companion object {
        private const val TAG = "PLAYLIST_DIALOG"
        fun showDialog(
            fragmentManager: FragmentManager
        ): CreatePlaylistDialog {
            val dialog = CreatePlaylistDialog()
            dialog.show(fragmentManager, TAG)
            return dialog
        }
    }
}