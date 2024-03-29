# BFS 第一里程碑需求

## 需求简介

在 Android 端，BFS 最终能够以桌面应用的形式存在，如果用户不配置成桌面，那么也会有一个类似桌面的界面。
而在第一里程碑里，我们只需要实现这个“类似桌面的界面”的精简版，下文我们称它为“主页”。

在完整的 BFS 中，会有完整的账户系统、设置中心、以及一些基本的系统应用。
而在第一里程碑中，我们只需要提供一个精简的“Web 应用容器”。它的作用就是运行目前运作在 Cordova 上的碳元域 App。

在 IOS 端，为了送审通过，需要更多的工作，需要将“主页”进一步完善成一个“网页浏览器”一般。然后以浏览器应用进行送审。

## 第一里程碑 BFS-APK 的工作流程

1. BFS-APK 会有一个系统可写的 `recommend-app/` 文件夹，存放的是 `bfs-app-id/` 为名词的的二级文件夹。里头有 `boot/` 与 `/sys` 这个两个三级文件夹，可能还会由一个 `tmp/` 文件夹（下文会介绍它的作用）。`boot/` 里头一般存放着 `link.json`，`sys/` 里头一般有 `icon.png`（目前图标只能是 png 格式）。在这个 `link.json` 文件中提供着一些基本的描述信息，**包括但不仅限于**以下信息：
   ```yaml
   version: {Semantic Version} 该文件格式的版本号，用于告知解析器该如何认知接下来的字段。以下字段是 1.0.0 的字段描述（未来默认向下兼容）
   bfsAppId: {string} 唯一标识，也就是 bfs-app-id，跟文件夹一致。未来该数据需要从链上申请，所以格式需要保持一致：长度为7+1（校验位）的大写英文字母或数字（链就是系统的“证书颁发机构”，资深用户可以配置不同的的链来安装那些未知来源的应用）
   name: {string} 应用名词（没有i18n的支持）
   icon: 应用图标（没有不同主题的支持），一般是 file:///sys/icon.png（这里使用 /sys 文件夹，意味着在虚拟文件系统中，顶多只能访问到bfs-app-id/下的文件内容） 或者是 https://example.com/icon.png （第一里程碑可以不支持链接图片，一般该图片下载下来后，放到 tmp/ 缓存文件夹中直接使用），未来可能支持分布式幂等文件的协议 dweb://
   author: {string[]} 作者名称与TA的链接，用“,”进行分割，比如： ["kzf,kezhaofeng@bnqkl.cn,https://bnqkl.cn/developer/kzf"]
   homepage: 应用网络主页，一般是https网站。用户可以通过一些特定的操作来访问应用主页了解更多应用信息
   autoUpdate: 自动更新的相关配置
      maxAge: {number} 最大缓存时间，一般6小时更新一次。最快不能快于1分钟，否则按1分钟算。
      provider: {"generic"} 该更新的适配器信息，默认使用“通用适配器”
      url: {string} 自动更新的链接，一般是https开头，请求该链接可以获得以下“通用适配器”的字段：
         version: {string} 版本号
         files: 文件列表
            url: {string} 链接
            size: {number} 大小
            sha512: {string} 校验码
         releaseNotes: {string} 本次发布的信息，一般存放更新信息
         releaseName: {string} 本次发布的标题，用于展示更新信息时的标题
         releaseDate: {Date} 发布日期
   ```
   > 可以看出，这个 `link.json` 中只是存放着这个应用相关的“资源链接”，方便用户查看该应用的主页等信息，或者直接下载安装该应用。
1. BFS-APK 会有一个系统可写的 `system-app/` 文件夹（未来会有`user-app/`文件夹，第一里程碑里不需要实现），里头存放的也是以 `bfs-app-id` 为文件夹名词的二级文件夹。在第一里程碑里，system-app 是空的，我们不需要内置任何应用。在二级文件夹中，包含但不限于以下文件与文件夹：

   ```yaml
   boot/: 应用启动所需的一些文件。对用户只读，对系统可写。应用还没有启动的时候，它所有暴露给外界的信息，全部都在这个文件夹中可以找到，也只能在这个文件夹中找到。
      bfsa-matedata.json: 如果应用以 bfs-app 的标准启动，这是所需要的应用描述文件。未来不同的标准会使用其它文件替代
      bfsa-matedata.beta.json: 未来一个程序可能会支持不同的 channel，比如：beta（测试版、公测版）、alpha（初行版、内测版）、dev（开发人员版）、bus（商户版） 等等。甚至应用双开、地区特供版也可以基于此来实现，让它们共享一个 bfs-app-id，并尽可能复用用户数据与资源文件（第一里程碑中不需要实现）
      link.json: {*} 应用外部链接信息
      icon.png: 图标文件
   sys/: 应用的核心数据，对用户只读，在这里可以找到整个 web-app 的资源文件。一般只有系统更新程序可以对其进行写入
   tmp/: 共用的缓存文件夹，被清空也不影响运作的文件、非私密信息的文件 可以存放其中。用户可以通过 Temporary-API 来使用这个文件夹的数据（第一里程碑中不需要实现）
   home/$USER_ADDRESS/: 用户的主目录，对此我们需要改写 WebStorage-API（第一里程碑中不需要实现）
   usr/: 用户之间共享资源的空间。可以通过 Share-API 来在不同用户之间共享文件，不同程序之间也可以通过用户授权获得这些文件的读写权（对于怎么安全的写入，未来会有保护用户数据与恢复的方案）。（第一里程碑中不需要实现）
   ```

   > 简而言之。第一里程碑中，我们只需要实现基于 boot 中的 bfsa-metadata.json ，找到存放在 sys 中的文件来启动一个 SingleWebApp 即可

1. BFS-APK 启动后，我们会将 `recommend-app/` 与 `system-app/` 中的文件夹收集出来作为应用罗列出来，因为都是以 bfs-app-id 为命名标准，所以如果重复，优先使用 system-app。无需排序，将这些数据整理好，罗列在“主页”中，以传统桌面的形式展示它们的图标和名词

   1. 罗列出来后，开始进行轮询更新的机制。基于 `link.json`（如果有的话）中的 autoUpdate 来获取数据更新信息。更新频率以 maxAge 的配置为参考进行信息更新。更新的信息通用缓存在 `bfs-app-id/tmp/autoUpdate/` 中，文件格式为 `TIME.MD5.json`（TIME 是可读数字，格式为 `YYYYMMDDhhmmss`，基于国际 0 时区，比如 20220823113946.json）。基于这些文件可以展示给用户 CHANGELOG 信息，所以默认不需要做自动清理。
   1. 用户点击的如果是 `recommend-app`，执行以下步骤：
      1. 基于 `autoUpdate` 去获取下载链接与对应的更新信息（如果优先使用 maxAge 中的数据，但即便命中缓存，也要立即进行更新），弹出“下载安装的对话框”让用户确认下载
         > 在命中缓存后，如果发现还有新的更新的信息，那么需要立刻更新对话框里的信息，如果用户已经点击了下载，那么打断之前的下载，直接下载最新的。
      1. 下载交互行为与 IOS 的下载安装的动效一致，在图标上以环状图绘制进度。
      1. 下载的是 `.bfsa` 的格式的压缩文件。将它解压放入 `system-app/` 文件夹中（依然以 bfs-app-id 作为文件夹名称）
      1. 解压完成意味着安装完成，显示 badge（图标右上角由一个小红点）。
         > 不需要自动启动应用
   1. 用户点击的如果是 `system-app`，执行以下步骤：

      1. 读取 bfsa-metadata.json 文件，这个文件包含但不仅限于以下信息：

         ```yaml
         version: {Semantic Version} 该应用的版本号
         id: {string} 唯一标识。和 link.json 中的 bfsAppId 一样
         name: {string} 同上
         engines: 依赖的版本号，第一里程碑中只需要提供以下固有字段：
           dwebview: "~1.0.0"
         icon: {string} 图标链接，这里暂时不能是传统 https:// 网络链接，只能是本地文件路径： file://
         enter: {
           "main": "dweb+file:///sys/www/?default=index.html" 这里 `dweb+file://` 指代使用 dwebview 引擎将指定文件夹作为静态文件托管起来， `?default=index.html` 是指默认的启动路径。最终站点的域名类似于： `https://{bfs-app-id}.dweb/index.html`
         }
         ```

         > 这里提到的 dwebview，是指 Android 上的一个 Activty。详见现有 Plaoc 项目中的源码，之前的封装较为粗糙，需要将之进一步改造成所需效果。后续我们还需要在此基础上增加一些所需的插件功能。

      1. 基于 id 字段，启动一个 webview-host-server（拦截 webview 的链接前缀为 `https://{bfs-app-id}.dweb/` 的访问），基于 enter 字段，提供静态的文件服务
      1. 启动一个 dwebview-activity，访问地址 `https://{bfs-app-id}.dweb/index.html`

## 代码要求

1. 所有关于文件系统的编程，都需要进行一定的封装。比如说上文提到的 Share-API（这个不需要在第一里程碑中实现）、Temporary-API、还有关于 link.json、icon.png 的读取等等。这是因为在未来很快会有独立的加密数据库来替代现有的文件系统，以提供更好的性能和不同平台之间更好的一致性。
1. 这里有一个虚拟的文件系统，使得 BFS-APP 应用在读取 `file:///` 这个根路径的时候，本质上指向的是 `bfs-app-id/` 这个路径。第一里程碑中无需刻意去实现该模块，在第二里程碑里，该文件系统有 BFS 内部使用 js 自行实现。所以如果需要深度封装，那么第一里程碑里需要充分理解第二里程碑的需求后再进行对应的开发，否则不建议在其中投入过多精力。
1. 为dwebview提供静态文件服务的时候，尽量不要启动任何http服务来任何功能。基于webview的api进行拦截虽然功能很受限，但是这是安全性的原则问题。

## 加分项：

1. Android 上，如果使用 chromium 替代现有的系统默认的 WebView，那是最好不过了。建议使用 https://github.com/ridi/chromium-aw 这个包
