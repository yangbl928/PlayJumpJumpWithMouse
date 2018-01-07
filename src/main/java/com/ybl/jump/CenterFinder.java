package com.ybl.jump;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 找小人的底盘中心点
 *
 * @author yangbolin
 */
public class CenterFinder {

    public static int findStartCenter(BufferedImage bufferedImage) {
        // 屏幕最大宽度
        int width = bufferedImage.getWidth();
        // 屏幕最大高度
        int height = bufferedImage.getHeight();

        // w坐标和
        int peopleSumW = 0;
        // 参与计算的w坐标个数
        int peopleCountW = 0;

        // rgb颜色(每个像素)
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = bufferedImage.getRGB(w, h);

                Color color = new Color(pixel);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // 小人的色素
                if ((50 < red && red < 60)
                    && (53 < green && green < 63)
                    && (95 < blue && blue < 110)) {
                    peopleSumW += w;
                    peopleCountW += 1;
                }

            }
        }

        int x = peopleSumW / peopleCountW;

        return x;
    }

    public static int findEndCenter(BufferedImage bufferedImage, int peopleX) {
        int hight = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int centorX = width / 2;

        int boardSumX = 0;
        int boardCountX = 0;

        // 根据小人的横坐标在屏幕左还是右,判定下一个方块的扫描的开始和结束坐标
        // 两个模块特别小,且特别近的时候,可能小人的高度 > 下一个模块的最高点,所以要减去小人的宽度
        int peopleHeadWith = 60;
        int boardStartX = peopleX + peopleHeadWith;
        int boardEndX = width;
        if (peopleX > centorX) {
            boardStartX = 0;
            boardEndX = peopleX - peopleHeadWith;
        }

        // 查找下一个模块的最上面那个点
        int boardX = 0;
        int backgroundPixel = bufferedImage.getRGB(0, 630);
        Color backgroundColor = new Color(backgroundPixel);
        int backgroundRed = backgroundColor.getRed();
        int backgroundGreen = backgroundColor.getGreen();
        int backgroundBlue = backgroundColor.getBlue();
        for (int h = 600; h < hight; h++) {
            for (int w = boardStartX; w < boardEndX; w++) {
                int pixel = bufferedImage.getRGB(w, h);
                Color color = new Color(pixel);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                if (Math.abs(red - backgroundRed)
                    + Math.abs(green - backgroundGreen)
                    + Math.abs(blue - backgroundBlue) > 20) {
                    boardSumX += w;
                    boardCountX += 1;
                }
            }
            if (boardSumX > 0) {
                boardX = boardSumX / boardCountX;
                break;
            }
        }

        return boardX;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(Constants.SCREENSHOT_LOCATION));
        int startX = CenterFinder.findStartCenter(bufferedImage);
        System.out.println(startX);

        int endX = findEndCenter(bufferedImage, startX);
        System.out.println(endX);
    }

}
