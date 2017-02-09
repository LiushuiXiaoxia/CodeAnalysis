Android静态代码分析

---

最佳项目里面来了很多新的小伙伴，然后每个人的代码风格还不一样，虽然有代码风格文档以及代码review。

但是这些东西需要花费很多人力和时间来做，所以就研究了下静态代码分析，能用工具完成的坚决不用人肉。同时静态代码分析还能解决很多潜在的bug问题。

下面依次对介绍几个Android常用的静态代码分析工具，同时顺便介绍下我厂刘全栈的对静态代码分析做的工具
[AndroidCodeQuality](https://github.com/MasonLiuChn/AndroidCodeQuality)。

# CheckStyle

[CheckStyle官网](http://checkstyle.sourceforge.net/)

『Checkstyle是一个开发工具用来帮助程序员编写符合代码规范的Java代码。它能自动检查Java代码为空闲的人进行这项无聊(但重要)的任务。』

正如Checkstyle的开发者所言，这个工具能够帮助你在项目中定义和维持一个非常精确和灵活的代码规范形式。当你启动CheckStyle，它会根据所提供的配置文件分析你的Java代码并告诉你发现的所有错误。

Checkstyle会发现大量的问题，特别是在你运用了大量的规则配置，如同你设置了一个非常精确的语法。
尽管我通过Gradle使用checkstyle，例如在我进行推送之前，我仍然推荐你为IntellJ/Android Studio使用checkstyle插件(你可以通过Android Studio的工作面板文件/设置/插件直接安装插件)。
这种方式下，你可以根据那些为Gradle配置的相同文件在你的工程中使用checkstyle，但是远不止这些，你可以直接在Android Studio中获取带有超链接结果，这些结果通过超链接在你的代码中对应，这是非常有用的(Gradle的这种方式仍然很重要的，因为你可以使用它自动构建系统，如Jenkins)。

## 集成

在Android中集成CheckStyle也很简单，只需要在Gradle添加少许配置即可。

```gradle
// 定义生成文件目录
def checkStyleReportPath = "${project.rootDir}/reports/"

// 在clean时候，自动删除原先生成报告
clean.doFirst {
    delete checkStyleReportPath
}

task projectCheckStyle(type: Checkstyle) {
    source 'src'
    configFile file("checkstyle.xml")
    include '**/*.java'
    exclude '**/gen/**'
    classpath = files()
    ignoreFailures true // 配置是否忽略失败

    // 自定义报告生成路径
    reports {
        html {
            destination "${checkStyleReportPath}/Checkstyle.html"
        }
        xml {
            destination "${checkStyleReportPath}/Checkstyle.xml"
        }
    }
}

tasks.withType(Checkstyle).each { checkstyleTask ->
    checkstyleTask.doLast {
        reports.all { report ->
            // 检查生成报告中是否有错误
            def outputFile = report.destination
            if (outputFile.exists() && outputFile.text.contains("<error ") && !checkstyleTask.ignoreFailures) {
                throw new GradleException("There were checkstyle errors! For more info check $outputFile")
            }
        }
    }
}

// preBuild的时候，执行projectCheckStyle任务
preBuild.dependsOn projectCheckStyle
```

# Findbugs


# PMD

# Infer

# Lint

# CI集成

# 相关链接


[CheckStyle官网](http://checkstyle.sourceforge.net/)

[Android Studio配置CheckStyle](http://www.jianshu.com/p/fc2f45a9ee37)

[Github AndroidCodeQuality](https://github.com/MasonLiuChn/AndroidCodeQuality)
