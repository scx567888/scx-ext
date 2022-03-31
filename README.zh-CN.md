<p align="center">
    <img src="https://scx.cool/img/scx-ext-logo.svg" width="300px"  alt="scx-ext-logo"/>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/scx567888/scx-ext/actions/workflows/ci.yml">
        <img src="https://github.com/scx567888/scx-ext/actions/workflows/ci.yml/badge.svg" alt="CI"/>
    </a>
    <a target="_blank" href="https://search.maven.org/artifact/cool.scx/scx-ext">
        <img src="https://img.shields.io/maven-central/v/cool.scx/scx-ext?color=ff69b4" alt="maven-central"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx-ext">
        <img src="https://img.shields.io/github/languages/code-size/scx567888/scx-ext?color=orange" alt="code-size"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx-ext/issues">
        <img src="https://img.shields.io/github/issues/scx567888/scx-ext" alt="issues"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx-ext/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/scx567888/scx-ext" alt="license"/>
    </a>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/badge/SCX-f44336" alt="SCX"/>
    </a>
    <a target="_blank" href="https://github.com/google/guava">
        <img src="https://img.shields.io/badge/Guava-44be16" alt="Guava"/>
    </a>
    <a target="_blank" href="https://github.com/zxing/zxing">
        <img src="https://img.shields.io/badge/ZXing-6269d3" alt="ZXing"/>
    </a>
    <a target="_blank" href="https://github.com/apache/poi">
        <img src="https://img.shields.io/badge/Apache POI-9c27b0" alt="Apache POI"/>
    </a>
</p>

简体中文 | [English](./README.md)

> SCX 的拓展模块

## Maven

``` xml
<dependency>
    <groupId>cool.scx</groupId>
    <artifactId>scx-ext</artifactId>
    <version>{version}</version>
</dependency>
```

## 快速开始

#### 1. 编写您自己的模块并运行 main 方法 。

``` java
import cool.scx.Scx;
import cool.scx.ScxModule;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fss.FSSModule;

// 注意 : 自定义的模块需要继承 ScxModule
public class YourModule implements ScxModule {

    // 使用提供的拓展模块
    public static void main(String[] args) {
        // 使用 Scx 构建器 ,构建并运行 项目
        Scx.builder()
                .setMainClass(YourModule.class) // 1, Main 方法的 Class
                .addModules(                    // 2, 引入拓展模块和您自己的模块
                        new CRUDModule(),       //     CRUD 模块
                        new FSSModule(),        //     文件存储模块
                        new CMSModule(),        //     CMS 模块
                        new YourModule())       //     您自己的模块
                .setArgs(args)                  // 3, 外部参数
                .run();                         // 4, 构建并运行项目
    }

}
```

有关更多信息，请参阅 [文档](https://scx.cool/docs/scx/index.html)
