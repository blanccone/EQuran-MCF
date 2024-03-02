package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
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
    private var exoPlayer: ExoPlayer? = null

    private val audioUrlList = arrayListOf<String>()
    private var audioPosition = 0
    private var audioIsPlaying = false
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
                when {
                    !audioIsPlaying -> {
                        audioPosition = 0
                        playExoPlayer(audioUrlList[0])
                        setIconPlay(true)
                        playAllAudio = true
                    }

                    audioIsPlaying && !playAllAudio -> {
                        stopExoPlayer()
                        audioPosition = 0
                        ayatAdapter.setAudio(null)
                        setIconPlay(true)
                        playAllAudio = true
                        playExoPlayer(audioUrlList[0])
                    }

                    else -> {
                        stopExoPlayer()
                        audioPosition = 0
                        setIconPlay(false)
                        playAllAudio = false
                    }
                }
            }

            with(ayatAdapter) {
                setOnItemPlayListener {
                    when {
                        !audioIsPlaying -> {
                            audioPosition = it
                            playExoPlayer(audioUrlList[it])
                        }

                        audioIsPlaying && playAllAudio -> {
                            stopExoPlayer()
                            audioPosition = 0
                            playAllAudio = false
                            setIconPlay(false)
                            playExoPlayer(audioUrlList[it])
                        }

                        audioIsPlaying && audioPosition != it -> {
                            stopExoPlayer()
                            audioPosition = it
                            playExoPlayer(audioUrlList[it])
                        }

                        else -> stopExoPlayer()
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
                    audioUrlList.add(ayat.audio.audio02)
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

    private fun playExoPlayer(audioUrl: String) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(audioUrl)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            addListener(eventListener)
        }
    }

    private val eventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            audioIsPlaying = playbackState == Player.STATE_READY && exoPlayer?.playWhenReady == true
            if (playbackState == Player.STATE_ENDED) {
                stopExoPlayer()
                when {
                    !playAllAudio -> {
                        ayatAdapter.setAudio(null)
                    }

                    playAllAudio && audioPosition < audioUrlList.size - 1 -> {
                        audioPosition++
                        playExoPlayer(audioUrlList[audioPosition])
                    }

                    else -> setIconPlay(false)
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            exoPlayer?.release()
            if (playAllAudio) {
                setIconPlay(false)
            } else {
                ayatAdapter.setAudio(null)
            }
            Toast.makeText(
                this@DetailSurahActivity,
                "Terjadi kesalahan saat memutar audio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stopExoPlayer() {
        exoPlayer?.stop()
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

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }

    companion object {
        private const val MAX_BUFFERING = 10
        private var surah: Data? = null
        fun newInstance(context: Context, surah: Data) {
            this.surah = surah
            val intent = Intent(context, DetailSurahActivity::class.java)
            context.startActivity(intent)
        }
    }
}