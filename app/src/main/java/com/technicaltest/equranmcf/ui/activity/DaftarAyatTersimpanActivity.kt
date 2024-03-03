package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.equranmcf.databinding.ActivityDaftarAyatTersimpanBinding
import com.technicaltest.equranmcf.ui.adapter.DaftarAyatTersimpanAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import com.technicaltest.equranmcf.ui.widget.ActionBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaftarAyatTersimpanActivity : CoreActivity<ActivityDaftarAyatTersimpanBinding>() {

    private val viewModel: EQuranViewModel by viewModels()
    private val adapter by lazy { DaftarAyatTersimpanAdapter() }
    private var exoPlayer: ExoPlayer? = null

    private val daftarAyat = arrayListOf<AyatData>()
    private var audioIsPreparing = false
    private var audioIsPlaying = false
    private var audioPosition = 0

    override fun inflateLayout(inflater: LayoutInflater): ActivityDaftarAyatTersimpanBinding {
        return ActivityDaftarAyatTersimpanBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchData()
        setView()
        setEvent()
        setObserves()
    }

    private fun fetchData() {
        viewModel.getAyatList()
    }

    private fun setView() {
        binding.rvAyat.adapter = adapter
    }

    private fun setEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            etSearch.doAfterTextChanged {
                searchData(it.toString())
            }
        }

        with(adapter) {
            setOnItemPlayListener {
                when {
                    !audioIsPlaying && !audioIsPreparing -> {
                        requestToPlayAudio(it)
                    }
                    audioIsPlaying && audioPosition != it -> {
                        stopExoPlayer()
                        requestToPlayAudio(it)
                    }
                    audioIsPreparing && audioPosition != it -> {
                        resetAudio()
                        requestToPlayAudio(it)
                    }
                    audioIsPlaying -> stopAudio()
                    else -> resetAudio()
                }
            }
            setOnItemDeleteListener {
                if (audioIsPlaying) {
                    stopExoPlayer()
                }
                showDeleteAyatBottomSheet(it)
            }
        }
    }

    private fun searchData(text: String) {
        val filteredAyat = arrayListOf<AyatData>()
        if (text.isEmpty()) {
            filteredAyat.apply {
                clear()
                addAll(daftarAyat)
            }
        } else {
            if (daftarAyat.isNotEmpty()) {
                daftarAyat.forEach {
                    if (it.namaLatin.contains(text, true)) {
                        filteredAyat.add(it)
                    }
                }
            }
        }
        adapter.filterData(filteredAyat)
    }

    private fun setObserves() {
        viewModel.ayatList.observe(this) {
            if (it.isNotEmpty()) {
                daftarAyat.clear()
                daftarAyat.addAll(it)
                adapter.submitData(it)
            }
        }

        viewModel.deleteSuccessful.observe(this) {
            it?.let { ayatData ->
                daftarAyat.remove(ayatData)
                adapter.deleteData(ayatData)
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

    private fun requestToPlayAudio(selectedPosition: Int) {
        audioPosition = selectedPosition
        audioIsPreparing = true
        adapter.setPlaybackState(STATE_IDLE)
        viewModel.setPreparingAudioDebounced(true)
    }

    private fun prepareAudio() {
        adapter.setPlaybackState(STATE_PREPARE, audioPosition)
        playExoPlayer(daftarAyat[audioPosition].audio)
    }

    private fun stopAudio() {
        stopExoPlayer()
        audioPosition = 0
        adapter.setPlaybackState(STATE_IDLE)
    }

    private fun resetAudio() {
        if (audioIsPreparing) {
            audioIsPreparing = false
            exoPlayer?.release()
        }
        adapter.setPlaybackState(STATE_IDLE)
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
                adapter.setPlaybackState(STATE_PLAY, audioPosition)
            }
            if (playbackState == Player.STATE_ENDED) {
                stopExoPlayer()
                adapter.setPlaybackState(STATE_IDLE)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            exoPlayer?.release()
            adapter.setPlaybackState(STATE_IDLE)
            Toast.makeText(
                this@DaftarAyatTersimpanActivity,
                "Terjadi kesalahan saat memutar audio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun stopExoPlayer() {
        exoPlayer?.stop()
    }

    private fun showDeleteAyatBottomSheet(ayatData: AyatData) {
        ActionBottomSheetDialog.showDialog(supportFragmentManager, "Hapus").apply {
            setOnItemClickListener {
                viewModel.deleteAyat(ayatData)
            }
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

        fun newInstance(context: Context) {
            val intent = Intent(context, DaftarAyatTersimpanActivity::class.java)
            context.startActivity(intent)
        }
    }
}