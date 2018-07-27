package cn.mycommons.codeanalysis

class AnalysePluginExtension {

    boolean ignoreFailures = true

    String checkStyleConfig
    String pmdConfig
    String findbugsConfig

    String reportPath

    @Override
    String toString() {
        return "CodeAnalysisPluginExtension{" +
                "ignoreFailures=" + ignoreFailures +
                ", checkStyleConfig=" + checkStyleConfig +
                ", pmdConfig=" + pmdConfig +
                ", findbugsConfig=" + findbugsConfig +
                ", reportPath=" + reportPath +
                '}';
    }
}