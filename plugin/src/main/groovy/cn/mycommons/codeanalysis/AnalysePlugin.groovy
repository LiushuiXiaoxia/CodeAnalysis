package cn.mycommons.codeanalysis

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel

class AnalysePlugin implements Plugin<Project> {

    public static final APPLICATION = 'com.android.application'
    public static final LIBRARY = 'com.android.library'


    void apply(Project project) {
        if (project.plugins.hasPlugin(APPLICATION) || project.plugins.hasPlugin(LIBRARY)) {
            project.extensions.create("analyse", AnalysePluginExtension)
            project.analyse.reportPath = "${project.buildDir}/reports"

            createCleanTask(project)
            createCheckStyleTask(project)
            createPmdTask(project)
            createFindbugsTask(project)
            configLint(project)
        } else {
            logE(project, "project must apply '${APPLICATION}' or '${LIBRARY}' plugin.")
        }
    }

    static void createCleanTask(Project project) {
        if (project.clean != null) {
            def analyseClean = project.tasks.create('analyseClean', AnalyseCleanTask)
            analyseClean.group = 'analyse'
            project.clean.dependsOn(analyseClean)
        }
    }

    static void createCheckStyleTask(Project project) {
        if (project.plugins.hasPlugin('checkstyle')) {
            logE(project, "project already use 'checkstyle' plugin.")
        } else {
            if (project.preBuild != null) {
                def analyseCheckstyle = project.tasks.create('analyseCheckstyle', AnalyseCheckstyleTask)
                analyseCheckstyle.group = 'analyse'
                project.preBuild.dependsOn(analyseCheckstyle)
            }
        }
    }

    static void createPmdTask(Project project) {
        if (project.plugins.hasPlugin('pmd')) {
            logE(project, "project already use 'pmd' plugin.")
        } else {
            if (project.preBuild != null) {
                def analysePmd = project.tasks.create('analysePmd', AnalysePmdTask)
                analysePmd.group = 'analyse'
                project.preBuild.dependsOn(analysePmd)
            }
        }
    }

    static void createFindbugsTask(Project project) {
        if (project.plugins.hasPlugin('findbugs')) {
            logE(project, "project already use 'findbugs' plugin.")
        } else {
            def analyseFindbugs = project.tasks.create('analyseFindbugs', AnalyseFindbugsTask)
            analyseFindbugs.group = 'analyse'

            project.afterEvaluate {
                project.tasks.withType(Task).each { task ->
                    task.doLast {
                        if (task.name.startsWith("assemble")) {
                            analyseFindbugs.execute()
                        }
                    }
                }
            }
        }
    }

    private static void configLint(Project project) {
    }

    static void logE(Project project, String msg) {
        project.logger.log(LogLevel.ERROR, msg)
    }
}