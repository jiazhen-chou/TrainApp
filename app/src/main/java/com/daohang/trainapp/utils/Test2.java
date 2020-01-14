package com.daohang.trainapp.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test2 {
    public static void main(String[] args) {
        System.out.println(ImageToBase64("/Users/maodou/Downloads/WechatIMG2.jpeg"));
    }

    /**
     * 本地图片转换Base64的方法
     *
     * @param imgPath     
     */

    public static String ImageToBase64(String imgPath) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //待处理的图片
        InputStream in = null;
        byte[] data = null;
        String str = "";
        //读取图片字节数组
        try {
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            data = null;
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (data != null) {
            //对字节数组Base64编码
            //BASE64Encoder encoder = new BASE64Encoder();
            //返回Base64编码过的字节数组字符串
            //str=encoder.encode(data);
            str = Base64Kt.encodeBase64ToString(data);
        }
        return str;
    }
}
