package cn.mycommons.codeanalysis

public class AnalysePluginExtension {

    boolean ignoreFailures = true

    String checkStyleConfig
    String pmdConfig
    String findbugsConfig

    String reportPath

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