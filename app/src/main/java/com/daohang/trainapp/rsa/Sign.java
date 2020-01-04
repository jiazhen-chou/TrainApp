package com.daohang.trainapp.rsa;

//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.PrivateKey;

import javax.crypto.Cipher;

//import org.springframework.stereotype.Component;

//import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;


public class Sign implements ISign {

	String RSA = "RSA/ECB/PKCS1Padding"; //"RSA";//"
	public String sign(String data, long timestamp, PrivateKey key) throws Exception {
		return sign(data.getBytes("utf-8"), timestamp, key);
	}
	
	public String sign(String data, PrivateKey key) throws Exception {
		return sign(data.getBytes("utf-8"), 0, key);
	}
	
	public String sign(byte [] data, PrivateKey key) throws Exception {
		return sign(data, 0, key);
	}
	
	public String sign(byte [] data, long timestamp, PrivateKey key) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		if(timestamp > 0){
			md.update(EncodeUtil.toBE(timestamp));
		}
		
		byte[] hash = md.digest();
//		Cipher cipher = Cipher.getInstance(RSA, new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(RSA, new BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(hash);
		return HexBin.encode(encrypted);
	}

}
