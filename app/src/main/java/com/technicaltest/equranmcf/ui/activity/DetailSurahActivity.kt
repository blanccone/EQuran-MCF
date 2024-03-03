package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.core.ui.widget.LoadingDialog
import com.technicaltest.core.util.ViewUtils.hide
import com.technicaltest.core.util.ViewUtils.invisible
import com.technicaltest.core.util.ViewUtils.show
import com.technicaltest.equranmcf.R
import com.technicaltest.equranmcf.databinding.ActivityDetailSurahBinding
import com.technicaltest.equranmcf.databinding.BottomSheetDialogDaftarTafsirBinding
import com.technicaltest.equranmcf.ui.adapter.DaftarAyatAdapter
import com.technicaltest.equranmcf.ui.adapter.DaftarTafsirAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import com.technicaltest.equranmcf.ui.widget.ActionBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSurahActivity : CoreActivity<ActivityDetailSurahBinding>() {

    private val viewModel: EQuranViewModel by viewModels()
    private val ayatAdapter by lazy { DaftarAyatAdapter(STATE_IDLE) }
    private val tafsirAdapter by lazy { DaftarTafsirAdapter() }
    private var exoPlayer: ExoPlayer? = null

    private val audioUrlList = arrayListOf<String>()
    private var audioPosition = 0
    private var audioIsPreparing = false
    private var audioIsPlaying = false
    private var isSurahAudio = false

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
            tvSurahNamaLatin.text = surah?.namaLatin
            tvSurahArti.text = "${surah?.tempatTurun} - ${surah?.arti}"
            rvAyat.adapter = ayatAdapter
            layoutBottomSheet.rvTafsir.adapter = tafsirAdapter

            bottomSheet = layoutBottomSheet.llBottomSheetTafsir
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        viewDimmedOverlay.hide()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    viewDimmedOverlay.alpha = slideOffset
                }
            })
        }
    }

    private fun setEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnClickListener
                }
                onBackPressedDispatcher.onBackPressed()
            }

            tbDaftarSurah.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    return@setOnClickListener
                }
            }

            viewDimmedOverlay.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return@setOnClickListener
            }

            cvShowTafsir.setOnClickListener {
                fetchDetailTafsir()
            }

            cvSurahAudio.setOnClickListener {
                when {
                    !audioIsPlaying && !audioIsPreparing -> {
                        requestToPlayAudio(
                            selectedPosition = 0,
                            isSurahAudioPreparing = true
                        )
                    }
                    audioIsPlaying && !isSurahAudio -> {
                        stopExoPlayer()
                        requestToPlayAudio(
                            selectedPosition = 0,
                            isSurahAudioPreparing = true
                        )
                    }
                    audioIsPreparing && !isSurahAudio -> {
                        resetAudio()
                        requestToPlayAudio(
                            selectedPosition = 0,
                            isSurahAudioPreparing = true
                        )
                    }
                    audioIsPlaying -> stopAudio()
                    else -> resetAudio()
                }
            }

            with(ayatAdapter) {
                setOnItemPlayListener {
                    when {
                        !audioIsPlaying && !audioIsPreparing -> {
                            requestToPlayAudio(it)
                        }
                        audioIsPlaying && (isSurahAudio || audioPosition != it) -> {
                            stopExoPlayer()
                            requestToPlayAudio(it)
                        }
                        audioIsPreparing && (isSurahAudio || audioPosition != it) -> {
                            resetAudio()
                            requestToPlayAudio(it)
                        }
                        audioIsPlaying -> stopAudio()
                        else -> resetAudio()
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
                    state = BottomSheetBehavior.STATE_EXPANDED
                    binding.viewDimmedOverlay.show()
                }
                tafsirAdapter.submitData(tafsirList)
            }
        }

        viewModel.insertSuccessful.observe(this) {
            if (it) {
                Toast.makeText(this, "Ayat Tersimpan", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isAudioPreparing.observe(this) {
            it?.let { isPreparing ->
                if (isPreparing) {
                    prepareAudio()
                    viewModel.setPreparingAudioDebounced(false)
                }
            }
        }
    }

    private fun requestToPlayAudio(
        selectedPosition: Int,
        isSurahAudioPreparing: Boolean = false
    ) {
        audioPosition = selectedPosition
        audioIsPreparing = true
        isSurahAudio = isSurahAudioPreparing
        setAudioPlaybackState(STATE_IDLE)
        viewModel.setPreparingAudioDebounced(true)
    }

    private fun prepareAudio() {
        setAudioPlaybackState(STATE_PREPARE)
        playExoPlayer(audioUrlList[audioPosition])
    }

    private fun stopAudio() {
        stopExoPlayer()
        setAudioPlaybackState(STATE_IDLE)
        audioIsPreparing = false
        isSurahAudio = false
        audioPosition = 0
    }

    private fun resetAudio() {
        if (audioIsPreparing) {
            audioIsPreparing = false
            exoPlayer?.release()
        }
        setAudioPlaybackState(STATE_IDLE)
        isSurahAudio = false
        audioPosition = 0
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
            audioIsPlaying = playbackState == Player.STATE_READY
            if (playbackState == Player.STATE_READY) {
                audioIsPreparing = false
                setAudioPlaybackState(STATE_PLAY)
            }

            if (playbackState == Player.STATE_ENDED) {
                when {
                    isSurahAudio && audioPosition < audioUrlList.size - 1 -> {
                        audioPosition++
                        playExoPlayer(audioUrlList[audioPosition])
                    }

                    else -> stopAudio()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            exoPlayer?.release()
            setAudioPlaybackState(STATE_IDLE)
            isSurahAudio = false
            audioPosition = 0
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

    private fun setAudioPlaybackState(state: Int) {
        with(binding) {
            if (isSurahAudio) {
                ayatAdapter.setPlaybackState(STATE_IDLE)
                if (state != STATE_PREPARE) {
                    piLoading.hide()
                    ivSurahAudio.apply {
                        show()
                        background = ContextCompat.getDrawable(
                            this@DetailSurahActivity,
                            if (state == STATE_PLAY) R.drawable.ic_pause_circle_24
                            else R.drawable.ic_play_circle_24
                        )
                    }
                } else {
                    ivSurahAudio.invisible()
                    piLoading.apply {
                        show()
                        isIndeterminate = true
                    }
                }
            } else {
                ayatAdapter.setPlaybackState(state, audioPosition)
                ivSurahAudio.apply {
                    show()
                    background = ContextCompat.getDrawable(
                        this@DetailSurahActivity,
                        R.drawable.ic_play_circle_24
                    )
                }
            }
        }
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
        private const val STATE_PLAY = 1000
        private const val STATE_PREPARE = 2000
        private const val STATE_IDLE = 3000
        private var surah: Data? = null
        fun newInstance(context: Context, surah: Data) {
            this.surah = surah
            val intent = Intent(context, DetailSurahActivity::class.java)
            context.startActivity(intent)
        }
    }
}