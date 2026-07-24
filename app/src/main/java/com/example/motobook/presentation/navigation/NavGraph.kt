package com.example.motobook.presentation.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.motobook.MotoBookApplication
import com.example.motobook.domain.model.ChainEntry
import com.example.motobook.domain.model.TyrePressureEntry
import com.example.motobook.domain.model.WashEntry
import com.example.motobook.presentation.backup.BackupScreen
import com.example.motobook.presentation.bike.AddBikeScreen
import com.example.motobook.presentation.bike.BikeViewModel
import com.example.motobook.presentation.chain.ChainScreen
import com.example.motobook.presentation.dashboard.DashboardScreen
import com.example.motobook.presentation.dashboard.DashboardViewModel
import com.example.motobook.presentation.fuel.AddFuelScreen
import com.example.motobook.presentation.fuel.FuelHistoryScreen
import com.example.motobook.presentation.fuel.FuelViewModel
import com.example.motobook.presentation.history.HistoryScreen
import com.example.motobook.presentation.history.HistoryViewModel
import com.example.motobook.presentation.language.LanguageSelectionScreen
import com.example.motobook.presentation.mileage.MileageStatsScreen
import com.example.motobook.presentation.mileage.MileageViewModel
import com.example.motobook.presentation.service.AddServiceScreen
import com.example.motobook.presentation.service.ServiceHistoryScreen
import com.example.motobook.presentation.service.ServiceViewModel
import com.example.motobook.presentation.settings.SettingsScreen
import com.example.motobook.presentation.settings.SettingsViewModel
import com.example.motobook.presentation.splash.SplashScreen
import com.example.motobook.presentation.tyre.TyrePressureScreen
import com.example.motobook.presentation.wash.WashScreen
import com.example.motobook.presentation.welcome.WelcomeScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as MotoBookApplication
    val container = app.container

    val scope = rememberCoroutineScope()

    val isOnboardingComplete by container.userPreferences.isOnboardingComplete
        .collectAsState(initial = false)
    val currentLanguage by container.userPreferences.language
        .collectAsState(initial = "en")

    val bikes by container.bikeRepository.getAllBikes().collectAsState(initial = emptyList())
    val currentBike = bikes.firstOrNull()
    val activeBikeId = currentBike?.bikeId ?: 1L

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(tween(300)) + slideInVertically(tween(350)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) + slideOutVertically(tween(350)) { it / 4 } }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isOnboardingComplete = isOnboardingComplete,
                hasBike = bikes.isNotEmpty(),
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.LanguageSelection.route)
                },
                onRestoreBackupClick = {
                    navController.navigate(Screen.Backup.route)
                }
            )
        }

        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                currentLanguage = currentLanguage,
                onLanguageSelected = { lang ->
                    scope.launch { container.userPreferences.setLanguage(lang) }
                },
                onContinueClick = {
                    scope.launch { container.userPreferences.setOnboardingComplete(true) }
                    if (bikes.isEmpty()) {
                        navController.navigate(Screen.AddBike.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(
                    container.bikeRepository,
                    container.fuelRepository,
                    container.serviceRepository
                )
            )

            val bike by dashboardViewModel.selectedBike.collectAsState()
            val mileageStats by dashboardViewModel.mileageStats.collectAsState()
            val lastFuelEntry by dashboardViewModel.lastFuelEntry.collectAsState()

            DashboardScreen(
                bike = bike,
                mileageStats = mileageStats,
                lastFuelEntry = lastFuelEntry,
                onAddBikeClick = { navController.navigate(Screen.AddBike.route) },
                onNavigateToAddFuel = { navController.navigate(Screen.AddFuel.route) },
                onNavigateToFuelHistory = { navController.navigate(Screen.FuelHistory.route) },
                onNavigateToMileageStats = { navController.navigate(Screen.MileageStats.route) },
                onNavigateToAddService = { navController.navigate(Screen.AddService.route) },
                onNavigateToServiceHistory = { navController.navigate(Screen.ServiceHistory.route) },
                onNavigateToTyrePressure = { navController.navigate(Screen.TyrePressure.route) },
                onNavigateToWash = { navController.navigate(Screen.Wash.route) },
                onNavigateToChain = { navController.navigate(Screen.Chain.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.AddBike.route) {
            val bikeViewModel: BikeViewModel = viewModel(
                factory = BikeViewModel.Factory(container.bikeRepository)
            )

            AddBikeScreen(
                existingBike = currentBike,
                onSaveClick = { id, name, brand, model, year, reg, fuelType, tank, res, fPsi, rPsi ->
                    bikeViewModel.saveBike(id, name, brand, model, year, reg, fuelType, tank, res, fPsi, rPsi)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onBackClick = if (bikes.isNotEmpty()) {
                    { navController.popBackStack() }
                } else null
            )
        }

        composable(Screen.AddFuel.route) {
            val fuelViewModel: FuelViewModel = viewModel(
                factory = FuelViewModel.Factory(container.fuelRepository, activeBikeId)
            )

            AddFuelScreen(
                onSaveClick = { id, date, odo, qty, price, type, station, notes ->
                    fuelViewModel.saveFuelEntry(id, date, odo, qty, price, type, station, notes)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.FuelHistory.route) {
            val fuelViewModel: FuelViewModel = viewModel(
                factory = FuelViewModel.Factory(container.fuelRepository, activeBikeId)
            )
            val entries by fuelViewModel.fuelEntries.collectAsState()
            val totalCost by fuelViewModel.totalCost.collectAsState()
            val totalQty by fuelViewModel.totalQuantity.collectAsState()

            FuelHistoryScreen(
                entries = entries,
                totalCost = totalCost,
                totalQuantity = totalQty,
                onAddFuelClick = { navController.navigate(Screen.AddFuel.route) },
                onEditEntryClick = { entry -> /* Edit entry */ },
                onDeleteEntryClick = { entry -> fuelViewModel.deleteEntry(entry) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MileageStats.route) {
            val mileageViewModel: MileageViewModel = viewModel(
                factory = MileageViewModel.Factory(container.fuelRepository, activeBikeId)
            )
            val stats by mileageViewModel.mileageStats.collectAsState()

            MileageStatsScreen(
                stats = stats,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddService.route) {
            val serviceViewModel: ServiceViewModel = viewModel(
                factory = ServiceViewModel.Factory(container.serviceRepository, activeBikeId)
            )

            AddServiceScreen(
                onSaveClick = { id, date, odo, cat, items, isOfficial, center, cost, notes ->
                    serviceViewModel.saveServiceEntry(id, date, odo, cat, items, isOfficial, center, cost, notes)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ServiceHistory.route) {
            val serviceViewModel: ServiceViewModel = viewModel(
                factory = ServiceViewModel.Factory(container.serviceRepository, activeBikeId)
            )
            val entries by serviceViewModel.serviceEntries.collectAsState()
            val totalCost by serviceViewModel.totalServiceCost.collectAsState()

            ServiceHistoryScreen(
                entries = entries,
                totalCost = totalCost,
                onAddServiceClick = { navController.navigate(Screen.AddService.route) },
                onDeleteEntryClick = { entry -> serviceViewModel.deleteServiceEntry(entry) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.Factory(
                    container.fuelRepository,
                    container.serviceRepository,
                    container.tyrePressureRepository,
                    container.washRepository,
                    container.chainRepository,
                    activeBikeId
                )
            )

            val filteredItems by historyViewModel.filteredItems.collectAsState()
            val selectedCategory by historyViewModel.selectedCategory.collectAsState()

            HistoryScreen(
                items = filteredItems,
                selectedCategory = selectedCategory,
                onCategorySelect = { historyViewModel.setCategory(it) },
                onDeleteItem = { historyViewModel.deleteItem(it) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.TyrePressure.route) {
            val tyreEntries by container.tyrePressureRepository.getTyrePressureEntriesByBike(activeBikeId)
                .collectAsState(initial = emptyList())

            TyrePressureScreen(
                recommendedFrontPsi = currentBike?.frontTyrePressure ?: 28f,
                recommendedRearPsi = currentBike?.rearTyrePressure ?: 32f,
                entries = tyreEntries,
                onSaveLog = { front, rear, notes ->
                    scope.launch {
                        container.tyrePressureRepository.insertTyrePressureEntry(
                            TyrePressureEntry(
                                bikeId = activeBikeId,
                                date = System.currentTimeMillis(),
                                frontPsi = front.toFloatOrNull() ?: 28f,
                                rearPsi = rear.toFloatOrNull() ?: 32f,
                                notes = notes
                            )
                        )
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Wash.route) {
            val washEntries by container.washRepository.getWashEntriesByBike(activeBikeId)
                .collectAsState(initial = emptyList())

            WashScreen(
                entries = washEntries,
                onSaveWash = { type, cost, notes ->
                    scope.launch {
                        container.washRepository.insertWashEntry(
                            WashEntry(
                                bikeId = activeBikeId,
                                date = System.currentTimeMillis(),
                                washType = type,
                                cost = cost.toFloatOrNull(),
                                notes = notes
                            )
                        )
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Chain.route) {
            val chainEntries by container.chainRepository.getChainEntriesByBike(activeBikeId)
                .collectAsState(initial = emptyList())

            ChainScreen(
                entries = chainEntries,
                onSaveChain = { odo, lube, notes ->
                    scope.launch {
                        container.chainRepository.insertChainEntry(
                            ChainEntry(
                                bikeId = activeBikeId,
                                date = System.currentTimeMillis(),
                                odometer = odo.toFloatOrNull(),
                                lubricantType = lube,
                                notes = notes
                            )
                        )
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Backup.route) {
            val lastBackupTime by container.userPreferences.lastBackupTime.collectAsState(initial = 0L)
            val autoBackup by container.userPreferences.backupEnabled.collectAsState(initial = true)

            BackupScreen(
                lastBackupTime = lastBackupTime,
                autoBackupEnabled = autoBackup,
                onAutoBackupToggle = { enabled ->
                    scope.launch { container.userPreferences.setBackupEnabled(enabled) }
                },
                onCreateBackup = {
                    scope.launch {
                        container.userPreferences.setLastBackupTime(System.currentTimeMillis())
                    }
                },
                onRestoreBackup = { /* Trigger restore */ },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(container.userPreferences)
            )

            val themeName by settingsViewModel.selectedTheme.collectAsState()
            val glassIntensity by settingsViewModel.glassIntensity.collectAsState()
            val cardRadius by settingsViewModel.cardRadius.collectAsState()

            SettingsScreen(
                currentLanguage = currentLanguage,
                onLanguageChange = { lang -> settingsViewModel.setLanguage(lang) },
                currentTheme = themeName,
                onThemeChange = { theme -> settingsViewModel.setTheme(theme) },
                glassIntensity = glassIntensity,
                onGlassIntensityChange = { valInt -> settingsViewModel.setGlassIntensity(valInt) },
                cardRadius = cardRadius,
                onCardRadiusChange = { radius -> settingsViewModel.setCardRadius(radius) },
                onNavigateToBackup = { navController.navigate(Screen.Backup.route) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
