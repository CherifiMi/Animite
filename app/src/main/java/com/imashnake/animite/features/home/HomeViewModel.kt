package com.imashnake.animite.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imashnake.animite.data.repos.MediaListRepository
import com.imashnake.animite.dev.ext.nextSeason
import com.imashnake.animite.dev.ext.season
import com.imashnake.animite.type.MediaSort
import com.imashnake.animite.type.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaListRepository: MediaListRepository
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    private var fetchJob: Job? = null
    fun populateMediaLists(mediaType: MediaType) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val trendingMedia = mediaListRepository.fetchMediaList(
                    mediaType = mediaType,
                    page = 0,
                    perPage = 10,
                    sort = listOf(MediaSort.TRENDING_DESC),
                    season = null,
                    seasonYear = null
                )

                val now = LocalDate.now()
                val popularMediaThisSeason = mediaListRepository.fetchMediaList(
                    mediaType = mediaType,
                    page = 0,
                    perPage = 10,
                    sort = listOf(MediaSort.POPULARITY_DESC),
                    season = now.month.season,
                    seasonYear = now.year
                )

                val upcomingMediaNextSeason = mediaListRepository.fetchMediaList(
                    mediaType = mediaType,
                    page = 0,
                    perPage = 10,
                    sort = listOf(MediaSort.POPULARITY_DESC),
                    season = now.month.season.nextSeason(now).first,
                    seasonYear = now.month.season.nextSeason(now).second
                )

                val allTimePopularMedia = mediaListRepository.fetchMediaList(
                    mediaType = mediaType,
                    page = 0,
                    perPage = 10,
                    sort = listOf(MediaSort.POPULARITY_DESC),
                    season = null,
                    seasonYear = null
                )

                uiState = with(uiState) {
                    copy(
                        trendingList = trendingMedia,
                        popularList = popularMediaThisSeason,
                        upcomingList = upcomingMediaNextSeason,
                        allTimePopularList = allTimePopularMedia
                    )
                }
            } catch (ioe: IOException) {
                TODO()
            }
        }
    }
}
