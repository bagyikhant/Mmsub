package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaItem
import com.example.ui.components.DownloadLinksSheet
import com.example.ui.components.VideoPlayerModal
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.ChannelMyanmarTheme
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChannelMyanmarTheme {
                ChannelMyanmarApp(viewModel = viewModel)
            }
        }
    }
}

sealed class NavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavTab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : NavTab("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Watchlist : NavTab("watchlist", "Watchlist", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Settings : NavTab("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun ChannelMyanmarApp(viewModel: MainViewModel) {
    val tabs = listOf(NavTab.Home, NavTab.Explore, NavTab.Watchlist, NavTab.Settings)
    var selectedTabItem by remember { mutableIntStateOf(0) }

    val selectedMedia by viewModel.selectedMedia.collectAsState()
    val isPlayerVisible by viewModel.isPlayerVisible.collectAsState()
    val activeEpisode by viewModel.activeStreamEpisode.collectAsState()

    var showDownloadSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (selectedMedia == null && !isPlayerVisible) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTabItem == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTabItem = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.route}")
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (selectedTabItem) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onMediaSelect = { viewModel.selectMedia(it) },
                    onNavigateSearch = { selectedTabItem = 1 },
                    onNavigateWatchlist = { selectedTabItem = 2 },
                    onWatchTrailer = { item ->
                        viewModel.selectMedia(item)
                        viewModel.openPlayer()
                    }
                )
                1 -> ExploreScreen(
                    viewModel = viewModel,
                    onMediaSelect = { viewModel.selectMedia(it) }
                )
                2 -> WatchlistScreen(
                    viewModel = viewModel,
                    onMediaSelect = { viewModel.selectMedia(it) }
                )
                3 -> SettingsScreen(
                    viewModel = viewModel
                )
            }

            // Detail Screen Overlay
            selectedMedia?.let { media ->
                DetailScreen(
                    mediaItem = media,
                    viewModel = viewModel,
                    onBackClick = { viewModel.selectMedia(null) },
                    onOpenStream = { episode ->
                        viewModel.openPlayer(episode)
                    },
                    onOpenDownloadSheet = {
                        showDownloadSheet = true
                    },
                    onSelectRelatedMedia = { viewModel.selectMedia(it) }
                )
            }

            // Video Player Sheet Overlay
            if (isPlayerVisible && selectedMedia != null) {
                VideoPlayerModal(
                    item = selectedMedia!!,
                    episode = activeEpisode,
                    onClose = { viewModel.closePlayer() }
                )
            }

            // Download Links Bottom Sheet Overlay
            if (showDownloadSheet && selectedMedia != null) {
                DownloadLinksSheet(
                    mediaItem = selectedMedia!!,
                    links = activeEpisode?.downloadLinks?.ifEmpty { selectedMedia!!.downloadLinks }
                        ?: selectedMedia!!.downloadLinks,
                    onDismiss = { showDownloadSheet = false },
                    onDirectStream = {
                        showDownloadSheet = false
                        viewModel.openPlayer(activeEpisode)
                    }
                )
            }
        }
    }
}
