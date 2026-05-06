package com.example.matchtail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.matchtail.data.repositories.AuthListener
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.ui.theme.MatchTailTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Project Owners: Hadar Lachmy, Tal Eyal
 */
class MainActivity : AppCompatActivity() {
    var navController: NavController? = null
    var previousIsLogged: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        supportFragmentManager.executePendingTransactions()
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        navController = navHostFragment?.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_bar)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }

        if (UserRepository.getInstance().isLogged()) {
            bottomNavigationView.visibility = View.VISIBLE
        }

        UserRepository.getInstance().addAuthStateListener(object : AuthListener {
            override fun onAuthStateChanged() {
                if (previousIsLogged == UserRepository.getInstance().isLogged()) return

                previousIsLogged = UserRepository.getInstance().isLogged()
                navController?.let {
                    val options =
                        NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
                            .setPopUpTo(
                                it.graph.findStartDestination().id,
                                inclusive = false,
                                saveState = true
                            ).build()
                    if (previousIsLogged == true) {
                        bottomNavigationView.visibility = View.VISIBLE
                        it.navigate(R.id.postsListFragment, null, options)
                        it.graph.setStartDestination(R.id.postsListFragment)
                    } else {
                        bottomNavigationView.visibility = View.GONE
                        it.navigate(R.id.loginFragment, null, options)
                        it.graph.setStartDestination(R.id.loginFragment)
                    }
                }
            }
        })
    }
}