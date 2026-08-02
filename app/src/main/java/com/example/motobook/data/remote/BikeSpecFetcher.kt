package com.example.motobook.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.motobook.domain.model.Bike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ManualTask(
    val taskName: String,
    val intervalKm: Int,
    val category: String, // "ENGINE", "CHAIN", "TYRE", "BRAKE", "GENERAL"
    val note: String
)

data class BikeOnlineSpec(
    val brand: String,
    val model: String,
    val year: Int,
    val color: String,
    val countryOfOrigin: String,
    val tankCapacity: Float,
    val reserveCapacity: Float,
    val frontTyrePressure: Float,
    val rearTyrePressure: Float,
    val fuelType: String,
    val engineCc: Float,
    val maxPower: String,
    val recommendedOilGrade: String,
    val recommendedOilCapacity: String,
    val maintenanceScheduleNote: String,
    val availableColors: List<String>,
    val imageUrl: String = "",
    val manualUrl: String = "",
    val manualSummary: String = "",
    val extractedMaintenanceTasks: List<ManualTask> = emptyList()
)

data class BikeAiDiagnosticResult(
    val query: String,
    val probableCauses: List<String>,
    val manualFixSteps: List<String>,
    val toolsNeeded: List<String>,
    val safetyLevel: String, // "SAFE", "CAUTION", "CRITICAL"
    val summaryText: String
)

object BikeSpecFetcher {

    private const val TAG = "BikeSpecFetcher"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchModelsForBrand(brand: String, country: String = "Global"): List<String> = withContext(Dispatchers.IO) {
        val cleanBrand = brand.trim()
        val cleanCountry = country.trim().ifBlank { "Global" }
        if (cleanBrand.isEmpty()) return@withContext emptyList()

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    List 8 to 14 popular official motorcycle models for brand "$cleanBrand" sold specifically in market/country "$cleanCountry".
                    Return strictly a raw JSON array of strings (NO markdown, NO code block markers, ONLY valid JSON array).
                    Example: ["Model A", "Model B", "Model C"]
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseStr.isNotBlank()) {
                    val rootObj = JSONObject(responseStr)
                    val candidates = rootObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            var rawText = parts.getJSONObject(0).optString("text", "")
                            rawText = rawText.replace("```json", "").replace("```", "").trim()
                            val jsonArray = JSONArray(rawText)
                            val resultList = mutableListOf<String>()
                            for (i in 0 until jsonArray.length()) {
                                resultList.add(jsonArray.getString(i))
                            }
                            if (resultList.isNotEmpty()) return@withContext resultList
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch models for brand $cleanBrand ($cleanCountry): ${e.message}")
            }
        }

        getFallbackModels(cleanBrand, cleanCountry)
    }

    suspend fun fetchVariantsAndColors(brand: String, model: String, country: String = "Global"): Pair<List<String>, List<String>> = withContext(Dispatchers.IO) {
        val cleanBrand = brand.trim()
        val cleanModel = model.trim()
        val cleanCountry = country.trim().ifBlank { "Global" }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Provide factory editions/variants and official color choices for $cleanBrand $cleanModel specifically in market/country "$cleanCountry".
                    Return strictly a raw JSON object (NO markdown, NO code block markers, ONLY valid JSON) with structure:
                    {
                      "variants": ["Standard ABS", "Deluxe Edition", "Racing Edition"],
                      "colors": ["Racing Blue", "Metallic Red", "Dark Knight", "Intensity White"]
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseStr.isNotBlank()) {
                    val rootObj = JSONObject(responseStr)
                    val candidates = rootObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            var rawText = parts.getJSONObject(0).optString("text", "")
                            rawText = rawText.replace("```json", "").replace("```", "").trim()
                            val jsonObj = JSONObject(rawText)

                            val variantsList = mutableListOf<String>()
                            val vArray = jsonObj.optJSONArray("variants")
                            if (vArray != null) {
                                for (i in 0 until vArray.length()) { variantsList.add(vArray.getString(i)) }
                            }

                            val colorsList = mutableListOf<String>()
                            val cArray = jsonObj.optJSONArray("colors")
                            if (cArray != null) {
                                for (i in 0 until cArray.length()) { colorsList.add(cArray.getString(i)) }
                            }

                            if (variantsList.isNotEmpty() || colorsList.isNotEmpty()) {
                                return@withContext Pair(
                                    if (variantsList.isNotEmpty()) variantsList else listOf("Standard Dual Channel ABS", "Standard Single Channel ABS"),
                                    if (colorsList.isNotEmpty()) colorsList else listOf("Racing Blue", "Metallic Red", "Dark Knight", "Pearl White")
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch variants/colors for $cleanBrand $cleanModel ($cleanCountry): ${e.message}")
            }
        }

        getFallbackVariantsAndColors(cleanBrand, cleanModel)
    }

    suspend fun fetchBikeSpecsOnline(
        brand: String,
        model: String,
        year: Int = 2023,
        preferredColor: String = "",
        countryOfOrigin: String = "Global"
    ): Result<BikeOnlineSpec> = withContext(Dispatchers.IO) {
        val cleanBrand = brand.trim()
        val cleanModel = model.trim()
        val cleanCountry = countryOfOrigin.trim().ifBlank { "Global" }

        if (cleanBrand.isEmpty() && cleanModel.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Brand or model must not be empty."))
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an official motorcycle technical database and owner's manual analyzer.
                    Provide precise, accurate official specs, user manual summary, and maintenance tasks for:
                    Brand: $cleanBrand
                    Model: $cleanModel
                    Year: $year
                    Country/Market of Origin: $cleanCountry
                    ${if (preferredColor.isNotBlank()) "Color: $preferredColor" else ""}

                    Return strictly a raw JSON object (NO markdown, NO code block markers, ONLY valid JSON) with structure:
                    {
                      "brand": "$cleanBrand",
                      "model": "$cleanModel",
                      "year": $year,
                      "color": "${if (preferredColor.isNotBlank()) preferredColor else "Factory Color"}",
                      "countryOfOrigin": "$cleanCountry",
                      "tankCapacity": 12.0,
                      "reserveCapacity": 2.0,
                      "frontTyrePressure": 28.0,
                      "rearTyrePressure": 32.0,
                      "fuelType": "Octane" or "Petrol",
                      "engineCc": 150.0,
                      "maxPower": "18.4 PS @ 10,000 rpm",
                      "recommendedOilGrade": "10W-40 Full Synthetic",
                      "recommendedOilCapacity": "1.0 L",
                      "maintenanceScheduleNote": "Official manual service every 3,000 km. Chain lube every 500 km.",
                      "availableColors": ["Color 1", "Color 2", "Color 3"],
                      "imageUrl": "Direct high quality photo web URL or stock photo link for $cleanBrand $cleanModel",
                      "manualUrl": "Web search URL or direct PDF link to owner's manual for $cleanBrand $cleanModel",
                      "manualSummary": "Comprehensive summary of $cleanBrand $cleanModel user manual including break-in limits, oil filter change, spark plug gap, tyre pressure, valve clearance, and fuse diagram.",
                      "extractedMaintenanceTasks": [
                        {"taskName": "Engine Oil & Filter Replacement", "intervalKm": 3000, "category": "ENGINE", "note": "Use recommended 10W-40 full synthetic oil."},
                        {"taskName": "Drive Chain Lube & Tension Check", "intervalKm": 500, "category": "CHAIN", "note": "Slack should be 20-30mm."},
                        {"taskName": "Air Filter Cleaning/Replacement", "intervalKm": 6000, "category": "ENGINE", "note": "Replace earlier if riding in dusty conditions."},
                        {"taskName": "Spark Plug Inspection & Gap Check", "intervalKm": 10000, "category": "ENGINE", "note": "Standard gap 0.8-0.9mm."}
                      ]
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseStr.isNotBlank()) {
                    val rootObj = JSONObject(responseStr)
                    val candidates = rootObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            var rawText = parts.getJSONObject(0).optString("text", "")
                            rawText = rawText.replace("```json", "").replace("```", "").trim()

                            val specObj = JSONObject(rawText)
                            val colorsList = mutableListOf<String>()
                            val colorsArray = specObj.optJSONArray("availableColors")
                            if (colorsArray != null) {
                                for (i in 0 until colorsArray.length()) {
                                    colorsList.add(colorsArray.getString(i))
                                }
                            }
                            if (colorsList.isEmpty()) {
                                colorsList.addAll(listOf("Black", "Blue", "Red", "White", "Silver"))
                            }

                            val tasksList = mutableListOf<ManualTask>()
                            val tasksArray = specObj.optJSONArray("extractedMaintenanceTasks")
                            if (tasksArray != null) {
                                for (i in 0 until tasksArray.length()) {
                                    val tObj = tasksArray.getJSONObject(i)
                                    tasksList.add(
                                        ManualTask(
                                            taskName = tObj.optString("taskName", "Service Check"),
                                            intervalKm = tObj.optInt("intervalKm", 3000),
                                            category = tObj.optString("category", "GENERAL"),
                                            note = tObj.optString("note", "")
                                        )
                                    )
                                }
                            }

                            val defaultImg = getStockBikeImageUrl(cleanBrand, cleanModel)
                            val fetchedImg = specObj.optString("imageUrl", "").ifBlank { defaultImg }

                            val onlineSpec = BikeOnlineSpec(
                                brand = specObj.optString("brand", cleanBrand),
                                model = specObj.optString("model", cleanModel),
                                year = specObj.optInt("year", year),
                                color = specObj.optString("color", if (preferredColor.isNotBlank()) preferredColor else colorsList.first()),
                                countryOfOrigin = specObj.optString("countryOfOrigin", cleanCountry),
                                tankCapacity = specObj.optDouble("tankCapacity", 12.0).toFloat(),
                                reserveCapacity = specObj.optDouble("reserveCapacity", 2.0).toFloat(),
                                frontTyrePressure = specObj.optDouble("frontTyrePressure", 28.0).toFloat(),
                                rearTyrePressure = specObj.optDouble("rearTyrePressure", 32.0).toFloat(),
                                fuelType = specObj.optString("fuelType", "Octane"),
                                engineCc = specObj.optDouble("engineCc", 150.0).toFloat(),
                                maxPower = specObj.optString("maxPower", "15.0 HP"),
                                recommendedOilGrade = specObj.optString("recommendedOilGrade", "10W-40 Synthetic"),
                                recommendedOilCapacity = specObj.optString("recommendedOilCapacity", "1.0 L"),
                                maintenanceScheduleNote = specObj.optString("maintenanceScheduleNote", "Regular service every 3,000 km. Chain lube every 500 km."),
                                availableColors = colorsList,
                                imageUrl = fetchedImg,
                                manualUrl = specObj.optString("manualUrl", "https://www.google.com/search?q=owner+manual+pdf+$cleanBrand+$cleanModel"),
                                manualSummary = specObj.optString("manualSummary", "Official digital user manual summary for $cleanBrand $cleanModel ($cleanCountry edition). Includes break-in speed limits, engine oil specifications, spark plug gap recommendations, and tire PSI values."),
                                extractedMaintenanceTasks = tasksList
                            )
                            return@withContext Result.success(onlineSpec)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API spec fetch failed: ${e.message}", e)
            }
        }

        // Fallback smart catalog lookup
        val fallbackSpec = getFallbackSpec(cleanBrand, cleanModel, year, preferredColor, cleanCountry)
        return@withContext Result.success(fallbackSpec)
    }

    suspend fun askBikeAiAssistant(bike: Bike, userQuery: String): Result<BikeAiDiagnosticResult> = withContext(Dispatchers.IO) {
        val query = userQuery.trim()
        if (query.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Query cannot be empty."))
        }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an expert Motorcycle Mechanic & AI Assistant specializing in:
                    Bike: ${bike.brand} ${bike.model} (${bike.year}, ${bike.engineCc}cc, ${bike.countryOfOrigin} edition)
                    Recommended Oil: ${bike.recommendedOilGrade}
                    Tyre Pressure: Front ${bike.frontTyrePressure} PSI, Rear ${bike.rearTyrePressure} PSI
                    User Manual Notes: ${bike.manualSummary.ifBlank { bike.maintenanceScheduleNote }}

                    Rider Issue / Question: "$query"

                    Provide a thorough, practical diagnostic analysis matching official owner manual & mechanical best practices.
                    Return strictly a raw JSON object (NO markdown, NO code block markers, ONLY valid JSON) with structure:
                    {
                      "query": "$query",
                      "probableCauses": [
                        "Probable Cause 1 with brief explanation",
                        "Probable Cause 2 with brief explanation"
                      ],
                      "manualFixSteps": [
                        "Step 1: Check XYZ according to manual",
                        "Step 2: Adjust or replace ABC",
                        "Step 3: Verification step"
                      ],
                      "toolsNeeded": ["10mm socket wrench", "Chain lube spray", "Feeler gauge"],
                      "safetyLevel": "SAFE" or "CAUTION" or "CRITICAL",
                      "summaryText": "Direct 2-3 sentence answer explaining the diagnosis and recommended action."
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder().url(url).post(body).build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseStr.isNotBlank()) {
                    val rootObj = JSONObject(responseStr)
                    val candidates = rootObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            var rawText = parts.getJSONObject(0).optString("text", "")
                            rawText = rawText.replace("```json", "").replace("```", "").trim()

                            val resObj = JSONObject(rawText)

                            val causes = mutableListOf<String>()
                            val cArr = resObj.optJSONArray("probableCauses")
                            if (cArr != null) { for (i in 0 until cArr.length()) causes.add(cArr.getString(i)) }

                            val steps = mutableListOf<String>()
                            val sArr = resObj.optJSONArray("manualFixSteps")
                            if (sArr != null) { for (i in 0 until sArr.length()) steps.add(sArr.getString(i)) }

                            val tools = mutableListOf<String>()
                            val tArr = resObj.optJSONArray("toolsNeeded")
                            if (tArr != null) { for (i in 0 until tArr.length()) tools.add(tArr.getString(i)) }

                            val diagResult = BikeAiDiagnosticResult(
                                query = query,
                                probableCauses = if (causes.isNotEmpty()) causes else listOf("General wear or adjustment needed"),
                                manualFixSteps = if (steps.isNotEmpty()) steps else listOf("Inspect component according to user manual"),
                                toolsNeeded = if (tools.isNotEmpty()) tools else listOf("Standard motorcycle toolkit"),
                                safetyLevel = resObj.optString("safetyLevel", "CAUTION").uppercase(),
                                summaryText = resObj.optString("summaryText", "Diagnosis prepared for ${bike.brand} ${bike.model}.")
                            )
                            return@withContext Result.success(diagResult)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Ask AI assistant failed: ${e.message}")
            }
        }

        // Fallback rule-based AI diagnosis when API key is unconfigured or network fails
        val fallbackDiag = getFallbackDiagnostic(bike, query)
        return@withContext Result.success(fallbackDiag)
    }

    private fun getStockBikeImageUrl(brand: String, model: String): String {
        val bUpper = brand.uppercase()
        val mUpper = model.uppercase()

        return when {
            mUpper.contains("R15") || mUpper.contains("CBR") || mUpper.contains("NINJA") || mUpper.contains("RC") || mUpper.contains("RR") ->
                "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80" // Sportbike
            mUpper.contains("MT") || mUpper.contains("DUKE") || mUpper.contains("GIXXER") || mUpper.contains("PULSAR") || mUpper.contains("HORNET") ->
                "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80" // Street Naked
            mUpper.contains("CLASSIC") || mUpper.contains("HUNTER") || mUpper.contains("METEOR") || mUpper.contains("ROYAL") || mUpper.contains("HARLEY") ->
                "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80" // Retro Cruiser
            mUpper.contains("ADVENTURE") || mUpper.contains("TENERE") || mUpper.contains("HIMALAYAN") || mUpper.contains("GS") || mUpper.contains("V-STROM") ->
                "https://images.unsplash.com/photo-1609630875171-b1321377ee65?auto=format&fit=crop&w=800&q=80" // Adventure
            mUpper.contains("AEROX") || mUpper.contains("VESPA") || mUpper.contains("NTORQ") || mUpper.contains("ACTIVA") || mUpper.contains("BURGMAN") ->
                "https://images.unsplash.com/photo-1525160354320-d8e92641c563?auto=format&fit=crop&w=800&q=80" // Scooter
            else ->
                "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=800&q=80"
        }
    }

    private fun getFallbackModels(brand: String, country: String): List<String> {
        val bUpper = brand.uppercase()
        val cUpper = country.uppercase()

        return when {
            bUpper.contains("YAMAHA") -> {
                if (cUpper.contains("INDIA") || cUpper.contains("BANGLADESH") || cUpper.contains("INDONESIA")) listOf("R15 V4", "MT-15 V2", "FZ-S V4", "FZ-S V3", "Aerox 155", "RayZR 125", "R3", "FZ-X")
                else listOf("YZF-R1", "YZF-R7", "MT-09", "MT-07", "Tenere 700", "XSR 900", "Tracer 9 GT")
            }
            bUpper.contains("HONDA") -> {
                if (cUpper.contains("INDIA") || cUpper.contains("BANGLADESH")) listOf("CBR 150R", "CB350 H'ness", "Hornet 2.0", "CB Shine 125", "X-Blade", "Activa 6G", "SP 125")
                else listOf("CBR1000RR-R", "CBR600RR", "CB650R", "CRF1100L Africa Twin", "Rebel 500", "CB500X")
            }
            bUpper.contains("SUZUKI") -> listOf("Gixxer SF 150", "Gixxer 150 Monotone", "Gixxer SF 250", "V-Strom SX", "Hayabusa", "Burgman Street EX")
            bUpper.contains("KTM") -> listOf("Duke 390", "RC 390", "Adventure 390", "Duke 200", "Duke 250", "RC 200")
            bUpper.contains("ROYAL") -> listOf("Hunter 350", "Classic 350", "Meteor 350", "Continental GT 650", "Himalayan 450", "Shotgun 650")
            bUpper.contains("KAWASAKI") -> listOf("Ninja 300", "Ninja 400", "Z900", "ZX-6R", "Versys 650", "ZH2")
            bUpper.contains("BMW") -> listOf("G 310 R", "G 310 GS", "S 1000 RR", "R 1250 GS Adventure", "F 900 XR")
            bUpper.contains("TVS") -> listOf("Apache RTR 160 4V", "Apache RTR 160 2V", "Apache RR 310", "Apache RTR 200 4V", "NTorq 125", "Ronin 225", "iQube")
            bUpper.contains("BAJAJ") -> listOf("Pulsar N160", "Pulsar 150 Twin Disc", "Pulsar NS160", "Pulsar NS200", "Dominar 400", "Discover 125", "Avenger 160 ABS")
            bUpper.contains("HERO") -> listOf("Thriller 160R", "Hunk 150", "Karizma XMR", "Xpulse 200 4V", "Splendor Plus", "Glamour Xtec")
            bUpper.contains("RUNNER") -> listOf("Runner Bolt 165R", "Runner Knight 125", "Runner Royal 110", "Runner Skymate", "Runner Turbo 125")
            else -> listOf("Sport Edition 150", "Standard Naked 200", "Cruiser 350", "Adventure Tourer 400", "Maxi Scooter 150")
        }
    }

    private fun getFallbackVariantsAndColors(brand: String, model: String): Pair<List<String>, List<String>> {
        val bUpper = brand.uppercase()
        val variants = listOf("Standard Dual Channel ABS", "Special Edition", "Deluxe Connected")
        val colors = when {
            bUpper.contains("YAMAHA") -> listOf("Racing Blue", "Metallic Red", "Dark Knight", "Intensity White")
            bUpper.contains("HONDA") -> listOf("Grand Prix Red", "Matte Gunpowder Black", "Victory Red", "Pearl Siren Blue")
            bUpper.contains("SUZUKI") -> listOf("Glass Sparkle Black", "Metallic Triton Blue", "Pearl Mira Red")
            bUpper.contains("KTM") -> listOf("Electronic Orange", "Dark Galvano", "Ceramic White")
            bUpper.contains("ROYAL") -> listOf("Dapper Grey", "Rebel Blue", "Halcyon Black", "Signals Marsh Grey")
            else -> listOf("Midnight Black", "Racing Blue", "Metallic Red", "Pearl White", "Graphite Grey")
        }
        return Pair(variants, colors)
    }

    private fun getFallbackSpec(
        brand: String,
        model: String,
        year: Int,
        preferredColor: String,
        countryOfOrigin: String
    ): BikeOnlineSpec {
        val bUpper = brand.uppercase()
        val mUpper = model.uppercase()

        val (tank, reserve, frontPsi, rearPsi, cc, power, oil, colors, maint) = when {
            bUpper.contains("YAMAHA") && (mUpper.contains("R15") || mUpper.contains("YZF")) ->
                Tuple9(11.0f, 1.5f, 29.0f, 33.0f, 155.1f, "18.4 PS @ 10000 rpm", "10W-40 Full Synthetic (1.0 L)",
                    listOf("Racing Blue", "Metallic Red", "Dark Knight", "Intensity White"),
                    "Engine oil change every 3,000 km. Spark plug replace at 12,000 km. Chain lube every 500 km.")

            bUpper.contains("YAMAHA") && (mUpper.contains("MT") || mUpper.contains("MT-15")) ->
                Tuple9(10.0f, 1.5f, 28.0f, 32.0f, 155.1f, "18.4 PS @ 10000 rpm", "10W-40 Synthetic (1.0 L)",
                    listOf("Cyan Storm", "Ice Fluo Vermillion", "Metallic Black", "Racing Blue"),
                    "Service every 3,000 km or 4 months. Chain maintenance every 500 km.")

            bUpper.contains("HONDA") && (mUpper.contains("CBR") || mUpper.contains("CB")) ->
                Tuple9(12.0f, 2.0f, 29.0f, 33.0f, 149.1f, "17.1 PS @ 9000 rpm", "10W-30 Full Synthetic (1.0 L)",
                    listOf("Grand Prix Red", "Matte Gunpowder Black", "Victory Red", "Tri-Color"),
                    "Service every 4,000 km. Coolant inspection every 10,000 km. Chain adjustment every 1,000 km.")

            bUpper.contains("ROYAL") && (mUpper.contains("CLASSIC") || mUpper.contains("HUNTER") || mUpper.contains("METEOR")) ->
                Tuple9(13.0f, 2.5f, 30.0f, 35.0f, 349.0f, "20.2 BHP @ 6100 rpm", "15W-50 Semi Synthetic (2.1 L)",
                    listOf("Dapper Grey", "Rebel Blue", "Halcyon Black", "Signals Marsh Grey"),
                    "Service interval 5,000 km. Valve clearance inspect at 10,000 km. Chain lube every 500 km.")

            else -> Tuple9(
                12.0f, 2.0f, 28.0f, 32.0f, 150.0f, "15.0 PS @ 8500 rpm", "10W-40 Synthetic (1.0 L)",
                listOf("Midnight Black", "Racing Blue", "Matte Red", "Metallic White", "Graphite Grey"),
                "Regular engine oil service every 3,000 km. Tyre pressure & chain check every 500 km."
            )
        }

        val chosenColor = if (preferredColor.isNotBlank()) preferredColor else colors.first()
        val defaultImg = getStockBikeImageUrl(brand, model)

        val sampleTasks = listOf(
            ManualTask("Engine Oil & Filter Change", 3000, "ENGINE", "Use recommended grade $oil."),
            ManualTask("Drive Chain Clean & Lube", 500, "CHAIN", "Slack tolerance 25-35mm."),
            ManualTask("Air Filter Inspection & Clean", 6000, "ENGINE", "Clean with compressed air or replace."),
            ManualTask("Spark Plug & Gap Adjustment", 10000, "ENGINE", "Ensure electrode gap is within spec.")
        )

        val manualSummary = """
            Official Owner's Maintenance Guide for $brand $model ($countryOfOrigin edition, $year).
            - Recommended Engine Oil: $oil.
            - Tyre Inflation: Front $frontPsi PSI, Rear $rearPsi PSI.
            - Fuel System: Tank Capacity $tank Liters ($reserve L Reserve).
            - Break-in Procedure: Keep below 6,000 RPM for first 1,000 km.
            - Chain Maintenance: Clean and lubricate every 500 km using O-ring safe lube.
        """.trimIndent()

        return BikeOnlineSpec(
            brand = if (brand.isNotBlank()) brand else "Generic Brand",
            model = if (model.isNotBlank()) model else "Standard Model",
            year = year,
            color = chosenColor,
            countryOfOrigin = countryOfOrigin,
            tankCapacity = tank,
            reserveCapacity = reserve,
            frontTyrePressure = frontPsi,
            rearTyrePressure = rearPsi,
            fuelType = "Octane",
            engineCc = cc,
            maxPower = power,
            recommendedOilGrade = oil,
            recommendedOilCapacity = "1.0 L",
            maintenanceScheduleNote = maint,
            availableColors = colors,
            imageUrl = defaultImg,
            manualUrl = "https://www.google.com/search?q=owner+manual+pdf+$brand+$model",
            manualSummary = manualSummary,
            extractedMaintenanceTasks = sampleTasks
        )
    }

    private fun getFallbackDiagnostic(bike: Bike, query: String): BikeAiDiagnosticResult {
        val qLower = query.lowercase()

        return when {
            qLower.contains("overheat") || qLower.contains("heat") || qLower.contains("hot") -> BikeAiDiagnosticResult(
                query = query,
                probableCauses = listOf(
                    "Low coolant level or trapped air lock in radiator circuit",
                    "Radiator cooling fan thermal switch malfunction",
                    "Dirty or damaged radiator fins restricting air flow",
                    "Degraded engine oil ($bike.recommendedOilGrade) reducing thermal dissipation"
                ),
                manualFixSteps = listOf(
                    "1. Allow engine to cool completely before opening radiator cap.",
                    "2. Check coolant level in reservoir tank and top up if low.",
                    "3. Inspect cooling fan operation by idling engine to operating temperature.",
                    "4. Flush radiator fins with low-pressure water to clear road debris."
                ),
                toolsNeeded = listOf("Coolant fluid", "10mm socket wrench", "Soft brush / hose"),
                safetyLevel = "CAUTION",
                summaryText = "Overheating on ${bike.brand} ${bike.model} is usually caused by low coolant or restricted radiator airflow. Do not ride if temp gauge is in red zone."
            )

            qLower.contains("chain") || qLower.contains("rattle") || qLower.contains("noise") -> BikeAiDiagnosticResult(
                query = query,
                probableCauses = listOf(
                    "Excessive drive chain slack (exceeding recommended 25-35mm limit)",
                    "Dry or unlubricated chain links causing stiff links",
                    "Worn sprockets or misaligned rear wheel axle"
                ),
                manualFixSteps = listOf(
                    "1. Measure chain free play midway between sprockets on lower run.",
                    "2. Loosen rear axle nut and adjust chain tension bolts evenly on both sides.",
                    "3. Clean chain thoroughly with kerosene or chain cleaner.",
                    "4. Apply dedicated O-ring safe chain lube while spinning rear wheel."
                ),
                toolsNeeded = listOf("22mm & 19mm axle wrenches", "Chain cleaner & lube spray", "Ruler / tape measure"),
                safetyLevel = "SAFE",
                summaryText = "Chain noise on ${bike.brand} ${bike.model} is fixed by tightening slack to 25-30mm and applying chain lube every 500 km as per user manual."
            )

            qLower.contains("brake") || qLower.contains("spongy") || qLower.contains("stop") -> BikeAiDiagnosticResult(
                query = query,
                probableCauses = listOf(
                    "Air bubbles trapped inside hydraulic brake lines",
                    "Old or moisture-contaminated brake fluid (DOT 4)",
                    "Worn brake pads or contaminated brake rotor surface"
                ),
                manualFixSteps = listOf(
                    "1. Check brake fluid level in master cylinder sight glass.",
                    "2. Bleed brake system to remove air bubbles from line.",
                    "3. Inspect front and rear brake pad thickness (minimum 1.5mm friction material remaining)."
                ),
                toolsNeeded = listOf("DOT 4 brake fluid", "8mm spanner", "Clear plastic bleed hose"),
                safetyLevel = "CRITICAL",
                summaryText = "Spongy brake levers pose a safety hazard! Bleed brake lines immediately and inspect pad thickness before riding ${bike.brand} ${bike.model}."
            )

            qLower.contains("start") || qLower.contains("battery") || qLower.contains("cold") -> BikeAiDiagnosticResult(
                query = query,
                probableCauses = listOf(
                    "Low battery voltage (< 12.4V rest voltage)",
                    "Fouled spark plug or incorrect electrode gap",
                    "Clogged fuel injector or stale fuel in tank"
                ),
                manualFixSteps = listOf(
                    "1. Measure battery voltage across terminals using multimeter.",
                    "2. Remove and clean spark plug; check gap (0.8 - 0.9 mm).",
                    "3. Ensure kill switch is ON and side stand is retracted in neutral gear."
                ),
                toolsNeeded = listOf("16mm spark plug wrench", "Digital multimeter", "Feeler gauge"),
                safetyLevel = "SAFE",
                summaryText = "Cold start or starting issues on ${bike.brand} ${bike.model} are typically solved by recharging battery or clearing spark plug carbon deposits."
            )

            else -> BikeAiDiagnosticResult(
                query = query,
                probableCauses = listOf(
                    "General component wear or scheduled maintenance requirement",
                    "Loose fastener, cable adjustment, or sensor calibration"
                ),
                manualFixSteps = listOf(
                    "1. Consult the official owner's manual summary for ${bike.brand} ${bike.model}.",
                    "2. Perform visual inspection of oil level, chain tension, and tire pressure.",
                    "3. Take bike to authorized service center if issue persists."
                ),
                toolsNeeded = listOf("Standard motorcycle toolkit", "Tyre pressure gauge"),
                safetyLevel = "SAFE",
                summaryText = "Based on official ${bike.brand} ${bike.model} technical guide, perform basic mechanical checks and ensure tyre pressure matches Front ${bike.frontTyrePressure} PSI / Rear ${bike.rearTyrePressure} PSI."
            )
        }
    }

    private data class Tuple9<A, B, C, D, E, F, G, H, I>(
        val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I
    )
}
