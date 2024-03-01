package com.technicaltest.equranmcf.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.equranmcf.databinding.ItemDaftarSurahBinding

class DaftarSurahAdapter: RecyclerView.Adapter<DaftarSurahAdapter.ViewHolder>() {

    private val daftarSurah = arrayListOf<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDaftarSurahBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surah = daftarSurah[position]

        with(holder.binding) {
            tvSurahNomor.text = "${surah.nomor}"
            tvSurahNama.text = surah.nama
            tvSurahNamaLatin.text = surah.namaLatin
            tvSurahArti.text = "${surah.tempatTurun} - ${surah.arti}"

            root.setOnClickListener {
                onItemClickListener?.let { clicked ->
                    clicked(surah)
                }
            }
        }
    }

    override fun getItemCount(): Int = daftarSurah.size

    fun submitData(dataList: List<Data>) {
        daftarSurah.clear()
        daftarSurah.addAll(dataList)
        notifyItemRangeChanged(0, daftarSurah.size)
    }

    private var onItemClickListener: ((Data) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Data) -> Unit)) {
        onItemClickListener = listener
    }

    inner class ViewHolder(
        val binding: ItemDaftarSurahBinding
    ): RecyclerView.ViewHolder(binding.root)
}