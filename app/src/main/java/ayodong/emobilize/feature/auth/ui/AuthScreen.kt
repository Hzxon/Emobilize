package ayodong.emobilize.feature.auth.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.ui.component.FormField
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.core.ui.theme.appStyle

@Composable
fun AuthScreen(
    state: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onShowLogin: () -> Unit,
    onShowRegister: () -> Unit,
    onSkip: () -> Unit,
) {
    val palette = LocalPalette.current
    BackHandler(enabled = state.page == AuthPage.Register) {
        onShowLogin()
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
                if (state.page == AuthPage.Login) "Log in to continue" else "Create an account",
                style = appStyle(15.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
            Spacer(Modifier.height(28.dp))
            if (state.page == AuthPage.Register) {
                FormField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = "Name",
                    error = state.nameError,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                )
                Spacer(Modifier.height(4.dp))
            }
            FormField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Email",
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            Spacer(Modifier.height(4.dp))
            FormField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Password",
                error = state.passwordError,
                keyboardType = KeyboardType.Password,
                imeAction = if (state.page == AuthPage.Register) ImeAction.Next else ImeAction.Done,
                hidden = true,
            )
            if (state.page == AuthPage.Register) {
                Spacer(Modifier.height(4.dp))
                FormField(
                    value = state.confirm,
                    onValueChange = onConfirmChange,
                    label = "Confirm password",
                    error = state.confirmError,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    hidden = true,
                )
            }
            Spacer(Modifier.height(16.dp))
            val submitLabel = if (state.page == AuthPage.Login) "Log in" else "Create account"
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(AppMetrics.actionHeight)
                    .clip(RoundedCornerShape(AppMetrics.actionRadius))
                    .background(palette.scheme.primary)
                    .clickable(onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    submitLabel,
                    style = appStyle(14.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                    color = palette.scheme.primaryForeground,
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(AppMetrics.actionHeight)
                    .clip(RoundedCornerShape(ShadcnRadius.lg))
                    .background(palette.scheme.secondary)
                    .clickable(onClick = onSkip),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Skip",
                    style = appStyle(14.sp, FontWeight.Bold),
                    color = palette.scheme.secondaryForeground,
                )
            }
            Spacer(Modifier.height(18.dp))
            if (state.page == AuthPage.Login) {
                SwitchLine(
                    prompt = "Need an account?",
                    action = "Register",
                    onClick = onShowRegister,
                )
            } else {
                SwitchLine(
                    prompt = "Already have an account?",
                    action = "Log in",
                    onClick = onShowLogin,
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
