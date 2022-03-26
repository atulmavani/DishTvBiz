package com.zplus.dishtvbiz.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.zplus.dishtvbiz.R
import com.zplus.dishtvbiz.activity.LoginActivity
import com.zplus.dishtvbiz.apicall.DishtvBizApicall
import com.zplus.dishtvbiz.apicall.ZplusApicall
import com.zplus.dishtvbiz.database.model.LogModel
import com.zplus.dishtvbiz.database.model.RechargeRequestModel
import com.zplus.dishtvbiz.database.model.SimListModel
import com.zplus.dishtvbiz.database.table.LogTable
import com.zplus.dishtvbiz.database.table.RechargeRequest
import com.zplus.dishtvbiz.database.table.SimList
import com.zplus.dishtvbiz.model.body.RechargeRequestBodyParam
import com.zplus.dishtvbiz.model.body.UpdateCurrentBalance
import com.zplus.dishtvbiz.model.body.UpdateRechargeStatus
import com.zplus.dishtvbiz.model.response.MainResponse
import com.zplus.dishtvbiz.utility.NetworkAvailable
import com.zplus.dishtvbiz.utility.SharedPreference
import com.zplus.dishtvbiz.utility.SoapHelper
import com.zplus.dishtvbiz.utility.StaticUtility
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.XML
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class RechargeService : Service() {
    var mContext: Context = this
    lateinit var RechargeReqHandler1: Handler
    lateinit var RechargeReqHandler2: Handler
    lateinit var RechargeReqHandler3: Handler
    lateinit var RechargeReqHandler4: Handler
    lateinit var RechargeReqHandler5: Handler
    lateinit var RechargeReqHandler6: Handler
    lateinit var RechargeReqHandler7: Handler
    lateinit var RechargeReqHandler8: Handler
    lateinit var RechargeReqHandler9: Handler
    lateinit var RechargeReqHandler10: Handler
    lateinit var UpdateStatusRechargeHandler: Handler
    lateinit var getcurrentbalanceHandler: Handler
    lateinit var currentbalanceHandler: Handler
    lateinit var RechargeReqUpdateHandler: Handler
    lateinit var updatesimstatusHandler: Handler
    lateinit var RechargeProcessHandler: Handler
    lateinit var rechargeCustomerHandler: Handler
    lateinit var GetvcDetailHandler: Handler
    lateinit var GetallDealerHandler: Handler
    lateinit var TransferDealerHandler: Handler
    var issend1 = true
    var issend2 = true
    var issend3 = true
    var issend4 = true
    var issend5 = true
    var issend6 = true
    var issend7 = true
    var issend8 = true
    var issend9 = true
    var issend10 = true
    var iscall = false
    var req1 = true
    var req2 = true
    var req3 = true
    var req4 = true
    var req5 = true
    var req6 = true
    var req7 = true
    var req8 = true
    var req9 = true
    var req10 = true
    var time = 0
    var balancechktime = 0
    var JSON = MediaType.parse("application/json; charset=utf-8")
    lateinit var realm: Realm
    var simListModel = SimListModel()
    var rechargeRequestModel = RechargeRequestModel()
    var logModel = LogModel()
    lateinit var Sim_List: RealmResults<SimList>
    lateinit var rechargearray: RealmResults<RechargeRequest>
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground()
        else
            startForeground(1, Notification())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startMyOwnForeground(){
        var NOTIFICATION_CHANNEL_ID = "permanence"
        var channelName = "Background Service"
        var chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

        manager.createNotificationChannel(chan)

        var notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        var  notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Send a notification that service is started
        Log.d("Service:=>", "Service started.")
        realm = Realm.getDefaultInstance()
        Sim_List = simListModel.getSimList(realm)
        rechargearray = rechargeRequestModel.getRequest(realm)
        // Do a periodic task
        val handler = Handler()
        val delay = 1000 * 120 * 60 //milliseconds
        currentbalance(0)
        //dorechargeRequest()
        handler.postDelayed(object : Runnable {
            override fun run() {
                //do something
                //Log.d("Service:=>","Service Worked!")


                /*val calendar = Calendar.getInstance()
                calendar.time = Date()
                val currentMilli = calendar.get(Calendar.MILLISECOND)
                val startHourMilli = 24
                var endHourMilli = 6
                // just add one day (in your case in millis)
                if (startHourMilli > endHourMilli) endHourMilli += 24 * 60 * 60 * 1000
                // now here you can check without any problems
                if (currentMilli >= startHourMilli && currentMilli < endHourMilli) {
                    // within timeframe, do stuff you need
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            getRechargeList()
                            handler.postDelayed(this, delay_night.toLong())
                        }
                    }, delay_night.toLong())
                } else {
                    // not in timeframe, find solution
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            getRechargeList()
                            handler.postDelayed(this, delay.toLong())
                        }
                    }, delay.toLong())
                }*/
                //getRechargeList()
                //get current balance in 30 minute interval in day and 3 hours of interval in night and do entry in database and check every time in datatbase
                //for next get current balance
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())

        getRechargeList()
        getRechargeList2()
        getRechargeList3()
        getRechargeList4()
        getRechargeList5()
        getRechargeList6()
        getRechargeList7()
        getRechargeList8()
        getRechargeList9()
        getRechargeList10()

        return START_STICKY
    }

    fun currentbalance(pos : Int){
        Handler().postDelayed({
            Log.d("currentbalance:=>", "in")
            /*GetCurrentbalance(pos)
            NetworkAvailable(getcurrentbalanceHandler).execute()*/
            balancechktime = 1000*180*60
        }, balancechktime.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        if(SharedPreference.GetPreference(mContext, StaticUtility.LOGINPREFERENCE, StaticUtility.AUTHTOKEN) != null){
            var broadcastIntent = Intent()
            broadcastIntent.action = "restartservice"
            broadcastIntent.setClass(this, Restarter::class.java)
            this.sendBroadcast(broadcastIntent)
        }
        Log.d("Service:=>", "Service destroyed.")
    }



    //region for current balance
    fun UpdatCurrentBalance(hash_id: String, bal: String, pos :Int) {
        var posi = pos
        currentbalanceHandler = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    val body = UpdateCurrentBalance(
                        hash_id, bal
                    )
                    ZplusApicall.UpdatecurrentBalancecall(body, currentbalanceHandler, mContext)
                } else
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
            } else if (msg.arg1 == 0) {
                posi += 1
                if (posi < Sim_List.size) {
                    /*GetCurrentbalance(posi)
                    NetworkAvailable(getcurrentbalanceHandler).execute()*/
                }else{
                    currentbalance(0)
                }
            }
            true
        })
    }
    //endregion

    fun getRechargeList() {
        if(issend1) {
            issend1 = false
            Handler().postDelayed({
                Log.d("Recharge1:=>", "in")
                if (Sim_List.size > 0) {
                    if (Sim_List[0].status == "1") {
                        GetRechargeRequest1(Sim_List[0])
                        NetworkAvailable(RechargeReqHandler1).execute()
                    }else{
                        issend1 = true
                        getRechargeList()
                    }
                }else{
                    issend1 = true
                    getRechargeList()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList2() {
        if(issend2) {
            issend2 = false
            Handler().postDelayed({
                Log.d("Recharge2:=>", "in")
                if (Sim_List.size > 1) {
                    if (Sim_List[1].status == "1") {
                        GetRechargeRequest2(Sim_List[1])
                        NetworkAvailable(RechargeReqHandler2).execute()
                    }else{
                        issend2 = true
                        getRechargeList2()
                    }
                }else{
                    issend2 = true
                    getRechargeList2()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList3() {
        if(issend3) {
            issend3 = false
            Handler().postDelayed({
                Log.d("Recharge3:=>", "in")
                if (Sim_List.size > 2) {
                    if (Sim_List[2].status == "1") {
                        GetRechargeRequest3(Sim_List[2])
                        NetworkAvailable(RechargeReqHandler3).execute()
                    }else{
                        issend3 = true
                        getRechargeList3()
                    }
                }else{
                    issend3 = true
                    getRechargeList3()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList4() {
        if(issend4) {
            issend4 = false
            Handler().postDelayed({
                Log.d("Recharge4:=>", "in")
                if (Sim_List.size > 3) {
                    if (Sim_List[3].status == "1") {
                        GetRechargeRequest4(Sim_List[3])
                        NetworkAvailable(RechargeReqHandler4).execute()
                    }else{
                        issend4 = true
                        getRechargeList4()
                    }
                }else{
                    issend4 = true
                    getRechargeList4()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList5() {
        if(issend5) {
            issend5 = false
            Handler().postDelayed({
                Log.d("Recharge5:=>", "in")
                if (Sim_List.size > 4) {
                    if (Sim_List[4].status == "1") {
                        GetRechargeRequest5(Sim_List[4])
                        NetworkAvailable(RechargeReqHandler5).execute()
                    }else{
                        issend5 = true
                        getRechargeList5()
                    }
                }else{
                    issend5 = true
                    getRechargeList5()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList6() {
        if(issend6) {
            issend6 = false
            Handler().postDelayed({
                Log.d("Recharge6:=>", "in")
                if (Sim_List.size > 5) {
                     if (Sim_List[5].status == "1") {
                        GetRechargeRequest6(Sim_List[5])
                        NetworkAvailable(RechargeReqHandler6).execute()
                    }else{
                        issend6 = true
                        getRechargeList6()
                    }
                }else{
                    issend6 = true
                    getRechargeList6()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList7() {
        if(issend7) {
            issend7 = false
            Handler().postDelayed({
                Log.d("Recharge7:=>", "in")
                if (Sim_List.size > 6) {
                    if (Sim_List[6].status == "1") {
                        GetRechargeRequest7(Sim_List[6])
                        NetworkAvailable(RechargeReqHandler7).execute()
                    }else{
                        issend7 = true
                        getRechargeList7()
                    }
                }else{
                    issend7 = true
                    getRechargeList7()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList8() {
        if(issend8) {
            issend8 = false
            Handler().postDelayed({
                Log.d("Recharge8:=>", "in")
                if (Sim_List.size > 7) {
                    if (Sim_List[7].status == "1") {
                        GetRechargeRequest8(Sim_List[7])
                        NetworkAvailable(RechargeReqHandler8).execute()
                    }else{
                        issend8 = true
                        getRechargeList8()
                    }
                }else{
                    issend8 = true
                    getRechargeList8()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList9() {
        if(issend9) {
            issend9 = false
            Handler().postDelayed({
                Log.d("Recharge9:=>", "in")
                if (Sim_List.size > 8) {
                    if (Sim_List[8].status == "1") {
                        GetRechargeRequest9(Sim_List[8])
                        NetworkAvailable(RechargeReqHandler9).execute()
                    }else{
                        issend9 = true
                        getRechargeList9()
                    }
                }else{
                    issend9 = true
                    getRechargeList9()
                }
                time = 10000
            }, time.toLong())
        }
    }

    fun getRechargeList10() {
        if(issend10) {
            issend10 = false
            Handler().postDelayed({
                Log.d("Recharge10:=>", "in")
                if (Sim_List.size > 9) {
                    if (Sim_List[9].status == "1") {
                        GetRechargeRequest10(Sim_List[9])
                        NetworkAvailable(RechargeReqHandler10).execute()
                    }else{
                        issend10 = true
                        getRechargeList10()
                    }
                }else{
                    issend10 = true
                    getRechargeList10()
                }
                time = 10000
            }, time.toLong())
        }
    }

    //region for get recharge request
    fun GetRechargeRequest1(sim: SimList) {
        RechargeReqHandler1 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler1, mContext)
                } else {
                    issend1 = true
                    getRechargeList()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend1 = true
                    getRechargeList()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req1 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend1 = true
                        getRechargeList()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest2(sim: SimList) {
        RechargeReqHandler2 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler2, mContext)
                } else {
                    issend2 = true
                    getRechargeList2()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend2 = true
                    getRechargeList2()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle,sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req2 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend2 = true
                        getRechargeList2()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest3(sim: SimList) {
        RechargeReqHandler3 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler3, mContext)
                } else {
                    issend3 = true
                    getRechargeList3()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend3 = true
                    getRechargeList3()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req3 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend3 = true
                        getRechargeList3()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest4(sim: SimList) {
        RechargeReqHandler4 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler4, mContext)
                } else {
                    issend4 = true
                    getRechargeList4()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend4 = true
                    getRechargeList4()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req4 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend4 = true
                        getRechargeList4()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest5(sim: SimList) {
        RechargeReqHandler5 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler5, mContext)
                } else {
                    issend5 = true
                    getRechargeList5()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend5 = true
                    getRechargeList5()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req5 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend5 = true
                        getRechargeList5()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest6(sim: SimList) {
        RechargeReqHandler6 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler6, mContext)
                } else {
                    issend6 = true
                    getRechargeList6()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend6 = true
                    getRechargeList6()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req6 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend6 = true
                        getRechargeList6()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest7(sim: SimList) {
        RechargeReqHandler7 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler7, mContext)
                } else {
                    issend7 = true
                    getRechargeList7()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend7 = true
                    getRechargeList7()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req7 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend7 = true
                        getRechargeList7()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest8(sim: SimList) {
        RechargeReqHandler8 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler8, mContext)
                } else {
                    issend8 = true
                    getRechargeList8()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend8 = true
                    getRechargeList8()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req8 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend8 = true
                        getRechargeList8()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest9(sim: SimList) {
        RechargeReqHandler9 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler9, mContext)
                } else {
                    issend9 = true
                    getRechargeList9()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend9 = true
                    getRechargeList9()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req9 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend9 = true
                        getRechargeList9()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for get recharge request
    fun GetRechargeRequest10(sim: SimList) {
        RechargeReqHandler10 = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Get Request Send",current)
                    logModel.addLog(realm, log)*/
                    var body = RechargeRequestBodyParam(sim.hash_id)
                    ZplusApicall.GetRechargeRequestcall(body, RechargeReqHandler10, mContext)
                } else {
                    issend10 = true
                    getRechargeList10()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id,
                        obj.toString(),current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    issend10 = true
                    getRechargeList10()
                }else {
                    var respo = msg.obj as MainResponse
                    /*val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,sim.hash_id, "Request Response get",current)
                    logModel.addLog(realm, log)*/
                    var hash_array = JSONArray()
                    for (rechreq in respo.payload!!.recharges) {
                        var isadd = true
                        var rechargesarray = rechargeRequestModel.getRequest(realm)
                        for(recharge in rechargesarray){
                            if(recharge.recharge_txn_code == rechreq.recharge_txn_code){
                                isadd = false
                                break
                            }
                        }
                        if(isadd) {
                            var id = 0
                            if (rechargesarray.size > 0) {
                                id = rechargeRequestModel.getLastRequest(realm)._ID + 1
                            }
                            var rechargerequestobj = RechargeRequest(id, rechreq.request_datetime, rechreq.recharge_txn_code,
                                rechreq.operator_type_code, rechreq.operator_type, rechreq.recharge_type, rechreq.recharge_type_code,
                                rechreq.gateway_slug, rechreq.from_sim_lapu_no, rechreq.to_sim_lapu_no, rechreq.amount, rechreq.category_name,
                                rechreq.sub_category_name, rechreq.sub_category_code, rechreq.rechargetype_code, rechreq.from_sim_pin_no,
                                sim.imei, sim.circle, sim.entity_type, sim.entity_id)
                            rechargeRequestModel.addrequest(realm, rechargerequestobj)
                            hash_array.put(rechreq.recharge_txn_code)
                        }
                    }
                    var mainobj = JSONObject()
                    mainobj.put("txn_id", hash_array)
                    mainobj.put("status", "accepted")
                    if (hash_array.length() > 0) {
                        req10 = false
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val current = sdf.format(Date())
                        var id = 0
                        if(logModel.getlog(realm).size > 0){
                            id = logModel.getLastid(realm)._ID + 1
                        }
                        var log = LogTable(id,sim.hash_id, "Update recharge request Send",current)
                        logModel.addLog(realm, log)
                        RechargeRequestUpdate(sim.hash_id)
                        var authtoken = SharedPreference.GetPreference(mContext,StaticUtility.LOGINPREFERENCE,
                            StaticUtility.AUTHTOKEN).toString()
                        OkHttpHandler(mainobj,JSON,authtoken,StaticUtility.queryStringUrl1(mContext),RechargeReqUpdateHandler)
                            .execute()
                    } else {
                        issend10 = true
                        getRechargeList10()
                    }
                }
            }
            true
        })
    }
    //endregion

    //region for update recharge request status
    class OkHttpHandler(
        var mainobj: JSONObject,
        var JSON: MediaType,
        var auth: String,
        var query: String,
        handler: Handler
    ) : AsyncTask<Void, Void, String>() {
        var client = OkHttpClient()
        var handler = handler
        override fun doInBackground(vararg params: Void?): String? {
            Log.d("Recharge update Get","send")
            val body = RequestBody.create(JSON, mainobj.toString())
            val request = Request.Builder()
                .addHeader("Content-Type", StaticUtility.CONTENT_TYPE)
                .addHeader("app-id", StaticUtility.APP_ID)
                .addHeader("app-secret", StaticUtility.APP_SECRET)
                .addHeader("auth-token", auth)
                .url(StaticUtility.URLZPLUS + StaticUtility.RECHARGEREQUESTSTATUS + query)
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            var responseobj = response.body().string()
            return responseobj
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var json = JSONObject(result)
            if (handler != null) {
                val msg = Message()
                msg.obj = result
                msg.arg1 = 0
                handler.sendMessage(msg)
            }
        }
    }
    //endregion

    //region for recharge request update
    fun RechargeRequestUpdate(hash_id : String) {
        RechargeReqUpdateHandler = Handler(Handler.Callback { msg ->
            var respo = JSONObject(msg.obj.toString())
            if (respo.optString("code") == "200") {

            }
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val current = sdf.format(Date())
            var id = 0
            if(logModel.getlog(realm).size > 0){
                id = logModel.getLastid(realm)._ID + 1
            }
            var log = LogTable(id,hash_id, respo.toString(),current)
            logModel.addLog(realm, log)

            dorechargeRequest()
            //getRechargeList()
            true
        })
    }
    //endregion

    //region for recharge request get from database
    fun dorechargeRequest() {
        if (rechargearray.size <= 0) {
            rechargearray = rechargeRequestModel.getRequest(realm)
        }
        if (!iscall && rechargearray.size > 0) {
            iscall = true
            if(rechargearray[0].entity_type == "FOS"){
                GetAllDealer(rechargearray[0])
                NetworkAvailable(GetallDealerHandler).execute()
            }else{
                GetVcDetail(rechargearray[0])
                NetworkAvailable(GetvcDetailHandler).execute()
            }
        }
    }
    //endregion

    //region for get all dealer detail
    private fun GetAllDealer(rechargeRequest: RechargeRequest) {
        GetallDealerHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("EntityType",rechargeRequest.entity_type),
                        arrayOf("EntityID",rechargeRequest.entity_id), arrayOf("RequestedEntityType","DL"),
                        arrayOf("BeatFlag","0"))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url+StaticUtility.GETALLDEALER,
                        StaticUtility.user_id_public,StaticUtility.password_public, StaticUtility.getalldealer,arry)
                    DishtvBizApicall.AsyncCallWS(GetallDealerHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        var dl_id = ""
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                var jsonarra = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").
                                        optJSONObject("GetTaggedEntityDetailByIdResponse").
                                    optJSONObject("GetTaggedEntityDetailByIdResult").optJSONArray("EntityDetails")
                                for(i in 0 until jsonarra.length()){
                                    var obj = jsonarra.optJSONObject(i)
                                    if(obj.optString("MobileNoTransaction") == rechargeRequest.to_sim_lapu_no){
                                        dl_id = obj.optString("EntityID")
                                        break
                                    }
                                }
                                if(dl_id == ""){
                                    UpdaterechargeStatus(rechargearray[0].recharge_txn_code, "", rechargearray[0].amount,
                                        "","failed", "Dealer not found...!")
                                    NetworkAvailable(UpdateStatusRechargeHandler).execute()
                                }else{
                                    TransfertoDealer(rechargearray[0], dl_id)
                                    NetworkAvailable(GetallDealerHandler).execute()
                                }
                            }else{
                                StaticUtility.showMessage(mContext, "Try again after sometime...!")
                            }
                        }else{
                            StaticUtility.showMessage(mContext, "Try again after sometime...!")
                        }
                    }
                    else -> StaticUtility.showMessage(mContext, "Try again after sometime...!")
                }
            }
            true
        })
    }
    //endregion

    //region for get all dealer detail
    private fun TransfertoDealer(rechargeRequest: RechargeRequest, dl_id : String) {
        TransferDealerHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("CompanyID","1"),
                        arrayOf("Process","SEC"), arrayOf("EntityIDFrom",rechargeRequest.entity_id),
                        arrayOf("EntityPassword",rechargeRequest.from_sim_pin_no), arrayOf("EntityIDTo",dl_id),
                        arrayOf("UtilizationType","0"), arrayOf("ItemType","0"), arrayOf("ItemNo","0"),
                        arrayOf("Amount",rechargeRequest.amount), arrayOf("Remarks","source"))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url+StaticUtility.TRANSACTIONTODEALER,
                        StaticUtility.user_id_public,StaticUtility.password_public, StaticUtility.transfertodealer,arry)
                    DishtvBizApicall.AsyncCallWS(TransferDealerHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        var dl_id = ""
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                var json = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").
                                    optJSONObject("InsertProcessForwardTransactionResponse").
                                    optJSONObject("InsertProcessForwardTransactionResult")
                               if(json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").
                                       optJSONObject("InsertProcessForwardTransactionResponse").
                                       optJSONObject("InsertProcessForwardTransactionResult").optString("ErrorCode")!="-100"){

                               }else{
                                   UpdaterechargeStatus(rechargearray[0].recharge_txn_code, "", rechargearray[0].amount,
                                       "","failed", json.optString("ErrorDescription"))
                                   NetworkAvailable(UpdateStatusRechargeHandler).execute()
                               }
                            }else{
                                StaticUtility.showMessage(mContext, "Try again after sometime...!")
                            }
                        }else{
                            StaticUtility.showMessage(mContext, "Try again after sometime...!")
                        }
                    }
                    else -> StaticUtility.showMessage(mContext, "Try again after sometime...!")
                }
            }
            true
        })
    }
    //endregion


    //region for get vc detail
    private fun GetVcDetail(rechargeRequest: RechargeRequest) {
        GetvcDetailHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("vcNo",""),
                        arrayOf("mobileNo",rechargeRequest.to_sim_lapu_no), arrayOf("BizOps","1"),
                        arrayOf("UserID",rechargeRequest.entity_id), arrayOf("UserType","DL"))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url1+StaticUtility.GETVCDETAILE,
                        StaticUtility.user_id_public1,StaticUtility.password_public1, StaticUtility.getvcdetail,arry)
                    DishtvBizApicall.AsyncCallWS(GetvcDetailHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        var dl_id = ""
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                json = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").
                                    optJSONObject("GetSubscriberInfoVCLogV3Response").
                                    optJSONObject("GetSubscriberInfoVCLogV3Result")
                                if(json.optString("ErrorCode") == "0"){
                                    var smsid = json.optString("SMSID")
                                    var vc_no = json.optString("VCNO")
                                    Rechargetocustomer(rechargearray[0], smsid, vc_no)
                                    NetworkAvailable(rechargeCustomerHandler).execute()
                                }else{
                                    UpdaterechargeStatus(rechargearray[0].recharge_txn_code, "", rechargearray[0].amount,
                                        "","failed", "Error in vc detail...!")
                                    NetworkAvailable(UpdateStatusRechargeHandler).execute()
                                }
                            }else{
                                StaticUtility.showMessage(mContext, "Try again after sometime...!")
                            }
                        }else{
                            StaticUtility.showMessage(mContext, "Try again after sometime...!")
                        }
                    }
                    else -> StaticUtility.showMessage(mContext, "Try again after sometime...!")
                }
            }
            true
        })
    }
    //endregion

    //region for Recharge to customer
    private fun Rechargetocustomer(rechargeRequest: RechargeRequest, smsid :String, vc_no : String) {
        rechargeCustomerHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("itzAccountNo","001"+rechargeRequest.entity_id),
                        arrayOf("password",rechargeRequest.from_sim_pin_no), arrayOf("smsID",smsid),
                        arrayOf("vcNo",vc_no), arrayOf("amount",rechargeRequest.amount),arrayOf("loginID",rechargeRequest.entity_id),
                        arrayOf("entityType","DL"), arrayOf("VersionNo","7.3.1"),
                        arrayOf("CellIMEINo",rechargeRequest.imei), arrayOf("BizOps","1"),
                        arrayOf("MobileNo",rechargeRequest.to_sim_lapu_no), arrayOf("bonusPoint","0"))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url1+StaticUtility.RECHARGETOCUSTOMER,
                        StaticUtility.user_id_public1,StaticUtility.password_public1, StaticUtility.rechargetocustomer,arry)
                    DishtvBizApicall.AsyncCallWS(rechargeCustomerHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        var dl_id = ""
                        /*<?xml version="1.0" encoding="utf-8"?>
                        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                        <soap:Body>
                        <ProcessRechargeV3Response xmlns="http://tempuri.org/">
                        <ProcessRechargeV3Result>CustomErrorMsg:Code :-5001|Insufficient fund. You can not debit more than 1.00.</ProcessRechargeV3Result>
                        </ProcessRechargeV3Response>
                        </soap:Body>
                        </soap:Envelope>*/
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                json = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").
                                    optJSONObject("ProcessRechargeV3Response")
                                /*if(json.optString("ErrorCode") == "0"){
                                    var smsid = json.optString("SMSID")
                                    var vc_no = json.optString("VCNO")
                                    Rechargetocustomer(rechargearray[0], smsid, vc_no)
                                    NetworkAvailable(GetvcDetailHandler).execute()
                                }else{
                                    UpdaterechargeStatus(rechargearray[0].recharge_txn_code, "", rechargearray[0].amount,
                                        "","failed", "Error in vc detail...!")
                                    NetworkAvailable(UpdateStatusRechargeHandler).execute()
                                }*/
                            }else{
                                StaticUtility.showMessage(mContext, "Try again after sometime...!")
                            }
                        }else{
                            StaticUtility.showMessage(mContext, "Try again after sometime...!")
                        }
                    }
                    else -> StaticUtility.showMessage(mContext, "Try again after sometime...!")
                }
            }
            true
        })
    }
    //endregion


    //region for recharge status update
    fun UpdaterechargeStatus(
        txt_code: String,
        tr_id: String,
        amount: String,
        cur_bal: String,
        status: String,
        masg: String
    ) {
        UpdateStatusRechargeHandler = Handler(Handler.Callback { msg ->
            if (msg.arg1 == 1) {
                if (msg.obj as Boolean) {
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,txt_code, "Recharge request update to server Send",current)
                    logModel.addLog(realm, log)
                    val body = UpdateRechargeStatus(txt_code, tr_id, amount, cur_bal, status, masg)
                    ZplusApicall.UpdateRechargeRequestStatuscall(body, UpdateStatusRechargeHandler, mContext)
                } else {
                    call()
                    StaticUtility.showMessage(mContext, getString(R.string.network_error))
                }
            } else if (msg.arg1 == 0) {
                if(msg.arg2 == 1){
                    var obj = JSONObject(msg.obj.toString())
                    if (obj.optString("code") == "401") {
                        SharedPreference.ClearPreference(mContext, StaticUtility.LOGINPREFERENCE)
                        startActivity(Intent(mContext, LoginActivity::class.java))
                        StaticUtility.showMessage(mContext, obj.optString("message"))
                    }
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,txt_code, "Recharge request update to server Error...!",
                        current)
                    logModel.addLog(realm, log)
                }else if(msg.arg2 == 2){
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    var id = 0
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,txt_code, "Recharge request update to server Error...!",
                        current)
                    logModel.addLog(realm, log)
                }else {
                    var id = 0
                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val current = sdf.format(Date())
                    if(logModel.getlog(realm).size > 0){
                        id = logModel.getLastid(realm)._ID + 1
                    }
                    var log = LogTable(id,txt_code,
                        "Recharge request updated to server."+masg,current)
                    logModel.addLog(realm, log)
                }
                call()

            }
            true
        })
    }
    //endregion

    fun <T> getlist(t: T): List<T> {
        return listOf(t)
    }

    fun call(){
        if (rechargearray.size > 0) {
           /* NormalRecharge(rechargearray[0])
            NetworkAvailable(RechargeProcessHandler).execute()*/
        } else {
            iscall = false
            //doGetrechargeRequest()
            if(!req1){
                req1 = true
                issend1 = true
                getRechargeList()
            }
            if(!req2){
                req2 = true
                issend2 = true
                getRechargeList2()
            }
            if(!req3){
                req3 = true
                issend3 = true
                getRechargeList3()
            }
            if(!req4){
                req4 = true
                issend4 = true
                getRechargeList4()
            }
            if(!req5){
                req5 = true
                issend5 = true
                getRechargeList5()
            }
            if(!req6){
                req6 = true
                issend6 = true
                getRechargeList6()
            }
            if(!req7){
                req7 = true
                issend7 = true
                getRechargeList7()
            }
            if(!req8){
                req8 = true
                issend8 = true
                getRechargeList8()
            }
            if(!req9){
                req9 = true
                issend9 = true
                getRechargeList9()
            }
            if(!req10){
                req10 = true
                issend10 = true
                getRechargeList10()
            }
        }
    }
}