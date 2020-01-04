package com.daohang.trainapp.rsa;

public class HexBin{
	public static byte[] decode(String str)
	{
		if(str==null)return null;
		byte[]bt = new byte[str.length()/2];
		int sH;
		for(int i = 0; i < bt.length; i++){
		  sH = Integer.parseInt(str.substring(2*i,2*i+2),16);
		  bt[i] =  (byte)sH;
		}
		return bt;
	}
	
	public static String encode(byte[] hex)
	{
		 if(hex==null)return null;
  		StringBuffer sb = new StringBuffer(hex.length);
  		int sH, sL;
        for(int i = 0; i < hex.length; i++){
      	  sH=((hex[i]>> 4)&0x0f);//��λ�󣬱��10��ʼ
      	  sL=(hex[i]&0x0f);
      	  if(sH>=10){//�ж������С��0�ľ���0-9���������A-F
      	  sb.append((char)(sH+55));
      	  }else sb.append(sH);
      	  if(sL>=10){//�ж������С��0�ľ���0-9���������A-F
      	    sb.append((char)(sL+55));
      	  }else sb.append(sL);
        }
  	   return sb.toString().toUpperCase();
	}
}