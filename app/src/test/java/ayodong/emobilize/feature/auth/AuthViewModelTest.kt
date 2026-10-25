package ayodong.emobilize.feature.auth

import ayodong.emobilize.feature.auth.data.FakeAuthRepository
import ayodong.emobilize.feature.auth.domain.LoginUseCase
import ayodong.emobilize.feature.auth.domain.RegisterAccountUseCase
import ayodong.emobilize.feature.auth.ui.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {
    private val repository = FakeAuthRepository()
    private val viewModel = AuthViewModel(LoginUseCase(repository), RegisterAccountUseCase(repository))

    @Test
    fun registerSignsIn() {
        viewModel.showRegister()
        viewModel.updateName("Ayu")
        viewModel.updateEmail("ayu@example.com")
        viewModel.updatePassword("secret1")
        viewModel.updateConfirm("secret1")
        viewModel.submit()

        assertTrue(viewModel.uiState.value.signedIn)
    }

    @Test
    fun wrongPasswordShowsError() {
        repository.register("Ayu", "ayu@example.com", "secret1")
        viewModel.updateEmail("ayu@example.com")
        viewModel.updatePassword("wrong-password")
        viewModel.submit()

        assertFalse(viewModel.uiState.value.signedIn)
        assertEquals("Email or password is incorrect", viewModel.uiState.value.passwordError)
    }

    @Test
    fun switchingPageClearsPasswords() {
        viewModel.updatePassword("secret1")
        viewModel.showRegister()
        assertEquals("", viewModel.uiState.value.password)
    }
}
