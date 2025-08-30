# Health Tracker - 健康数据跟踪

采集血压、血糖等健康数据，并以图表形式直观显示数据的波动情况，从而便于识别健康风险。

## Debug

- 若 Release 版本安装失败，可尝试通过 `adb` 安装以得到失败原因：
  ```bash
  adb install -t  app/build/outputs/apk/release/app-release.apk
  ```
  > Note: 目标环境需启用 USB 调试，并通过 USB 连接到开发主机
