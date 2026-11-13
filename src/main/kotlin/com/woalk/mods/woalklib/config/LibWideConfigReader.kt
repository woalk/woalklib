package com.woalk.mods.woalklib.config

/**
 * Config files directly read by WoalkLib.
 */
object LibWideConfigReader: AbstractConfigReader() {
    /**
     * The main configuration file for all Woalk mods.
     */
    val woalkConfig: WoalkConfig = run {
        val root = readJson("woalk")
        if (root == null) {
            WoalkConfig()
        } else {
            WoalkConfig(
                ignoreRestrictedItemRecommendations = root["ignoreRestrictedItemRecommendations"]?.asBoolean ?: false
            )
        }
    }
}