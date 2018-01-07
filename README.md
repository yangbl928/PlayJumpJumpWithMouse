## 原理
用usb调试安卓手机，用adb截图并用鼠标测量距离，然后计算按压时间后模拟按压。
```
// 截图
db shell screencap -p /sdcard/screen.png
// 拷贝到本地
adb pull /sdcard/screen.png
// 跳跃（只看时间）
adb shell input swipe 0 0 0 0 750
```
