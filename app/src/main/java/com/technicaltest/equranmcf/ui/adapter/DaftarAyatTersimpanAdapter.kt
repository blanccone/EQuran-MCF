package com.technicaltest.equranmcf.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.local.AyatData
import com.technicaltest.equranmcf.R
import com.technicaltest.equranmcf.databinding.ItemDaftarAyatTersimpanBinding

@SuppressLint("NotifyDataSetChanged")
class DaftarAyatTersimpanAdapter(
    private var playingAudioPosition: Int? = null
) : RecyclerView.Adapter<DaftarAyatTersimpanAdapter.ViewHolder>() {

    private val daftarSurah = arrayListOf<AyatData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDaftarAyatTersimpanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ayat = daftarSurah[position]

        with(holder.binding) {
            tvNamaLatin.text = "${ayat.namaLatin} : ${ayat.nomorAyat}"
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

            ivDelete.setOnClickListener {
                onItemDeleteListener?.let { Delete ->
                    Delete(ayat)
                }
            }
        }
    }

    override fun getItemCount(): Int = daftarSurah.size

    fun submitData(dataList: List<AyatData>) {
        daftarSurah.clear()
        daftarSurah.addAll(dataList)
        notifyItemRangeChanged(0, daftarSurah.size)
    }

    fun deleteData(ayatData: AyatData) {
        val position = daftarSurah.indexOf(ayatData)
        daftarSurah.remove(ayatData)
        notifyItemRemoved(position)
    }

    fun filterData(dataList: List<AyatData>) {
        daftarSurah.clear()
        daftarSurah.addAll(dataList)
        notifyDataSetChanged()
    }

    fun setAudio(position: Int?) {
        playingAudioPosition = position
        notifyDataSetChanged()
    }

    private var onItemPlayListener: ((Int) -> Unit)? = null

    fun setOnItemPlayListener(listener: ((Int) -> Unit)) {
        onItemPlayListener = listener
    }

    private var onItemDeleteListener: ((AyatData) -> Unit)? = null

    fun setOnItemDeleteListener(listener: ((AyatData) -> Unit)) {
        onItemDeleteListener = listener
    }

    inner class ViewHolder(
        val binding: ItemDaftarAyatTersimpanBinding
    ) : RecyclerView.ViewHolder(binding.root)
}