package com.example.motobook.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object LanguageSelection : Screen("language_selection")
    object Dashboard : Screen("dashboard")
    object AddBike : Screen("add_bike")
    object EditBike : Screen("edit_bike/{bikeId}") {
        fun createRoute(bikeId: Long) = "edit_bike/$bikeId"
    }
    object AddFuel : Screen("add_fuel")
    object EditFuel : Screen("edit_fuel/{fuelId}") {
        fun createRoute(fuelId: Long) = "edit_fuel/$fuelId"
    }
    object FuelHistory : Screen("fuel_history")
    object MileageStats : Screen("mileage_stats")
    object Maintenance : Screen("maintenance")
    object AddService : Screen("add_service")
    object ServiceHistory : Screen("service_history")
    object History : Screen("history")
    object TyrePressure : Screen("tyre_pressure")
    object Wash : Screen("wash")
    object Chain : Screen("chain")
    object Backup : Screen("backup")
    object Settings : Screen("settings")
}
