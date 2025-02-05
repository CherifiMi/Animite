package com.imashnake.animite.features.searchbar

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.imashnake.animite.SearchQuery
import com.imashnake.animite.features.destinations.MediaPageDestination
import com.imashnake.animite.type.MediaType
import com.ramcosta.composedestinations.navigation.navigate
import com.imashnake.animite.R as Res

// TODO:
//  - UX concern: This blocks content sometimes!
//  - `SearchList` goes beyond the status bar.
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    modifier: Modifier,
    viewModel: SearchViewModel = viewModel(),
    navController: NavHostController
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // TODO: Customize this animation.
        AnimatedContent(targetState = isExpanded) { targetExpanded ->
            if (targetExpanded) {
                SearchList(
                    viewModel = hiltViewModel(),
                    modifier = Modifier
                        .clip(
                            // TODO: Either remove this or change the resource.
                            RoundedCornerShape(dimensionResource(Res.dimen.media_card_corner_radius))
                        )
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95F))
                        .align(Alignment.End)
                        .fillMaxWidth(),
                    onClick = {
                        isExpanded = false
                        // TODO: Double clicking makes the navigation happen twice.
                        navController.navigate(
                            MediaPageDestination(
                                id = it,
                                mediaTypeArg = MediaType.ANIME.rawValue
                            )
                        )
                    }
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                isExpanded = !isExpanded
                viewModel.run {
                    searchAnime("")
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .wrapContentSize(),
            shadowElevation = 20.dp,
            shape = CircleShape
        ) {
            // TODO: Customize this animation.
            AnimatedContent(targetState = isExpanded) { targetExpanded ->
                if (targetExpanded) {
                    ExpandedSearchBar(hiltViewModel())
                } else {
                    CollapsedSearchBar()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CollapsedSearchBar() {
    Row(
        modifier = Modifier.padding(dimensionResource(Res.dimen.search_bar_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = Res.drawable.search),
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun ExpandedSearchBar(viewModel: SearchViewModel = viewModel()) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowRight,
            contentDescription = stringResource(Res.string.collapse),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(dimensionResource(Res.dimen.search_bar_padding))
        )

        var text by remember { mutableStateOf("") }
        val focusRequester = FocusRequester()
        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            enabled = true,
            value = text,
            onValueChange = { input ->
                text = input
                viewModel.run {
                    searchAnime(input)
                }
            },
            placeholder = {
                Text(
                    text = stringResource(Res.string.search),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5F),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            },
            modifier = Modifier
                .wrapContentWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    keyboardController?.show()
                },
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            trailingIcon = {
                IconButton(
                    onClick = {
                        text = ""
                        viewModel.run {
                            searchAnime("")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(Res.string.close),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(
                            dimensionResource(Res.dimen.search_bar_padding)
                        )
                    )
                }
            }
        )
        // TODO: How does this work?
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun SearchList(
    viewModel: SearchViewModel = viewModel(),
    modifier: Modifier,
    onClick: (Int?) -> Unit
) {
    val searchList = viewModel.uiState.searchList?.media

    // TODO: Improve this animation.
    if (!searchList.isNullOrEmpty()) {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(searchList, key = { it!!.id }) {
                SearchItem(
                    item = it,
                    onClick = onClick,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    } else {
        // TODO: Handle errors.
        Log.d("bruh", "bruh")
    }
}

@Composable
private fun SearchItem(item: SearchQuery.Medium?, onClick: (Int?) -> Unit, modifier: Modifier) {
    Text(
        // TODO: Do something about this chain.
        text = item?.title?.romaji ?:
        item?.title?.english ?:
        item?.title?.native.orEmpty(),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        modifier = modifier
            .clickable {
                onClick(item?.id)
            }
            .padding(dimensionResource(Res.dimen.search_list_padding))
            .fillMaxSize()
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewSearchBar() {
    Column(modifier = Modifier.padding(200.dp)) {
        CollapsedSearchBar()
        Spacer(modifier = Modifier.height(20.dp))
        ExpandedSearchBar()
    }
}
