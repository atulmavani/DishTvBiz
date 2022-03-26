package com.zplus.dishtvbiz.apiInterface

import com.zplus.dishtvbiz.model.body.*
import com.zplus.dishtvbiz.model.response.MainResponse
import com.zplus.dishtvbiz.utility.StaticUtility
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ZplusApiInterface {
    @POST(StaticUtility.LOGINMAIN)
    fun Logincall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body body: LoginBodyParam
    ): Call<MainResponse>

    @POST(StaticUtility.GETAPPSETTING)
    fun GetAppsettingcall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>
    ): Call<MainResponse>

    @POST(StaticUtility.LOGOUT)
    fun DoLogoutcall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body body: LogOutBodyParam
    ): Call<MainResponse>

    @POST(StaticUtility.GETCONECTEDSIMLIST)
    fun GetConnectedSimListcall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>
    ): Call<MainResponse>

    @POST(StaticUtility.UPDATEBALANCE)
    fun UpdateCurrentBalancecall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body body : UpdateCurrentBalance
    ): Call<MainResponse>

    @POST(StaticUtility.UPDATESIMSTATUS)
    fun UpdateSimStatuscall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body bdy : UpdateSimStatusBodyParam
    ): Call<MainResponse>

    @POST(StaticUtility.GETRECHARGEREQUEST)
    fun GetRechargeRequestcall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body body : RechargeRequestBodyParam
    ): Call<MainResponse>

    @POST(StaticUtility.UPDATERECHARGEREQUEST)
    fun UpdateRechargeRequestStatuscall(
        @Header("Content-Type") Content_Type: String,
        @Header("App-Id") app_id: String,
        @Header("App-Secret") app_secret: String,
        @Header("Auth-Token") auth_token: String,
        @QueryMap(encoded = true) query: Map<String, String>,
        @Body body : UpdateRechargeStatus
    ): Call<MainResponse>
}