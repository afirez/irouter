# IRouter 组件化最佳实践
![irouter-app](https://raw.githubusercontent.com/afirez/images/master/vscode/README_CN-irouter-app.png)

你是否把所有初始化都写在一个 Applocation 中，没有组件化的时候， 项目中需要的初始化， 我们必须在一个 Applocation 中进行， 组件化之后， 基本也得在一个 Applocation 中进行。 能不能在新增组件时不改变 Application ？

你的 BaseActivity 就是上帝， 公共的业务逻辑都往 BaseActivity 里面扔， 随着项目慢慢膨胀， BaseActivity 该怎么维护 ？

组件化之后， 为了组件间通信， 你是否又新增了一个模块下沉组件间通信的接口， 随着项目的膨胀， 该下沉模块复杂度也大增 ？

项目中的耦合总是喜欢聚集在一个地方， 臭味相投， 怎么办 ？

当进行项目组件化的时候， 我们是希望能把一个稍大的项目拆分为一个个更小的组件， 使得每个更小的组件能在自己的业务边界内职责更清晰， 更容易维护， 并且随着项目的发展， 项目复杂度增长也更平缓。 在组件化实践后， 我想分享下自己在实践时的一些取舍， 如上图所示。 组件化用的路由为 IRouter， 基于另一个项目 [SPI](https://github.com/afirez/spi) 实现的路由。 SPI 原理为编译时 gradle transform 加 ASM 字节码插桩， 功能主要是接口发现与注册， 基于 SPI 还实现了 Application 生命周期组件化。 IRouter 用法像 Retrofit， 通过接口加动态代理实现。不妨看看怎么使用， 先有个感性的认识，顺便注意下项目结构。

## 在项目中引入 [irouter](https://github.com/afirez/irouter)

添加 **spi-gradle-plugin** 插件到你的项目

```

buildscript {
  repositories {
    jcenter()

    // add maven repository for spi-plugin at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/spi/master/repo/" }
  }

  dependencies {
    // gradle plugin version <= 3.6.4
    classpath 'com.afirez.spi:spi-gradle-plugin:1.0.1'
    
    // gradle plugin version >= 4.0.2
    // classpath 'com.afirez.spi:spi-gradle-plugin:2.0.0'
  }
}

// in module build.gradle
apply plugin: 'com.android.application'
apply plugin: 'spi'
// or apply plugin: 'com.afirez.spi'
```

添加 **[spi](https://github.com/afirez/spi)** 到需要的子模块

```
allprojects {
  repositories {
    ...

    // add maven repository for spi at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/spi/master/repo/" }

    ...
  }
}
```


```

implementation "com.afirez.spi:spi:1.0.1"

// 实现 Application 组件化（可选）
implementation "com.afirez.applike:applike:1.0.1"
```

添加 **irouter** 到需要的子模块
```
allprojects {
  repositories {
    ...

    // add maven repository for irouter at build.gradle file of root project
    maven { url "https://raw.githubusercontent.com/afirez/irouter/master/repo/" }

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

## 如何使用 irouter

### 0、IRouter 初始化

![irouter-init](https://raw.githubusercontent.com/afirez/images/master/vscode/README_CN-irouter-init.png)

- AppDelegate: 组件化代理 Appcation 生命周期（ AppLike ）相关初始化
- RxActivityResult: startActivityForResult 响应式封装， IRouter 用 RxActivityResult 跳转 Activity。

### 1、IRouter 之 Activity 跳转

#### 1、无参跳转 Activity

![router-activity-01](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-01.png)

#### 2、带参跳转 Activity

![irouter-activity-02](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-02.png)

- 带参数跳转 Activity 时， 参数支持大部分 intent 支持的参数类型。

#### 3、startActivityforResult

![irouter-activity-03](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-03.png)

#### 4、跳转 Activity 时动态替换路径

![irouter-activity-04](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-activity-04.png)

- 根据路径 path 动态跳转路由， 在部分场景中路由跳转会更加灵活。 例如在我们的路由是可以通过后台配置的时候。

### 2、IRouter 之 Fragment 加载

![iouter-fragment](https://raw.githubusercontent.com/afirez/images/master/vscode/README-iouter-fragment.png)

- 带参数加载 Fragment 时， 参数支持大部分 intent 支持的参数类型。

### 3、IRouter 之 Provider 自定义接口加载

![irouter-provider](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-provider.png)

### 4、IRouter 之 Interceptor 拦截器

![irouter-interceptor](https://raw.githubusercontent.com/afirez/images/master/vscode/README-irouter-interceptor.png)

- 拦截器实现基于 RxJava， 通过发送 Observable.error() 中断请求。

### 5、IRouter 之 AppLike 组件化 Application 生命周期

![irouter-applike](https://raw.githubusercontent.com/afirez/images/master/vscode/README_CN-irouter-applike.png)

 其实 [AppLike](https://github.com/afirez/spi) 不需要 irouter 也能实现 Application 生命周期组件化， AppLike 基于 [SPI](https://github.com/afirez/spi) 实现， 而且 IRouter 本身基于 SPI 实现。 如果遇到其他类似 AppLike 这样的接口加载场景， 我们还可以模仿 AppLike 实现， 这就需要看具体的场景。

## 最后

最后推荐运行一下 [irouter-app](https://github.com/afirez/irouter) 示例, 体验一下，并且在本地熟悉下示例项目的模块结构，项目组件化业务拆分其实很重要，我在完成示例的时候有意的拆分了一下组件，基本每一个业务组件都会额外加一个组件接口暴露模块， 这样的话一个组件由一个组件接口模块和一个组件实现模块构成，职责划分更清晰，组件间交叉通信时也方便，两个组件对彼此的实现不可见， 但是通过组件接口能实现通信，再一次， 我们又面向接口编程了。

## 感谢

- [RxActivityResult](https://github.com/VictorAlbertos/RxActivityResult)

![afirez](https://raw.githubusercontent.com/afirez/images/master/vscode/README_CN-afirez-200.jpg)
