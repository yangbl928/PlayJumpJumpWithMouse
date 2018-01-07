package com.ybl.jump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * main类
 *
 * @author yangbolin
 */
public class Main {

    /**
     * 测试入口
     *
     * @param args 参数列表
     */
    public static void main(String[] args) {

        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help");
        opt.setRequired(false);
        options.addOption(opt);

        // adb执行文件位置
        opt = new Option("a", "adb-path", true,
            "adb path in system, eg: /usr/local/Caskroom/android-platform-tools/27.0.1/platform-tools/adb");
        opt.setRequired(false);
        options.addOption(opt);

        // 截图本地存储位置
        opt = new Option("p", "screenshot-path", true,
            "screenshot path, eg: /Users/yangbolin/git/screen/screen.png");
        opt.setRequired(false);
        options.addOption(opt);

        // 每隔多久进行一次截图
        opt = new Option("t", "interval", true, "screenshot interval, unit millisecond, eg: 2500");
        opt.setRequired(false);
        options.addOption(opt);

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine commandLine = null;
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption('h')) {
                hf.printHelp("PlayJump", options, true);
            }

            if (commandLine.getOptionValue('a') != null) {
                Constants.ADB_PATH = commandLine.getOptionValue('a');
            }

            if (commandLine.getOptionValue('p') != null) {
                Constants.SCREENSHOT_LOCATION = commandLine.getOptionValue('o');
            }

            if (commandLine.getOptionValue('t') != null) {
                Constants.SCREENSHOT_INTERVAL = Long.valueOf(commandLine.getOptionValue('t'));
            }
        } catch (ParseException e) {
            hf.printHelp("PlayJump", options, true);
            return;
        }

        autoJumpMode(Constants.SCREENSHOT_INTERVAL, Constants.SCREENSHOT_LOCATION);

    }

    /**
     * 自动跳动模型
     *
     * @param screenshotInterval 截图间隔
     * @param screenshotPath     本地存放截图路径
     */
    private static void autoJumpMode(final Long screenshotInterval,
                                     final String screenshotPath) {
        new Thread() {
            @Override
            public void run() {
                double resizedDistancePressTimeRatio = 765D / 453D;
                //// 初始值 (前一个跳转开始点)
                //int lastStartX = -1;
                //// 上一次耗时
                //double lastCostTime = -1D;

                while (true) {
                    try {
                        AdbUtil.printScreen();
                        BufferedImage bufferedImage = ImageIO.read(new File(screenshotPath));

                        // 计算跳动X坐标
                        int firstPointX = CenterFinder.findStartCenter(bufferedImage);
                        int secondPointX = CenterFinder.findEndCenter(bufferedImage, firstPointX);
                        int distance = Math.abs(secondPointX - firstPointX);

                        //// 调整跳转参数
                        //if (lastStartX != -1 && lastCostTime != -1) {
                        //    resizedDistancePressTimeRatio = lastCostTime / Math.abs(firstPointX - lastStartX);
                        //}

                        System.out.println("firstPointX = " + firstPointX + ", secondPointX = " + secondPointX
                            + ", distance = " + distance + ", ratio = " + resizedDistancePressTimeRatio);

                        // 跳转
                        double time = distance * resizedDistancePressTimeRatio;
                        //lastStartX = firstPointX;
                        //lastCostTime = time;
                        AdbUtil.longPress(time);
                        AdbUtil.printScreen();
                        // number
                        try {
                            Thread.sleep(screenshotInterval);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
