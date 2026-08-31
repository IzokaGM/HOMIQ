package com.homiq.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.ui.viewmodel.AppLockViewModel
import com.homiq.app.ui.viewmodel.SyncViewModel

@Composable
fun OnboardingScreen(
    syncViewModel: SyncViewModel,
    appLockViewModel: AppLockViewModel,
    onFinished: (createFirstProperty: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val syncState by syncViewModel.state.collectAsStateWithLifecycle()
    var step by rememberSaveable { mutableStateOf(0) }
    var pin by rememberSaveable { mutableStateOf("") }
    var confirmPin by rememberSaveable { mutableStateOf("") }
    var pinError by rememberSaveable { mutableStateOf(false) }

    val authorizationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        syncViewModel.completeAuthorization(result.data)
    }

    val pendingResolution = syncState.pendingResolution
    if (pendingResolution != null) {
        LaunchedEffect(pendingResolution) {
            syncViewModel.resolutionLaunched()
            authorizationLauncher.launch(
                IntentSenderRequest.Builder(
                    pendingResolution.intentSender,
                ).build(),
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(PaddingValues(horizontal = 24.dp, vertical = 24.dp)),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                OnboardingBrand(step = step)
                when (step) {
                    0 -> WelcomeStep()
                    1 -> AccountStep(
                        connected = syncState.runtime.enabled,
                        syncing = syncState.runtime.isSyncing,
                        onConnect = syncViewModel::connect,
                    )
                    else -> SecurityStep(
                        pin = pin,
                        confirmPin = confirmPin,
                        pinError = pinError,
                        onPinChanged = {
                            pin = it.filter(Char::isDigit).take(8)
                            pinError = false
                        },
                        onConfirmChanged = {
                            confirmPin = it.filter(Char::isDigit).take(8)
                            pinError = false
                        },
                    )
                }
            }

            Column(
                modifier = Modifier.padding(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                when (step) {
                    0 -> Button(
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.onboarding_start))
                    }
                    1 -> {
                        Button(
                            onClick = { step = 2 },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                stringResource(
                                    if (syncState.runtime.enabled) {
                                        R.string.onboarding_continue
                                    } else {
                                        R.string.onboarding_continue_offline
                                    },
                                ),
                            )
                        }
                        TextButton(
                            onClick = { step = 0 },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.back))
                        }
                    }
                    else -> {
                        Button(
                            onClick = {
                                val wantsPin = pin.isNotBlank() || confirmPin.isNotBlank()
                                val validPin = !wantsPin || (pin.length in 4..8 && pin == confirmPin)
                                val saved = if (wantsPin && validPin) appLockViewModel.setPin(pin) else validPin
                                if (saved) {
                                    onFinished(true)
                                } else {
                                    pinError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.onboarding_add_property))
                        }
                        OutlinedButton(
                            onClick = { onFinished(false) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.onboarding_dashboard))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingBrand(step: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_homika_mark),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp).size(30.dp),
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = "${step + 1}/3",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WelcomeStep() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = stringResource(R.string.onboarding_welcome_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        FeatureRow(Icons.Outlined.CalendarMonth, stringResource(R.string.onboarding_feature_booking))
        FeatureRow(Icons.Outlined.Payments, stringResource(R.string.onboarding_feature_money))
        FeatureRow(Icons.Outlined.CloudDone, stringResource(R.string.onboarding_feature_sync))

        LanguageChooser()
    }
}

@Composable
private fun LanguageChooser() {
    val configuration = LocalConfiguration.current
    val explicit = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val language = if (explicit.isBlank()) configuration.locales[0].language else explicit.substringBefore(",").substringBefore("-")

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Outlined.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
            }
            HorizontalDivider()
            LanguageChoice(
                label = stringResource(R.string.language_malay),
                selected = language == "ms",
                onClick = { setLanguage("ms") },
            )
            LanguageChoice(
                label = stringResource(R.string.language_english),
                selected = language != "ms",
                onClick = { setLanguage("en") },
            )
        }
    }
}

@Composable
private fun LanguageChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

private fun setLanguage(tag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
}

@Composable
private fun AccountStep(
    connected: Boolean,
    syncing: Boolean,
    onConnect: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.onboarding_account_title), style = MaterialTheme.typography.headlineMedium)
            Text(
                stringResource(R.string.onboarding_account_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = if (connected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    Icons.Outlined.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp),
                )
                Text(
                    stringResource(if (connected) R.string.sync_connected else R.string.onboarding_google_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    stringResource(if (connected) R.string.onboarding_connected_body else R.string.onboarding_google_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!connected) {
                    Button(
                        onClick = onConnect,
                        enabled = !syncing,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (syncing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.size(8.dp))
                        }
                        Text(stringResource(R.string.onboarding_google_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityStep(
    pin: String,
    confirmPin: String,
    pinError: Boolean,
    onPinChanged: (String) -> Unit,
    onConfirmChanged: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.onboarding_security_title), style = MaterialTheme.typography.headlineMedium)
            Text(
                stringResource(R.string.onboarding_security_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.onboarding_pin_optional), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = pin,
                    onValueChange = onPinChanged,
                    label = { Text(stringResource(R.string.pin)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = pinError,
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = onConfirmChanged,
                    label = { Text(stringResource(R.string.confirm_pin)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = pinError,
                )
                if (pinError) {
                    Text(
                        stringResource(R.string.onboarding_pin_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(9.dp).size(20.dp),
            )
        }
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
