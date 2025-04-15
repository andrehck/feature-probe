
package com.seunome.featuregate

object FeatureGate {
    private lateinit var client: AllowListClient

    fun init(config: FeatureGateConfig) {
        client = AllowListClient(config)
    }

    fun isEnabled(feature: String, id: String): Boolean {
        if (!::client.isInitialized) {
            throw IllegalStateException("FeatureGate não foi inicializado")
        }
        return client.isEnabled(feature, id)
    }
}
