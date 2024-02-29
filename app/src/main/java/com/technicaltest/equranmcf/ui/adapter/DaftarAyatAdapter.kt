package com.technicaltest.equranmcf.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.equranmcf.databinding.ItemDaftarAyatBinding

class DaftarAyatAdapter: RecyclerView.Adapter<DaftarAyatAdapter.ViewHolder>() {

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

            ivPlay.setOnClickListener {
                onItemPlayListener?.let { play ->
                    play(ayat.audio.audio01)
                }
            }

            ivBookmark.setOnClickListener {
                onItemBookmarkListener?.let { bookmark ->
                    bookmark(position)
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

    private var onItemPlayListener: ((String) -> Unit)? = null

    fun setOnItemPlayListener(listener: ((String) -> Unit)) {
        onItemPlayListener = listener
    }

    private var onItemBookmarkListener: ((Int) -> Unit)? = null

    fun setOnItemBookmarkListener(listener: ((Int) -> Unit)) {
        onItemBookmarkListener = listener
    }

    inner class ViewHolder(
        val binding: ItemDaftarAyatBinding
    ): RecyclerView.ViewHolder(binding.root)
}