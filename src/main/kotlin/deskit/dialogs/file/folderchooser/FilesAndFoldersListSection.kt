/*
Copyright 2025 Zahid Khalilov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package deskit.dialogs.file.folderchooser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import deskit.dialogs.defaults.FolderChooserColors
import deskit.resources.Res
import deskit.resources.folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.io.File


@Composable
internal fun FilesAndFoldersListSection(
    coroutineScope: CoroutineScope,
    pathScrollState: ScrollState,
    items: List<File>,
    onFolderSelected: (File) -> Unit,
    onShowFileInfo: (File) -> Unit,
    modifier: Modifier = Modifier,
    colors: FolderChooserColors,
    isListView: Boolean,
    allowSoftWrapFolderName: Boolean = false
){
    Box(
        modifier = modifier
    ){
        val listState = rememberLazyListState()
        val gridState = rememberLazyGridState()

        Box(modifier = Modifier
            .fillMaxSize()
        ){
            if(isListView){
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(colors.fileAndFolderListBG)
                        .padding(end = 12.dp)
                ) {
                    items(items) { item ->
                        val folderInteractionSource = remember { MutableInteractionSource() }
                        val isFolderHovered by folderInteractionSource.collectIsHoveredAsState()

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onFolderSelected(item)
                                    coroutineScope.launch {
                                        pathScrollState.animateScrollTo(pathScrollState.maxValue)
                                    }
                                }
                                .padding(9.dp)
                                .hoverable(folderInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    painter = painterResource(Res.drawable.folder),
                                    contentDescription = null,
                                    tint = colors.folderIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    overflow = TextOverflow.Ellipsis,
                                    color = colors.folderNameColor,
                                    softWrap = allowSoftWrapFolderName
                                )
                            }
                            AnimatedVisibility(
                                visible = isFolderHovered,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ){
                                IconButton(onClick = {onShowFileInfo(item)}, modifier = Modifier.size(20.dp)){
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Folder info",
                                        modifier = Modifier.size(20.dp),
                                        tint = colors.infoIconTint
                                    )
                                }
                            }
                        }
                    }
                }
            }else{
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(colors.fileAndFolderListBG)
                        .padding(end = 12.dp)
                ){
                    items(items) { item ->
                        val folderInteractionSource = remember { MutableInteractionSource() }
                        val isFolderHovered by folderInteractionSource.collectIsHoveredAsState()

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onFolderSelected(item)
                                    coroutineScope.launch {
                                        pathScrollState.animateScrollTo(pathScrollState.maxValue)
                                    }
                                }
                                .padding(9.dp)
                                .animateItem(placementSpec = tween())
                                .hoverable(folderInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    painter = painterResource(Res.drawable.folder),
                                    contentDescription = null,
                                    tint = colors.folderIconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    overflow = TextOverflow.Ellipsis,
                                    color = colors.folderNameColor,
                                    softWrap = allowSoftWrapFolderName
                                )
                            }
                            AnimatedVisibility(
                                visible = isFolderHovered,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ){
                                IconButton(onClick = {onShowFileInfo(item)}, modifier = Modifier.size(20.dp)){
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Folder info",
                                        modifier = Modifier.size(20.dp),
                                        tint = colors.infoIconTint
                                    )
                                }
                            }
                        }
                    }
                }
            }


            if(isListView){
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .pointerHoverIcon(PointerIcon.Hand),
                    adapter = rememberScrollbarAdapter(scrollState = listState),
                    style = LocalScrollbarStyle.current.copy(
                        hoverColor = colors.scrollbarHoverColor,
                        unhoverColor = colors.scrollbarUnhoverColor
                    )
                )
            }else{
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .pointerHoverIcon(PointerIcon.Hand),
                    adapter = rememberScrollbarAdapter(scrollState = gridState),
                    style = LocalScrollbarStyle.current.copy(
                        hoverColor = colors.scrollbarHoverColor,
                        unhoverColor = colors.scrollbarUnhoverColor
                    )
                )
            }
        }
    }
}
