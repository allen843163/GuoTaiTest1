package com.allen.guotaitest.ui.home

import android.util.Log
import androidx.collection.toMutableScatterMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allen.guotaitest.api.doOnError
import com.allen.guotaitest.api.doOnSuccess
import com.allen.guotaitest.api.get
import com.allen.guotaitest.model.local.StockInfoUiModel
import com.allen.guotaitest.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toSortedMap

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var refreshJob: Job? = null

    private var _sortState = MutableStateFlow(false)
    var sortState = _sortState.asStateFlow()
    private val _loadingUiState = MutableStateFlow(false)
    val loadingUiState = _loadingUiState.asStateFlow()
    private val _stockInfoUiState = MutableStateFlow<HashMap<String, StockInfoUiModel>>(hashMapOf())
    val stockInfoUiState = combine(_stockInfoUiState, _sortState) { stockFlow, sort ->
        val stockComparator = Comparator<String> { a, b ->
            val numA = a.takeWhile { it.isDigit() }
            val numB = b.takeWhile { it.isDigit() }
            val suffixA = a.drop(numA.length)
            val suffixB = b.drop(numB.length)

            when {
                numA.length != numB.length -> numA.length.compareTo(numB.length)

                numA != numB -> numA.compareTo(numB)

                else -> suffixA.compareTo(suffixB)
            }
        }

        stockFlow.toSortedMap(stockComparator).values.toList().let {
            if(sort) it else it.reversed()
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = listOf<StockInfoUiModel>()
    )

    init {
        refresh()
    }

    fun refresh() {
        if(refreshJob?.isActive?: false) return

        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            _loadingUiState.update { true }

            val stockDayAvgAll = async { stockRepository.getExchangeReportStockDayAvgAll() }

            val bwibbuAll = async { stockRepository.getExchangeReportBWIBBUAll() }

            val stockDayAll = async {
                stockRepository.getExchangeReportStockDayAll()
                    .doOnSuccess {
                        Log.d("Allen", "api success")
                    }.doOnError {
                        Log.d("Allen", "api error = ${it.exception}")
                    }
            }

            val result = HashMap<String, StockInfoUiModel>()

            val bwibbuAllResult = bwibbuAll.await().get()?.forEach {  data ->
                result[data.code] = result
                    .getOrElse(data.code, {StockInfoUiModel(code = data.code)}).copy(
                        code = data.code,
                        name = data.name,
                        date = data.date,
                        dividendYield = data.dividendYield,
                        pBratio = data.pBratio,
                        pEratio = data.pEratio
                    )
            }
            val stockDayAllResult = stockDayAll.await().get()?.forEach {  data ->
                result[data.code] = result
                    .getOrElse(data.code, {StockInfoUiModel(code = data.code)}).copy(
                        code = data.code,
                        name = data.name,
                        date = data.date,
                        change = data.change,
                        closingPrice = data.closingPrice,
                        highestPrice = data.highestPrice,
                        lowestPrice = data.lowestPrice,
                        openingPrice = data.openingPrice,
                        tradeValue = data.tradeValue,
                        tradeVolume = data.tradeVolume,
                        transaction = data.transaction
                    )
            }
            val stockDayAvgAllResult = stockDayAvgAll.await().get()?.forEach {  data ->
                result[data.code] = result
                    .getOrElse(data.code, {StockInfoUiModel(code = data.code)}).copy(
                        code = data.code,
                        name = data.name,
                        date = data.date,
                        monthlyAveragePrice = data.monthlyAveragePrice,
                        closingPrice = data.closingPrice,
                    )
            }

            _stockInfoUiState.update({ result })

            _loadingUiState.update { false }
        }
    }

    fun changeSort(sort: Boolean) {
        _sortState.update { sort }
    }
}
