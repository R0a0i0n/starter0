<div align="center">
  <!-- 占位图：Hero Banner -->
  <img src="https://via.placeholder.com/1200x500/F8F9FA/343A40?text=Rolling+Start+Hero+Banner" alt="Rolling Start Banner" width="100%">

  <h1>Rolling Start (执行力 App)</h1>
  
  <p>
    <b>打破拖延，用 AI 将大目标拆解为 6 个 10 分钟内可落地的极简任务</b>
  </p>

  <p>
    <a href="https://kotlinlang.org">
      <img src="https://img.shields.io/badge/Kotlin-1.9.0-7F52FF.svg?style=flat&logo=kotlin" alt="Kotlin">
    </a>
    <a href="https://developer.android.com/jetpack/compose">
      <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4.svg?style=flat&logo=android" alt="Compose">
    </a>
    <a href="https://developer.android.com/studio">
      <img src="https://img.shields.io/badge/Android%20Studio-Hedgehog-3DDC84.svg?style=flat&logo=android-studio" alt="Android Studio">
    </a>
  </p>
</div>

<br/>

## 📖 关于项目 (About The Project)

**Rolling Start** 是一款专注于提升个人执行力的 Android 应用程序。面对庞大且令人生畏的目标，人们往往容易陷入拖延。本项目通过引入大语言模型（LLM），将任何模糊的宏大目标，智能拆解为 **6 个具体、可执行、耗时不超过 10 分钟的小动作**。

配合类似 Tinder 的直观卡片滑动交互，用户只需专注于眼前的“下一步”，像滚雪球一样积累微小的胜利，最终完成大目标。

---

## ✨ 核心特性 (Key Features)

### 🤖 AI 智能拆解
<div align="center">
  <!-- 占位图：AI 拆解插图 -->
  <img src="https://via.placeholder.com/800x450/F8F9FA/4CAF50?text=AI+Goal+Breakdown+Illustration" alt="AI Goal Breakdown" width="80%">
</div>

- **四段式 Prompt 架构**：内置严谨的场景-角色-任务-格式提示词模板，确保 AI 输出高质量的可落地动作。
- **智能输入校验**：在输入阶段提供 AI 视角的修改建议，帮助用户将模糊想法转化为具体目标。
- **A/B 测试验证**：内置 A/B 测试逻辑，随机对比不同提示词策略的实际采纳效果。

### 🃏 沉浸式卡片交互
<div align="center">
  <!-- 占位图：卡片交互插图 -->
  <img src="https://via.placeholder.com/800x450/F8F9FA/2196F3?text=Swipe+and+Execute+Illustration" alt="Swipe Interaction" width="80%">
</div>

- **手势驱动**：向下滑动标记“完成”，向上滑动“换一个”。
- **流畅动画**：基于速度（Velocity）与距离阈值的弹性物理动画，带有动态透明度与颜色渐变的视觉反馈。
- **无压排版**：大字号、舒适行高、首行缩进，专注于当前单一任务，消除心理负担。

### 📊 清晰的进度追踪
- 直观的 `1/6` 到 `6/6` 进度体系，每完成一步即刻获得正向反馈。
- 拒绝无尽的任务列表，6步封顶，完成后展示全屏庆祝页。

---

## 🛠️ 技术栈 (Tech Stack)

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose (Material Design 3)
- **架构**: MVVM (Model-View-ViewModel) 配合 StateFlow
- **本地存储**: Room Database, DataStore Preferences
- **网络请求**: Retrofit 2, OkHttp 3
- **异步处理**: Kotlin Coroutines

---

## 🚀 快速开始 (Getting Started)

### 前置要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34

### 编译与运行
1. 克隆本仓库到本地：
   ```bash
   git clone https://github.com/R0a0i0n/starter0.git
   ```
2. 使用 Android Studio 打开项目。
3. 等待 Gradle 同步完成。
4. (可选) 在 `LlmRepository.kt` 中配置您的 AI 服务 API Key（如果项目未自带测试 Key）。
5. 点击运行按钮，将应用部署到模拟器或 Android 实体设备。

---

## 📝 待办事项与未来规划 (Roadmap)

- [x] 重构字体排版与视觉层级
- [x] 优化手势交互逻辑与动画反馈
- [x] 引入输入目标的智能校验弹窗
- [x] 规范化 AI 提示词结构并加入 A/B 测试支持
- [ ] 接入真实的数据埋点 SDK (Analytics)
- [ ] 增加历史目标回顾与成就统计面板

---

## 📄 许可证 (License)

本项目采用 [MIT License](LICENSE) 开源许可证。

<br/>
<div align="center">
  <i>"Don't focus on the entire mountain, just focus on the next step."</i>
</div>
