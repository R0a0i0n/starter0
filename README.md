<div align="center">
  <!-- 占位图：Hero Banner -->
  <img src="https://via.placeholder.com/1200x500/F8F9FA/343A40?text=Rolling+Start+Hero+Banner" alt="Rolling Start Banner" width="100%">

  <h1>启动执行力 App (Rolling Start)</h1>
  
  <p>
    <b>一款帮助你“马上开始行动”的轻量化安卓应用</b>
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

## 📖 作品简介

**启动执行力 App** 是一款专为解决“启动困难”而设计的轻量化应用。

很多人不是没有目标，而是卡在“知道该做什么，却迟迟开始不了”的状态里。本 App 会先了解用户想做什么、现在正在做什么、以及当前的阻力，然后通过 AI 把大目标拆成 **5 个极小的可执行步骤 + 1 个最终目标**，一步一步带着用户往前走。

它巧妙地融合了**卡片滑动、计时器、进度条、音效和鼓励反馈**，把“开始做事”这件原本困难且充满压力的事，变成一个更轻松、更有节奏、更容易坚持的小过程。

### 💡 核心创意
- **一次只给一步**：不一次丢给用户完整计划，降低认知负荷。
- **极致微小**：每一步都尽量小、简单、能立刻开始，彻底降低心理压力。
- **心态转变**：让用户从“我做不到”变成“我先做这一小步试试”。

---

## 🎯 需求分析

### 痛点与挑战
很多人在面对学习、工作、写作、整理、运动等任务时，经常出现“启动困难”。问题往往不在于任务本身太难，而是目标太大、步骤太模糊、情绪阻力太强。常见表现包括：
- 想做事，但总在刷手机、发呆、拖延。
- 知道目标很重要，却不知道第一步该做什么。
- 即使列了计划，也容易觉得复杂、麻烦，最后放弃。
- 做到一半卡住时，没有及时的引导和鼓励。

### 要解决的问题
1. 帮用户把“大目标”变成“马上能做的小动作”。
2. 帮用户跨过最难的“开始那一下”。
3. 在执行过程中持续提供下一步引导，减少中途放弃。
4. 用更轻松的交互方式降低拖延和畏难情绪。

### 👥 目标人群
- **容易拖延、启动困难的人群**。
- **学生群体**：如写作业、复习、备考、写论文时容易卡住的人。
- **上班族**：如写周报、整理资料、做方案、处理待办时容易拖延的人。
- **自由职业者或创作者**：如写文章、画图、剪视频时难以进入状态的人。
- **需要温和提醒和陪伴式执行支持的人群**。

---

## ✨ 功能介绍

### 1. 三项输入，快速开始
用户打开应用后，首先只需回答三个问题：**你想做什么？你现在正在做什么？你现在的阻力是什么？**
输入完成后，点击“启动”，应用即可快速获取用户的目标与当前状态，为后续 AI 拆解任务提供精准依据。

### 2. 目标合理性检验
在正式开始前，系统会智能判断用户输入的目标是否清晰、具体、可执行。如果目标过于模糊或不现实，系统会弹出温和的提示弹窗，引导用户修改为更明确、更容易落地的表达，避免因目标过大而难以开始。

### 3. AI 拆解任务，分步推进
<div align="center">
  <img src="https://via.placeholder.com/800x450/F8F9FA/4CAF50?text=AI+Goal+Breakdown+Illustration" alt="AI Goal Breakdown" width="80%">
</div>
AI 会将目标拆解为固定的 6 张卡片（前 5 张为小任务，第 6 张为最终目标）。每张卡片只展示当前最适合执行的一步，用户不用一次想很多，只需要专注当下这一小步。

### 4. 滑动卡片交互，操作直观
<div align="center">
  <img src="https://via.placeholder.com/800x450/F8F9FA/2196F3?text=Swipe+and+Execute+Illustration" alt="Swipe Interaction" width="80%">
</div>
类似 Tinder 的手势操作：**向上滑表示“完成”，向下滑表示“换一个”**。
滑动过程中界面会显示不同颜色的渐变反馈，这种交互形式比普通按钮更轻松，也更有参与感。

### 5. 计时器与进度条，强化执行感
卡片界面中会显示当前步骤的**计时器**，记录用户在这一小步上花费的时间。顶部通过**进度条（如 1/6）**展示当前所处阶段，可视化的正向反馈能够增强行动感和持续完成的动力。

### 6. “换一个”机制，降低卡点
如果觉得当前任务仍然太难或不适合当下状态，用户可以下滑选择“换一个”。填写当前困难后，AI 会重新生成更适合的当前步骤，避免用户因为卡在某一步而放弃整个流程。

### 7. 中断后可恢复，提升连续性
中途退出应用后，下次打开时系统会自动检测未完成的目标。用户可选择继续上次进度，减少中断带来的执行断层，轻松回到原本的任务节奏中。

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
4. 点击运行按钮，将应用部署到模拟器或 Android 实体设备。

---

## 📄 许可证 (License)

本项目采用 [MIT License](LICENSE) 开源许可证。

<br/>
<div align="center">
  <i>"Don't focus on the entire mountain, just focus on the next step."</i>
</div>
