package com.zplus.dishtvbiz.database.table

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SimList(
    @PrimaryKey open var _ID: Int = 0,
    open var lapu_no: String = "",
    open var lapu_name: String = "",
    open var sim_no: String = "",
    open var pin_no: String = "",
    open var sim_type: String = "",
    open var recharge_type_name: String = "",
    open var recharge_type_code: String = "",
    open var hash_id: String = "",
    open var has_credentials: String = "",
    open var uuid: String = "",
    open var circle: String = "",
    open var status: String = "",
    open var entity_type: String = "",
    open var entity_id: String = "",
    open var master_id: String = "",
    open var imei: String = "",
    open var imsi: String = ""
)
    : RealmObject()