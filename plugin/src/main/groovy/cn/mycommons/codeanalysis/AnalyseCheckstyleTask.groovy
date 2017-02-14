package cn.mycommons.codeanalysis

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.tasks.TaskAction

/**
 * AnalyseCheckstyleTask <br/>
 * Created by xiaqiulei on 2017-02-14.
 */
public class AnalyseCheckstyleTask extends DefaultTask {

    @TaskAction
    void doAction() {
        project.apply(plugin: 'checkstyle')
        def analyse = project.analyse as AnalysePluginExtension

        def checkstyleTask = project.tasks.create('checkstyleTask', Checkstyle) {
            source 'src'
            configFile project.file(analyse.checkStyleConfig)
            include '**/*.java'
            exclude '**/gen/**'
            classpath = project.files()
            ignoreFailures analyse.ignoreFailures

            reports {
                html {
                    destination "${analyse.checkStyleReportPath}/Checkstyle.html"
                }
                xml {
                    destination "${analyse.checkStyleReportPath}/Checkstyle.xml"
                }
            }
        }

        checkstyleTask.doLast {
            reports.all { report ->
                def outputFile = report.destination
                if (outputFile.exists() && outputFile.text.contains("<error ")) {
                    def msg = "There were checkstyle errors! For more info check $outputFile"
                    if (checkstyleTask.ignoreFailures) {
                        project.logger.log(LogLevel.ERROR, "\n[CodeAnalysis:checkstyle] $msg\n")
                    } else {
                        throw new GradleException(msg)
                    }
                }
            }
        }

        checkstyleTask.execute()
    }
}