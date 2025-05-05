package pl.jsyty.audiobookshelfnative.features.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator

object TabsScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            Scaffold(
                content = { innerPadding ->
                    Box(
                        modifier = Modifier
                            .consumeWindowInsets(innerPadding)
                            .padding(innerPadding)
                    ) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(tab = HomeTab)
                        TabNavigationItem(tab = LibraryTab)
                        TabNavigationItem(tab = ProfileTab)
                    }
                }
            )
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            label = { Text(text = tab.options.title) },
            icon = {
                Icon(
                    painter = requireNotNull(tab.options.icon),
                    contentDescription = tab.options.title
                )
            })
    }
}
