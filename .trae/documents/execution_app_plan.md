# 执行力 App 系统性优化与重构计划

## 1. 总结 (Summary)
本计划涵盖了对现有 App 的 7 项系统性优化，包括：字体排版重构、滑动手势交互改造、任务进度计数修正、结束页内容替换、AI 提示词优化与 A/B 测试、输入校验弹窗体验升级，以及替换 App 图标。此外，作为前置步骤，将先在本地初始化 Git 仓库，备份当前（上一个）版本，并准备将其推送到 GitHub 的新项目中。

## 2. 当前状态分析 (Current State Analysis)
- **Git 仓库**: 代码库目前尚未初始化 Git 仓库，需作为前置步骤进行备份。
- **UI 与排版**: `CardScreen.kt` 中的文本缩进和行高尚未完全符合需求，且未全局过滤句尾句号和开头的数字标号；`InitialScreen.kt` 的启动按钮文案目前为“开始滚雪球”。
- **手势交互**: 滑动操作提示目前仍在卡片内部，使用背景变色，不符合需求中“在屏幕上下边缘渐现”和速度距离阈值触发的交互逻辑。
- **进度计数**: 进度当前基于 `completedSteps.size/6` 显示，从 0/6 开始。需改为 1/6–6/6 的逻辑，并在单元测试中进行验证。
- **结束页**: `SummaryScreen.kt` 目前通过网络请求动态生成总结文本，需替换为常驻 3 秒的静态文本并自动返回首页。
- **AI 提示词**: `LlmRepository.kt` 中的提示词尚未采用“场景-角色-任务-格式”四段式结构，且未加入 A/B 测试逻辑。
- **输入校验**: 校验失败时 `InitialScreen.kt` 使用默认 `AlertDialog`。需要改为每次输入后必现的底部滑入弹窗（带遮罩），并软化文案。
- **App 图标**: 目前使用的是安卓默认图标，需使用提供的图片生成 mipmap 资源并替换。

## 3. 建议的修改 (Proposed Changes)

### 3.0 前置任务：版本控制与备份
- **目标**: 备份当前版本代码。
- **操作**: 
  - 在项目根目录运行 `git init`。
  - 执行 `git add .` 和 `git commit -m "chore: save previous version before optimizations"`。
  - 由于本地未检测到 `gh` CLI，将准备好完整的 GitHub 推送命令指引供您后续绑定远程仓库。

### 3.1 字体与排版重构
- **文件**: `CardScreen.kt`, `InitialScreen.kt`
- **操作**:
  - `CardScreen.kt`: 为“具体小任务” `Text` 组件配置 `TextStyle(textIndent = TextIndent(firstLine = 2.em))` 以实现首行缩进；字号提升至 22sp，行高设置为 33sp（1.5倍）。
  - `CardScreen.kt`: 完善 `cleanTaskText()` 扩展函数，使用正则 `replace(Regex("^[0-9]+[、.]\\s*"), "")` 和 `replace(Regex("[。.]+$"), "")` 全局过滤首部标号和句尾句号。
  - `InitialScreen.kt`: 将主按钮文案修改为“启动”。

### 3.2 滑动手势交互层改造
- **文件**: `CardScreen.kt`
- **操作**:
  - 移除卡片内部自带的滑动背景色渐变和文本提示。
  - 在外层 `BoxWithConstraints` 中，根据卡片的 `offsetY` 与 `screenHeightPx` 计算滑动比例。
  - 添加位于屏幕顶部（“换一个”）和底部（“完成”）的绝对定位文本组件。
  - **渐现逻辑**: 当滑动比例在 0% - 25% 时透明度线性增加，达到 25% 距离时透明度为 80%。根据阈值动态插值计算颜色（可触发状态为 #4CAF50，未触发状态为 #9E9E9E）。
  - **滑动阈值**: 引入 `VelocityTracker`。在手指释放时（`onDragEnd`），如果滑动距离 ≥ 1/3 屏幕高度，且速度 ≥ 300 px/s，则触发相应操作（完成或换一个）；否则触发 `animateTo(0f)` 弹性回滚。

### 3.3 任务进度计数修正
- **文件**: `MainViewModel.kt`, `CardScreen.kt`, `app/src/test/.../ProgressLogicTest.kt` (新建)
- **操作**:
  - `MainViewModel.kt`: 添加计算属性 `currentProgressStep`，逻辑为 `min(completedSteps.size + 1, 6)`。
  - `CardScreen.kt`: 进度条 UI 绑定此新逻辑，文本显示 `${currentProgressStep}/6`，进度条比例为 `currentProgressStep / 6f`。
  - 新增基于 ViewModel 或纯逻辑的单元测试 `ProgressLogicTest`，确保进度的初始状态为 1，完成每次任务后递增 1，最终达到 6 时不溢出，覆盖率 ≥ 90%。

### 3.4 结束页内容替换
- **文件**: `SummaryScreen.kt`, `MainViewModel.kt`
- **操作**:
  - `MainViewModel.kt`: 移除 `llmRepository.generateSummary` 相关的网络请求和逻辑。
  - `SummaryScreen.kt`: 清除旧 UI，替换为居中显示的静态文本“恭喜你完成了目标”（字号 24 sp，字重 600，颜色 #212121）。
  - 添加 `LaunchedEffect(Unit)` 和可点击修饰符，在停留 3 秒或被点击时触发路由返回首页。

### 3.5 AI 提示词优化与 A/B 测试
- **文件**: `LlmRepository.kt`, `PreferencesManager.kt`
- **操作**:
  - `PreferencesManager.kt`: 新增随机分配 A/B 组的持久化标识（50%概率）。
  - `LlmRepository.kt`: 重构 `generateNextStep` 的 System Prompt 为四段式（场景、角色、任务、格式）。
  - 对于分配到新策略组的用户，在 Prompt 末尾追加强制约束：“请给出 1条可在 10 分钟内落地的具体动作，不说废话。”
  - 增加日志埋点输出当前所属的 A/B 组及耗时信息，以备后续 SDK 收集“建议采纳率”与“任务完成时长”。

### 3.6 输入校验弹窗体验升级
- **文件**: `InitialScreen.kt`, `LlmRepository.kt`, `MainViewModel.kt`
- **操作**:
  - `LlmRepository.kt`: 修改 `validateGoal` 的 Prompt，使其无论输入如何，都强制给出一个后端修改意见（或在完美时给予特定结构），不再单纯用“合理”做区分。
  - `InitialScreen.kt`: 引入 `ModalBottomSheet` 替代原有 `AlertDialog`，设置 200ms 动画与减速曲线，遮罩设为 50% 透明度并支持点击关闭。
  - 弹窗内应用新文案：“再确认一下”及“我们注意到你输入了...你是不是想...”。提供“不做修改”、“接受修改”及“我再想想”按钮。

### 3.7 替换 App 图标
- **文件**: `app/src/main/res/mipmap-*` 
- **操作**: 
  - 从当前的上下文中提取您提供的图片。
  - 在执行阶段使用脚本或 Android Studio 资源生成工具，将其转换为各 DPI 下的 `ic_launcher.webp` 和 `ic_launcher_round.webp` 并覆盖旧文件。

## 4. 假设与决策 (Assumptions & Decisions)
1. **数据层决策**: 用户需求提及 `TaskRepository`，但在当前项目中该指责由 `MainViewModel` 的 StateFlow 承担。为避免过度重构，进度修改将直接基于现有的 `MainViewModel` 实现。
2. **A/B 测试埋点**: 由于目前无具体的 Analytics SDK 依赖，暂用 DataStore 分组和 Log 打印来验证 A/B 测试逻辑。
3. **GitHub 推送**: 本地缺乏 GitHub CLI（`gh`）工具，计划仅在本地完成 Git 仓库初始化与首次 commit 备份。远程仓库的创建及 `git remote add`、`git push` 将通过提示引导您完成。
4. **App 图标**: 将通过提供的图片资源替换安卓默认的 `ic_launcher` 资源，并保证透明背景等处理尽可能兼容。

## 5. 验证步骤 (Verification steps)
1. 在终端运行 `git log`，确认“上一个版本”已正确 commit。
2. 运行应用，输入目标后，验证底部滑入弹窗是否必现、动画是否平滑、文案和按钮逻辑是否正确。
3. 检查卡片页面：文字是否缩进 2em，是否去除了开头的数字和结尾的句号。
4. 验证进度展示：初始进入卡片即显示 1/6，完成任务时依次递增至 6/6。
5. 运行单元测试并验证进度逻辑覆盖率。
6. 上下滑动卡片，观察顶部与底部操作按钮的颜色（根据是否达标在 #4CAF50 和 #9E9E9E 间切换）和透明度（最高 80%）的渐变，释放时检查触发速度（≥ 300px/s）和阈值（≥ 1/3 屏高）。
7. 完成全部任务后，验证结束页是否静态显示目标文案，且在 3 秒后或点击时回到首页。
8. 观察 Logcat 中打印的 A/B 组别及新四段式 Prompt 是否生效。