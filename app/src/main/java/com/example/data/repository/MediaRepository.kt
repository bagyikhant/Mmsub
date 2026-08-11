package com.example.data.repository

import com.example.data.local.MediaDao
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchlistEntity
import com.example.model.DownloadLink
import com.example.model.EpisodeItem
import com.example.model.MediaItem
import com.example.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaRepository(private val mediaDao: MediaDao) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre

    private val _selectedType = MutableStateFlow<MediaType?>(null)
    val selectedType: StateFlow<MediaType?> = _selectedType

    val watchlist: Flow<List<WatchlistEntity>> = mediaDao.getAllWatchlist()
    val watchHistory: Flow<List<WatchHistoryEntity>> = mediaDao.getWatchHistory()

    fun isInWatchlist(id: String): Flow<Boolean> = mediaDao.isInWatchlist(id)

    suspend fun toggleWatchlist(item: MediaItem, isSaved: Boolean) {
        if (isSaved) {
            mediaDao.deleteWatchlist(item.id)
        } else {
            mediaDao.insertWatchlist(
                WatchlistEntity(
                    id = item.id,
                    titleEn = item.titleEn,
                    titleMm = item.titleMm,
                    posterUrl = item.posterUrl,
                    rating = item.rating,
                    year = item.year,
                    quality = item.quality,
                    type = item.type.name
                )
            )
        }
    }

    suspend fun updateWatchlistStatus(id: String, status: String) {
        mediaDao.updateWatchlistStatus(id, status)
    }

    suspend fun addToHistory(item: MediaItem, episodeProgress: String = "Full Movie") {
        mediaDao.insertHistory(
            WatchHistoryEntity(
                id = item.id,
                titleEn = item.titleEn,
                titleMm = item.titleMm,
                posterUrl = item.posterUrl,
                episodeProgress = episodeProgress
            )
        )
    }

    suspend fun clearHistory() {
        mediaDao.clearHistory()
    }

    fun getSampleMediaList(): List<MediaItem> = sampleMediaData

    fun getFeaturedMedia(): List<MediaItem> = sampleMediaData.filter { it.isFeatured }

    fun getTrendingMedia(): List<MediaItem> = sampleMediaData.filter { it.isTrending }

    fun getTopRatedMedia(): List<MediaItem> = sampleMediaData.filter { it.isTopRated }

    fun getMediaById(id: String): MediaItem? = sampleMediaData.find { it.id == id }

    fun searchAndFilter(
        query: String = "",
        genre: String = "All",
        type: MediaType? = null,
        quality: String = "All",
        minRating: Float = 0f
    ): List<MediaItem> {
        return sampleMediaData.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.titleEn.contains(query, ignoreCase = true) ||
                    item.titleMm.contains(query, ignoreCase = true) ||
                    item.genres.any { it.contains(query, ignoreCase = true) } ||
                    item.cast.any { it.contains(query, ignoreCase = true) }

            val matchesGenre = genre == "All" || item.genres.contains(genre)
            val matchesType = type == null || item.type == type
            val matchesQuality = quality == "All" || item.quality.contains(quality, ignoreCase = true)
            val matchesRating = item.rating >= minRating

            matchesQuery && matchesGenre && matchesType && matchesQuality && matchesRating
        }
    }

    companion object {
        val sampleGenres = listOf(
            "All", "Action", "Adventure", "Romance", "K-Drama", "Anime",
            "Sci-Fi", "Thriller", "Comedy", "Animation", "Drama", "Horror"
        )

        private val sampleMediaData = listOf(
            MediaItem(
                id = "cm_001",
                titleEn = "Deadpool & Wolverine",
                titleMm = "ဒက်ဒ်ပူးလ် နှင့် ဝူးဗားရင်း (၂၀၂၄)",
                type = MediaType.MOVIE,
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
                rating = 8.1f,
                year = 2024,
                quality = "1080p WEB-DL",
                audioSubInfo = "English [Burmese Subbed]",
                genres = listOf("Action", "Comedy", "Sci-Fi"),
                duration = "2h 08m",
                synopsisEn = "Deadpool's peaceful existence comes crashing down when the Time Variance Authority recruits him to safeguard the multiverse alongside a reluctant Wolverine.",
                synopsisMm = "ဝိဉာဉ်သစ်နဲ့အတူ Marvel စာမျက်နှာသစ်ကို ဖွင့်လှစ်လိုက်တဲ့ ဒက်ဒ်ပူးလ်နဲ့ ဝူးဗားရင်းတို့ရဲ့ အမိုက်စား စွန့်စားခန်း။ မြန်မာစာတန်းထိုးဖြင့် အကြည်ကြည့်ရှုနိုင်ပါပြီ။",
                cast = listOf("Ryan Reynolds", "Hugh Jackman", "Emma Corrin"),
                director = "Shawn Levy",
                downloadLinks = listOf(
                    DownloadLink("Mega.nz Fast Server", "1080p 60fps", "2.4 GB", "https://mega.nz/file/cm001_1080p"),
                    DownloadLink("VIP Direct Drive", "1080p WEB-DL", "2.1 GB", "https://vipdrive.cm/file/cm001_1080p", true),
                    DownloadLink("Google Drive Backup", "720p HD", "1.1 GB", "https://drive.google.com/file/d/cm001_720p"),
                    DownloadLink("Direct Stream 1", "360p Data Saver", "450 MB", "https://stream.channelmyanmar.info/play?id=cm001")
                ),
                trailerUrl = "https://www.youtube.com/watch?v=73_1biulk6s",
                isFeatured = true,
                isTrending = true,
                isTopRated = true
            ),
            MediaItem(
                id = "cm_002",
                titleEn = "Queen of Tears",
                titleMm = "မျက်ရည်မိဖုရား (၂၀၂၄)",
                type = MediaType.K_DRAMA,
                posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
                rating = 8.8f,
                year = 2024,
                quality = "1080p NF WEB-DL",
                audioSubInfo = "Korean [Burmese Subbed]",
                genres = listOf("Romance", "Drama", "K-Drama"),
                duration = "16 Episodes",
                synopsisEn = "The queen of department stores and the prince of supermarkets weather a marital crisis until love miraculously begins to bloom again.",
                synopsisMm = "အိမ်ထောင်ရေး အကျပ်အတည်းနဲ့ ရင်ဆိုင်နေရတဲ့ စူပါမားကတ် မင်းသားလေးနဲ့ ကုန်တိုက်မိဖုရားကြီးတို့ရဲ့ အံ့ဩဖွယ် အချစ်ဇာတ်လမ်း။ အပိုင်း ၁ မှ ၁၆ အထိ မြန်မာစာတန်းထိုး စုံလင်စွာ။",
                cast = listOf("Kim Soo-hyun", "Kim Ji-won", "Park Sung-hoon"),
                director = "Jang Young-woo",
                downloadLinks = listOf(
                    DownloadLink("Mega Full Batch S01", "1080p Complete", "18.5 GB", "https://mega.nz/file/cm002_batch"),
                    DownloadLink("VIP Drive Direct", "720p Complete", "9.2 GB", "https://vipdrive.cm/file/cm002_batch", true)
                ),
                episodes = (1..16).map { ep ->
                    EpisodeItem(
                        episodeNumber = ep,
                        title = "Episode $ep (မြန်မာစာတန်းထိုး)",
                        duration = "1h 12m",
                        streamUrl = "https://stream.channelmyanmar.info/play?id=cm002_ep$ep",
                        downloadLinks = listOf(
                            DownloadLink("VIP Direct Drive", "1080p HD", "1.2 GB", "https://vipdrive.cm/ep$ep", true),
                            DownloadLink("Mega.nz Server", "720p HD", "600 MB", "https://mega.nz/ep$ep")
                        )
                    )
                },
                trailerUrl = "https://www.youtube.com/watch?v=342894129",
                isFeatured = true,
                isTrending = true,
                isTopRated = true
            ),
            MediaItem(
                id = "cm_003",
                titleEn = "Dune: Part Two",
                titleMm = "ဒွန်း: အပိုင်း (၂) (၂၀၂၄)",
                type = MediaType.MOVIE,
                posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                rating = 8.6f,
                year = 2024,
                quality = "4K Ultra HD",
                audioSubInfo = "English [Burmese Subbed]",
                genres = listOf("Sci-Fi", "Adventure", "Action"),
                duration = "2h 46m",
                synopsisEn = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
                synopsisMm = "Paul Atreides နဲ့ Fremen တို့ပေါင်းစည်းပြီး အိမ်တော်ကို ဖျက်ဆီးခဲ့တဲ့ ရန်သူတွေကို လက်စားချေမယ့် မဟာဗျူဟာမြောက် စိုက်ပျိုးရေးနှင့် စွန့်စားခန်း။ 4K/1080p မြန်မာစာတန်းထိုး။",
                cast = listOf("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
                director = "Denis Villeneuve",
                downloadLinks = listOf(
                    DownloadLink("Mega 4K HDR", "2160p 4K", "6.5 GB", "https://mega.nz/file/cm003_4k"),
                    DownloadLink("VIP Drive Direct", "1080p WEB-DL", "2.8 GB", "https://vipdrive.cm/file/cm003_1080p", true),
                    DownloadLink("Google Drive Server", "720p HD", "1.3 GB", "https://drive.google.com/file/d/cm003_720p")
                ),
                trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
                isFeatured = true,
                isTrending = true,
                isTopRated = true
            ),
            MediaItem(
                id = "cm_004",
                titleEn = "Solo Leveling",
                titleMm = "ဆောလို လယ်ဗယ်လင်း (အနိမေ)",
                type = MediaType.ANIME,
                posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1200&auto=format&fit=crop&q=80",
                rating = 8.5f,
                year = 2024,
                quality = "1080p Anime HD",
                audioSubInfo = "Japanese [Burmese Subbed]",
                genres = listOf("Anime", "Action", "Fantasy"),
                duration = "12 Episodes",
                synopsisEn = "In a world where hunters must battle deadly monsters, weak hunter Sung Jinwoo discovers a quest log that grants him infinite leveling powers.",
                synopsisMm = "အနိမ့်ဆုံးအဆင့် E-rank မုဆိုးလေး Sung Jinwoo တစ်ယောက် ကမ္ဘာပေါ်မှာ တစ်ဦးတည်းသော Level တက်နိုင်တဲ့ စနစ်ကို ရရှိလိုက်တဲ့အခါ မဟာစွမ်းအားရှင် ဖြစ်လာပုံ။ အပိုင်း ၁ မှ ၁၂ စုံ။",
                cast = listOf("Taito Ban", "Genta Nakamura"),
                director = "Shunsuke Nakashige",
                downloadLinks = listOf(
                    DownloadLink("VIP Drive Batch", "1080p Complete", "4.8 GB", "https://vipdrive.cm/file/cm004_batch", true)
                ),
                episodes = (1..12).map { ep ->
                    EpisodeItem(
                        episodeNumber = ep,
                        title = "Episode $ep (မြန်မာစာတန်းထိုး)",
                        duration = "24m",
                        streamUrl = "https://stream.channelmyanmar.info/play?id=cm004_ep$ep",
                        downloadLinks = listOf(
                            DownloadLink("Mega.nz Direct", "1080p HD", "450 MB", "https://mega.nz/ep$ep"),
                            DownloadLink("VIP Drive", "720p HD", "250 MB", "https://vipdrive.cm/ep$ep", true)
                        )
                    )
                },
                trailerUrl = "https://www.youtube.com/watch?v=91283012",
                isFeatured = false,
                isTrending = true,
                isTopRated = true
            ),
            MediaItem(
                id = "cm_005",
                titleEn = "Godzilla x Kong: The New Empire",
                titleMm = "ဂေါက်ဇီလာ နှင့် ကောင်း: အင်ပါယာသစ် (၂၀၂၄)",
                type = MediaType.MOVIE,
                posterUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
                rating = 7.4f,
                year = 2024,
                quality = "1080p Bluray",
                audioSubInfo = "English [Burmese Subbed]",
                genres = listOf("Action", "Sci-Fi", "Adventure"),
                duration = "1h 55m",
                synopsisEn = "Two ancient titans, Godzilla and Kong, clash in an epic battle as humans unravel their intertwined origins and connection to Skull Island's mysteries.",
                synopsisMm = "ကမ္ဘာ့အကြီးမားဆုံး Titan နှစ်ကောင်ဖြစ်တဲ့ ဂေါက်ဇီလာနဲ့ ကောင်းတို့ ပူးပေါင်းပြီး ငုပ်လျှိုးနေတဲ့ အန္တရာယ်သစ်ကို ရင်ဆိုင်ကြမယ့် မဟာတိုက်ပွဲ။ မြန်မာစာတန်းထိုး။",
                cast = listOf("Rebecca Hall", "Brian Tyree Henry", "Dan Stevens"),
                director = "Adam Wingard",
                downloadLinks = listOf(
                    DownloadLink("Mega.nz High Quality", "1080p Bluray", "2.2 GB", "https://mega.nz/file/cm005_1080p"),
                    DownloadLink("VIP Drive Direct", "720p WEB-DL", "1.0 GB", "https://vipdrive.cm/file/cm005_720p", true)
                ),
                trailerUrl = "https://www.youtube.com/watch?v=lV1OOlGwExM",
                isFeatured = false,
                isTrending = true,
                isTopRated = false
            ),
            MediaItem(
                id = "cm_006",
                titleEn = "Oppenheimer",
                titleMm = "အိုပင်ဟိုင်းမား (၂၀၂၃)",
                type = MediaType.MOVIE,
                posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
                rating = 8.9f,
                year = 2023,
                quality = "4K IMAX Bluray",
                audioSubInfo = "English [Burmese Subbed]",
                genres = listOf("Drama", "Biography", "History"),
                duration = "3h 00m",
                synopsisEn = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
                synopsisMm = "အော်စကာ ၇ ဆုရ အမေရိကန် သိပ္ပံပညာရှင် Oppenheimer ရဲ့ အက်တမ်ဗုံး တီထွင်မှုနဲ့ သမိုင်းဝင် တိုက်ပွဲအကြောင်း။ ရုပ်သံအကြည်ဆုံး 4K IMAX မြန်မာစာတန်းထိုး။",
                cast = listOf("Cillian Murphy", "Emily Blunt", "Matt Damon", "Robert Downey Jr."),
                director = "Christopher Nolan",
                downloadLinks = listOf(
                    DownloadLink("Mega.nz 4K Server", "2160p IMAX", "7.8 GB", "https://mega.nz/file/cm006_4k"),
                    DownloadLink("VIP Drive Direct", "1080p Bluray", "3.1 GB", "https://vipdrive.cm/file/cm006_1080p", true)
                ),
                trailerUrl = "https://www.youtube.com/watch?v=uYPbbksJxIg",
                isFeatured = false,
                isTrending = false,
                isTopRated = true
            ),
            MediaItem(
                id = "cm_007",
                titleEn = "Squid Game: Season 2",
                titleMm = "ပြည်သူ့ကစားပွဲ - အပိုင်း (၂)",
                type = MediaType.SERIES,
                posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                rating = 8.4f,
                year = 2024,
                quality = "1080p NF WEB-DL",
                audioSubInfo = "Korean [Burmese Subbed]",
                genres = listOf("Thriller", "Drama", "K-Drama"),
                duration = "8 Episodes",
                synopsisEn = "Gi-hun abandons his plans to go to the US and starts a chase with a motive after the dangerous game's mastermind.",
                synopsisMm = "မဟာကစားပွဲကြီးရဲ့ နောက်ကွယ်မှ လျှို့ဝှက်ချက်တွေကို ဖော်ထုတ်ဖို့ ပြန်လည်ဝင်ရောက်လာတဲ့ Gi-hun ရဲ့ အသက်လု ကစားပွဲ။ မြန်မာစာတန်းထိုး။",
                cast = listOf("Lee Jung-jae", "Lee Byung-hun", "Wi Ha-joon"),
                director = "Hwang Dong-hyuk",
                downloadLinks = listOf(
                    DownloadLink("Mega Full Season 2", "1080p Complete", "9.8 GB", "https://mega.nz/file/cm007_s2")
                ),
                episodes = (1..8).map { ep ->
                    EpisodeItem(
                        episodeNumber = ep,
                        title = "Season 2 - Episode $ep (မြန်မာစာတန်းထိုး)",
                        duration = "55m",
                        streamUrl = "https://stream.channelmyanmar.info/play?id=cm007_ep$ep",
                        downloadLinks = listOf(
                            DownloadLink("VIP Drive Direct", "1080p HD", "1.1 GB", "https://vipdrive.cm/s2ep$ep", true)
                        )
                    )
                },
                trailerUrl = "https://www.youtube.com/watch?v=SquidGame2",
                isFeatured = false,
                isTrending = true,
                isTopRated = true
            )
        )
    }
}
