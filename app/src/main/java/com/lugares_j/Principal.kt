package com.lugares_j

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares_j.databinding.ActivityPrincipalBinding

class Principal : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarPrincipal.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_lugar, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        actualiza(navView)
    }

    private fun actualiza(navView: NavigationView) { // Permite ver nombre usuario, correo y foto del usuario
        val vista: View = navView.getHeaderView(0)

        val nombre: TextView = vista.findViewById(R.id.nombre_usuario)
        val correo: TextView = vista.findViewById(R.id.correo_usuario)
        val foto: ImageView = vista.findViewById(R.id.foto_usuario)

        val usuario = Firebase.auth.currentUser
        nombre.text = usuario?.displayName
        correo.text = usuario?.email
        var fotoUrl = usuario?.photoUrl.toString()
        if(fotoUrl.isNotEmpty()){
            Glide.with(this)
                .load(fotoUrl)
                .circleCrop()
                .into(foto)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_logoff->{
                Firebase.auth.signOut()  //Desconectarse
                finish()  //Regresar a pantalla anterior
                true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}