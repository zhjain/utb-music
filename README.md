# YouTube Audio Player

一个简洁的 Android 应用，专注于 YouTube 视频的音频播放功能。

## 功能特点

- 播放 YouTube 视频的音频
- 创建和管理本地播放列表
- 导入 YouTube 播放列表
- 支持多种播放模式（顺序播放、循环播放、随机播放、单曲循环）
- 后台播放支持
- 媒体通知控制

## 技术栈

- Kotlin
- Jetpack Compose UI
- Room 数据库
- YouTube Data API
- Media3 / MediaSession
- Coroutines & Flow
- Material 3 设计

## 开发环境要求

- Android Studio Iguana | 2023.2.1 或更高版本
- JDK 11 或更高版本
- Android SDK 35 (Android 15)
- Gradle 8.11.1

## 构建与运行

1. 克隆仓库
   ```
   git clone https://github.com/yourusername/youtubeaudioplayer.git
   ```

2. 在 Android Studio 中打开项目

3. 配置 YouTube API 密钥
   - 在 [Google Cloud Console](https://console.cloud.google.com/) 创建项目并启用 YouTube Data API v3
   - 获取 API 密钥
   - 将 API 密钥添加到 `local.properties` 文件中:
     ```
     youtube.api.key=你的API密钥
     ```
   - 或者设置环境变量 `YOUTUBE_API_KEY`

4. 构建并运行应用

## 项目结构

- `app/src/main/java/com/startend/youtubeaudioplayer/`
  - `data/` - 数据模型、数据库和仓库
  - `service/` - 后台服务和 API 服务
  - `ui/` - Compose UI 组件
    - `player/` - 播放器相关组件
    - `playlist/` - 播放列表相关组件
    - `theme/` - 应用主题

## 待实现功能

- 搜索功能
- 用户认证
- 播放列表排序和重排序
- 离线播放
- 播放历史
- 设置页面
- 音频均衡器
- 定时关闭
- 分享功能

## 贡献

欢迎提交 Pull Request 或创建 Issue 来帮助改进这个项目。

## 许可证

[MIT License](LICENSE)
