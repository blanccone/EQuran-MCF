package com.technicaltest.equranmcf.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.core.ui.widget.LoadingDialog
import com.technicaltest.equranmcf.databinding.ActivityDaftarSurahBinding
import com.technicaltest.equranmcf.ui.adapter.DaftarSurahAdapter
import com.technicaltest.equranmcf.ui.viewmodel.EQuranViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaftarSurahActivity : CoreActivity<ActivityDaftarSurahBinding>() {

    private val viewModel: EQuranViewModel by viewModels()
    private val adapter by lazy { DaftarSurahAdapter() }

    override fun inflateLayout(inflater: LayoutInflater): ActivityDaftarSurahBinding {
        return ActivityDaftarSurahBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissions()
        fetchData()
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
            }
        }
    }

    private fun fetchData() {
        viewModel.getDaftarSurah()
    }

    private fun setView() {
        with(binding) {
            rvSurah.adapter = adapter
        }
    }

    private fun setEvent() {
        with(binding) {
            etSearch
            ivBookmarks

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