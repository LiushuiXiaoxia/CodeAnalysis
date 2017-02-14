package cn.mycommons.codeanalysis

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.tasks.TaskAction

/**
 * MyCheckstyleTask <br/>
 * Created by xiaqiulei on 2017-02-14.
 */
public class AnalysePmdTask extends DefaultTask {

    @TaskAction
    void doAction() {
        project.apply(plugin: 'pmd')
        def analyse = project.analyse as AnalysePluginExtension

        def pmdTask = project.tasks.create('pmdTask', Pmd) {
            ignoreFailures = analyse.ignoreFailures
            ruleSetFiles = project.files(analyse.pmdConfig)
            ruleSets = []

            source 'src'
            include '**/*.java'
            exclude '**/gen/**', '**/build/**'

            reports {
                xml {
                    enabled = false
                    destination "${analyse.pmdReportPath}/Pmd.xml"
                }
                html {
                    enabled = true
                    destination "${analyse.pmdReportPath}/Pmd.html"
                }
            }
        }

        pmdTask.execute()
    }
}