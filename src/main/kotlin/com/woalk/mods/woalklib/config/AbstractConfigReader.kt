package com.woalk.mods.woalklib.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.woalk.mods.woalklib.WoalklibMod
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.IOException

abstract class AbstractConfigReader {
    open fun configDir(): File = FabricLoader.getInstance().configDir.toFile()
    open fun configBaseDir(): File = File(configDir(), "woalk")

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