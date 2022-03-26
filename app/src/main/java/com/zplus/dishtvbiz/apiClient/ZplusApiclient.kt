package com.zplus.dishtvbiz.apiClient

import com.zplus.dishtvbiz.utility.StaticUtility
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ZplusApiclient {
    companion object {

        var retofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (retofit == null) {
                    retofit = Retrofit.Builder()
                        .baseUrl(StaticUtility.URLZPLUS)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retofit!!
            }
    }
}