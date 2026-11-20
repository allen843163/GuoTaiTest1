package com.allen.guotaitest.ui.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.allen.guotaitest.api.ApiResult
import com.allen.guotaitest.model.remote.BWIBBUModel
import com.allen.guotaitest.model.remote.StockDayAllModel
import com.allen.guotaitest.model.remote.StockDayAvgAllModel
import com.allen.guotaitest.repository.StockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val stockRepository: StockRepository = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()

    private fun setupRepositorySuccessDefaults() {
        coEvery { stockRepository.getExchangeReportBWIBBUAll() } returns ApiResult.Success(emptyList(), null)
        coEvery { stockRepository.getExchangeReportStockDayAll() } returns ApiResult.Success(emptyList(), null)
        coEvery { stockRepository.getExchangeReportStockDayAvgAll() } returns ApiResult.Success(emptyList(), null)
    }

    @Before
    fun setUp() {
        setupRepositorySuccessDefaults()
    }

    @Test
    fun `init calls refresh and fetches data from repository`() = runTest {
        val bwData = listOf(
            BWIBBUModel.Response(
                code = "2330",
                date = "20231001",
                dividendYield = "2.5",
                name = "TSMC",
                pBratio = "5.0",
                pEratio = "20.0"
            )
        )
        val dayData = listOf(
            StockDayAllModel.Response(
                code = "2330",
                name = "TSMC",
                date = "20231001",
                change = "+5",
                closingPrice = "550",
                highestPrice = "555",
                lowestPrice = "545",
                openingPrice = "548",
                tradeValue = "100000",
                tradeVolume = "20000",
                transaction = "5000"
            )
        )
        val avgData = listOf(
            StockDayAvgAllModel.Response(
                code = "2330",
                name = "TSMC",
                date = "20231001",
                monthlyAveragePrice = "540",
                closingPrice = "550"
            )
        )

        coEvery { stockRepository.getExchangeReportBWIBBUAll() } returns ApiResult.Success(bwData, null)
        coEvery { stockRepository.getExchangeReportStockDayAll() } returns ApiResult.Success(dayData, null)
        coEvery { stockRepository.getExchangeReportStockDayAvgAll() } returns ApiResult.Success(avgData, null)

        val viewModel = HomeViewModel(stockRepository, savedStateHandle)

        viewModel.stockInfoUiState.test {
            val list = awaitItem()
            assertEquals(1, list.size)
            val stock = list.first()
            assertEquals("2330", stock.code)
            assertEquals("TSMC", stock.name)
            assertEquals("2.5", stock.dividendYield)
            assertEquals("5.0", stock.pBratio)
            assertEquals("20.0", stock.pEratio)
            assertEquals("+5", stock.change)
            assertEquals("550", stock.closingPrice)
            assertEquals("555", stock.highestPrice)
            assertEquals("545", stock.lowestPrice)
            assertEquals("548", stock.openingPrice)
            assertEquals("100000", stock.tradeValue)
            assertEquals("20000", stock.tradeVolume)
            assertEquals("5000", stock.transaction)
            assertEquals("540", stock.monthlyAveragePrice)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { stockRepository.getExchangeReportBWIBBUAll() }
        coVerify(exactly = 1) { stockRepository.getExchangeReportStockDayAll() }
        coVerify(exactly = 1) { stockRepository.getExchangeReportStockDayAvgAll() }
    }

    @Test
    fun `changeSort toggles sortState and updates stockInfoUiState sorting`() = runTest {
        val bwData = listOf(
            BWIBBUModel.Response(code = "999", name = "Stock999", date = null, dividendYield = null, pBratio = null, pEratio = null),
            BWIBBUModel.Response(code = "1101", name = "Stock1101", date = null, dividendYield = null, pBratio = null, pEratio = null),
            BWIBBUModel.Response(code = "2330", name = "Stock2330", date = null, dividendYield = null, pBratio = null, pEratio = null),
            BWIBBUModel.Response(code = "2330A", name = "Stock2330A", date = null, dividendYield = null, pBratio = null, pEratio = null),
            BWIBBUModel.Response(code = "10000", name = "Stock10000", date = null, dividendYield = null, pBratio = null, pEratio = null)
        )
        coEvery { stockRepository.getExchangeReportBWIBBUAll() } returns ApiResult.Success(bwData, null)

        val viewModel = HomeViewModel(stockRepository, savedStateHandle)

        viewModel.stockInfoUiState.test {
            val itemsReversed = awaitItem()
            val codesReversed = itemsReversed.map { it.code }
            assertEquals(listOf("10000", "2330A", "2330", "1101", "999"), codesReversed)

            viewModel.changeSort(true)
            val itemsAscending = awaitItem()
            val codesAscending = itemsAscending.map { it.code }
            assertEquals(listOf("999", "1101", "2330", "2330A", "10000"), codesAscending)

            viewModel.changeSort(false)
            val itemsReversed2 = awaitItem()
            assertEquals(listOf("10000", "2330A", "2330", "1101", "999"), itemsReversed2.map { it.code })

            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.sortState.value)
    }

    @Test
    fun `repository error results in empty stockInfoUiState`() = runTest {
        coEvery { stockRepository.getExchangeReportBWIBBUAll() } returns ApiResult.Error(
            code = 500,
            message = "Server error",
            raw = null,
            errorBody = null,
            exception = Exception("Network failure")
        )
        coEvery { stockRepository.getExchangeReportStockDayAll() } returns ApiResult.Error(
            code = 500,
            message = "Server error",
            raw = null,
            errorBody = null,
            exception = Exception("Network failure")
        )
        coEvery { stockRepository.getExchangeReportStockDayAvgAll() } returns ApiResult.Error(
            code = 500,
            message = "Server error",
            raw = null,
            errorBody = null,
            exception = Exception("Network failure")
        )

        val viewModel = HomeViewModel(stockRepository, savedStateHandle)

        viewModel.stockInfoUiState.test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.loadingUiState.value)
    }

    @Test
    fun `partial API responses merge correctly for distinct stock codes`() = runTest {
        val bwData = listOf(
            BWIBBUModel.Response(code = "2330", name = "TSMC", date = "20231001", dividendYield = "2.5", pBratio = "5.0", pEratio = "20.0")
        )
        val dayData = listOf(
            StockDayAllModel.Response(code = "2317", name = "Foxconn", date = "20231001", change = "+2", closingPrice = "105", highestPrice = "106", lowestPrice = "104", openingPrice = "105", tradeValue = "50000", tradeVolume = "10000", transaction = "2000")
        )
        val avgData = listOf(
            StockDayAvgAllModel.Response(code = "2330", name = "TSMC", date = "20231001", monthlyAveragePrice = "540", closingPrice = "550")
        )

        coEvery { stockRepository.getExchangeReportBWIBBUAll() } returns ApiResult.Success(bwData, null)
        coEvery { stockRepository.getExchangeReportStockDayAll() } returns ApiResult.Success(dayData, null)
        coEvery { stockRepository.getExchangeReportStockDayAvgAll() } returns ApiResult.Success(avgData, null)

        val viewModel = HomeViewModel(stockRepository, savedStateHandle)

        viewModel.stockInfoUiState.test {
            val list = awaitItem()
            assertEquals(2, list.size)

            val stock2330 = list.find { it.code == "2330" }!!
            assertEquals("TSMC", stock2330.name)
            assertEquals("2.5", stock2330.dividendYield)
            assertEquals("540", stock2330.monthlyAveragePrice)

            val stock2317 = list.find { it.code == "2317" }!!
            assertEquals("Foxconn", stock2317.name)
            assertEquals("105", stock2317.closingPrice)
            assertEquals("+2", stock2317.change)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
