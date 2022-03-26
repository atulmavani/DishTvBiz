package com.zplus.dishtvbiz.apicall

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import com.zplus.dishtvbiz.apiClient.ZplusApiclient
import com.zplus.dishtvbiz.model.body.*
import com.zplus.dishtvbiz.model.response.MainResponse
import com.zplus.dishtvbiz.utility.SharedPreference
import com.zplus.dishtvbiz.utility.StaticUtility
import com.zplus.dishtvbiz.apiInterface.ZplusApiInterface
import com.zplus.dishtvbiz.database.model.LogModel
import com.zplus.dishtvbiz.database.table.LogTable
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object ZplusApicall {
    //private lateinit var databaseHandler: DBHelper
    //region for login
    fun Logincall(body: LoginBodyParam, handler: Handler, context: Context) {
        var id = body.username
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.Logincall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET,StaticUtility.queryStringUrl(context),
            body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    if(response.code() == 200) {
                        msg.obj = response.body()
                        msg.arg2 = 0
                    }else{
                        msg.obj = StaticUtility.convertStreamToString(response).replace("\n","")
                        msg.arg2 = 1
                    }
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
               /* val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                databaseHandler = DBHelper(context)
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                databaseHandler.addlog(id, "Login Error:= "+sw.toString(),current)*/
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for get app setting
    fun GetAppSettingcall(handler: Handler, context: Context) {
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.GetAppsettingcall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET,authtoken,
            StaticUtility.queryStringUrl(context)
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    if(response.code() == 200) {
                        msg.obj = response.body()
                        msg.arg2 = 0
                    }else{
                        msg.obj = StaticUtility.convertStreamToString(response).replace("\n","")
                        msg.arg2 = 1
                    }
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for logout
    fun DoLogoutcall(handler: Handler, context: Context) {
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        var body = LogOutBodyParam(authtoken)
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.DoLogoutcall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET,StaticUtility.queryStringUrl(context),body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    msg.obj = response.body()
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,"0",
                    "Logout Error :="+sw.toString(),current)
                logModel.addLog(realm, log)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for update current balance request
    fun UpdatecurrentBalancecall(body : UpdateCurrentBalance, handler: Handler, context: Context) {
        var id = body.hash_id
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.UpdateCurrentBalancecall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET, authtoken, StaticUtility.queryStringUrl(context)
            ,body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    msg.obj = response.body()
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,"0",
                    "Update Current balance Error:= "+sw.toString(),current)
                logModel.addLog(realm, log)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for get connected sim list
    fun GetConnectedSimListcall(handler: Handler, context: Context) {
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.GetConnectedSimListcall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET, authtoken,StaticUtility.queryStringUrl(context)
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    msg.obj = response.body()
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,"0",
                    "Connected Sim list Error:="+sw.toString(),current)
                logModel.addLog(realm, log)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for update sim status detail
    fun UpdateSimStatuscall(body : UpdateSimStatusBodyParam, handler: Handler, context: Context) {
        var txt_code = body.hash_id
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.UpdateSimStatuscall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET, authtoken, StaticUtility.queryStringUrl(context)
            ,body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    msg.obj = response.body()
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,txt_code,
                    "update sim status Error :="+sw.toString(),current)
                logModel.addLog(realm, log)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequestcall(body : RechargeRequestBodyParam, handler: Handler, context: Context) {
        var txt_code = body.hash_id
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.GetRechargeRequestcall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET, authtoken, StaticUtility.queryStringUrl(context)
            ,body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                val msg = Message()
                if(response.code() == 200) {
                    msg.obj = response.body()
                    msg.arg2 = 0
                }else{
                    msg.obj = StaticUtility.convertStreamToString(response).replace("\n","")
                    msg.arg2 = 1
                }
                msg.arg1 = 0
                handler.sendMessage(msg)
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,txt_code,
                    "Get recharge Request Error:= "+sw.toString(),current)
                logModel.addLog(realm, log)
                val msg = Message()
                msg.arg2 = 2
                msg.arg1 = 0
                handler.sendMessage(msg)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion

    //region for update recharge status request
    fun UpdateRechargeRequestStatuscall(body : UpdateRechargeStatus, handler: Handler, context: Context) {
        var txt_code = body.txn_id
        var authtoken = SharedPreference.GetPreference(context,StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN).toString()
        val client = ZplusApiclient.client.create(ZplusApiInterface::class.java)
        client.UpdateRechargeRequestStatuscall(
            StaticUtility.CONTENT_TYPE, StaticUtility.APP_ID, StaticUtility.APP_SECRET, authtoken, StaticUtility.queryStringUrl(context)
            ,body
        ).enqueue(object : Callback<MainResponse> {
            @SuppressLint("ShowToast")
            override fun onResponse(call: Call<MainResponse>, response: Response<MainResponse>) {
                if (handler != null) {
                    val msg = Message()
                    if(response.code() == 200) {
                        msg.obj = response.body()
                        msg.arg2 = 0
                    }else{
                        msg.obj = StaticUtility.convertStreamToString(response).replace("\n","")
                        msg.arg2 = 1
                    }
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }

            override fun onFailure(call: Call<MainResponse>, t: Throwable) {
                t.stackTrace
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                t.printStackTrace(pw)
                var id = 0
                var logModel = LogModel()
                lateinit var realm: Realm
                realm = Realm.getDefaultInstance()
                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val current = sdf.format(Date())
                if(logModel.getlog(realm).size > 0){
                    id = logModel.getLastid(realm)._ID + 1
                }
                var log = LogTable(id,txt_code,
                    "update recharge request Error :="+sw.toString(),current)
                logModel.addLog(realm, log)
                val msg = Message()
                msg.arg2 = 2
                msg.arg1 = 0
                handler.sendMessage(msg)
                //  StaticUtility.showMessage(mContext, t.toString())
                //Creating SendMail object
                /*StaticUtility.sendMail(
                    "Getting error in Dealer login from server in MainActivity.\n",
                    t.toString()
                )*/
            }
        })
    }
    //endregion
}