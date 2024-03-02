package com.technicaltest.equranmcf.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.model.remote.detailtafsir.Data
import com.technicaltest.core.model.remote.detailtafsir.Tafsir
import com.technicaltest.equranmcf.databinding.ItemDaftarTafsirBinding

class DaftarTafsirAdapter: RecyclerView.Adapter<DaftarTafsirAdapter.ViewHolder>() {

    private val daftarTafsir = arrayListOf<Tafsir>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDaftarTafsirBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tafsir = daftarTafsir[position]

        with(holder.binding) {
            tvSurahAyatNomor.text = "Ayat ${tafsir.ayat}"
            tvTafsir.text = tafsir.teks
        }
    }

    override fun getItemCount(): Int = daftarTafsir.size

    fun submitData(dataList: List<Tafsir>) {
        daftarTafsir.clear()
        daftarTafsir.addAll(dataList)
        notifyItemRangeChanged(0, daftarTafsir.size)
    }

    inner class ViewHolder(
        val binding: ItemDaftarTafsirBinding
    ): RecyclerView.ViewHolder(binding.root)
}