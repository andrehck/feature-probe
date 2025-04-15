
package com.seunome.featuregate

import com.seunome.featuregate.exceptions.FeatureGateException
import java.net.HttpURLConnection
import java.net.URL

class AllowListClient(private val config: FeatureGateConfig) {
    companion object{
        private const val BASE_URL = ""
        private const val API_KEY = ""
        private const val TIMEOUT = ""
    }

    fun isEnabled(feature: String, id: String): Boolean {
        var allow = true

        if (config.useAllowList) {
            allow = try {
                val url = URL("\${config.baseUrl}/check?feature=\$feature&id=\$id")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("x-api-key", config.apiKey)
                conn.connectTimeout = config.timeoutMillis.toInt()

                conn.inputStream.bufferedReader().use { it.readText().trim().toBoolean() }
            } catch (ex: Exception) {
                throw FeatureGateException("Erro ao verificar allow list", ex)
            }
        }

        if (!allow) return false

        if (config.useRollout) {
            val hash = id.hashCode().absoluteValue % 100
            return hash < config.rolloutPercentage
        }

        return true
    }

    private val Int.absoluteValue: Int
        get() = if (this < 0) -this else this
}
