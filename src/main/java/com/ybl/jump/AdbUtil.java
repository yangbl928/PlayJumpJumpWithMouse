package com.ybl.jump;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

/**
 * adb工具类(与手机联动)
 *
 * @author yangbolin
 */
public class AdbUtil {

    /**
     * 调用adb长按屏幕
     *
     * @param timeMilli 按的秒数 (跳一跳与坐标没有关系)
     */
    public static void longPress(double timeMilli) {
        try {
            Process process = Runtime.getRuntime()
                .exec(Constants.ADB_PATH + " shell input touchscreen swipe 0 0 0 0 " + (int)timeMilli);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                System.out.println(s);
            }
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 截图并保存到本地
     */
    public static void printScreen() {
        try {
            Process p1 = Runtime.getRuntime().exec(Constants.ADB_PATH + " shell screencap -p /sdcard/screenshot.png");
            p1.waitFor();
            Process p2 = Runtime.getRuntime().exec(
                Constants.ADB_PATH + " pull /sdcard/screenshot.png " + Constants.SCREENSHOT_LOCATION);
            p2.waitFor();
            checkScreenSuccess();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判定是否截屏成功
     *
     * @throws IOException
     */
    private static void checkScreenSuccess() throws IOException {
        BufferedImage image = ImageIO.read(new File(Constants.SCREENSHOT_LOCATION));
        if (image == null) {
            throw new IOException("cann't read file \"" + Constants.SCREENSHOT_LOCATION + "\"");
        }
    }

}
