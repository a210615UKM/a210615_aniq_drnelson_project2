package com.example.a210615_aniq_drnelson_project2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a210615_aniq_drnelson_project2.navigation.AppScreen
@Composable
fun BottomNavigationBar(navController: NavController) {

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val navItems = listOf(
        Triple("Home", Icons.Default.Home, AppScreen.Main.name),
        Triple("Donate", Icons.Default.Favorite, AppScreen.Donate.name),
        Triple("Jobs", Icons.Default.Work, AppScreen.Jobs.name),
        Triple("Volunteer", Icons.Default.VolunteerActivism, AppScreen.Volunteer.name)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        navItems.forEach { (label, icon, route) ->

            val isActive = currentRoute == route

            Column(
                modifier = Modifier
                    .clickable {
                        navController.navigate(route) {
                            popUpTo(AppScreen.Main.name)
                            launchSingleTop = true
                        }
                    }
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (isActive)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else
                                Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint =
                            if (isActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = label,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color =
                        if (isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}