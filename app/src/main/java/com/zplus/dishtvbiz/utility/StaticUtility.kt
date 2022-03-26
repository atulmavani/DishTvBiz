package com.zplus.dishtvbiz.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.zplus.dishtvbiz.model.response.MainResponse
import org.json.JSONArray
import org.w3c.dom.Document
import org.xml.sax.InputSource
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap


object StaticUtility {

    //region for dishtv
    const val user_id_public = "24"
    const val password_public = "4dS7sI921cfd"
    const val user_id_public1 = "128"
    const val password_public1 = "6EksdH#dbeIl*65M"
    //endregion

    //region for dishtv method name
    const val Sendotp = "GetEntityOTPbyRMN"
    const val VerifyOTP = "ValidateEntityByOTPRMN"
    const val getalldealer = "GetTaggedEntityDetailById"
    const val rechargetocustomer = "ProcessRechargeV3"
    const val getvcdetail = "GetSubscriberInfoVCLogV3"
    const val transfertodealer = "InsertProcessForwardTransaction"


    //region for login shared preference
    val LOGINPREFERENCE = "loginpreference"
    val AUTHTOKEN = "auth_token"
    val LOGOURL = "logo_url"
    val FIRMNAME = "firm_name"
    val APPID = "app_id"
    val APPSECRET = "app_secret"
    //endregion

    //region For device information
    var devicename = ""
    var deviceos = ""
    var app_version = ""
    var device_id = ""
    val DEVICEINFOPREFERENCE = "deviceinfoPreference"
    var DeviceName = "DeviceName"
    var Device_Name = "Device_Name"
    var DeviceOs = "DeviceOs"
    var App_Version = "App_Version"
    //endregion

    //SharedPreferences Name
    var DATA = "data"
    //end

    //staging
    val APP_ID = "98mif739527394rer12294a1951df7dd11fa3d3123"
    val APP_SECRET = "d1ebadd478dfrer8afed3595ac93812a1dc12398mi"
    val CONTENT_TYPE = "application/json"
    val CONTENT_TYPE_FORM = "application/x-www-form-urlencoded"

    //live
    /*val APP_ID = "a4f5c444412eb78bf7ec428b29bd2e44"
    val APP_SECRET = "9b1179b09265f69bad5c9572377cb93a"
    val CONTENT_TYPE = "application/json"*/

    //staging
    val URLZPLUS= "http://18.220.66.173/me-autobots/robotic-mob-app/"

    //live
    //val URLMAIN = "http://roboaccount.co.in/me-autobots/robotic-mob-app/"

    //url dishtvbiz
    const val Dishtv_Main_Url = "http://public.webservices.easy-pay.in/"
    const val Dishtv_Main_Url1 = "http://webservices.dishtv.in/"

    //region for idea api name
    const val REGISTRATION = "registration/v1/self"
    const val PREAUTHORIZATION = "preauth/v1/preauthorize"
    const val AUTHORIZATION = "az/v1/authorization"
    const val TOKEN = "az/v1/token"
    const val SENDOTP = "WebService/MobileApp/EPRSOTP.asmx?wsdl"
    const val VERIFYOTP = "WebService/MobileApp/EPRSOTP.asmx?wsdl"
    const val RECHARGETOCUSTOMER = "Services/Mobile/Trade/TradeRecharge.asmx?wsdl"
    const val GETVCDETAILE = "Services/Mobile/Trade/TradeSubscriberInfo.asmx?wsdl"
    const val GETALLDEALER = "WebService/MobileApp/MobileApp.asmx?wsdl"
    const val TRANSACTIONTODEALER = "WebService/MobileApp/EPRSDetails.asmx?wsdl"
    const val GETCURRENTBALANCE = "adapters/EtopupAdapterVIL/balanceEnquiry"
    const val NORMALRECHARGE = "adapters/EtopupAdapterVIL/normalRecharge"

    //region for zplus api name
    const val LOGINMAIN = "login"
    const val GETAPPSETTING = "api/application-setting/get"
    const val LOGOUT = "logout"
    const val GETCONECTEDSIMLIST = "api/recharge-sims/list"
    const val UPDATEBALANCE = "api/balance/save"
    const val UPDATESIMSTATUS = "api/recharge-sims/update-sim-status"
    const val GETRECHARGEREQUEST = "api/recharges/list"
    const val RECHARGEREQUESTSTATUS = "api/recharges/update-status"
    const val UPDATERECHARGEREQUEST = "api/recharges/recharge-response"
    //endregion

    fun entity_Type():JSONArray {
        var jsonArray = JSONArray()
        jsonArray.put("RET")
        jsonArray.put("SBA")
        jsonArray.put("PROMO")
        return jsonArray
    }

    fun showMessage(context : Context, message : String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun CheckInternetConnection(context: Context) : Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null
    }

    //region For make static url...
    fun queryStringUrl(context: Context): HashMap<String, String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val query = HashMap<String, String>()
        try {
            devicename =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, DeviceName).toString()
            deviceos =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, DeviceOs).toString()
            app_version =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, App_Version).toString()
            device_id =
                "dfsd"
            devicename = devicename.replace(" ", "")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        query.put("app_type","android")
        query.put("app_version", app_version)
        query.put("device_name", devicename)
        query.put("system_version", deviceos)
        query.put("device_id", device_id)
        /*return "?app_type=Android&app_version=" + app_version + "&device_name=" + devicename + "&system_version=" +
                deviceos + "&device_id=" + device_id*/
        return query

    }
    //endregion

    //region For make static url...
    fun queryStringUrl1(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        try {
            devicename =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, DeviceName).toString()
            deviceos =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, DeviceOs).toString()
            app_version =
                SharedPreference.GetPreference(context, DEVICEINFOPREFERENCE, App_Version).toString()
            device_id =
                "dfsd"
            devicename = devicename.replace(" ", "")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return "?app_type=android&app_version=" + app_version + "&device_name=" + devicename + "&system_version=" +
                deviceos + "&device_id=" + device_id


    }
    //endregion


    //region for mask number
    fun maskCardNumber(cardNumber: String, mask: String): String {

        // format the number
        var index = 0
        val maskedNumber = StringBuilder()
        for (i in 0 until mask.length) {
            val c = mask[i]
            if (c == '#') {
                maskedNumber.append(cardNumber[index])
                index++
            } else if (c == '*') {
                maskedNumber.append(c)
                index++
            } else {
                maskedNumber.append(c)
            }
        }

        // return the masked number
        return maskedNumber.toString()
    }
    //endregion





    //region For add fragment
    fun addFragmenttoActivity(manager: FragmentManager, fragment: Fragment, frameId: Int, backstak : String) {
        val transaction = manager.beginTransaction()
        transaction.replace(frameId, fragment)
        if(backstak == ""){
            transaction.addToBackStack(null)
        }else {
            transaction.addToBackStack(backstak)
        }
        transaction.commit()
    }
    //endregion

    fun getuuid() : String {
        return UUID.randomUUID().toString()
    }

    fun extractParametersFromURL(str: String): String {
        var code = ""
        var split = str.split("?")
        split = split[1].split("=")
        return split[1]
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceIMEI(mContext : Context): String {
        var deviceUniqueIdentifier: String = ""
        val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (null != tm) {
            deviceUniqueIdentifier = tm.deviceId
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length) {
            deviceUniqueIdentifier =
                Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)
        }
        return deviceUniqueIdentifier
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceIMSI(mContext : Context): String {
        var deviceUniqueIdentifier: String = ""
        val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        deviceUniqueIdentifier = tm.subscriberId
        return deviceUniqueIdentifier
    }

    fun ConverttohexString(str: String): String {
        val charArray = str.toCharArray()
        val stringBuffer = StringBuffer()
        for (hexString in charArray) {
            stringBuffer.append(Integer.toHexString(hexString.toInt()))
        }
        return stringBuffer.toString()
    }

    fun convertHexToString(hex: String): String? {
        val sb = java.lang.StringBuilder()
        val temp = java.lang.StringBuilder()
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        var i = 0
        while (i < hex.length - 1) {
            //grab the hex in pairs
            val output = hex.substring(i, i + 2)
            //convert hex to decimal
            val decimal = output.toInt(16)
            //convert the decimal to character
            sb.append(decimal.toChar())
            temp.append(decimal)
            i += 2
        }
        println("Decimal : $temp")
        return sb.toString()
    }

    fun paresedouble(str: String): String {
        val parseDouble = str.toDouble()
        return ((Math.ceil(parseDouble / 10.0) * 10.0).toInt() - parseDouble.toInt()).toString()
    }


    //region for convert response to string
    fun convertStreamToString(response: Response<MainResponse>): String {
        val reader = BufferedReader(InputStreamReader(response.errorBody()!!.byteStream()))
        val sb = StringBuilder()

        var line = reader.readLine()
        try {
            while (line != null) {
                sb.append(line).append('\n')
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
    //endregion

    fun convertStringToXMLDocument(xmlString: String): Document? { //Parser that produces DOM object trees from XML content
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        //API to obtain DOM Document instance
        var builder: DocumentBuilder? = null
        try { //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder()
            //Parse the content to Document object
            return builder.parse(InputSource(StringReader(xmlString)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}