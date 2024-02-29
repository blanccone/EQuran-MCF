package com.technicaltest.core.model.remote.daftarsurah

import com.technicaltest.core.model.remote.CoreResponse

data class DaftarSurah(
    val data: List<Data>
): CoreResponse()