package com.example.trubbi.activities

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trubbi.R
import com.example.trubbi.fragments.LoginFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar : Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toogle: ActionBarDrawerToggle //Boton hamburguesa
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration // SETTINGS HAMBURGUER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar) //Habilita la toolbar
        getSupportActionBar()?.setDisplayShowTitleEnabled(true) // Elimina el Title, de los fragments, en la toolbar
        drawerLayout = findViewById(R.id.drawer_layout) // menu drawer
        navigationView = findViewById(R.id.nav_view) //navegación

        navController = Navigation.findNavController(this,R.id.nav_host_fragment)

        navigationView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        //Creación del Botón Hamburguesa
        toogle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav_drawer,
            R.string.close_nav_drawer
        )

        //APPBAR SETTINGS
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf()
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        //MANTENGO LA HAMBURGUER EN LA TOOLBAR
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id != R.id.detailsFragment && destination.id != R.id.categoriesFragment){
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
            } else if(destination.id == R.id.detailsFragment){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                toolbar.setNavigationOnClickListener {
                    val fm: FragmentManager = this.supportFragmentManager
                    val ft = fm.beginTransaction()
                    ft.setCustomAnimations(R.anim.anim_test_left,R.anim.animation_test_right,R.anim.anim_test_left,R.anim.animation_test_right)
                    navController.popBackStack()
                    ft.commit()
                }
            } else if (destination.id == R.id.categoriesFragment){
                toolbar.setNavigationOnClickListener {
                    val navOptions = NavOptions.Builder().setEnterAnim(R.anim.animation_test_right).build()
                    navController.navigate(R.id.mainFragment, null, navOptions)
                }
            }
        }
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

    }

    //Navegación por ID: en el menu drawer el id es = al nombre del fragmento que figura en el navigation
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}