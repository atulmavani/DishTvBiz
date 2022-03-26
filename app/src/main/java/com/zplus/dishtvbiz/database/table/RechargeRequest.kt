package com.zplus.dishtvbiz.database.table

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RechargeRequest (
    @PrimaryKey open var _ID : Int = 0,
    open var request_datetime : String = "",
    open var recharge_txn_code : String = "",
    open var operator_type_code : String = "",
    open var operator_type : String = "",
    open var recharge_type : String = "",
    open var recharge_type_code : String  = "",
    open var gateway_slug : String = "",
    open var from_sim_lapu_no : String = "",
    open var to_sim_lapu_no : String = "",
    open var amount : String = "",
    open var category_name : String = "",
    open var sub_category_name : String = "",
    open var sub_category_code : String = "",
    open var rechargetype_code : String = "",
    open var from_sim_pin_no : String = "",
    open var imei : String = "",
    open var circle : String = "",
    open var entity_type : String = "",
    open var entity_id : String = ""
) : RealmObject()