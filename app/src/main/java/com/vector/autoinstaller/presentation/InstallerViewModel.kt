package com.vector.autoinstaller.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vector.autoinstaller.data.VectorInstallerRepositoryImpl
import com.vector.autoinstaller.domain.InstallVectorUseCase
import com.vector.autoinstaller.domain.InstallerProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class InstallerViewModel(
    private val installVector: InstallVectorUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(InstallerUiState())
    val uiState: StateFlow<InstallerUiState> = _uiState.asStateFlow()

    fun onCheckRootClicked() {
        if (_uiState.value.isRunning) return
        _uiState.value = _uiState.value.copy(rootStatus = RootUiStatus.Checking, messageText = "")
        viewModelScope.launch {
            val granted = runCatching { installVector.checkRootAccess() }.getOrDefault(false)
            _uiState.value = _uiState.value.copy(
                rootStatus = if (granted) RootUiStatus.Granted else RootUiStatus.Denied,
                messageText = if (granted) "تم منح صلاحية الروت بنجاح." else "افتح Magisk أو KernelSU واسمح للتطبيق بصلاحية الروت."
            )
        }
    }

    fun onModuleSelectionChanged(moduleName: String, selected: Boolean) {
        if (_uiState.value.isRunning) return
        val modules = _uiState.value.selectedModules.toMutableSet()
        if (selected) modules += moduleName else modules -= moduleName
        _uiState.value = _uiState.value.copy(selectedModules = modules, messageText = "")
    }

    fun onInstallClicked() {
        if (_uiState.value.isRunning || _uiState.value.selectedModules.isEmpty()) return

        viewModelScope.launch {
            installVector(_uiState.value.selectedModules)
                .catch {
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        messageText = "Installation failed."
                    )
                }
                .collect(::handleProgress)
        }
    }

    private fun handleProgress(progress: InstallerProgress) {
        val current = _uiState.value
        _uiState.value = when (progress) {
            InstallerProgress.Idle -> current.copy(isRunning = false, statusText = "", messageText = "")
            InstallerProgress.CheckingInternet -> current.copy(
                isRunning = true,
                statusText = "جارٍ فحص الاتصال بالإنترنت...",
                messageText = ""
            )

            InstallerProgress.CheckingRoot -> current.copy(
                isRunning = true,
                rootStatus = RootUiStatus.Checking,
                statusText = "جارٍ طلب صلاحية الروت..."
            )

            InstallerProgress.RootGranted -> current.copy(
                isRunning = true,
                rootStatus = RootUiStatus.Granted,
                statusText = "تم منح صلاحية الروت."
            )

            is InstallerProgress.Downloading -> current.copy(
                isRunning = true,
                statusText = "جارٍ تنزيل ${progress.moduleName}..."
            )

            is InstallerProgress.Installing -> current.copy(
                isRunning = true,
                statusText = "جارٍ تثبيت ${progress.moduleName}..."
            )

            InstallerProgress.Success -> current.copy(
                isRunning = true,
                messageText = "تم تثبيت الإضافات المحددة بنجاح.\nسيُعاد تشغيل الجهاز الآن."
            )

            InstallerProgress.Rebooting -> current.copy(
                isRunning = true,
                statusText = "جارٍ إعادة التشغيل...",
                messageText = "تم تثبيت الإضافات المحددة بنجاح."
            )

            is InstallerProgress.Error -> current.copy(
                isRunning = false,
                rootStatus = if (progress.error is com.vector.autoinstaller.domain.InstallerError.RootNotGranted) RootUiStatus.Denied else current.rootStatus,
                statusText = "",
                messageText = progress.error.message.orEmpty()
            )
        }
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = VectorInstallerRepositoryImpl(context.applicationContext)
            return InstallerViewModel(InstallVectorUseCase(repository)) as T
        }
    }
}
