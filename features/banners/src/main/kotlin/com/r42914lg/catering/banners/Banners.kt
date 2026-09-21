package com.r42914lg.catering.banners

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.r42914lg.catering.core.data.model.Banner
import com.r42914lg.catering.designsys.CateringTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private const val TIME_UPDATE_BANNERS_MS = 8000L
private const val BANNER_WIDTH = 343
private const val BANNER_HEIGHT = 110

private val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
val Number.dpToPx: Int
    get() = (this.toFloat() * displayMetrics.density).roundToInt()

@Composable
fun BannersListView(
    banners: List<Banner>,
    baseUrl: String,
    modifier: Modifier = Modifier,
    bannerBackgroundColor: Color = Color.Transparent,
    onBannerClick: (Long) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState)
    val configuration = LocalConfiguration.current
    val bannerScrollOffset = remember {
        ((configuration.screenWidthDp - BANNER_WIDTH - 16) / 2) - 8
    }

    when {
        banners.size > 1 -> {
            LazyRow(
                modifier = modifier.background(bannerBackgroundColor),
                state = lazyListState,
                flingBehavior = flingBehavior,
                horizontalArrangement = spacedBy(CateringTheme.spacing.s, Alignment.CenterHorizontally),
                contentPadding = PaddingValues(start = CateringTheme.spacing.l)
            ) {
                items(Int.MAX_VALUE) {
                    val index = it % banners.size
                    val banner = banners[index]
                    BannerView(
                        banner = banner,
                        baseUrl = baseUrl,
                        onBannerClick = onBannerClick,
                    )
                }

            }
        }
        banners.size == 1 -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BannerView(
                    banner = banners[0],
                    baseUrl = baseUrl,
                    backgroundColor = bannerBackgroundColor,
                    onBannerClick = onBannerClick,
                )
            }
        }
        else -> { /* no-op */ }
    }

    LaunchedEffect(Unit) {
        if (banners.size > 1) {
            while (true) {
                delay(TIME_UPDATE_BANNERS_MS.milliseconds)
                val targetIndex = lazyListState.firstVisibleItemIndex +
                        if (lazyListState.layoutInfo.visibleItemsInfo.size == 3) 2 else 1
                lazyListState.animateScrollToItem(
                    targetIndex,
                    (-bannerScrollOffset).dpToPx
                )
            }
        }
    }
}

@Composable
fun BannerView(
    banner: Banner,
    baseUrl: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CateringTheme.colors.background,
    onBannerClick: (Long) -> Unit = {},
) {
    Box (
        modifier = modifier
            .size(BANNER_WIDTH.dp, BANNER_HEIGHT.dp)
            .background(backgroundColor, CateringTheme.shapes.card)
            .clip(CateringTheme.shapes.card)
            .noRippleClick {
                banner.eventId?.let {
                    onBannerClick(it)
                }
            }
    ) {
        AsyncImage(
            model = baseUrl + banner.imagePath,
            contentDescription = banner.description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

fun Modifier.noRippleClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit
): Modifier = composed {
    this.clickable(
        interactionSource = remember { interactionSource },
        enabled = enabled,
        indication = null,
        onClick = onClick
    )
}