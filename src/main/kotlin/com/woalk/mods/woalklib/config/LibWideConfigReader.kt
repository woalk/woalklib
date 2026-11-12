package com.woalk.mods.woalklib.config

object LibWideConfigReader: AbstractConfigReader() {
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