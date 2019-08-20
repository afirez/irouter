# irouter

[中文](README_CN.md)
> Router for componentization.

## Download

Add **spi-gradle-plugin** to your project !

```

buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath 'com.afirez.spi:spi-gradle-plugin:1.0.1'
  }
}

// in module build.gradle
apply plugin: 'com.android.application'
apply plugin: 'spi' 
// or apply plugin: 'com.afirez.spi'

```

Add **spi** to module project if needed !

```

implementation "com.afirez.spi:spi:1.0.1" 

```

Add **irouter** to module project if needed !

```

implementation "com.afirez.irouter:irouter-api:1.0.0"

// implementation "com.afirez.rxactivityresult:rxactivityresult:1.0.0"
// implementation "io.reactivex.rxjava2:rxjava:2.2.10"
// implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'

```



## Usage

Waiting for use case.

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