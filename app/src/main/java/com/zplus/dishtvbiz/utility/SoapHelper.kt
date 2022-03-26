package com.zplus.dishtvbiz.utility

import android.util.Log
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.MarshalDate
import org.ksoap2.serialization.MarshalFloat
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.kxml2.kdom.Element
import java.net.URL
import javax.net.ssl.SSLContext

object SoapHelper {
    private const val NAMESPACE = "http://tempuri.org/"
    private var methodName: String? = null
    var soapAction: String? = null
    private const val soapURL = "http://tempuri.org/"
    var envelope: SoapSerializationEnvelope? = null
    private var l_url: URL? = null
    var request: SoapObject? = null
    private val sslContext: SSLContext? = null
    var url: String? = null

    fun creatSoapHelper(url : String, userid : String, password : String, methodName : String, params : Array<Array<String>>):
    SoapSerializationEnvelope{
        Log.d("SoapHelper", "Start")
        this.methodName = methodName
        this.url = url
        val str = StringBuilder()
        str.append(soapURL)
        str.append(methodName)
        soapAction = str.toString()
        request = SoapObject(soapURL, methodName)
        envelope = SoapSerializationEnvelope(110)
        Log.d("SoapHelper", "Done")
        envelope!!.headerOut = arrayOfNulls(1)
        Log.d("username", userid)
        envelope!!.headerOut[0] = buildAuthHeader(userid, password)
        MarshalFloat().register(envelope)
        MarshalDate().register(envelope)
        //new MarshalDouble().register(this.envelope);
        val env = envelope
        env!!.bodyOut = request
        env.dotNet = true
        env.implicitTypes = true
        env.encodingStyle = "utf-8"
        env.enc = SoapEnvelope.ENC2003
        env.xsd = SoapEnvelope.XSD
        env.xsi = SoapEnvelope.XSI
        env.encodingStyle = SoapEnvelope.ENC
        for (param in params) {
            val str5 = param[0]
            val stringBuilder = java.lang.StringBuilder()
            stringBuilder.append("property : ")
            stringBuilder.append(param[1])
            Log.d(str5, stringBuilder.toString())
            request!!.addProperty(param[0], param[1])
        }
        return envelope as SoapSerializationEnvelope
    }

    private fun buildAuthHeader(userid: String, password: String): Element? {
        val createElement =
            Element().createElement(NAMESPACE, "AuthenticationHeader")
        var createElement2 =
            Element().createElement(NAMESPACE, "UserID")
        createElement2.addChild(4, userid)
        createElement.addChild(2, createElement2)
        createElement2 = Element().createElement(NAMESPACE, "Password")
        createElement2.addChild(4, password)
        createElement.addChild(2, createElement2)
        return createElement
    }
}