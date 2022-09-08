# irouter

[中文](README_CN.md)
> Router for componentization.

![README-irouter-app](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-app)

## Download

Add **spi-gradle-plugin** to your project !

```

buildscript {
  ext {
    kotlin_version = '1.5.31'
    booster_version = '4.6.0'
  }
  
  repositories {
    mavenCentral()
    google()
    // jcenter()
    
    // didi booster
    maven { url 'https://oss.sonatype.org/content/repositories/public/' }
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    
    // add maven repository for spi-plugin at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/spi/master/repo/" }
  }

  dependencies {
    classpath 'com.afirez.spi:spi-gradle-plugin:3.0.0'
    // ①
    classpath "com.didiglobal.booster:booster-gradle-plugin:$booster_version"
    // ② figure out the features you really need, then choose the right module for integration
    // ② 弄清楚真正需要的特性，然后从下面的模块列表中选择正确的模块进行集成
    // classpath "com.didiglobal.booster:booster-task-all:$booster_version"
    // classpath "com.didiglobal.booster:booster-transform-all:$booster_version"
        
    // gradle plugin version <= 4.1.2
    // classpath 'com.afirez.spi:spi-gradle-plugin:2.0.0'
    
    // gradle plugin version <= 3.6.4
    // classpath 'com.afirez.spi:spi-gradle-plugin:1.0.1'
  }
}

// in module build.gradle
apply plugin: 'com.android.application'

// didi booster
apply plugin: 'com.didiglobal.booster' // ③

// gradle plugin version <= 4.1.2
// apply plugin: 'spi' 
// or apply plugin: 'com.afirez.spi'

```

Add **spi** to module project if needed !
```
allprojects {
  repositories {
    ...

    mavenCentral()
    google()
    // jcenter()
    
    // didi booster
    maven { url 'https://oss.sonatype.org/content/repositories/public/' }
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }

    // add maven repository for spi at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/apps_android_repo/master" }

    ...
  }
}
```


```

implementation "com.afirez.spi:spi:1.0.1"

implementation "com.afirez.applike:applike:1.0.1"

```

Add **irouter** to module project if needed !

```
allprojects {
  repositories {
    ...
    
    mavenCentral()
    google()
    // jcenter()
    
    // didi booster
    maven { url 'https://oss.sonatype.org/content/repositories/public/' }
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }

    // add maven repository for spi at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/apps_android_repo/master" }

    ...
  }
}

```

```

implementation "com.afirez.irouter:irouter-api:1.0.1"

implementation "com.afirez.rxactivityresult:rxactivityresult:1.0.0"

implementation "io.reactivex.rxjava2:rxjava:2.2.12"
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'

```

## Usage

### 0、 IRouter init

![README-irouter-init](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-init)

### 1、 IRouter for Activity

#### 1、Activity without arguments

![README-irouter-activity-01.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-01.png)

#### 2、Activity with arguments

![README-irouter-activity-02.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-02.png)

#### 3、Activity for Result

![README-irouter-activity-03.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-03.png)

#### 4、Activity by path

![README-irouter-activity-04.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-04.png)

### 2、 IRouter for Fragment

![README-iouter-fragment.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-iouter-fragment.png)

### 3、 IRouter for Provider

![README-irouter-provider.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-provider.png)

### 4、 IRouter for Interceptor

![README-irouter-interceptor.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-interceptor.png)

### 5、 IRouter for AppLike

![README-irouter-applike.png](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-applike.png)

### More

1、 Load extensions by [SPI](https://github.com/afirez/spi) like [AppLike](https://github.com/afirez/spi)， if you need discovery extensions from other module.

2、 Route Fragment with IRouter can pass arguments like routing Activity.

## Thanks

- [RxActivityResult](https://github.com/VictorAlbertos/RxActivityResult)

## License


    Copyright 2019 afirez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.