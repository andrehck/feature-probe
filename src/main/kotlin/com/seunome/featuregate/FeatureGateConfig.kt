
package com.seunome.featuregate

data class FeatureGateConfig(
    val rolloutPercentage: Int = 100,
    val useAllowList: Boolean = true,
    val useRollout: Boolean = true
)
