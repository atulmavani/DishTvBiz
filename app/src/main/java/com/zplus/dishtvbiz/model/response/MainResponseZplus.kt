package com.zplus.dishtvbiz.model.response

data class MainResponse(val code : String, val status : String, val message : String, val payload : Payload?)

data class Payload(val logo_url : String, val firm_name : String, val authUser : AuthUser, val app_id : String, val is_live : String,
                   val app_secret : String, val sim : ArrayList<Sim>, val recharges : ArrayList<RechargeRequest>,
                   val app_setting : App_Settings)

data class App_Settings(val force_update_msg : String, val force_update_title : String)

data class AuthUser(val user_token : String, val token_expired_on : String)

data class Sim(val lapu_no : String, val lapu_name : String, val sim_no : String, val pin_no : String,
               val sim_type : String, val recharge_type_name : String, val recharge_type_code : String, val hash_id : String,
               val has_credentials : String, var status : String, val entitytype : String, val entityid : String,
               val entityloginid : String/*, var app_credentials : AppCredential*/)

data class RechargeRequest(val request_datetime : String, val recharge_txn_code : String, val operator_type_code : String,
                           val operator_type : String, val recharge_type : String, val recharge_type_code : String,
                           val gateway_slug : String, val from_sim_lapu_no : String, val to_sim_lapu_no : String,
                           val amount : String, val category_name : String, val sub_category_name : String,
                           val sub_category_code : String, val rechargetype_code : String, val from_sim_pin_no : String,
                           val usertype : String, val is_frc : String, val salutation : String, val name : String,
                           var last_name : String, var date_of_birth : String, val address_1 : String, val address_2 : String,
                           val landmark : String, val pincode : String, val city : String, val state : String, val box_no : String,
                           val vc_no : String, val product_frc_id : String, val sbttype : String, val stbcategory : String, val av_pin : String,
                           val package_price : String)
