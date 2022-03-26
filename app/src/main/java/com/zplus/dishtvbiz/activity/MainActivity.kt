package com.zplus.dishtvbiz.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.zplus.dishtvbiz.R
import com.zplus.dishtvbiz.apicall.ZplusApicall
import com.zplus.dishtvbiz.fragment.HomeFragment
import com.zplus.dishtvbiz.fragment.LogFragment
import com.zplus.dishtvbiz.model.response.MainResponse
import com.zplus.dishtvbiz.service.RechargeService
import com.zplus.dishtvbiz.utility.NetworkAvailable
import com.zplus.dishtvbiz.utility.SharedPreference
import com.zplus.dishtvbiz.utility.StaticUtility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    var doubleBackToExitPressedOnce = false
    var mContext =  this@MainActivity
    lateinit var loginHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
        setonclicklistner()
        StaticUtility.addFragmenttoActivity(supportFragmentManager,HomeFragment(),R.id.frame,"")

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if(supportFragmentManager.backStackEntryCount >1){
                super.onBackPressed()
            }else{
                if (doubleBackToExitPressedOnce) {
                    //super.onBackPressed()
                    System.exit(0)
                    return
                }
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }

        }
    }

    //region for setonclick listner
    private fun setonclicklistner() {
        ll_logout.setOnClickListener(this)
        ll_home.setOnClickListener(this)
        ll_log.setOnClickListener(this)
    }
    //endregion

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.ll_home -> {
                StaticUtility.addFragmenttoActivity(supportFragmentManager, HomeFragment(),R.id.frame,"")
            }
            R.id.ll_logout -> {
                DoLogout()
                NetworkAvailable(loginHandler).execute()
            }
            R.id.ll_log -> {
                StaticUtility.addFragmenttoActivity(supportFragmentManager, LogFragment(),R.id.frame,HomeFragment::class.java.name)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    //region for get sim list
    fun DoLogout(){
        loginHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    ZplusApicall.DoLogoutcall(loginHandler, mContext)
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                var respo = msg.obj as MainResponse
                if(respo.code == "200"){
                    val stopServiceIntent = Intent(mContext, RechargeService::class.java)
                    stopService(stopServiceIntent)
                    SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                    SharedPreference.ClearPreference(mContext, StaticUtility.DATA)
                    startActivity(Intent(mContext, LoginActivity::class.java))
                    finish()
                }
            }
            true
        })
    }
    //endregion

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {


        }

        return true
    }
}
