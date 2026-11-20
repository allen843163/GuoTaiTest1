package com.allen.guotaitest.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.allen.guotaitest.model.local.StockInfoUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenUI_displaysTitleAndStockList() {
        val testData = listOf(
            StockInfoUiModel(code = "2330", name = "TSMC", closingPrice = "580"),
            StockInfoUiModel(code = "2317", name = "Foxconn", closingPrice = "105")
        )

        composeTestRule.setContent {
            HomeScreenUI(
                uiState = HomeScreenUiState(data = testData)
            )
        }

        composeTestRule.onNodeWithText("股票").assertIsDisplayed()
        composeTestRule.onNodeWithText("2330").assertIsDisplayed()
        composeTestRule.onNodeWithText("TSMC").assertIsDisplayed()
        composeTestRule.onNodeWithText("2317").assertIsDisplayed()
        composeTestRule.onNodeWithText("Foxconn").assertIsDisplayed()
    }

    @Test
    fun homeScreenUI_refreshIconClick_triggersOnRefreshEvent() {
        var refreshTriggered = false

        composeTestRule.setContent {
            HomeScreenUI(
                uiEvent = HomeScreenUiEvent(
                    onRefresh = { refreshTriggered = true }
                )
            )
        }

        composeTestRule.onAllNodesWithContentDescription("refreshIcon")
            .onFirst()
            .performClick()

        assertTrue(refreshTriggered)
    }

    @Test
    fun homeScreenUI_stockItemClick_triggersOnDataClickEvent() {
        var clickedStock: StockInfoUiModel? = null
        val targetStock = StockInfoUiModel(code = "2330", name = "TSMC", closingPrice = "580")

        composeTestRule.setContent {
            HomeScreenUI(
                uiState = HomeScreenUiState(data = listOf(targetStock)),
                uiEvent = HomeScreenUiEvent(
                    onDataClick = { clickedStock = it }
                )
            )
        }

        composeTestRule.onNodeWithText("TSMC").performClick()

        assertEquals("2330", clickedStock?.code)
        assertEquals("TSMC", clickedStock?.name)
    }
}
