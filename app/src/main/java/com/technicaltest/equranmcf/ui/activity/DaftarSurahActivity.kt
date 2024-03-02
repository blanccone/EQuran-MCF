package com.technicaltest.equranmcf.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.core.ui.widget.LoadingDialog
import com.technicaltest.equranmcf.databinding.ActivityDaftarSurahBinding
import com.technicaltest.equranmcf.ui.adapter.DaftarSurahAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DaftarSurahActivity : CoreActivity<ActivityDaftarSurahBinding>() {

    private val viewModel: EQuranViewModel by viewModels()
    private val adapter by lazy { DaftarSurahAdapter() }

    private var daftarSurah = listOf<Data>()

    override fun inflateLayout(inflater: LayoutInflater): ActivityDaftarSurahBinding {
        return ActivityDaftarSurahBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissions()
        setView()
        setEvent()
        setObserves()
    }

    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            } else {
                fetchData()
            }
        } else{
            fetchData()
        }
    }

    private fun fetchData() {
        viewModel.getDaftarSurah()
    }

    private fun setView() {
        with(binding) {
            val formatter = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
            val currentTime = Calendar.getInstance().time
            val currentTimeFormatted = formatter.format(currentTime)
            tvToday.text = "$currentTimeFormatted"
            rvSurah.adapter = adapter
        }
    }

    private fun setEvent() {
        with(binding) {
            etSearch.doAfterTextChanged {
                val filteredSurah = arrayListOf<Data>()
                val text = it.toString()
                if (text.isEmpty()) {
                    filteredSurah.apply {
                        clear()
                        addAll(daftarSurah)
                    }
                } else {
                    if (daftarSurah.isNotEmpty()) {
                        daftarSurah.forEach {
                            if (it.namaLatin.contains(text, true)) {
                                filteredSurah.add(it)
                            }
                        }
                    }
                }
                adapter.filterData(filteredSurah)
            }

            ivBookmarks.setOnClickListener {
                DaftarAyatTersimpanActivity.newInstance(this@DaftarSurahActivity)
            }

            adapter.setOnItemClickListener {
                DetailSurahActivity.newInstance(this@DaftarSurahActivity, it)
            }
        }
    }

    private fun setObserves() {
        viewModel.isLoading.observe(this) {
            it?.let { isLoading ->
                showLoadingDialog(isLoading)
            }
        }

        viewModel.daftarSurah.observe(this) {
            daftarSurah = it.data
            adapter.submitData(it.data)
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
        fun newInstance(context: Context) {
            val intent = Intent(context, DaftarSurahActivity::class.java)
            context.startActivity(intent)
        }
    }
}