package com.technicaltest.equranmcf.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.util.ViewUtils.hide
import com.technicaltest.core.util.ViewUtils.invisible
import com.technicaltest.core.util.ViewUtils.show
import com.technicaltest.equranmcf.R
import com.technicaltest.equranmcf.databinding.ItemDaftarAyatBinding

class DaftarAyatAdapter(
    private var playbackState: Int? = null,
    private var audioPlayingPosition: Int? = null
): RecyclerView.Adapter<DaftarAyatAdapter.ViewHolder>() {

    private var surahNomor = ""
    private val daftarSurah = arrayListOf<Ayat>()

    companion object {
        private const val STATE_PREPARE = 2000
        private const val STATE_IDLE = 3000
    }

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
            if (playbackState != STATE_IDLE) {
                Log.d("WHYWHY", "$playbackState::$position")
                if (audioPlayingPosition == position) {
                    if (playbackState != STATE_PREPARE) {
                        piLoading.hide()
                        ivPlay.apply {
                            show()
                            background = ContextCompat.getDrawable(
                                root.context,
                                R.drawable.ic_pause_item
                            )
                        }
                    } else {
                        piLoading.apply {
                            show()
                            isIndeterminate = true
                        }
                        ivPlay.invisible()
                    }
                }
            } else {
                Log.d("WHYWHY", "$playbackState::$position")
                ivPlay.apply {
                    show()
                    background = ContextCompat.getDrawable(
                        root.context,
                        R.drawable.ic_play_item
                    )
                }
                piLoading.hide()
            }

            ivPlay.setOnClickListener {
                onItemPlayListener?.let { play ->
                    play(position)
                }
            }

            piLoading.setOnClickListener {
                onItemPlayListener?.let { play ->
                    play(position)
                }
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
    fun setPlaybackState(state: Int?, position: Int? = null) {
        playbackState = state
        audioPlayingPosition = position
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