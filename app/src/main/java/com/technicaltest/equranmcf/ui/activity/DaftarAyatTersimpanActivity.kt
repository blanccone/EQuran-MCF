package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
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
    private val mediaPlayer = MediaPlayer()
    private val daftarAyat = arrayListOf<AyatData>()
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
        subscribeOnBackPressed()
    }

    private fun fetchData() {
        viewModel.getAyatList()
    }

    private fun setView() {
        binding.rvAyat.adapter = adapter
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

            etSearch.doAfterTextChanged {
                searchData(it.toString())
            }
        }

        with(adapter) {
            setOnItemPlayListener {
                when {
                    !mediaPlayer.isPlaying -> {
                        audioPosition = it
                        playAudio(daftarAyat[it].audio)
                    }
                    mediaPlayer.isPlaying && audioPosition != it -> {
                        stopAudio()
                        audioPosition = it
                        playAudio(daftarAyat[it].audio)
                    }
                    else -> stopAudio()
                }
            }
            setOnItemDeleteListener {
                if (mediaPlayer.isPlaying) {
                    stopAudio()
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
                adapter.deleteData(ayatData)
            }
        }
    }

    private fun playAudio(audioUrl: String) {
        try {
            mediaPlayer.apply {
                setDataSource(audioUrl)
                prepare()
                start()

                setOnCompletionListener {
                    stopAudio()
                    adapter.setAudio(null)
                }

                setOnErrorListener { _, _, _ ->
                    stopAudio()
                    true
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAudio() {
        mediaPlayer.apply {
            stop()
            reset()
        }
        adapter.setAudio(null)
    }

    private fun showDeleteAyatBottomSheet(ayatData: AyatData) {
        ActionBottomSheetDialog.showDialog(supportFragmentManager, "Hapus").apply {
            setOnItemClickListener {
                viewModel.deleteAyat(ayatData)
            }
        }
    }

    private fun subscribeOnBackPressed() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mediaPlayer.isPlaying) {
                    stopAudio()
                }
            }
        })
    }

    companion object {

        fun newInstance(context: Context) {
            val intent = Intent(context, DaftarAyatTersimpanActivity::class.java)
            context.startActivity(intent)
        }
    }
}