package com.imashnake.animite.features.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.imashnake.animite.MediaListQuery
import com.imashnake.animite.type.MediaType
import com.imashnake.animite.R as Res

/**
 * A [LazyRow] of [MediaSmall]s.
 *
 * @param mediaList A list of [MediaListQuery.Medium]s.
 * @param onItemClick Action that is propagated to the [MediaSmall]s, it takes a unique id.
 */
@Composable
fun <T> MediaSmallRow(
    mediaList: List<T>,
    onItemClick: (itemId: Int?) -> Unit,
    content: @Composable (T, (itemId: Int?) -> Unit) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(Res.dimen.small_padding)),
        contentPadding = PaddingValues(
            start = dimensionResource(Res.dimen.large_padding),
            end = dimensionResource(Res.dimen.large_padding)
        )
    ) {
        items(mediaList.filterNotNull()) { media ->
            content(media, onItemClick)
        }
    }
}

/**
 * A [Card] to display a media image and a label.
 *
 * @param image A URL of the image to be shown in the card that this component is.
 * @param label A label for the [image], if this is `null`, the [label] is not shown.
 * @param onClick Action to happen when the card is clicked.
 */
@Composable
fun MediaSmall(height: Dp, width: Dp, image: String?, label: String? = null, onClick: () -> Unit = {  }) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .width(width)
            .clip(RoundedCornerShape(dimensionResource(Res.dimen.media_card_corner_radius)))
            .clickable(
                enabled = true,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        AsyncImage(
            model = image,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(height)
                .clip(RoundedCornerShape(dimensionResource(Res.dimen.media_card_corner_radius)))
        )

        if (label != null)
            Box {
                Text(
                    text = " \n ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(Res.dimen.media_card_text_padding_vertical)
                    )
                )

                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    // TODO: Add a custom overflow indicator:
                    //  https://proandroiddev.com/detect-text-overflow-in-jetpack-compose-56c0b83da5a5.
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(Res.dimen.media_card_text_padding_horizontal),
                            vertical = dimensionResource(Res.dimen.media_card_text_padding_vertical)
                        )
                )
            }
    }
}

@Preview
@Composable
fun PreviewMediaSmallRow() {
    MediaSmallRow(
        mediaList = List(10) {
            MediaListQuery.Medium(
                id = it,
                type = MediaType.ANIME,
                title = MediaListQuery.Title(
                    romaji = "Sono Bisque Doll wa Koi wo Suru",
                    english = null,
                    native = null
                ),
                coverImage = MediaListQuery.CoverImage(
                    extraLarge = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx132405-qP7FQYGmNI3d.jpg",
                    large = null
                )
            )
        },
        onItemClick = {  },
        content = { media, onItemClick ->
            MediaSmall(
                height = dimensionResource(Res.dimen.media_card_height),
                width = dimensionResource(Res.dimen.media_card_width),
                image = media.coverImage?.extraLarge,
                // TODO: Do something about this chain.
                label = media.title?.romaji ?:
                media.title?.english ?:
                media.title?.native ?: "",
                onClick = {
                    onItemClick(media.id)
                }
            )
        }
    )
}

@Preview
@Composable
fun PreviewMediaSmall() {
    MediaSmall(
        height = dimensionResource(Res.dimen.media_card_height),
        width = dimensionResource(Res.dimen.media_card_width),
        image =
        "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx132405-qP7FQYGmNI3d.jpg",
        label =
        "Sono Bisque Doll wa Koi wo Suru",
        onClick = {  }
    )
}
