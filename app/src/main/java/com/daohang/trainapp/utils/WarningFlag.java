package com.daohang.trainapp.utils;

import com.daohang.trainapp.services.AMapLocationServicesKt;
import com.daohang.trainapp.services.ClientMessagesKt;
import com.daohang.trainapp.services.StaticVariablesKt;

import java.util.HashMap;
import java.util.Map;

public class WarningFlag {
    //紧急报警
    public static final int WARNING_URGENT = 0;
    //超速报警
    public static final int WARNING_OVER_SPEED = 1;
    //疲劳驾驶
    //预警
    //GNNS模块发生故障(未定位)
    public static final int WARNING_NOT_LOCATED = 4;
    //GNNS天线未连接或被剪断
    public static final int WARNING_ANTENNA_NOT_CONNECTED = 5;
    //GNNS天线短路
    //终端主电源欠压
    //终端主电源掉电
    //终端LCD或显示器故障
    //TTS模块故障
    //摄像头故障
    //当天累计驾驶超时
    //超时停车
    //进出区域
    public static final int WARNING_OUT_RANGE = 20;
    //进出路线
    //路段行驶时间不足/过长
    //路线偏离报警
    //车辆VSS故障
    //车辆油量异常
    //车辆被盗
    //车辆非法点火
    //车辆非法位移
    //OBD断开报警
    public static final int WARNING_OBD_NOT_CONNECTED = 29;

    private static Map<Byte, Character> outOfRangeMap = new HashMap<>();
    private static char[] flagArray = new char[32];

    public static void initFlagArray() {
        for (int i = 0; i < flagArray.length; i++)
            flagArray[i] = '0';
        outOfRangeMap.put((byte) 2, '0');
        outOfRangeMap.put((byte) 3, '0');
    }

    /**
     * 设置报警标识
     *
     * @param position 标识位
     */
    public static void setWarning(int position) {
        if (position < flagArray.length && position > -1)
            flagArray[position] = '1';
    }

    /**
     * 解除报警标识
     *
     * @param position 标识位
     */
    public static void clearWarning(int position) {
        if (position < flagArray.length && position > -1)
            flagArray[position] = '0';
    }

    /**
     * 非培训状态下的报警标识
     *
     * @return
     */
    public static byte[] getNoWarningFlag() {
        return new byte[]{0, 0, 0, 0};
    }

    /**
     * 设置超出围栏报警
     *
     * @param id 科目id
     * @param ch 0--围栏内，1--围栏外
     */
    public static void setOutOfRangeWarning(byte id, char ch) {
        outOfRangeMap.put(id, ch);
    }

    /**
     * 清除围栏标识
     */
    public static void clearOutOfRangeWarning(){
        outOfRangeMap.put((byte) 2, '0');
        outOfRangeMap.put((byte) 3, '0');
    }

    /**
     * 获取报警标识
     *
     * @return
     */
    public static byte[] getWarningFlag() {
        if (outOfRangeMap.containsKey(StaticVariablesKt.getCurrentClassItem()))
            flagArray[WARNING_OUT_RANGE] = outOfRangeMap.get(StaticVariablesKt.getCurrentClassItem());
        else
            flagArray[WARNING_OUT_RANGE] = '0';

        //超出围栏
        AMapLocationServicesKt.setOutOfGeoFence(flagArray[WARNING_OUT_RANGE] == (char)1);

        return intToByteArray(Integer.parseInt(String.valueOf(reverseFlagArray()), 2));
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    private static char[] reverseFlagArray() {
        char[] newArray = new char[flagArray.length];
        for (int i = 0; i < flagArray.length; i++) {
            newArray[i] = flagArray[flagArray.length - 1 - i];
        }
        return newArray;
    }
}
