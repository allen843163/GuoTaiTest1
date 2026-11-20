package com.allen.guotaitest.ui.home.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.allen.guotaitest.model.local.StockInfoUiModel
import com.allen.guotaitest.ui.theme.Color_FF20BF50
import com.allen.guotaitest.ui.theme.Color_FFFB1010

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StockListItem(
    data: StockInfoUiModel,
    onClick: (StockInfoUiModel) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 360.dp)
            .clickable {
                onClick(data)
            },
        border = BorderStroke(1.dp, MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "${data.code}",
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall)

            Text(
                text = "${data.name}",
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(4.dp))

            Price(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                lLabel = "開盤價",
                rLabel = "收盤價",
                lPrice = "${data.openingPrice}",
                rPrice = "${data.closingPrice}",
                rPriceStatus = if((data.closingPrice?.toFloatOrNull()?: 0f) > (data.monthlyAveragePrice?.toFloatOrNull()?: 0f)) {
                    StockPriceStatus.Rise
                } else if((data.closingPrice?.toFloatOrNull()?: 0f) < (data.monthlyAveragePrice?.toFloatOrNull()?: 0f)) {
                    StockPriceStatus.Fall
                } else {
                    StockPriceStatus.UnChange
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Price(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                lLabel = "最高價",
                rLabel = "最低價",
                lPrice = data.highestPrice?:"-",
                rPrice = data.lowestPrice?:"-"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Price(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                lLabel = "漲跌價差",
                rLabel = "月平均價",
                lPrice = data.change?:"-",
                rPrice = data.monthlyAveragePrice?:"-",
                lPriceStatus = if((data.change?.toFloatOrNull() ?: 0f) > 0f) {
                    StockPriceStatus.Rise
                } else if((data.change?.toFloatOrNull() ?: 0f) < 0f) {
                    StockPriceStatus.Fall
                } else {
                    StockPriceStatus.UnChange
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Trading(
                transaction = data.transaction?:"-",
                volume = data.tradeVolume?:"-",
                value = data.tradeValue?:"-"
            )
        }
    }
}

@Composable
fun Price(
    modifier: Modifier = Modifier,
    lLabel: String = "",
    rLabel: String = "",
    lPrice: String = "",
    rPrice: String = "",
    lPriceStatus: StockPriceStatus = StockPriceStatus.UnChange,
    rPriceStatus: StockPriceStatus = StockPriceStatus.UnChange
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(modifier = Modifier
                .align(Alignment.CenterStart),
                text = lLabel,
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
            Text(modifier = Modifier
                .align(Alignment.CenterEnd),
                text = lPrice,
                color = when(lPriceStatus) {
                    StockPriceStatus.Fall -> {
                        Color_FF20BF50
                    }
                    StockPriceStatus.Rise -> {
                        Color_FFFB1010
                    }
                    StockPriceStatus.UnChange -> {
                        MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                    }
                },
                style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.size(8.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(modifier = Modifier
                .align(Alignment.CenterStart),
                text = rLabel,
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
            Text(modifier = Modifier
                .align(Alignment.CenterEnd),
                text = rPrice,
                color = when(rPriceStatus) {
                    StockPriceStatus.Fall -> {
                        Color_FF20BF50
                    }
                    StockPriceStatus.Rise -> {
                        Color_FFFB1010
                    }
                    StockPriceStatus.UnChange -> {
                        MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                    }
                },
                style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun Trading(
    modifier: Modifier = Modifier,
    transaction: String = "",
    value: String = "",
    volume: String = "",
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.9f)) {
            Text(modifier = Modifier
                .align(Alignment.CenterStart),
                text = "成交筆數",
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
            Text(modifier = Modifier
                .align(Alignment.CenterEnd),
                text = transaction,
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.size(2.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Text(modifier = Modifier
                .align(Alignment.CenterStart),
                text = "成交股數",
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
            Text(modifier = Modifier
                .align(Alignment.CenterEnd),
                text = volume,
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.size(2.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1.4f)) {
            Text(modifier = Modifier
                .align(Alignment.CenterStart),
                text = "成交金額",
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
            Text(modifier = Modifier
                .align(Alignment.CenterEnd),
                text = value,
                color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

sealed class StockPriceStatus {
    object Rise: StockPriceStatus()
    object Fall: StockPriceStatus()
    object UnChange: StockPriceStatus()
}

@Preview
@Composable
fun Preview_StockListItem() {
    StockListItem(
        data = StockInfoUiModel(code = "0000")
    )
}