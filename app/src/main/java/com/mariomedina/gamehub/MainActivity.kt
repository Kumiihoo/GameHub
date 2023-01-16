package com.mariomedina.gamehub

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.mariomedina.gamehub.activity.FormActivity
import com.mariomedina.gamehub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityMainBinding
    var actionBarDrawerToggle : ActionBarDrawerToggle ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val navController = findNavController(R.id.fragment)

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.rateus -> {
                Toast.makeText(this, "Valóranos", Toast.LENGTH_SHORT).show()
            }
            R.id.developer -> {
                startActivity(Intent(this, FormActivity::class.java))
            }
            R.id.favourite -> {
                Toast.makeText(this, "Favorito", Toast.LENGTH_SHORT).show()
            } R.id.share -> {
            Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show()
        }
            R.id.termsCondition -> {
                Toast.makeText(this, "Términos y Condiciones", Toast.LENGTH_SHORT).show()
            }
            R.id.privacyPolicy -> {
                Toast.makeText(this, "Privacidad", Toast.LENGTH_SHORT).show()
            }

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }
}