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
    <a target="_blank" href="https://github.com/zxing/zxing">
        <img src="https://img.shields.io/badge/ZXing-44be16" alt="ZXing"/>
    </a>
    <a target="_blank" href="https://github.com/apache/poi">
        <img src="https://img.shields.io/badge/Apache POI-6269d3" alt="Apache POI"/>
    </a>
    <a target="_blank" href="https://github.com/cbeust/testng">
        <img src="https://img.shields.io/badge/TestNG-9c27b0" alt="TestNG"/>
    </a>
</p>

English | [简体中文](./README.zh-CN.md)

> Extension modules for SCX

## Maven

``` xml
<dependency>
    <groupId>cool.scx</groupId>
    <artifactId>scx-ext</artifactId>
    <version>{version}</version>
</dependency>
```

## Quick start

#### 1. Write your own module and run the main method .

``` java
import cool.scx.Scx;
import cool.scx.ScxModule;
import cool.scx.ext.cms.CMSModule;
import cool.scx.ext.crud.CRUDModule;
import cool.scx.ext.fss.FSSModule;

// Note : Custom modules need implements ScxModule
public class YourModule implements ScxModule {

    // Use the provided extension modules 
    public static void main(String[] args) {
        // Use Scx Builder, build and run project
        Scx.builder()
                .setMainClass(YourModule.class) // 1, The class of the Main method
                .addModules(                    // 2, Extension modules and your own modules
                        new CRUDModule(),       //     CRUD module
                        new FSSModule(),        //     FFS  module
                        new CMSModule(),        //     CMS  module
                        new YourModule())       //     Your own modules
                .setArgs(args)                  // 3, External parameters
                .run();                         // 4, Build and run project
    }

}
```

For more information, see [docs](https://scx.cool/docs/scx/index.html)

## Stats

![Alt](https://repobeats.axiom.co/api/embed/0f2641577ec3946348ea80315059aba02e1fd31e.svg "Repobeats analytics image")