package ayodong.emobilize.ui.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import ayodong.emobilize.ui.theme.AppPalette
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle

fun errorInk(palette: AppPalette): Color = if (palette.id == 2) Color(0xFFFECACA) else Color(0xFFB91C1C)

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    hidden: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    val palette = LocalPalette.current
    val ink = errorInk(palette)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        isError = error != null,
        label = { Text(label, style = appStyle(14.sp, FontWeight.Medium)) },
        supportingText = error?.let { message ->
            { Text(message, style = appStyle(12.sp, FontWeight.Medium)) }
        },
        textStyle = appStyle(16.sp, FontWeight.SemiBold),
        visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        shape = RoundedCornerShape(ShadcnRadius.lg),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = palette.text,
            unfocusedTextColor = palette.text,
            errorTextColor = palette.text,
            focusedBorderColor = palette.scheme.ring,
            unfocusedBorderColor = palette.scheme.input,
            errorBorderColor = palette.scheme.destructive,
            focusedLabelColor = palette.textMuted,
            unfocusedLabelColor = palette.textMuted,
            errorLabelColor = ink,
            cursorColor = palette.primary,
            errorCursorColor = ink,
            focusedSupportingTextColor = ink,
            unfocusedSupportingTextColor = ink,
            errorSupportingTextColor = ink,
            focusedContainerColor = palette.scheme.background,
            unfocusedContainerColor = palette.scheme.background,
            errorContainerColor = palette.scheme.background,
        ),
    )
}
