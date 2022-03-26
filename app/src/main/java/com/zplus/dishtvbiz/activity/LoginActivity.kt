package com.zplus.dishtvbiz.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.zplus.dishtvbiz.R
import com.zplus.dishtvbiz.apicall.ZplusApicall
import com.zplus.dishtvbiz.service.RechargeService
import com.zplus.dishtvbiz.utility.NetworkAvailable
import com.zplus.dishtvbiz.utility.SharedPreference
import com.zplus.dishtvbiz.utility.StaticUtility
import com.zplus.dishtvbiz.model.body.LoginBodyParam
import com.zplus.dishtvbiz.model.response.MainResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    lateinit var loginHandler: Handler
    var mContext = this@LoginActivity
    var PERMISSION_ALL = 1
    var type = 0
    var PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_SMS
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DeviceInfo(mContext)
        if(hasPermissions(PERMISSIONS)) {
            if (SharedPreference.GetPreference(mContext, StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN) != null) {
                startActivity(Intent(mContext, MainActivity::class.java))
                finish()
            } else {
                val stopServiceIntent = Intent(mContext, RechargeService::class.java)
                stopService(stopServiceIntent)
            }
        }
        btn_login.setOnClickListener{
            type = 1
            startActivity(Intent(mContext, MainActivity::class.java))
            //finish()
            /*if(edt_user_name.text.toString().isNotEmpty()) {
                user_name_text_input.error = null
                if(edt_password.text.toString().isNotEmpty()) {
                    password_text_input.error = null
                    if(edt_firm_id.text.toString().isNotEmpty()) {
                        firm_id_input_layout.error = null
                        if(hasPermissions(PERMISSIONS)) {
                            Login()
                            NetworkAvailable(loginHandler).execute()
                        }
                    }else{
                        firm_id_input_layout.error = "Enter Company id...!"
                        edt_firm_id.requestFocus()
                        //StaticUtility.showMessage(mContext, "Enter Company id...!")
                    }
                }else{
                    password_text_input.error = "Enter Password...!"
                    edt_password.requestFocus()
                    //StaticUtility.showMessage(mContext, "Enter Password...!")
                }
            }else{
                user_name_text_input.error = "Enter Username...!"
                edt_user_name.requestFocus()
                //StaticUtility.showMessage(mContext, "Enter Username...!")
            }*/
        }
    }

    fun hasPermissions(permissions: Array<String>) : Boolean {
        val per = ArrayList<String>()
        for(permision in permissions){
            if(ActivityCompat.checkSelfPermission(mContext, permision) != PackageManager.PERMISSION_GRANTED) {
                per.add(permision)
            }
        }
        if(per.isNotEmpty()){
            var perm : Array<String?> = arrayOfNulls<String>(per.size)
            ActivityCompat.requestPermissions(mContext, per.toArray(perm),PERMISSION_ALL)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            requestCode ->

                if (grantResults.size > 0) {

                    val read_phone_state = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val send_sms = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    val recieve_sms = grantResults[2] == PackageManager.PERMISSION_GRANTED
                    val read_sms = grantResults[3] == PackageManager.PERMISSION_GRANTED

                    if (read_phone_state && send_sms && recieve_sms && read_sms) {
                        if(type == 0) {
                            if (SharedPreference.GetPreference(mContext, StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN)
                                != null) {
                                startActivity(Intent(mContext, MainActivity::class.java))
                                finish()
                            } else {
                                val stopServiceIntent = Intent(mContext, RechargeService::class.java)
                                stopService(stopServiceIntent)
                            }
                        }
                    } else {
                        exitProcess(0)

                    }
                }
        }
    }

    //region for login
    fun Login(){
        login_loader.visibility = View.VISIBLE
        loginHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    val body = LoginBodyParam(edt_user_name.text.toString(), edt_password.text.toString(), edt_firm_id.text.toString())
                    ZplusApicall.Logincall(body, loginHandler, mContext)
                }else {
                    login_loader.visibility = View.GONE
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            }else if(msg.arg1 == 0){
                login_loader.visibility = View.GONE
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    StaticUtility.showMessage(mContext, obj.optString("message"))
                }else {
                    var respo = msg.obj as MainResponse
                    StaticUtility.showMessage(mContext, respo.message)
                    if (respo.code == "200") {
                        SharedPreference.CreatePreference(mContext, StaticUtility.LOGINPREFERENCE)
                        SharedPreference.SavePreference(StaticUtility.AUTHTOKEN, respo.payload!!.authUser.user_token)
                        SharedPreference.SavePreference(StaticUtility.LOGOURL, respo.payload!!.logo_url)
                        SharedPreference.SavePreference(StaticUtility.FIRMNAME, respo.payload!!.firm_name)
                        SharedPreference.SavePreference(StaticUtility.APPID, respo.payload!!.app_id)
                        SharedPreference.SavePreference(StaticUtility.APPSECRET, respo.payload!!.app_secret)
                        /*GetAppSetting()
                        NetworkAvailable(loginHandler).execute()*/
                        startActivity(Intent(mContext, MainActivity::class.java))
                        finish()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get app setting
    fun GetAppSetting(){
        login_loader.visibility = View.VISIBLE
        loginHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    ZplusApicall.GetAppSettingcall(loginHandler, mContext)
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                login_loader.visibility = View.GONE
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    StaticUtility.showMessage(mContext, obj.optString("message"))
                }else {
                    var respo = msg.obj as MainResponse
                    StaticUtility.showMessage(mContext, respo.message)
                    if (respo.code == "200") {
                        if(respo.payload!!.is_live == "0"){
                            val builder = AlertDialog.Builder(this)
                            //set title for alert dialog
                            builder.setTitle(respo.payload!!.app_setting.force_update_title)
                            //set message for alert dialog
                            builder.setMessage(respo.payload!!.app_setting.force_update_msg)
                            builder.setIcon(android.R.drawable.ic_dialog_alert)
                            //performing positive action
                            builder.setPositiveButton("Update"){ _, which ->
                               exitProcess(0)
                            }
                            // Create the AlertDialog
                            val alertDialog: AlertDialog = builder.create()
                            // Set other dialog properties
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }else {
                            startActivity(Intent(mContext, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
            true
        })
    }
    //endregion

    //region DeviceInfo
    fun DeviceInfo(mContext: Context) {
        val model = Build.MODEL
        val model_name = Build.MANUFACTURER
        val os = Build.VERSION.RELEASE
        val manager = mContext.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(
                mContext.packageName, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        assert(info != null)
        val version = info!!.versionName
        SharedPreference.CreatePreference(mContext, StaticUtility.DEVICEINFOPREFERENCE)
        SharedPreference.SavePreference(StaticUtility.DeviceName, model)
        SharedPreference.SavePreference(StaticUtility.Device_Name, model_name)
        SharedPreference.SavePreference(StaticUtility.DeviceOs, os)
        SharedPreference.SavePreference(StaticUtility.App_Version, version)
    }//endregion
}
