Android静态代码分析

---

[TOC]

最佳项目里面来了很多新的小伙伴，然后每个人的代码风格还不一样，虽然有代码风格文档以及代码review。

但是这些东西需要花费很多人力和时间来做，所以就研究了下静态代码分析，能用工具完成的坚决不用人肉。同时静态代码分析还能解决很多潜在的bug问题。

下面依次对介绍几个Android常用的静态代码分析工具，同时顺便介绍下我厂刘全栈的对静态代码分析做的工具
[AndroidCodeQuality](https://github.com/MasonLiuChn/AndroidCodeQuality)。

本文最先发表于[Github](https://github.com/LiushuiXiaoxia/CodeAnalysis)，如有转载，请注明转载出处。

# CheckStyle

[CheckStyle官网](http://checkstyle.sourceforge.net/)

『Checkstyle是一个开发工具用来帮助程序员编写符合代码规范的Java代码。它能自动检查Java代码为空闲的人进行这项无聊(但重要)的任务。』

正如Checkstyle的开发者所言，这个工具能够帮助你在项目中定义和维持一个非常精确和灵活的代码规范形式。当你启动CheckStyle，它会根据所提供的配置文件分析你的Java代码并告诉你发现的所有错误。

Checkstyle会发现大量的问题，特别是在你运用了大量的规则配置，如同你设置了一个非常精确的语法。
尽管我通过Gradle使用checkstyle，例如在我进行推送之前，我仍然推荐你为IntellJ/Android Studio使用checkstyle插件(你可以通过Android Studio的工作面板文件/设置/插件直接安装插件)。
这种方式下，你可以根据那些为Gradle配置的相同文件在你的工程中使用checkstyle，但是远不止这些，你可以直接在Android Studio中获取带有超链接结果，
这些结果通过超链接在你的代码中对应，这是非常有用的(Gradle的这种方式仍然很重要的，因为你可以使用它自动构建系统，如Jenkins)。

## 集成

在Android中集成CheckStyle也很简单，只需要在Gradle添加少许配置即可。

```gradle
apply plugin: 'checkstyle'


// 定义生成文件目录
def checkStyleReportPath = "${project.rootDir}/reports/"

// 在clean时候，自动删除原先生成报告
clean.doFirst {
    delete checkStyleReportPath
}

task projectCheckStyle(type: Checkstyle) {
    source 'src'
    configFile file("checkstyle.xml") // 配置样式
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

# PMD

[PMD官网](https://pmd.github.io/)

事实上，PMD是一个工作有点类似Findbugs的强大工具，但是(PMD)直接检查源代码而不是检查字节码(顺便说句，PMD适用很多语言)。
(PMD和Findbugs)的核心目标是相同的，通过静态分析方法找出哪些模式引起的bug。因此为什么同时使用Findbugs和PMD呢？
好吧！尽管Findbugs和PMD拥有相同的目标，(但是)他们的检查方法是不同的。所以PMD有时检查出的bug但是Findbugs却检查不出来，反之亦然。

## 集成

```gradle
apply plugin: 'pmd'

// 定义生成文件目录
def pmdReportPath = "${project.rootDir}/analysis/reports/"

task projectPmd(type: Pmd) {
    ignoreFailures = true
    ruleSetFiles = files("pmd.xml") // 自定义规则
    ruleSets = []

    source 'src'
    include '**/*.java'
    exclude '**/gen/**', '**/build/**'

    // 定义输出报告
    reports {
        xml {
            enabled = false
            destination "$pmdReportPath/Pmd.xml"
        }
        html {
            enabled = true
            destination "$pmdReportPath/Pmd.html"
        }
    }
}

preBuild.dependsOn projectPmd
```

# FindBugs

[FindBugs官网](http://findbugs.sourceforge.net/)

findbugs是一个分析bytecode并找出其中可疑部分的一个工具。它给项目字节码做一个全面扫描，通过一些通用规则去判断可能潜在的一些问题，比如性能，多线程安全等等。

FindBugs基本上只需要一个程序来做分析的字节码，所以这是非常容易使用。它能检测到常见的错误，如错误的布尔运算符。
FindBugs也能够检测到由于误解语言特点的错误，如Java参数调整（这不是真的有可能因为它的参数是传值）。

## 集成

```gradle
apply plugin: 'findbugs'

// 定义生成文件目录
def findbugsReportPath = "${project.rootDir}/reports/"

task findbugs(type: FindBugs) {
    ignoreFailures = true
    excludeFilter = new File("findbugs.xml") // 自定义配置文件
    classpath = files()
    classes = fileTree('build/intermediates/classes/')
    effort = 'max'

    source = fileTree('src')
    include '**/*.java'
    exclude '**/gen/**'

    // 定义输出报告
    reports {
        xml {
            enabled = false
            destination "$findbugsReportPath/FindBugs.xml"
            xml.withMessages true
        }
        html {
            enabled = true
            destination "$findbugsReportPath/FindBugs.html"
        }
    }
}

afterEvaluate {
    tasks.withType(Task).each { task ->
        task.doLast {
            if (task.name.startsWith("assemble")) {
                tasks.findByName("findbugs").execute()
            }
        }
    }
}
```

# Infer

[Facebook infer官网](http://fbinfer.com/)

Infer 是Facebook的一个开源代码扫描工具。目前在移动端, Infer 是比较好用的一个检查空对象引用的静态扫描工具.

在mac上面使用infer比较简单，参考如下步骤即可，其他平台可以参考[官网教程](http://fbinfer.com/docs/getting-started.html)。

首先使用brew安装infer

```
brew install infer
```

然后在项目根目录运行如下命令即可，最后会在根目录下面生成infer-out目录。

```
./gradlew clean; infer -- ./gradlew build
```

同时Github上面有个Infer的Gradle插件，[地址](https://github.com/uber-common/infer-plugin)，可以参考下。

使用也比较简单，根目录添加插件依赖

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        ...
        classpath "com.uber:infer-plugin:0.7.4"
    }
}
```

Java项目可以使用如下配置

```
apply plugin: 'java'
apply plugin: 'com.uber.infer.java'
```

Android的配置

```
apply plugin: 'com.android.application'
apply plugin: 'com.uber.infer.android'
```

同时支持配置

```
inferPlugin {
    infer {
        include = project.files("<PATH_TO_INCLUDE>")
        exclude = project.files("<PATH_TO_EXCLUDE>")
    }
    eradicate {
        include = project.files("<PATH_TO_INCLUDE>")
        exclude = project.files("<PATH_TO_EXCLUDE>")
    }
}
```

# Lint

[Lint官网](https://developer.android.com/studio/write/lint.html)

[Lint Api](http://google.github.io/android-gradle-dsl/2.3/index.html)


Lint是Android官网出的一款功能强大的静态代码分析工具，不仅可以分析代码，还可以分析布局文件，而且还可以硬编码、多余未使用的资源，以及可能出现的bug等。

这里有一篇介绍Lint使用的[博客](http://blog.csdn.net/u010687392/article/details/47835743)

Lint可以在Gradle中集成，当然也可以自定义输出，也可以在Android Studio中单独使用，使用『Analyze』->『Inspect Code』即可。

```
android {
    lintOptions {
        abortOnError false
        disable 'LogNotTimber', 'IconMissingDensityFolder'
        htmlReport true
        htmlOutput file("${project.rootDir}/analysis/reports/lint-report.html")
        xmlReport true
        xmlOutput file("${project.rootDir}/analysis/reports/lint-report.xml")
    }
}
```

# CI集成

[CI介绍](http://www.ruanyifeng.com/blog/2015/09/continuous-integration.html)

互联网软件的开发和发布，已经形成了一套标准流程，最重要的组成部分就是持续集成（Continuous integration，简称CI）。

![CI](https://github.com/LiushuiXiaoxia/CodeAnalysis/raw/master/doc/ci.png)

持续集成指的是，频繁地（一天多次）将代码集成到主干，它的好处主要有两个。

>（1）快速发现错误。每完成一点更新，就集成到主干，可以快速发现错误，定位错误也比较容易。
>
>（2）防止分支大幅偏离主干。如果不是经常集成，主干又在不断更新，会导致以后集成的难度变大，甚至难以集成。

在每次提交代码后，都会有CI服务器给代码做一次编译和代码检查，那么对软件开发的质量也会有很大的提高，如果在智能一点，可以通过邮件、短信、slack通知开发，那么整个开发流程也会更加友好。

后续考虑单独做一个实现一个gradle的插件，自动集成上面几种静态代码检查方式，同时可以实现一件配置，以及报告输出。

# 插件

[![](https://jitpack.io/v/LiushuiXiaoxia/CodeAnalysis.svg)](https://jitpack.io/#LiushuiXiaoxia/CodeAnalysis)

```
buildscript {
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
    dependencies {
        classpath 'com.github.LiushuiXiaoxia.CodeAnalysis:plugin:0.0.2'
    }
}
apply plugin: 'code.analyse'
analyse {
    ignoreFailures // 是否忽略失败,可选,默认为true
    checkStyleConfig // checkstyle自定义配置文件路径,可选
    pmdConfig // pmd自定义配置文件路径,可选
    findbugsConfig // findbugs自定义配置文件路径,可选
}
```


# 相关链接


[CheckStyle官网](http://checkstyle.sourceforge.net/)

[Android Studio配置CheckStyle](http://www.jianshu.com/p/fc2f45a9ee37)

[Github AndroidCodeQuality](https://github.com/MasonLiuChn/AndroidCodeQuality)

[PMD官网](https://pmd.github.io/)

[FindBugs官网](http://findbugs.sourceforge.net/)

[Facebook infer官网](http://fbinfer.com/)

[Infer插件](https://github.com/uber-common/infer-plugin)

[Infer参考](http://www.jianshu.com/p/c8a632837bf1)

[Infer参考](http://www.jdon.com/48051)

[Android lint 参考](http://blog.csdn.net/u010687392/article/details/47835743)

[Lint Api](http://google.github.io/android-gradle-dsl/2.3/index.html)

[CI介绍](http://www.ruanyifeng.com/blog/2015/09/continuous-integration.html)
