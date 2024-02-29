package com.technicaltest.equranmcf.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.core.ui.widget.LoadingDialog
import com.technicaltest.equranmcf.databinding.ActivityDetailSurah2Binding
import com.technicaltest.equranmcf.ui.adapter.DaftarAyatAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSurahActivity : CoreActivity<ActivityDetailSurah2Binding>() {

    private val viewModel: EQuranViewModel by viewModels()

    private val adapter by lazy { DaftarAyatAdapter() }

    override fun inflateLayout(inflater: LayoutInflater): ActivityDetailSurah2Binding {
        return ActivityDetailSurah2Binding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchData()
        setView()
        setEvent()
        setObserves()
    }

    private fun fetchData() {
        viewModel.getDetailSurah(nomor)
    }

    private fun setView() {
        binding.rvAyat.adapter = adapter
    }

    private fun setEvent() {
        with(binding) {
            cvPlayAllAudio.setOnClickListener {

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
            it.data.ayat?.let { daftarAyat ->
                adapter.submitData(nomor, daftarAyat)
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
        private var nomor = 1

        fun newInstance(context: Context, nomor: Int) {
            this.nomor = nomor
            val intent = Intent(context, DetailSurahActivity::class.java)
            context.startActivity(intent)
        }
    }
}