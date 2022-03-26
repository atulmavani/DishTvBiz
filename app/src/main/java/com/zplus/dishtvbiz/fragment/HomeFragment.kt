package com.zplus.dishtvbiz.fragment

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager

import com.zplus.dishtvbiz.R
import com.zplus.dishtvbiz.adapter.AdapterSimList
import com.zplus.dishtvbiz.apicall.DishtvBizApicall
import com.zplus.dishtvbiz.apicall.ZplusApicall
import com.zplus.dishtvbiz.database.migration.RealmMigrations
import com.zplus.dishtvbiz.database.model.LogModel
import com.zplus.dishtvbiz.database.model.SimListModel
import com.zplus.dishtvbiz.database.table.LogTable
import com.zplus.dishtvbiz.database.table.SimList
import com.zplus.dishtvbiz.model.body.UpdateSimStatusBodyParam
import com.zplus.dishtvbiz.model.response.MainResponse
import com.zplus.dishtvbiz.reciever.NetworkChangeReceiver
import com.zplus.dishtvbiz.service.RechargeService
import com.zplus.dishtvbiz.utility.NetworkAvailable
import com.zplus.dishtvbiz.utility.SharedPreference
import com.zplus.dishtvbiz.utility.SoapHelper
import com.zplus.dishtvbiz.utility.StaticUtility
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.XML
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), NetworkChangeReceiver.ConnectivityReceiverListener {

    var device_name = ""
    var model_name = ""
    lateinit var mContext : Activity
    lateinit var adapter : AdapterSimList
    lateinit var sim_list : RealmResults<SimList>
    lateinit var sim_list_db : RealmResults<SimList>
    lateinit var realm : Realm
    var simListModel = SimListModel()
    var logModel = LogModel()
    lateinit var getconnectedsimlistHandler: Handler
    lateinit var updatesimstatusHandler: Handler
    lateinit var RegistrationHandler: Handler
    lateinit var PreauthorizationHandler: Handler
    lateinit var AuthorizationHandler: Handler
    lateinit var TokenHandler: Handler
    lateinit var SendOtpHandler: Handler
    lateinit var verifyOtpHandler: Handler
    lateinit var loginHandler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        mContext = activity!!
        model_name = SharedPreference.GetPreference(mContext, StaticUtility.DEVICEINFOPREFERENCE,
            StaticUtility.DeviceName).toString()
        model_name = model_name.replace(" ", "")
        device_name = SharedPreference.GetPreference(mContext, StaticUtility.DEVICEINFOPREFERENCE,
            StaticUtility.Device_Name).toString()
        mContext.registerReceiver(NetworkChangeReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        Realm.init(activity!!)
        var c = RealmConfiguration.Builder().schemaVersion(3).
            migration(RealmMigrations())
        //c.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(c.build())

        realm = Realm.getDefaultInstance()
        setdata(view)

        return view
    }

    //region for set data
    private fun setdata(view: View) {
        sim_list_db = simListModel.getSimList(realm)
        sim_list = simListModel.getSimList(realm)

        if(sim_list.size>0){
            for(sim in sim_list){
                if(sim.status == "1"){
                    if(!isServiceRunning(RechargeService::class.java.name)){
                        mContext.startService(Intent(mContext, RechargeService::class.java))
                    }
                    break
                }
            }
        }

        view.recyclrer_sim_list.layoutManager = LinearLayoutManager(mContext)
        adapter = AdapterSimList(mContext, sim_list,
            object : AdapterSimList.OnClick {
                override fun OnClick(sim: SimList, type : Int) {
                    if(type == 0){
                        var arry = arrayOf(arrayOf("RMN",sim.lapu_no))
                        var simobj = SimList(sim._ID,sim.lapu_no, sim.lapu_name, sim.sim_no, sim.pin_no, sim.sim_type,
                            sim.recharge_type_name, sim.recharge_type_code, sim.hash_id, sim.has_credentials, "0","0",
                            "0","0","0","0","0","0")
                        simListModel.addSim(realm, simobj)
                        sim_list = simListModel.getSimList(realm)
                        adapter.updateData(sim_list)
                        StaticUtility.showMessage(mContext, "Logout Successfully...!")
                        UpdateSimStatus(sim.hash_id,"0")
                        NetworkAvailable(updatesimstatusHandler).execute()
                    }else {
                        SendOtp(view, sim)
                        NetworkAvailable(SendOtpHandler).execute()

                    }
                }
            })
        view.recyclrer_sim_list.adapter = adapter
        /*GetConnectedSimList(view)
        NetworkAvailable(getconnectedsimlistHandler).execute()*/



        view.swiperefresh.setOnRefreshListener {
            sim_list_db = simListModel.getSimList(realm)
            GetConnectedSimList(view)
            NetworkAvailable(getconnectedsimlistHandler).execute()
        }
    }
    //endregion


    //region for send otp
    private fun SendOtp(view: View, sim : SimList) {
        view.home_loader.visibility = View.VISIBLE
        SendOtpHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("RMN",sim.lapu_no))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url+StaticUtility.SENDOTP,
                        StaticUtility.user_id_public,StaticUtility.password_public, StaticUtility.Sendotp,arry)
                    DishtvBizApicall.AsyncCallWS(SendOtpHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                view.home_loader.visibility = View.GONE
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        //<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"><soap:Body><GetEntityOTPbyRMNResponse xmlns="http://tempuri.org/"><GetEntityOTPbyRMNResult><ErrorCode>0</ErrorCode><ErrorDescription /><EPRSOTPList><EPRSOTP><EntityID>2030942</EntityID><RespectiveMasterID>69678</RespectiveMasterID><EntityType>FOS</EntityType><RMN>7077494242</RMN><OTP>0</OTP><SecAllow>1</SecAllow><UTLAllow>0</UTLAllow><PRIAllow>0</PRIAllow><TRFAllow>1</TRFAllow><SVCAllow>0</SVCAllow><TechWebRowID>0</TechWebRowID></EPRSOTP></EPRSOTPList></GetEntityOTPbyRMNResult></GetEntityOTPbyRMNResponse></soap:Body></soap:Envelope>
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                var otprmnResult = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body")
                                    .optJSONObject("GetEntityOTPbyRMNResponse").optJSONObject("GetEntityOTPbyRMNResult")
                              if (otprmnResult.optString("ErrorCode") == "0") {
                                    var eprsotp = otprmnResult.optJSONObject("EPRSOTPList")
                                        .optJSONObject("EPRSOTP")
                                    var simobj = SimList(sim._ID, sim.lapu_no, sim.lapu_name, sim.sim_no, sim.pin_no, sim.sim_type,
                                        sim.recharge_type_name, sim.recharge_type_code, sim.hash_id, sim.has_credentials,
                                        sim.uuid, sim.circle, sim.status, eprsotp.optString("EntityType"), eprsotp.optString("EntityID"),
                                        eprsotp.optString("RespectiveMasterID"), sim.imei, sim.imsi)
                                    showOtpDialog(simobj)
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


    //region for otp dialog
    private fun showOtpDialog(sim : SimList) {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_otp_dialog)

        val img_close = dialog.findViewById(R.id.img_close) as ImageView
        val edt_otp = dialog.findViewById(R.id.edt_otp) as EditText
        val btn_submit = dialog.findViewById(R.id.btn_submit) as Button
        btn_submit.setOnClickListener {
            if(edt_otp.text.toString().isNotEmpty()) {
                if (StaticUtility.CheckInternetConnection(mContext)) {
                    verifyOtp(edt_otp.text.toString(),sim, dialog)
                    NetworkAvailable(verifyOtpHandler).execute()
                }
            }else{
                StaticUtility.showMessage(mContext, "Please Enter Otp...!")
            }
        }
        img_close.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    //endregion

    //region for send otp
    private fun verifyOtp(otp : String,sim: SimList, dialog: Dialog) {
        view!!.home_loader.visibility = View.VISIBLE
        verifyOtpHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var arry  = arrayOf(arrayOf("RMN",sim.lapu_no),arrayOf("OTP",otp),
                        arrayOf("EntityId",sim.entity_id),arrayOf("CellIMSINo",sim.imsi),arrayOf("CellIMEINo",sim.imei),
                        arrayOf("CellMFRName",device_name),arrayOf("CellMODELNo",model_name))
                    SoapHelper.creatSoapHelper(StaticUtility.Dishtv_Main_Url+StaticUtility.VERIFYOTP,
                        StaticUtility.user_id_public,StaticUtility.password_public, StaticUtility.VerifyOTP,arry)
                    DishtvBizApicall.AsyncCallWS(verifyOtpHandler).execute()
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                view!!.home_loader.visibility = View.GONE
                dialog.dismiss()
                when {
                    msg.arg2 == 0 -> {
                        var respo = msg.obj as String
                        if(!respo.equals("Error occured")) {
                            var json = XML.toJSONObject(respo)
                            if(!json.optJSONObject("soap:Envelope").optJSONObject("soap:Body").has("soap:Fault")){
                                var otprmnResult = json.optJSONObject("soap:Envelope").optJSONObject("soap:Body")
                                    .optJSONObject("ValidateEntityByOTPRMNResponse").optJSONObject("ValidateEntityByOTPRMNResult")
                                if (otprmnResult.optString("ErrorCode") == "0") {
                                    var simobj = SimList(sim._ID, sim.lapu_no, sim.lapu_name, sim.sim_no, sim.pin_no, sim.sim_type,
                                        sim.recharge_type_name, sim.recharge_type_code, sim.hash_id, sim.has_credentials,
                                        sim.uuid, "", "1",sim.entity_type,sim.entity_id,sim.master_id,sim.imei, sim.imsi)
                                    simListModel.addSim(realm, simobj)
                                    sim_list = simListModel.getSimList(realm)
                                    adapter.updateData(sim_list)
                                    if(!isServiceRunning(RechargeService::class.java.name)){
                                        mContext.startService(Intent(mContext, RechargeService::class.java))
                                    }
                                    UpdateSimStatus(sim.hash_id,"1")
                                    NetworkAvailable(updatesimstatusHandler).execute()
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

    //verifyotp response
    /*<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <soap:Body>
    <ValidateEntityByOTPRMNResponse xmlns="http://tempuri.org/">
    <ValidateEntityByOTPRMNResult>
    <ErrorCode>0</ErrorCode>
    <LoginID>0</LoginID>
    <UserID>2047171</UserID>
    <Password>0</Password>
    <Name />
    <MobileNo>9090825966</MobileNo>
    <IsActive>1</IsActive>
    <CreatedOn>0001-01-01T00:00:00</CreatedOn>
    <ModifiedOn>0001-01-01T00:00:00</ModifiedOn>
    <CellIMSINo>404051124094328</CellIMSINo>
    <CellIMEINo>358185065940766</CellIMEINo>
    <CellMFRName>samsung</CellMFRName>
    <CellMODELNo>SM-E700H</CellMODELNo>
    <IsMobileAppReg>0</IsMobileAppReg>
    <RegTIMEExp>0001-01-01T00:00:00</RegTIMEExp>
    <UpdAvail>0</UpdAvail>
    <SubUserType>FOS</SubUserType>
    <BizOps>1</BizOps>
    <CityID>0</CityID>
    <StateID>0</StateID>
    <PasswordChangedOn />
    <ASEID>0</ASEID>
    <EntitySubtype>FOS</EntitySubtype>
    </ValidateEntityByOTPRMNResult>
    </ValidateEntityByOTPRMNResponse>
    </soap:Body>
    </soap:Envelope>*/


    fun isServiceRunning(serviceClassName: String): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any { it.service.className == serviceClassName }
    }

    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.connectivityReceiverListener = this
    }
    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(isConnected) {
            if(sim_list.size>0){
                for(sim in sim_list){
                    if(sim.status == "1"){
                        if(!isServiceRunning(RechargeService::class.java.name)){
                            mContext.startService(Intent(mContext, RechargeService::class.java))
                        }
                        break
                    }
                }
            }
            // mContext.startService(Intent(mContext, RechargeService::class.java))
        }
        //Toast.makeText(mContext, isConnected.toString(), Toast.LENGTH_LONG).show()
    }

    //region for get sim list
    fun GetConnectedSimList(view : View){
        view.home_loader.visibility = View.VISIBLE
        getconnectedsimlistHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    ZplusApicall.GetConnectedSimListcall(getconnectedsimlistHandler, mContext)
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                view.home_loader.visibility = View.GONE
                view.swiperefresh.isRefreshing = false
                var respo = msg.obj as MainResponse
                if(respo.code == "200"){
                    for(sim in respo.payload!!.sim){
                        var imei = StaticUtility.getDeviceIMEI(mContext)
                        var uuid = StaticUtility.getuuid()
                        var imsi = StaticUtility.getDeviceIMSI(mContext)
                        if(sim_list.size > 0) {
                            var _id = simListModel.getLastSim(realm)._ID+1
                            var status = "0"
                            var circle = "0"
                            var entity_type = "0"
                            var master_id = "0"
                            var entity_id = "0"
                            var dsm_id = "0"

                            for (dbsim in sim_list_db) {
                                if (sim.lapu_no == dbsim.lapu_no) {
                                    status = dbsim.status
                                    _id = dbsim._ID
                                    uuid = dbsim.uuid
                                    circle = dbsim.circle
                                    entity_id = dbsim.entity_id
                                    master_id = dbsim.master_id
                                    entity_type = dbsim.entity_type
                                    imei = dbsim.imei
                                    imsi = dbsim.imsi
                                }
                            }
                            var simobj = SimList(_id,sim.lapu_no, sim.lapu_name, sim.sim_no, sim.pin_no, sim.sim_type,
                                sim.recharge_type_name, sim.recharge_type_code, sim.hash_id, sim.has_credentials, uuid,circle,status,
                                entity_type, entity_id, master_id,imei, imsi)
                            simListModel.addSim(realm, simobj)
                        }else{
                            var simobj = SimList(0,sim.lapu_no, sim.lapu_name, sim.sim_no, sim.pin_no, sim.sim_type,
                                sim.recharge_type_name, sim.recharge_type_code, sim.hash_id, sim.has_credentials,uuid,
                                "0","0","0","0","0",imei, imsi)
                            simListModel.addSim(realm, simobj)
                        }
                    }
                    for(dbsim in sim_list_db){
                        var isremove = true
                        var lapuno = dbsim.lapu_no
                        for(sim in respo.payload!!.sim){
                            if (sim.lapu_no == dbsim.lapu_no) {
                                isremove = false
                            }
                        }
                        if(isremove){
                            simListModel.delsim(realm,dbsim._ID)
                        }
                    }
                    sim_list = simListModel.getSimList(realm)
                    adapter.updateData(sim_list)
                }
            }
            true
        })
    }
    //endregion

    //region for update sim status
    fun UpdateSimStatus(hash_id : String, status : String){
        view!!.home_loader.visibility = View.VISIBLE
        updatesimstatusHandler = Handler(Handler.Callback { msg ->
            if(msg.arg1 == 1){
                if(msg.obj as Boolean) {
                    var body = UpdateSimStatusBodyParam(hash_id,status)
                    ZplusApicall.UpdateSimStatuscall(body,updatesimstatusHandler, mContext)
                }else
                    StaticUtility.showMessage(mContext,getString(R.string.network_error))
            }else if(msg.arg1 == 0){
                view!!.home_loader.visibility = View.GONE
                var respo = msg.obj as MainResponse
            }
            true
        })
    }
    //endregion
}
