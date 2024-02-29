package com.technicaltest.equranmcf.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanccone.mimecloud.service.util.Resource
import com.blanccone.mimecloud.util.ViewUtils.unknownMsg
import com.hadilq.liveevent.LiveEvent
import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import com.technicaltest.core.service.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EQuranViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private var _isLoading = LiveEvent<Boolean?>()
    val isLoading: LiveData<Boolean?> = _isLoading

    private var _error = LiveEvent<String?>()
    val error: LiveData<String?> = _error

    private var _daftarSurah = LiveEvent<DaftarSurah>()
    val daftarSurah: LiveData<DaftarSurah> = _daftarSurah
    fun getDaftarSurah() = viewModelScope.launch {
        repository.getDaftarSurah().collectLatest {
            _isLoading.postValue(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    it.data?.let { data ->
                        _daftarSurah.postValue(data)
                    }
                }
                is Resource.Error -> _error.postValue(it.message ?: unknownMsg())
                else -> Unit
            }
        }
    }

    private var _detailSurah = LiveEvent<DetailSurah>()
    val detailSurah: LiveData<DetailSurah> = _detailSurah
    fun getDetailSurah(nomor: Int) = viewModelScope.launch {
        repository.getDetailSurah(nomor).collectLatest {
            _isLoading.postValue(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    it.data?.let { data ->
                        _detailSurah.postValue(data)
                    }
                }
                is Resource.Error -> _error.postValue(it.message ?: unknownMsg())
                else -> Unit
            }
        }
    }

    private var _detailTafsir = LiveEvent<DetailTafsir>()
    val detailTafsir: LiveData<DetailTafsir> = _detailTafsir
    fun getDetailTafsir(nomor: Int) = viewModelScope.launch {
        repository.getDetailTafsir(nomor).collectLatest {
            _isLoading.postValue(it is Resource.Loading)
            when(it) {
                is Resource.Success -> {
                    it.data?.let { data ->
                        _detailTafsir.postValue(data)
                    }
                }
                is Resource.Error -> _error.postValue(it.message ?: unknownMsg())
                else -> Unit
            }
        }
    }
}