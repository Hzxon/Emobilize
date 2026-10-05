package ayodong.emobilize.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
) {
    val palette = LocalPalette.current
    BackHandler(enabled = viewModel.page == AuthPage.Register) {
        viewModel.showLogin()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(palette.surface),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppMetrics.headerHorizontal)
                .padding(top = 72.dp, bottom = 32.dp),
        ) {
            Text(
                "Emobilize",
                style = appStyle(30.sp, FontWeight.Bold, lineHeight = 36.sp),
                color = palette.text,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (viewModel.page == AuthPage.Login) "Log in to continue" else "Create an account",
                style = appStyle(15.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
            Spacer(Modifier.height(28.dp))
            if (viewModel.page == AuthPage.Register) {
                AuthField(
                    value = viewModel.name,
                    onValue = {
                        viewModel.name = it
                        viewModel.clearError()
                    },
                    placeholder = "Name",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
                Spacer(Modifier.height(12.dp))
            }
            AuthField(
                value = viewModel.email,
                onValue = {
                    viewModel.email = it
                    viewModel.clearError()
                },
                placeholder = "Email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            Spacer(Modifier.height(12.dp))
            AuthField(
                value = viewModel.password,
                onValue = {
                    viewModel.password = it
                    viewModel.clearError()
                },
                placeholder = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = if (viewModel.page == AuthPage.Register) ImeAction.Next else ImeAction.Done,
                hidden = true,
            )
            if (viewModel.page == AuthPage.Register) {
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = viewModel.confirm,
                    onValue = {
                        viewModel.confirm = it
                        viewModel.clearError()
                    },
                    placeholder = "Confirm password",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    hidden = true,
                )
            }
            viewModel.error?.let { message ->
                Spacer(Modifier.height(12.dp))
                Text(
                    message,
                    style = appStyle(13.sp, FontWeight.Medium),
                    color = palette.scheme.destructive,
                )
            }
            Spacer(Modifier.height(22.dp))
            val submitLabel = if (viewModel.page == AuthPage.Login) "Log in" else "Create account"
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(AppMetrics.actionHeight)
                    .clip(RoundedCornerShape(AppMetrics.actionRadius))
                    .background(palette.scheme.primary)
                    .clickable {
                        if (viewModel.page == AuthPage.Login) viewModel.submitLogin() else viewModel.submitRegister()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    submitLabel,
                    style = appStyle(14.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                    color = palette.scheme.primaryForeground,
                )
            }
            Spacer(Modifier.height(18.dp))
            if (viewModel.page == AuthPage.Login) {
                SwitchLine(
                    prompt = "Need an account?",
                    action = "Register",
                    onClick = viewModel::showRegister,
                )
            } else {
                SwitchLine(
                    prompt = "Already have an account?",
                    action = "Log in",
                    onClick = viewModel::showLogin,
                )
            }
        }
    }
}

@Composable
private fun SwitchLine(
    prompt: String,
    action: String,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(prompt, style = appStyle(14.sp, FontWeight.Medium), color = palette.textMuted)
        Text(
            action,
            modifier = Modifier
                .padding(start = 6.dp)
                .clickable(onClick = onClick),
            style = appStyle(14.sp, FontWeight.Bold),
            color = palette.text,
        )
    }
}

@Composable
private fun AuthField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    hidden: Boolean = false,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    BasicTextField(
        value = value,
        onValueChange = onValue,
        singleLine = true,
        visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        textStyle = TextStyle(
            fontFamily = AppFont,
            color = palette.text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        cursorBrush = SolidColor(palette.primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(palette.scheme.background)
            .border(1.dp, palette.scheme.input, shape)
            .padding(horizontal = 14.dp),
        decorationBox = { inner ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(placeholder, style = appStyle(15.sp, FontWeight.Medium), color = palette.textMuted)
                }
                inner()
            }
        },
    )
}
