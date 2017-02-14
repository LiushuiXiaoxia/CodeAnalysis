package cn.mycommons.codeanalysis

public class AnalysePluginExtension {

    def ignoreFailures = true

    def checkStyleConfig = "checkstyle.xml"
    def pmdConfig = "pmd.xml"
    def findbugsConfig = "findbugs.xml"

    def reportPath = "analysis/reports/"

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