package com.allen.guotaitest.ui.home

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allen.guotaitest.R
import com.allen.guotaitest.model.local.StockInfoUiModel
import com.allen.guotaitest.ui.home.view.SortBottomSheet
import com.allen.guotaitest.ui.home.view.StockDetailDialog
import com.allen.guotaitest.ui.home.view.StockListItem
import com.allen.guotaitest.ui.view.LoadingView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val stockData by viewModel.stockInfoUiState.collectAsStateWithLifecycle(listOf())
    val isLoading by viewModel.loadingUiState.collectAsStateWithLifecycle()
    val sortState by viewModel.sortState.collectAsStateWithLifecycle()
    val sheetState = rememberBottomSheetState(initialValue = Hidden)
    var sheetVisible by remember { mutableStateOf(false) }
    var showStockDetail by remember { mutableStateOf<StockInfoUiModel?>(null) }
    val scope = rememberCoroutineScope()

    if(isLoading) {
        LoadingView()
    }

    if(sheetVisible) {
        SortBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    sheetVisible = false
                }
            },
            onSortChange = viewModel::changeSort,
            sortState = sortState,
            sheetState = sheetState
        )
    }

    if(showStockDetail != null) {
        StockDetailDialog(
            data = showStockDetail!!,
            onDismissRequest = {
                showStockDetail = null
            }
        )
    }

    HomeScreenUI(
        uiState = HomeScreenUiState(
            data = stockData
        ),
        uiEvent = HomeScreenUiEvent(
            onRefresh = viewModel::refresh,
            onDataClick = {
                showStockDetail = it
            },
            onSort = {
                sheetVisible = true
                scope.launch { sheetState.expand() }
            }
        )
    )
}

@Composable
fun HomeScreenUI(
    uiState: HomeScreenUiState = HomeScreenUiState(),
    uiEvent: HomeScreenUiEvent = HomeScreenUiEvent()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("股票")
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                uiEvent.onRefresh()
                            },
                        contentDescription = "refreshIcon",
                        painter = painterResource(R.drawable.baseline_refresh_24),
                        tint = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                    )

                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                uiEvent.onSort()
                            },
                        contentDescription = "refreshIcon",
                        painter = painterResource(R.drawable.baseline_sort_24),
                        tint = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(innerPadding.plus(PaddingValues(horizontal = 16.dp))),
            columns = GridCells.FixedSize(360.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = uiState.data.size,
                key = {
                    uiState.data[it].code
                }
            ) {
                StockListItem(
                    data = uiState.data[it],
                    onClick = uiEvent.onDataClick)
            }
        }
//        LazyColumn(
//            modifier = Modifier
//                .padding(innerPadding.plus(PaddingValues(horizontal = 16.dp))),
//        ) {
//            items(
//                count = uiState.data.size,
//                key = {
//                    uiState.data[it].code
//                }
//            ) {
//                StockListItem( uiState.data[it])
//
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//        }
    }
}

data class HomeScreenUiState(
    val data: List<StockInfoUiModel> = listOf(),
)

data class HomeScreenUiEvent(
    val onRefresh: ()-> Unit = {},
    val onDataClick: (StockInfoUiModel)-> Unit = {},
    val onSort: ()-> Unit = {}
)

@Preview
@Composable
fun Preview_HomeScreenUI(
) {
    HomeScreenUI(
        uiState = HomeScreenUiState(
            data = (1000..1050).map {
                StockInfoUiModel(
                    code = "${it}",
                    name = "${it}",
                    lowestPrice = "${it}",
                    closingPrice = "${it}",
                    highestPrice = "${it}",
                    pBratio = "${it}",
                    pEratio = "${it}",
                )
            }
        )
    )
}

