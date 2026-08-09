package com.ustad.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

data class ServiceCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val templateChips: List<String>
)

object CategoryTemplates {
    val categories = listOf(
        ServiceCategory(
            id = "Electrician",
            name = "Electrician",
            icon = Icons.Rounded.Bolt,
            templateChips = listOf(
                "Switch tripping",
                "Short circuit",
                "New wiring",
                "Fan repair",
                "UPS setup"
            )
        ),
        ServiceCategory(
            id = "Plumber",
            name = "Plumber",
            icon = Icons.Rounded.WaterDrop,
            templateChips = listOf(
                "Pipe leak",
                "Tap repair",
                "Tank cleaning",
                "Sanitary fitting",
                "Drain blockage"
            )
        ),
        ServiceCategory(
            id = "AC",
            name = "AC Technician",
            icon = Icons.Rounded.AcUnit,
            templateChips = listOf(
                "Gas refill",
                "Master service",
                "Cooling problem",
                "Installation / Uninstallation"
            )
        ),
        ServiceCategory(
            id = "Carpenter",
            name = "Carpenter",
            icon = Icons.Rounded.Handyman,
            templateChips = listOf(
                "Door lock repair",
                "Furniture assembly",
                "Cabinet fixing",
                "Hinge replacement"
            )
        ),
        ServiceCategory(
            id = "Painter",
            name = "Painter",
            icon = Icons.Rounded.FormatPaint,
            templateChips = listOf(
                "Single room paint",
                "Wall dampness touchup",
                "Exterior whitewash",
                "Door/Window polish"
            )
        ),
        ServiceCategory(
            id = "Bike Mechanic",
            name = "Bike Mechanic",
            icon = Icons.Rounded.TwoWheeler,
            templateChips = listOf(
                "Engine oil change",
                "Brake service",
                "Tire puncture",
                "Chain adjustment / Tuning"
            )
        )
    )

    fun getCategoryById(id: String): ServiceCategory? {
        return categories.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) }
    }
}
