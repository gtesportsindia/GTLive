package com.gtesports.gtlive.data

import android.content.Context
import com.gtesports.gtlive.model.LayoutPreset
import com.gtesports.gtlive.model.OverlayItem
import com.gtesports.gtlive.model.OverlayType
import com.gtesports.gtlive.model.SceneConfiguration
import com.gtesports.gtlive.model.SceneItem
import com.gtesports.gtlive.model.SceneType
import org.json.JSONArray
import org.json.JSONObject

object ScenePersistenceManager {
    private const val PREF_NAME = "GTLive_SceneStorage"
    private const val KEY_SCENES_JSON = "key_scenes_json"
    private const val KEY_ACTIVE_SCENE_ID = "key_active_scene_id"

    fun saveScenes(context: Context, scenes: List<SceneItem>, activeSceneId: String) {
        try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val scenesArray = JSONArray()

            scenes.forEach { scene ->
                val sceneObj = JSONObject()
                sceneObj.put("id", scene.id)
                sceneObj.put("name", scene.name)
                sceneObj.put("type", scene.type.name)
                sceneObj.put("isActive", scene.id == activeSceneId)
                sceneObj.put("layoutPreset", scene.layoutPreset.name)
                sceneObj.put("backgroundColor", scene.backgroundColor)

                val overlaysArray = JSONArray()
                scene.overlays.forEach { overlay ->
                    val overlayObj = JSONObject()
                    overlayObj.put("id", overlay.id)
                    overlayObj.put("type", overlay.type.name)
                    overlayObj.put("name", overlay.name)
                    overlayObj.put("xRatio", overlay.xRatio.toDouble())
                    overlayObj.put("yRatio", overlay.yRatio.toDouble())
                    overlayObj.put("widthRatio", overlay.widthRatio.toDouble())
                    overlayObj.put("heightRatio", overlay.heightRatio.toDouble())
                    overlayObj.put("scale", overlay.scale.toDouble())
                    overlayObj.put("rotation", overlay.rotation.toDouble())
                    overlayObj.put("opacity", overlay.opacity.toDouble())
                    overlayObj.put("isVisible", overlay.isVisible)
                    overlayObj.put("isLocked", overlay.isLocked)
                    overlayObj.put("textContent", overlay.textContent)
                    overlayObj.put("imageUrl", overlay.imageUrl)
                    overlayObj.put("zIndex", overlay.zIndex)
                    overlayObj.put("goalCurrent", overlay.goalCurrent)
                    overlayObj.put("goalTarget", overlay.goalTarget)
                    overlayObj.put("colorHex", overlay.colorHex)
                    overlaysArray.put(overlayObj)
                }
                sceneObj.put("overlays", overlaysArray)
                scenesArray.put(sceneObj)
            }

            prefs.edit()
                .putString(KEY_SCENES_JSON, scenesArray.toString())
                .putString(KEY_ACTIVE_SCENE_ID, activeSceneId)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadScenes(context: Context): Pair<List<SceneItem>, String>? {
        return try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val jsonStr = prefs.getString(KEY_SCENES_JSON, null) ?: return null
            val activeSceneId = prefs.getString(KEY_ACTIVE_SCENE_ID, "scene_camera") ?: "scene_camera"

            val scenesArray = JSONArray(jsonStr)
            val sceneList = mutableListOf<SceneItem>()

            for (i in 0 until scenesArray.length()) {
                val sceneObj = scenesArray.getJSONObject(i)
                val id = sceneObj.getString("id")
                val name = sceneObj.getString("name")
                val typeStr = sceneObj.optString("type", "CAMERA")
                val type = try { SceneType.valueOf(typeStr) } catch (e: Exception) { SceneType.CAMERA }
                val layoutPresetStr = sceneObj.optString("layoutPreset", "FULL_SCREEN")
                val layoutPreset = try { LayoutPreset.valueOf(layoutPresetStr) } catch (e: Exception) { LayoutPreset.FULL_SCREEN }
                val backgroundColor = sceneObj.optString("backgroundColor", "#0A0A0E")

                val overlaysArray = sceneObj.optJSONArray("overlays")
                val overlayList = mutableListOf<OverlayItem>()

                if (overlaysArray != null) {
                    for (j in 0 until overlaysArray.length()) {
                        val overlayObj = overlaysArray.getJSONObject(j)
                        val ovId = overlayObj.getString("id")
                        val ovTypeStr = overlayObj.optString("type", "TEXT")
                        val ovType = try { OverlayType.valueOf(ovTypeStr) } catch (e: Exception) { OverlayType.TEXT }
                        val ovName = overlayObj.optString("name", "Overlay")
                        val xRatio = overlayObj.optDouble("xRatio", 0.05).toFloat()
                        val yRatio = overlayObj.optDouble("yRatio", 0.05).toFloat()
                        val widthRatio = overlayObj.optDouble("widthRatio", 0.3).toFloat()
                        val heightRatio = overlayObj.optDouble("heightRatio", 0.2).toFloat()
                        val scale = overlayObj.optDouble("scale", 1.0).toFloat()
                        val rotation = overlayObj.optDouble("rotation", 0.0).toFloat()
                        val opacity = overlayObj.optDouble("opacity", 1.0).toFloat()
                        val isVisible = overlayObj.optBoolean("isVisible", true)
                        val isLocked = overlayObj.optBoolean("isLocked", false)
                        val textContent = overlayObj.optString("textContent", "")
                        val imageUrl = overlayObj.optString("imageUrl", "")
                        val zIndex = overlayObj.optInt("zIndex", j)
                        val goalCurrent = overlayObj.optInt("goalCurrent", 25000)
                        val goalTarget = overlayObj.optInt("goalTarget", 50000)
                        val colorHex = overlayObj.optString("colorHex", "#FF0B3A")

                        overlayList.add(
                            OverlayItem(
                                id = ovId,
                                type = ovType,
                                name = ovName,
                                xRatio = xRatio,
                                yRatio = yRatio,
                                widthRatio = widthRatio,
                                heightRatio = heightRatio,
                                scale = scale,
                                rotation = rotation,
                                opacity = opacity,
                                isVisible = isVisible,
                                isLocked = isLocked,
                                textContent = textContent,
                                imageUrl = imageUrl,
                                zIndex = zIndex,
                                goalCurrent = goalCurrent,
                                goalTarget = goalTarget,
                                colorHex = colorHex
                            )
                        )
                    }
                }

                sceneList.add(
                    SceneItem(
                        id = id,
                        name = name,
                        type = type,
                        isActive = (id == activeSceneId),
                        layoutPreset = layoutPreset,
                        overlays = overlayList,
                        backgroundColor = backgroundColor
                    )
                )
            }

            if (sceneList.isEmpty()) null else Pair(sceneList, activeSceneId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
