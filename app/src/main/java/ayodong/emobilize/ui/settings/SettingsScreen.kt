package ayodong.emobilize.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle

@Composable
fun SettingsScreen(
    categories: List<Category>,
    onCategory: (Category) -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalPalette.current
    BackHandler(onBack = onClose)
    Column(
        Modifier
            .fillMaxSize()
            .background(palette.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppMetrics.headerHorizontal)
            .padding(top = 20.dp, bottom = 32.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Settings",
                modifier = Modifier.weight(1f),
                style = appStyle(30.sp, FontWeight.Bold, lineHeight = 36.sp),
                color = palette.text,
            )
            Text(
                "Close",
                modifier = Modifier.clickable(onClick = onClose),
                style = appStyle(14.sp, FontWeight.Bold),
                color = palette.textMuted,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Edit up to ${Category.Limit} categories and choose a color for each.",
            style = appStyle(14.sp, FontWeight.Medium),
            color = palette.textMuted,
        )
        Spacer(Modifier.height(24.dp))
        categories.forEach { category ->
            CategoryEditor(category = category, onCategory = onCategory)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryEditor(
    category: Category,
    onCategory: (Category) -> Unit,
) {
    val palette = LocalPalette.current
    var name by remember(category.id, category.name) { mutableStateOf(category.name) }
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppMetrics.cardRadius))
            .background(palette.card)
            .border(1.dp, palette.scheme.border, RoundedCornerShape(AppMetrics.cardRadius))
            .padding(14.dp),
    ) {
        BasicTextField(
            value = name,
            onValueChange = { next ->
                name = next
                val trimmed = next.trim()
                if (trimmed.isNotEmpty()) onCategory(category.copy(name = trimmed))
            },
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = AppFont,
                color = palette.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            cursorBrush = SolidColor(palette.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(shape)
                .background(palette.scheme.background)
                .border(1.dp, palette.scheme.input, shape)
                .padding(horizontal = 12.dp),
            decorationBox = { inner ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (name.isEmpty()) {
                        Text("Category name", style = appStyle(16.sp, FontWeight.Medium), color = palette.textMuted)
                    }
                    inner()
                }
            },
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Category.colorChoices.forEach { color ->
                val selected = category.color == color
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(color))
                        .border(if (selected) 2.dp else 1.dp, if (selected) palette.text else palette.scheme.border, CircleShape)
                        .clickable { onCategory(category.copy(name = name.trim().ifEmpty { category.name }, color = color)) },
                )
            }
        }
    }
}
