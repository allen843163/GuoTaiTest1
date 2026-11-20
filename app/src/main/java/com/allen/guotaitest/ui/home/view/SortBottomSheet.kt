package com.allen.guotaitest.ui.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    sheetState: SheetState,
    sortState: Boolean = false,
    onSortChange: (Boolean) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSortChange(false)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "降序排列",
                color = if(!sortState) {
                    MaterialTheme.LocalMaterialTheme.current.colorScheme.tertiary
                } else {
                    MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                },
                style = MaterialTheme.typography.titleLarge)

        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSortChange(true)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "升序排列",
                color = if(sortState) {
                    MaterialTheme.LocalMaterialTheme.current.colorScheme.tertiary
                } else {
                    MaterialTheme.LocalMaterialTheme.current.colorScheme.secondary
                },
                style = MaterialTheme.typography.titleLarge)

        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}