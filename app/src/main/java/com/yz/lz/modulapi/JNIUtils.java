package com.yz.lz.modulapi;

/**
 * JNI 接口
 * 添加libfp-lib.so|libgyro-lib.so|librfid-lib.so|libgpiocon-lib.so
 * 然后将包全部复制到项目中
 *
 * <br/>
 * rfid:
 * 跨扇区读写,要保证扇区的密码相同
 *
 * @author lz
 * @date 2018/05/03
 */

public class JNIUtils implements RfidClient{
//    public int fdcontext = 0;
    static {
        System.loadLibrary("rfid-lib");
    }


    private static JNIUtils instance = null;

    /**
     * 获取JINUtils 对象
     *
     * @return JINUtils 对象
     */
    public static JNIUtils getInstance() {
        if (instance == null) {
            instance = new JNIUtils();
        }
        return instance;
    }


    //指纹

    /**
     * 指纹 - 打开指纹模块
     *
     * @return true成功 false失败
     */
    @Override
    public native boolean openFingerDevice();

    /**
     * 指纹 - 验证密码
     *
     * @param pwd 初始密码："\0\0\0\0\0" 长度为5
     * @return true成功 false失败
     */
    @Override
    public native boolean checkPWD(String pwd);

    /**
     * 指纹 - 录入指纹
     *
     * @return 1表示成功 0表示失败
     */
    @Override
    public native int entryFingerprint();

    /**
     * 指纹 - 再次录入指纹
     *
     * @return 1表示成功 0表示失败
     */
    @Override
    public native int entryAgainFingerprint();

    /**
     * 指纹 - 对比指纹
     *
     * @return
     */
    @Override
    public native boolean matchFingerprint();

    /**
     * 指纹 - 获取指纹图片
     * @param fPath 指纹图片保存的绝对路径
     * @return 指纹图片是否保存成功
     */
    @Override
    public native boolean getFingerImage(String fPath);

    /**
     * 指纹 - 获取特征值
     *
     * @return 字节数组 或 null
     */
    @Override
    public native byte[] downChar();

    /**
     * 指纹 - 上传特征值
     *
     * @param bytes 要上传的特征值
     * @return true 成功 false 失败
     */
    @Override
    public native boolean upChar(byte[] bytes);


    /**
     * 指纹 - 两个指纹特征码生成模板
     *
     * @return
     */
    @Override
    public native byte[] genFingerTemp();

    /**
     * 指纹 - 重置
     * 重置之后，才能进行下一次的录入指纹->再次录入指纹->对比指纹等等
     *
     * @return null
     */
    @Override
    public native void reset();

    /**
     * 指纹 - 关闭指纹模块
     */
    @Override
    public native void closeFingerDevice();


    //rfid

    /**
     * 射频卡-打开rfid设备
     *
     * @return true 成功 false 失败
     */
    @Override
    public native boolean openRfidDevice();

    /**
     * 射频卡-是否检测到Card
     *
     * @return true 成功 false失败
     */
    @Override
    public native boolean checkCard();

    /**
     * 射频卡-读取卡的类型
     *
     * @return 字节 1:UltraLight卡 2:1k卡 3:4k卡 4：B卡或身份证
     */
    @Override
    public native byte readCardType();

    /**
     * 射频卡-读取卡的id
     *
     * @return uid
     */
    @Override
    public native byte[] readIdFromCard();

    /**
     * 射频卡-指定数据区地址读取数据
     *
     * @param addr 数据区编号
     * @param keyType 密码类型 0:A密码 1:B密码
     * @param key  指定数据区的密码
     * @param len  要读的数据长度
     *
     * @return 字节数组
     */
    @Override
    public native byte[] readDataByAddrNum(short addr, byte keyType, byte[] key, short len);


    /**
     * 射频卡-指定数据区地址写数据
     *
     * @param addr  数据区编号
     * @param keyType 密码类型 0:A密码 1:B密码
     * @param key   指定数据区的密码
     * @param bytes 要写的数据长度

     * @return true 成功 false 失败
     */
    @Override
    public native boolean writeDataByAddrNum(short addr, byte keyType, byte[] key, byte[] bytes);

    /**
     * 射频卡-修改密码
     *
     * @param addr 密码区编号
     * @param keyType 密码类型 0:A密码 1:B密码
     * @param originalKey  原始密码 6个字节 0-f
     * @param newKey 新密码 6个字节 0-f
     * @return
     */
    @Override
    public native boolean modifyPwd(short addr, byte keyType, byte[] originalKey, byte[] newKey);

    /**
     * 射频卡-修改控制位
     *
     * @param addr 密码区编号
     * @param keyType 密码类型 0:A密码 1:B密码
     * @param key 密码 6个字节 0-f
     * @param bytes a密码\控制位\b密码 16个字节
     * @return
     */
    @Override
    public native boolean modifyControl(short addr,byte keyType, byte[] key, byte[] bytes);

    /**
     * 射频卡-关闭rfid设备
     */
    @Override
    public native void closeRfidDevice();

    /**
     * GPIO- 设置gpio
     * @param pin  传入值5或6
     * @param type 0或1  1:拉高 0:拉低
     * @return false or true
     */
    @Override
    public native boolean setGPIO(int pin, int type);

    /**GPIO - 获取gpio信号
     *
     * @param pin 传入值 1\2\3\4
     * @return 0或1 0：低 1:高
     */
    @Override
    public native int getGPIO(int pin);

    @Override
    public native String stringFromJNI();
}
