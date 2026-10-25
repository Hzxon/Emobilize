package ayodong.emobilize.core.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ayodong.emobilize.core.model.formatInputTime
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.core.ui.theme.appStyle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// Pickers and labels shared by every task-shaped form (Tasks add/edit, Calendar add/edit, Settings).
@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text,
        style = appStyle(14.sp, FontWeight.Medium),
        color = LocalPalette.current.textMuted,
    )
}

@Composable
internal fun RangeRow(
    label: String,
    date: String,
    time: String,
    minDate: String?,
    rangeValid: Boolean,
    onChange: (String, String) -> Unit,
) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.width(34.dp), style = appStyle(11.sp, FontWeight.Bold), color = palette.textMuted)
        DateField(
            value = date,
            onValue = { onChange(it, time) },
            minIso = minDate,
            valid = rangeValid,
            modifier = Modifier.weight(3f),
        )
        Spacer(Modifier.width(7.dp))
        TimeField(
            value = time,
            onValue = { onChange(date, it) },
            allowClear = false,
            valid = rangeValid,
            modifier = Modifier.weight(2f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateField(
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    minIso: String? = null,
    valid: Boolean = true,
) {
    val palette = LocalPalette.current
    var open by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    val minDate = minIso?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: today
    Box(
        modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .background(palette.scheme.background)
            .border(1.dp, if (valid) palette.scheme.input else palette.scheme.destructive, RoundedCornerShape(ShadcnRadius.lg))
            .clickable { open = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = value.ifBlank { "Date" },
            style = appStyle(13.sp, FontWeight.SemiBold),
            color = if (value.isBlank()) palette.textMuted else palette.text,
            maxLines = 1,
        )
    }
    if (open) {
        val initial = runCatching { LocalDate.parse(value) }.getOrDefault(today)
        val state = rememberDatePickerState(
            initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    return !date.isBefore(minDate)
                }
            },
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onValue(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
                    }
                    open = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeField(
    value: String,
    onValue: (String) -> Unit,
    allowClear: Boolean,
    modifier: Modifier = Modifier,
    valid: Boolean = true,
    placeholder: String = "Time",
) {
    val palette = LocalPalette.current
    var open by remember { mutableStateOf(false) }
    Box(
        modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .background(palette.scheme.background)
            .border(1.dp, if (valid) palette.scheme.input else palette.scheme.destructive, RoundedCornerShape(ShadcnRadius.lg))
            .clickable { open = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val label = if (value.isBlank()) placeholder else formatInputTime(value)
        Text(
            text = label,
            style = appStyle(13.sp, FontWeight.SemiBold),
            color = if (value.isBlank()) palette.textMuted else palette.text,
            maxLines = 1,
        )
    }
    if (open) {
        val parts = value.split(":")
        val state = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = false,
        )
        Dialog(onDismissRequest = { open = false }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.card)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimePicker(state = state)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (allowClear) {
                        TextButton(onClick = {
                            onValue("")
                            open = false
                        }) { Text("Clear") }
                    }
                    TextButton(onClick = { open = false }) { Text("Cancel") }
                    TextButton(onClick = {
                        onValue("%02d:%02d".format(state.hour, state.minute))
                        open = false
                    }) { Text("OK") }
                }
            }
        }
    }
}
