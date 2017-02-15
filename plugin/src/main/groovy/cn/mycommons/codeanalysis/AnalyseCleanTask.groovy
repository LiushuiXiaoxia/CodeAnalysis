package cn.mycommons.codeanalysis

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskAction

/**
 * AnalyseCleanTask <br/>
 * Created by xiaqiulei on 2017-02-14.
 */
public class AnalyseCleanTask extends DefaultTask {

    @TaskAction
    void doAction() {
        def analyse = project.analyse as AnalysePluginExtension
        def analyseCleanTask = project.tasks.create('analyseCleanTask', Delete) {
            delete analyse.reportPath
        }

        analyseCleanTask.execute()
    }
}