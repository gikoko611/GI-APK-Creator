package com.gi.apkcreator.data

import android.content.Context
import com.gi.apkcreator.model.Project
import org.json.JSONArray
import org.json.JSONObject

class ProjectStore(context: Context) {

    private val prefs = context.getSharedPreferences(
        "gi_projects",
        Context.MODE_PRIVATE
    )

    fun getProjects(): List<Project> {
        val raw = prefs.getString(KEY_PROJECTS, null)
            ?: return emptyList()

        return runCatching {
            val array = JSONArray(raw)

            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)

                    add(
                        Project(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            prompt = obj.getString("prompt"),
                            template = obj.optString("template", "CUSTOM"),
                            packageName = obj.optString(
                                "packageName",
                                "com.gi.generated"
                            ),
                            versionName = obj.optString(
                                "versionName",
                                "0.1.0"
                            ),
                            createdAt = obj.optLong(
                                "createdAt",
                                System.currentTimeMillis()
                            ),
                            updatedAt = obj.optLong(
                                "updatedAt",
                                System.currentTimeMillis()
                            )
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveProject(project: Project) {
        val projects = getProjects()
            .filterNot { it.id == project.id }
            .toMutableList()

        projects.add(0, project)
        saveProjects(projects)
    }

    fun deleteProject(id: String) {
        val projects = getProjects()
            .filterNot { it.id == id }

        saveProjects(projects)
    }

    fun getProject(id: String): Project? {
        return getProjects().firstOrNull { it.id == id }
    }

    private fun saveProjects(projects: List<Project>) {
        val array = JSONArray()

        projects.forEach { project ->
            array.put(
                JSONObject().apply {
                    put("id", project.id)
                    put("name", project.name)
                    put("description", project.description)
                    put("prompt", project.prompt)
                    put("template", project.template)
                    put("packageName", project.packageName)
                    put("versionName", project.versionName)
                    put("createdAt", project.createdAt)
                    put("updatedAt", project.updatedAt)
                }
            )
        }

        prefs.edit()
            .putString(KEY_PROJECTS, array.toString())
            .apply()
    }

    companion object {
        private const val KEY_PROJECTS = "projects"
    }
}
