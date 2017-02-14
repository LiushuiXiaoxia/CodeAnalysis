package cn.mycommons.codeanalysis

public class AnalysePluginExtension {

    def ignoreFailures = true

    String checkStyleConfig
    String pmdConfig
    String findbugsConfig

    def reportPath = "analysis/reports/"

    AnalysePluginExtension() {
//        checkStyleConfig = AnalysePlugin.class.getResource("checkstyle.xml").getFile()
//        pmdConfig = AnalysePlugin.class.getResource("pmd.xml").getFile()
//        findbugsConfig = AnalysePlugin.class.getResource("findbugs.xml").getFile()
    }

    @Override
    public String toString() {
        return "CodeAnalysisPluginExtension{" +
                "ignoreFailures=" + ignoreFailures +
                ", checkStyleConfig=" + checkStyleConfig +
                ", pmdConfig=" + pmdConfig +
                ", findbugsConfig=" + findbugsConfig +
                ", reportPath=" + reportPath +
                '}';
    }
}