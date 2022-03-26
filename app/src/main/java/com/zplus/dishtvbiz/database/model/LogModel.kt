package com.zplus.dishtvbiz.database.model

import com.zplus.dishtvbiz.database.table.LogTable
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class LogModel {
    fun addLog(realm: Realm, log: LogTable): Boolean {
        return try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(log)
            realm.commitTransaction()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    fun getlog(realm: Realm): RealmResults<LogTable> {
        return realm.where(LogTable::class.java).findAllSorted("_ID", Sort.DESCENDING)
    }

    fun getLastid(realm: Realm): LogTable {
        return realm.where(LogTable::class.java).findAll().last()
    }
}