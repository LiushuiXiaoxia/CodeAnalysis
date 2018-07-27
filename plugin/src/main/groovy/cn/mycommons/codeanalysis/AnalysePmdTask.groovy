package cn.mycommons.codeanalysis

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.quality.Pmd
import org.gradle.api.tasks.TaskAction

/**
 * AnalysePmdTask <br/>
 * Created by xiaqiulei on 2017-02-14.
 */
class AnalysePmdTask extends DefaultTask {

    @TaskAction
    void doAction() {
        project.apply(plugin: 'pmd')
        def analyse = project.analyse as AnalysePluginExtension

        def pmdTask = project.tasks.create('pmdTask', Pmd) {
            ignoreFailures = analyse.ignoreFailures
            ruleSetFiles = project.files(Util.getFileOrTempFile(analyse.pmdConfig, "pmd.xml"))
            ruleSets = []

            source 'src'
            include '**/*.java'
            exclude '**/gen/**', '**/build/**'

            reports {
                xml {
                    enabled = false
                    destination "${analyse.reportPath}/Pmd.xml"
                }
                html {
                    enabled = true
                    destination "${analyse.reportPath}/Pmd.html"
                }
            }
        }

        pmdTask.execute()
    }
}