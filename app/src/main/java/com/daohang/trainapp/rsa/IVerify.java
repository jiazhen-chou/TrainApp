package com.daohang.trainapp.rsa;

import java.security.cert.X509Certificate;

public interface IVerify {
	public boolean verify(String data, long timestamp, String encodedEncryptedStr,
                          X509Certificate userCert) throws Exception;
	public boolean verify(String data, String encodedEncryptedStr,
                          X509Certificate userCert) throws Exception;
	public boolean verify(byte[] data, String encodedEncryptedStr,
                          X509Certificate userCert) throws Exception;
	public boolean verify(byte[] data, long timestamp, String encodedEncryptedStr,
                          X509Certificate userCert) throws Exception;
}
