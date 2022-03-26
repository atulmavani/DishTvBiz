package com.zplus.dishtvbiz.database.migration

import io.realm.*


class RealmMigrations : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        if (oldVersion < newVersion) {
            val Schema = schema.get("SimList")
            /*if(!Schema.hasField("access_token")) {
                Schema.addField("access_token", String::class.java, FieldAttribute.REQUIRED)
                .transform { obj -> obj.setString("access_token", "0") }
            }*/
        }
    }

    override fun hashCode(): Int {
        return RealmMigration::class.java.hashCode()
    }


    override fun equals(`object`: Any?): Boolean {
        return if (`object` == null) {
            false
        } else `object` is RealmMigrations
    }
}