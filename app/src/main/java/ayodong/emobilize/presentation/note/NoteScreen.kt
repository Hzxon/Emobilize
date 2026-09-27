package ayodong.emobilize.presentation.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ayodong.emobilize.domain.model.Note
import ayodong.emobilize.presentation.components.Navbar

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = hiltViewModel(),
    onNewNoteClick: () -> Unit = {},
    onNoteClick: (Note) -> Unit = {}
) {
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { Navbar() },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { innerPadding ->
        when (uiState) {
            is NoteUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NoteUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is NoteUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        TakeNoteBar(onTakeNoteClick = onNewNoteClick)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TakeNoteBar(
    onTakeNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTakeNoteClick() },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Take a note...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Outlined.CheckBox, contentDescription = "New List")
                }
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Outlined.Brush, contentDescription = "New Drawing")
                }
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Outlined.Image, contentDescription = "New Image Note")
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}