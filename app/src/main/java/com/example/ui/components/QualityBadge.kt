package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.BadgeRed
import com.example.ui.theme.GoldOnPrimary

@Composable
fun QualityBadge(
    quality: String,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        quality.contains("4K", ignoreCase = true) -> BadgeGold
        quality.contains("1080p", ignoreCase = true) -> Color(0xFF007AFF)
        else -> BadgeRed
    }

    Text(
        text = quality,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun RatingBadge(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(BadgeGold)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating Star",
            tint = GoldOnPrimary,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = " $rating",
            color = GoldOnPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
