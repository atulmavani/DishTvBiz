package com.zplus.dishtvbiz.model.body

data class LoginBodyParam(var username : String, var password : String, var firm_id : String)

data class LogOutBodyParam(var user_token : String)

data class BodyParam(var key : String, var data  :String)

data class UpdateSimStatusBodyParam(var hash_id : String, var status : String)

data class RechargeRequestBodyParam(var hash_id : String)

data class UpdateRechargeStatus(var recharge_id : String, var txn_id : String, var amount : String,
                                var current_balance : String, var status : String, var msg : String)

data class UpdateCurrentBalance(var hash_id : String, var amount : String)