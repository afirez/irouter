# 你想要的响应式路由 irouter

## 在项目中引入 [irouter](https://github.com/afirez/irouter)

添加 **spi-gradle-plugin** 插件到你的项目

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

添加 **[spi](https://github.com/afirez/spi)** 到需要的子模块

```

implementation "com.afirez.spi:spi:1.0.1"
```

添加 **irouter** 到需要的子模块

```

implementation "com.afirez.irouter:irouter-api:1.0.0"

// implementation "com.afirez.rxactivityresult:rxactivityresult:1.0.0"
// implementation "io.reactivex.rxjava2:rxjava:2.2.10"
// implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'

```

## 如何使用 irouter

可以先运行，看看示例，后续完善用例

## 感谢

- [RxActivityResult](https://github.com/VictorAlbertos/RxActivityResult)

![afirez](https://user-gold-cdn.xitu.io/2019/6/1/16b13c2f917705f9?w=200&h=200&f=jpeg&s=20853)