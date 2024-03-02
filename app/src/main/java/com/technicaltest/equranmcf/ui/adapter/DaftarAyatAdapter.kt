package com.technicaltest.equranmcf.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.equranmcf.R
import com.technicaltest.equranmcf.databinding.ItemDaftarAyatBinding

class DaftarAyatAdapter(
    private var playingAudioPosition: Int? = null
): RecyclerView.Adapter<DaftarAyatAdapter.ViewHolder>() {

    private var surahNomor = ""
    private val daftarSurah = arrayListOf<Ayat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDaftarAyatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ayat = daftarSurah[position]

        with(holder.binding) {
            tvSurahAyatNomor.text = "$surahNomor:${ayat.nomorAyat}"
            tvArab.text = ayat.teksArab
            tvLatin.text = ayat.teksLatin
            tvIndonesia.text = ayat.teksIndonesia
            ivPlay.background = ContextCompat.getDrawable(
                root.context,
                if (playingAudioPosition == position) R.drawable.ic_pause_item
                else R.drawable.ic_play_item
            )

            ivPlay.setOnClickListener {
                onItemPlayListener?.let { play ->
                    play(position)
                }
                setAudio(position)
            }

            ivBookmark.setOnClickListener {
                onItemBookmarkListener?.let { bookmark ->
                    bookmark(ayat)
                }
            }
        }
    }

    override fun getItemCount(): Int = daftarSurah.size

    fun submitData(nomor: Int, dataList: List<Ayat>) {
        daftarSurah.clear()
        daftarSurah.addAll(dataList)
        surahNomor = nomor.toString()
        notifyItemRangeChanged(0, daftarSurah.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAudio(position: Int?) {
        playingAudioPosition = position
        notifyDataSetChanged()
    }

    private var onItemPlayListener: ((Int) -> Unit)? = null

    fun setOnItemPlayListener(listener: ((Int) -> Unit)) {
        onItemPlayListener = listener
    }

    private var onItemBookmarkListener: ((Ayat) -> Unit)? = null

    fun setOnItemBookmarkListener(listener: ((Ayat) -> Unit)) {
        onItemBookmarkListener = listener
    }

    inner class ViewHolder(
        val binding: ItemDaftarAyatBinding
    ): RecyclerView.ViewHolder(binding.root)
}