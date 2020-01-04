package com.daohang.trainapp.rsa;

public class EncodeUtil {
	public static byte[] toBE(long data) {
		byte[] buffer = new byte[4];
        buffer[0] = (byte)(data >>> 24);
        buffer[1] = (byte)(data >>> 16);
        buffer[2] = (byte)(data >>> 8);
        buffer[3] = (byte)(data >>> 0);
      
        return buffer;
	}
	
}
