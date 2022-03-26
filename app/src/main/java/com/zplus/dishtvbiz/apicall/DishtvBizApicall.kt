package com.zplus.dishtvbiz.apicall

import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import com.zplus.dishtvbiz.utility.SoapHelper
import com.zplus.dishtvbiz.utility.StaticUtility
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

object DishtvBizApicall {
    internal class AsyncCallWS(handler : Handler) :
        AsyncTask<String?, Void?, String>() {
        var handler = handler
        override fun onPostExecute(result: String) { //Set response
            val msg = Message()
            msg.obj = result
            msg.arg2 = 0
            msg.arg1 = 0
            handler.sendMessage(msg)
        }

        override fun onPreExecute() { //Make ProgressBar invisible
        }

        protected fun onProgressUpdate(vararg values: Void) {}

        override fun doInBackground(vararg p0: String?): String {
            SoapHelper.envelope!!.setOutputSoapObject(SoapHelper.request)
            var resTxt = ""

            // Create HTTP call object
            // Create HTTP call object
            val androidHttpTransport = HttpTransportSE(SoapHelper.url)
            androidHttpTransport.debug = true

            resTxt = try { // Invoke web service
                androidHttpTransport.call(SoapHelper.soapAction, SoapHelper.envelope)
                // Get the response
                //val response = SoapHelper.envelope!!.bodyIn as SoapObject
                // Assign it to resTxt variable static variable
                androidHttpTransport.responseDump
                //response.toString()
            } catch (e: Exception) { //Print error
                e.printStackTrace()
                //Assign error message to resTxt
                "Error occured"
            }

            return resTxt

        }
    }
}