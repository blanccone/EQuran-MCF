package com.technicaltest.core.ui.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blanccone.mimecloud.databinding.ItemAddToPlaylistBinding
import com.blanccone.mimecloud.model.local.AnimePlaylist

class AddToPlaylistAdapter: RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder>() {

    private val playlist = arrayListOf<AnimePlaylist>()
    private var selectedPlaylist: AnimePlaylist? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddToPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = playlist[position]
        with(holder.itemBinding) {
            tvPlaylistName.text = data.playlistName
            cbPlaylistCheck.setOnCheckedChangeListener { _, isChecked ->
                selectedPlaylist = data
                if (isChecked) {
                    onItemCheckListener?.let { checked ->
                        checked(data)
                    }
                } else {
                    onItemUncheckListener?.let { unchecked ->
                        unchecked(data)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = playlist.size

    fun submitData(dataList: List<AnimePlaylist>) {
        playlist.clear()
        playlist.addAll(dataList)
        notifyItemRangeChanged(0, playlist.size)
    }

    fun addData(data: AnimePlaylist) {
        playlist.add(data)
        notifyItemInserted(playlist.size - 1)
    }

    inner class ViewHolder(
        val itemBinding: ItemAddToPlaylistBinding
    ) : RecyclerView.ViewHolder(itemBinding.root)

    data class AnimePlaylistChecker(val isChecked: Boolean, val data: AnimePlaylist)

    private var onItemCheckListener: ((AnimePlaylist) -> Unit)? = null
    fun setOnCheckListener(listener: ((AnimePlaylist) -> Unit)) {
        onItemCheckListener = listener
    }

    private var onItemUncheckListener: ((AnimePlaylist) -> Unit)? = null
    fun setOnUncheckListener(listener: ((AnimePlaylist) -> Unit)) {
        onItemUncheckListener = listener
    }
}