package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.core.ui.widget.LoadingDialog
import com.technicaltest.equranmcf.R
import com.technicaltest.equranmcf.databinding.ActivityDetailSurahBinding
import com.technicaltest.equranmcf.ui.adapter.DaftarAyatAdapter
import com.technicaltest.equranmcf.ui.adapter.DaftarTafsirAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import com.technicaltest.equranmcf.ui.widget.ActionBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSurahActivity : CoreActivity<ActivityDetailSurahBinding>() {

    private val viewModel: EQuranViewModel by viewModels()
    private val ayatAdapter by lazy { DaftarAyatAdapter() }
    private val tafsirAdapter by lazy { DaftarTafsirAdapter() }
    private val mediaPlayer = MediaPlayer()

    private val audioUrlList = arrayListOf<String>()
    private var audioPosition = 0
    private var playAllAudio = false

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun inflateLayout(inflater: LayoutInflater): ActivityDetailSurahBinding {
        return ActivityDetailSurahBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchDetailSurah()
        setView()
        setEvent()
        setObserves()
    }

    private fun fetchDetailSurah() {
        surah?.let {
            viewModel.getDetailSurah(it.nomor)
        }
    }

    private fun fetchDetailTafsir() {
        surah?.let {
            viewModel.getDetailTafsir(it.nomor)
        }
    }

    private fun setView() {
        with(binding) {
            rvAyat.adapter = ayatAdapter
            rvTafsir.adapter = tafsirAdapter
            tvSurahNamaLatin.text = surah?.namaLatin
            tvSurahArti.text = "${surah?.tempatTurun} - ${surah?.arti}"

            bottomSheet = llBottomSheetTafsir
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    private fun setEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            cvShowTafsir.setOnClickListener {
                fetchDetailTafsir()
            }

            cvPlayAllAudio.setOnClickListener {
                if (!mediaPlayer.isPlaying) {
                    audioPosition = 0
                    playAudio(audioUrlList[0])
                    setIconPlay(true)
                    playAllAudio = true
                } else {
                    stopAudio()
                    setIconPlay(false)
                    playAllAudio = false
                }
            }

            with(ayatAdapter) {
                setOnItemPlayListener {
                    when {
                        !mediaPlayer.isPlaying || audioPosition != it -> {
                            audioPosition = it
                            playAudio(audioUrlList[it])
                        }

                        mediaPlayer.isPlaying && playAllAudio -> {
                            audioPosition = 0
                            playAudio(audioUrlList[it])
                            setIconPlay(false)
                        }

                        else -> stopAudio()
                    }
                }

                setOnItemBookmarkListener {
                    showSimpanAyatBottomSheet(it)
                }
            }
        }
    }

    private fun setObserves() {
        viewModel.isLoading.observe(this) {
            it?.let { isLoading ->
                showLoadingDialog(isLoading)
            }
        }

        viewModel.detailSurah.observe(this) {
            it.data.ayat?.let { ayatList ->
                ayatAdapter.submitData(surah!!.nomor, ayatList)
                audioUrlList.clear()
                ayatList.forEach { ayat ->
                    audioUrlList.add(ayat.audio.audio01)
                }
            }
        }

        viewModel.detailTafsir.observe(this) {
            it.data.tafsir?.let { tafsirList ->
                bottomSheetBehavior.apply {
                    isFitToContents = true
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
                tafsirAdapter.submitData(tafsirList)
            }
        }

        viewModel.insertSuccessful.observe(this) {
            if (it) {
                Toast.makeText(this, "Ayat Tersimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAudio(audioUrl: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(audioUrl)
            prepare()
            start()

            setOnCompletionListener {
                when {
                    !playAllAudio -> {
                        stopAudio()
                        ayatAdapter.setAudio(null)
                    }

                    playAllAudio && audioPosition < audioUrlList.size - 1 -> {
                        audioPosition++
                        playAudio(audioUrlList[audioPosition])
                    }

                    else -> stopAudio()
                }
            }
        }
    }

    private fun stopAudio() {
        mediaPlayer.stop()
    }

    private fun setIconPlay(isPlaying: Boolean) {
        binding.ivPlayAllAudio.background = ContextCompat.getDrawable(
            this@DetailSurahActivity,
            if (isPlaying) R.drawable.ic_pause_circle_24
            else R.drawable.ic_play_circle_24
        )
    }

    private fun showSimpanAyatBottomSheet(ayat: Ayat) {
        ActionBottomSheetDialog.showDialog(supportFragmentManager, "Simpan").apply {
            setOnItemClickListener {
                viewModel.insertAyat(surah!!, ayat)
            }
        }
    }

    private fun showLoadingDialog(isLoading: Boolean) {
        if (isLoading) {
            LoadingDialog.showDialog(supportFragmentManager)
        } else {
            LoadingDialog.dismissDialog(supportFragmentManager)
        }
    }

    companion object {
        private var surah: Data? = null
        fun newInstance(context: Context, surah: Data) {
            this.surah = surah
            val intent = Intent(context, DetailSurahActivity::class.java)
            context.startActivity(intent)
        }
    }
}