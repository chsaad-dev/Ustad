package com.ustad.presentation.customer

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.CategoryTemplates
import com.ustad.domain.model.ServiceCategory
import com.ustad.presentation.components.CategoryCard
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CustomerViewModel,
    onCategorySelected: (ServiceCategory) -> Unit,
    onNavigateToCityWaitlist: (String) -> Unit,
    onNavigateToDevMenu: () -> Unit
) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentJobs by viewModel.recentJobs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    var showCityDropdown by remember { mutableStateOf(false) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCityDropdown = true }
                    ) {
                        Icon(Icons.Rounded.LocationOn, contentDescription = "Location", tint = Primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sahiwal, Punjab", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)

                        DropdownMenu(
                            expanded = showCityDropdown,
                            onDismissRequest = { showCityDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sahiwal (Active ⚡)") },
                                onClick = { showCityDropdown = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Lahore (Waitlist 🚀)") },
                                onClick = {
                                    showCityDropdown = false
                                    onNavigateToCityWaitlist("Lahore")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Multan (Waitlist 🚀)") },
                                onClick = {
                                    showCityDropdown = false
                                    onNavigateToCityWaitlist("Multan")
                                }
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu")
                    }
                }
            )
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            // Greeting
            Text(
                text = "Assalam-o-Alaikum, Ali 👋",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "What service do you need today?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search Electrician, Plumber, AC...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Promo Banner
            Surface(
                shape = UstadShapes.medium,
                color = Primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mahir Karigar 30 Minute Me ⚡",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verified Ustads in Sahiwal • Direct booking • Cash on completion",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Categories Section Title
            Text(
                text = "Services Categories",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // 2-Column Grid for filtered categories
            val filteredCategories = if (searchQuery.isBlank()) {
                CategoryTemplates.categories
            } else {
                CategoryTemplates.categories.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            }


            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                for (i in filteredCategories.indices step 2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CategoryCard(
                                title = filteredCategories[i].name,
                                icon = filteredCategories[i].icon,
                                onClick = {
                                    viewModel.selectCategory(filteredCategories[i])
                                    onCategorySelected(filteredCategories[i])
                                }
                            )
                        }
                        if (i + 1 < filteredCategories.size) {
                            Box(modifier = Modifier.weight(1f)) {
                                CategoryCard(
                                    title = filteredCategories[i + 1].name,
                                    icon = filteredCategories[i + 1].icon,
                                    onClick = {
                                        viewModel.selectCategory(filteredCategories[i + 1])
                                        onCategorySelected(filteredCategories[i + 1])
                                    }
                                )
                            }
                        } else {

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Recent Jobs Header & Empty State
            Text(
                text = "Recent Jobs",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            if (recentJobs.isEmpty()) {
                Surface(
                    shape = UstadShapes.medium,
                    color = Background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WorkHistory,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "No recent jobs yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Select a category above to create your first job request!",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
