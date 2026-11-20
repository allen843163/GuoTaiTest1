package com.allen.guotaitest.ui.home.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.allen.guotaitest.model.local.StockInfoUiModel
import com.allen.guotaitest.ui.theme.Color_FF20BF50
import com.allen.guotaitest.ui.theme.Color_FFFB1010

@Composable
fun StockDetailDialog(
    data: StockInfoUiModel,
    onDismissRequest: () -> Unit = {}
) {
    Log.d("Allen", "data = ${data}")
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        StockDetailDialogUI(data)
    }
}

@Composable
fun StockDetailDialogUI(
    data: StockInfoUiModel
) {
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 160.dp),
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

            Info(
                label = "本益比",
                info = data.pEratio?:"-"
            )

            Spacer(modifier = Modifier.height(4.dp))
            Info(
                label = "殖利率(%)",
                info = data.dividendYield?:"-"
            )

            Spacer(modifier = Modifier.height(4.dp))
            Info(
                label = "股價淨值比",
                info = data.pBratio?:"-"
            )
        }
    }
}


@Composable
fun Info(
    label: String = "",
    info: String = ""
) {
    Box(modifier = Modifier
        .fillMaxWidth()) {
        Text(modifier = Modifier
            .align(Alignment.CenterStart),
            text = label,
            color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
            style = MaterialTheme.typography.bodySmall)

        Text(modifier = Modifier
            .align(Alignment.CenterEnd),
            text = info,
            color = MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Composable
fun Preview_StockDetailDialogUI() {
    StockDetailDialogUI(
        data = StockInfoUiModel(code = "123")
    )
}
