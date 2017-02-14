package cn.mycommons.codeanalysis
/**
 * FileUtil <br/>
 * Created by xiaqiulei on 2017-02-14.
 */
class Util {

    static File getFileOrTempFile(String fileName, String defaultResourceName) {
        if (fileName != null && fileName.length() != 0 && new File(fileName).exists()) {
            return new File(fileName)
        }

        def tempFile = File.createTempFile("temp.config.", "." + defaultResourceName)
        tempFile.write(new Util().getClass().getResourceAsStream("/" + defaultResourceName).text)
        println "Config.tempFile = " + tempFile
        return tempFile
    }
}