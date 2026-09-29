package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NumberItem
import com.example.ui.MainViewModel

@Composable
fun ExploreNumbersScreen(
    viewModel: MainViewModel,
    onNumberSelected: (NumberItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All (0-20), 1: 0-10, 2: 11-20

    val filteredList = when (selectedFilter) {
        1 -> viewModel.numbersList.filter { it.value in 0..10 }
        2 -> viewModel.numbersList.filter { it.value in 11..20 }
        else -> viewModel.numbersList
    }

    val practicedList = viewModel.userProgress.value.practicedNumbersList
        .split(",")
        .filter { it.isNotBlank() }
        .mapNotNull { it.toIntOrNull() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == 0,
                onClick = { selectedFilter = 0 },
                label = { Text("الكل (٠-٢٠)", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedFilter == 1,
                onClick = { selectedFilter = 1 },
                label = { Text("من ٠ إلى ١٠", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedFilter == 2,
                onClick = { selectedFilter = 2 },
                label = { Text("من ١١ إلى ٢٠", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }

        // Numbers Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { item ->
                val isPracticed = practicedList.contains(item.value)
                val displayNumeral = viewModel.getDisplayNumeral(item)

                NumberGridCard(
                    item = item,
                    displayNumeral = displayNumeral,
                    isPracticed = isPracticed,
                    onCardClick = { onNumberSelected(item) },
                    onAudioClick = { viewModel.speakNumber(item) }
                )
            }
        }
    }
}

@Composable
private fun NumberGridCard(
    item: NumberItem,
    displayNumeral: String,
    isPracticed: Boolean,
    onCardClick: () -> Unit,
    onAudioClick: () -> Unit
) {
    val itemColor = Color(item.colorHex)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .clickable { onCardClick() }
            .testTag("number_card_${item.value}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Practiced indicator & speaker button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPracticed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "تم التعلم",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onAudioClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "استمع للنطق",
                        tint = itemColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Big numeral
            Text(
                text = displayNumeral,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = itemColor,
                fontSize = 38.sp
            )

            // Arabic Word with tashkeel
            Text(
                text = item.arabicWord,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            // Object preview emoji
            Text(
                text = item.emoji,
                fontSize = 18.sp
            )
        }
    }
}
