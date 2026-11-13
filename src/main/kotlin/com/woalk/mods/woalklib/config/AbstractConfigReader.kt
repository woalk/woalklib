package com.woalk.mods.woalklib.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.woalk.mods.woalklib.WoalklibMod
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.IOException

/**
 * A base class for config readers.
 *
 * @see readJson
 */
abstract class AbstractConfigReader {
    /**
     * The Fabric `/config` directory, used as base for all config files.
     *
     * @see configBaseDir
     */
    open fun configDir(): File = FabricLoader.getInstance().configDir.toFile()

    /**
     * The base directory that is used as root for all config files read by [readJson].
     * By default, it will be `/config/woalk`.
     */
    open fun configBaseDir(): File = File(configDir(), "woalk")

    /**
     * Read a `.json` config file in the location [configBaseDir]`/`[configFileName]`.json`
     * as a [JsonObject] for further processing.
     */
    fun readJson(configFileName: String): JsonObject? {
        val f = File(configBaseDir(), "${configFileName}.json")
        return if (!f.exists()) {
            WoalklibMod.LOGGER.info("${configFileName}.json not found, using defaults")
            null
        } else try {
            val reader = f.bufferedReader()
            JsonParser.parseReader(reader).asJsonObject
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: JsonSyntaxException) {
            WoalklibMod.LOGGER.error("Syntax error in ${configFileName}.json, using defaults")
            null
        }
    }
}