package com.woalk.mods.woalklib.config

/**
 * The data structure for [LibWideConfigReader.woalkConfig].
 */
data class WoalkConfig(
    /**
     * If `true`, display all items marked as `hidden` in the default Creative Mode tab.
     * If `false`, items marked as `hidden` are not added to any tab (default setting).
     */
    val ignoreRestrictedItemRecommendations: Boolean = false,
)
