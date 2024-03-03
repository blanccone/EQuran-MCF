package com.technicaltest.equranmcf.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technicaltest.core.service.util.Resource
import com.technicaltest.core.util.ViewUtils.unknownMsg
import com.hadilq.liveevent.LiveEvent
import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import com.technicaltest.core.service.repository.Repository
import com.technicaltest.persistence.service.repository.PersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EQuranViewModel @Inject constructor(
    private val repository: Repository,
    private val persistenceRepository: PersistenceRepository
): ViewModel() {

    private val _isLoading = LiveEvent<Boolean?>()
    val isLoading: LiveData<Boolean?> = _isLoading

    private val _error = LiveEvent<String?>()
    val error: LiveData<String?> = _error

    private val debounceDuration = 300L
    private var debounceJob: Job? = null

    private fun setLoadingDebounced(isLoading: Boolean) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(debounceDuration)
            _isLoading.postValue(isLoading)
        }
    }

    private val _isAudioPreparing = LiveEvent<Boolean?>()
    val isAudioPreparing: LiveData<Boolean?> = _isAudioPreparing
    fun setPreparingAudioDebounced(isPreparing: Boolean) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(debounceDuration)
            _isAudioPreparing.postValue(isPreparing)
        }
    }

    private val _daftarSurah = LiveEvent<DaftarSurah>()
    val daftarSurah: LiveData<DaftarSurah> = _daftarSurah
    fun getDaftarSurah() = viewModelScope.launch {
        repository.getDaftarSurah().collectLatest {
            setLoadingDebounced(it is Resource.Loading)
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

    private val _detailSurah = LiveEvent<DetailSurah>()
    val detailSurah: LiveData<DetailSurah> = _detailSurah
    fun getDetailSurah(nomor: Int) = viewModelScope.launch {
        repository.getDetailSurah(nomor).collectLatest {
            setLoadingDebounced(it is Resource.Loading)
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

    private val _detailTafsir = LiveEvent<DetailTafsir>()
    val detailTafsir: LiveData<DetailTafsir> = _detailTafsir
    fun getDetailTafsir(nomor: Int) = viewModelScope.launch {
        repository.getDetailTafsir(nomor).collectLatest {
            setLoadingDebounced(it is Resource.Loading)
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

    private val _insertSuccessful = LiveEvent<Boolean>()
    val insertSuccessful: LiveData<Boolean> = _insertSuccessful
    fun insertAyat(surah: Data, ayat: Ayat) = viewModelScope.launch {
        persistenceRepository.insertAyat(surah, ayat).collectLatest {
            setLoadingDebounced(it is Resource.Loading)
            when(it) {
                is Resource.Success -> _insertSuccessful.postValue(true)
                is Resource.Error -> _error.postValue(it.message ?: unknownMsg())
                else -> Unit
            }
        }
    }

    private val _ayatList = LiveEvent<List<AyatData>>()
    val ayatList: LiveData<List<AyatData>> = _ayatList
    fun getAyatList() = viewModelScope.launch {
        persistenceRepository.getAyatList().collectLatest {
            setLoadingDebounced(it is Resource.Loading)
            when(it) {
                is Resource.Success -> _ayatList.postValue(it.data ?: emptyList())
                is Resource.Error -> {
                    _ayatList.postValue(emptyList())
                    _error.postValue(it.message ?: unknownMsg())
                }
                else -> Unit
            }
        }
    }

    private val _deleteSuccessful = LiveEvent<AyatData?>()
    val deleteSuccessful: LiveData<AyatData?> = _deleteSuccessful
    fun deleteAyat(ayatData: AyatData) = viewModelScope.launch {
        persistenceRepository.deleteAyat(ayatData).collectLatest {
            setLoadingDebounced(it is Resource.Loading)
            when(it) {
                is Resource.Success -> _deleteSuccessful.postValue(ayatData)
                is Resource.Error -> {
                    _deleteSuccessful.postValue(null)
                    _error.postValue(it.message ?: unknownMsg())
                }
                else -> Unit
            }
        }
    }
}