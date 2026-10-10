# CyuSnowyLeaves · 积雪树叶

原创 Minecraft 树叶积雪模组，适用于 1.21.1、1.21.11 与 26.2，支持 Fabric 与 NeoForge 双端。视觉还原基岩版积雪树叶表现，下雪时树叶缓慢附上冷白雪纱，天晴后渐进融化退去

## 下载

前往 [Releases](https://github.com/HansOffice/cyusnowyleaves/releases) 页面下载对应版本与加载器的构建：
- **1.21.1**（Java 21）：`cyusnowyleaves-1.21.1-fabric-1.0.jar` / `cyusnowyleaves-1.21.1-neoforge-1.0.jar`
- **1.21.11**（Java 21）：`cyusnowyleaves-1.21.11-fabric-1.0.jar` / `cyusnowyleaves-1.21.11-neoforge-1.0.jar`
- **26.2**（Java 25）：`cyusnowyleaves-26.2-fabric-1.0.jar` / `cyusnowyleaves-26.2-neoforge-1.0.jar`

Fabric 端需安装对应版本的 Fabric API；NeoForge 端无需任何前置模组

## 内容介绍

### 纯视觉边界
- 纯客户端渲染，无需服务端安装
- 不替换真实方块，不注册新方块
- 不覆盖原版树叶材质资源
- 不改变方块破坏、掉落、衰减与刻逻辑
- 保持原版方块挖掘破坏裂纹动画
- 保持物品栏原始树叶外观，不产生杂色污染

### 支持树叶
| 树叶类型 | 覆雪强度 | 说明 |
| --- | --- | --- |
| **云杉树叶** | 100% | 寒带针叶林核心，覆雪最厚重 |
| **深色橡木树叶** | 85% | 浓郁树冠适度覆雪 |
| **橡木树叶** | 75% | 标准温带树叶覆雪 |
| **白桦树叶** | 70% | 浅色树冠微冷覆雪 |
| **金合欢树叶** | 65% | 稀树草原微弱霜冻 |
| **红树林树叶** | 65% | 沼泽湿冷微霜 |
| **丛林树叶** | 45% | 热带雨林极轻薄霜 |

樱花与杜鹃花树叶因原版纹理自带多色花卉，单色调色无法兼顾无雪表现与纯白积雪，暂保持原版外观

## 配置

配置文件位于 `config/cyusnowyleaves.json`，修改后重新进入世界生效：

```json
{
  "enabled": true,
  "accumulationSpeed": 0.003,
  "meltingSpeed": 0.004,
  "maxFrostAmount": 1.0,
  "snowColor": "#F2F7FA",
  "enablePerLeafVariation": true,
  "enableBiomeCheck": true,
  "enableDebugHud": false
}
```

- `enabled`：模组总开关
- `accumulationSpeed`：下雪时的积雪速度（推荐 0.003）
- `meltingSpeed`：天晴后的融化速度（推荐 0.004）
- `maxFrostAmount`：最大积雪厚度（0.0 ~ 1.0）
- `snowColor`：积雪十六进制颜色代码（默认冷白 `#F2F7FA`）
- `enablePerLeafVariation`：是否开启叶片微小随机霜感差异
- `enableBiomeCheck`：是否根据群系温度梯度判定覆雪
- `enableDebugHud`：左上角实时显示积雪与天气调试信息

## 构建

```bash
# 构建 1.21.1 (Java 21)
(cd cyusnowyleaves-1.21.1 && ./gradlew :fabric:build :neoforge:build)

# 构建 1.21.11 (Java 21)
(cd cyusnowyleaves-1.21.11 && ./gradlew :fabric:build :neoforge:build)

# 构建 26.2 (Java 25)
(cd cyusnowyleaves-26.2 && ./gradlew :fabric:build :neoforge:build)
```

构建产物输出在对应工程的 `fabric/build/libs/` 或 `neoforge/build/libs/` 目录下

## 协议

本项目采用 [MIT](LICENSE) 协议开源
