package com.vector.autoinstaller.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vector.autoinstaller.domain.InstallerConstants
import com.vector.autoinstaller.domain.AppInstallerConstants

@Composable
fun InstallerScreen(viewModel: InstallerViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF070B14)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("EID ROOT", color = Color(0xFF22D3EE), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Text("برمجة أبو أيوب", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text("تثبيت إضافات Zygisk بسهولة وأمان", color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
            Spacer(Modifier.height(22.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("صلاحية الروت", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(state.rootStatus.label, color = state.rootStatus.color, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(state.rootStatus.symbol, color = state.rootStatus.color, style = MaterialTheme.typography.headlineMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = viewModel::onCheckRootClicked,
                        enabled = !state.isRunning,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("فحص وطلب صلاحية الروت") }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.onSectionChanged(InstallerSection.Modules) },
                    enabled = !state.isRunning,
                    modifier = Modifier.weight(1f)
                ) { Text("إضافات Zygisk") }
                OutlinedButton(
                    onClick = { viewModel.onSectionChanged(InstallerSection.Apps) },
                    enabled = !state.isRunning,
                    modifier = Modifier.weight(1f)
                ) { Text("التطبيقات") }
            }
            Spacer(Modifier.height(8.dp))

            val entries = if (state.section == InstallerSection.Modules)
                InstallerConstants.Modules.map { it.displayName }
            else
                AppInstallerConstants.Apps.map { it.displayName }

            Text(
                if (state.section == InstallerSection.Modules) "إضافات Zygisk" else "تطبيقات الإدارة",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            entries.forEach { displayName ->
                val selected = if (state.section == InstallerSection.Modules)
                    displayName in state.selectedModules
                else
                    displayName in state.selectedApps
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF10243A) else Color(0xFF111827)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(displayName, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(if (selected) "محدد للتثبيت" else "غير محدد", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = selected,
                            enabled = !state.isRunning,
                            onCheckedChange = {
                                if (state.section == InstallerSection.Modules)
                                    viewModel.onModuleSelectionChanged(displayName, it)
                                else
                                    viewModel.onAppSelectionChanged(displayName, it)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (state.section == InstallerSection.Modules) viewModel.onInstallClicked()
                    else viewModel.onInstallAppsClicked()
                },
                enabled = !state.isRunning && if (state.section == InstallerSection.Modules)
                    state.selectedModules.isNotEmpty() else state.selectedApps.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    if (state.section == InstallerSection.Modules) "تثبيت الإضافات المحددة"
                    else "تنزيل وتثبيت التطبيقات المحددة",
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.isRunning) {
                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (state.statusText.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(state.statusText, color = Color(0xFF22D3EE), textAlign = TextAlign.Center)
            }
            if (state.messageText.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(state.messageText, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().background(Color(0xFF111827), RoundedCornerShape(12.dp)).padding(14.dp))
            }
        }
    }
}

private val RootUiStatus.label: String
    get() = when (this) {
        RootUiStatus.Unknown -> "لم يتم الفحص"
        RootUiStatus.Checking -> "بانتظار موافقة المستخدم"
        RootUiStatus.Granted -> "تم منح الصلاحية"
        RootUiStatus.Denied -> "الصلاحية مرفوضة"
    }

private val RootUiStatus.symbol: String get() = if (this == RootUiStatus.Granted) "✓" else if (this == RootUiStatus.Denied) "!" else "•"
private val RootUiStatus.color: Color get() = when (this) {
    RootUiStatus.Granted -> Color(0xFF34D399)
    RootUiStatus.Denied -> Color(0xFFF87171)
    RootUiStatus.Checking -> Color(0xFFFBBF24)
    RootUiStatus.Unknown -> Color(0xFF94A3B8)
}
