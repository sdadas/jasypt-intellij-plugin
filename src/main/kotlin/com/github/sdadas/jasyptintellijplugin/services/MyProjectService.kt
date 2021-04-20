package com.github.sdadas.jasyptintellijplugin.services

import com.github.sdadas.jasyptintellijplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
