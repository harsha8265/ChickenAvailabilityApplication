package com.mobile.chickenavailabilityapplication.util;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


/**
 * This class is used to encrypt and decrypt data using AES128.
 * 
 * @author Girdhar
 */
public final class Crypto {
	
	private static byte[] iv = new byte[16];
	
	/**
	 * Creates Cipher object from the given key and cipher mode.
	 * 
	 * @param key - The key which is required 
	 * @param cipherMode - The cipher mode indicates the mode of cipher as encryption or decryption.
	 * @return - Returns the Cipher object.
	 * @throws NoSuchPaddingException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static Cipher createCipher(byte[] key, int cipherMode) throws NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		if (cipherMode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
		}
		else if (cipherMode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
		}			
	
		return cipher;
	}
	
	/**
	 * Used to encrypt raw data.
	 * 
	 * @param key - The encryption key 
	 * @param dataToEncrypt - Data to be encrypted
	 * @return	The encrypted data as byte array.
	 */
	public static byte[] encrypt(byte[] key, byte[] dataToEncrypt) {
		byte[] encrypted = null;
		
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			encrypted = cipher.doFinal(dataToEncrypt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
		return encrypted;
	}

	/**
	 * Used to decrypt raw data.
	 * 
	 * @param key	- The decryption key 
	 * @param dataToDecrypt - Data to be decrypted
	 * @return The decrypted data as byte array.
	 */
	public static byte[] decrypt(byte[] key, byte[] dataToDecrypt) {		
		byte[] decrypted = null;
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			decrypted = cipher.doFinal(dataToDecrypt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
		return decrypted;
	}
	
	/**
     * This method generates a 64bit pseudo-random key which can be used as salt.
     * 
     * @return A 64bit random salt.
     */
    public static byte[] generate64bitSalt() {
		int endRand = 256; // taking only the ASCII range in consideration.
		byte[] byteForm = new byte[8];
		SecureRandom random = new SecureRandom();
		byteForm[0] = (byte) ((char) random.nextInt(endRand));
		byteForm[1] = (byte) ((char) random.nextInt(endRand));
		byteForm[2] = (byte) ((char) random.nextInt(endRand));
		byteForm[3] = (byte) ((char) random.nextInt(endRand));
		byteForm[4] = (byte) ((char) random.nextInt(endRand));
		byteForm[5] = (byte) ((char) random.nextInt(endRand));
		byteForm[6] = (byte) ((char) random.nextInt(endRand));
		byteForm[7] = (byte) ((char) random.nextInt(endRand));
		return byteForm;
    }
    
    /**
     * Takes an integer as argument, separates out the digits 
     * and returns the byte array representation of the 
     * short type (2 bytes) of the integer (4 bytes).
     * 
     * @param i The integer value of which to get the byte form.
     * @return The byte[] value of the digits from the integer in short format.
     */
    public static final byte[] getBytesFromDigitsOfInt(int num) {
		String numStr = "" + num;
		char[] cs = numStr.toCharArray();
		int csLength = cs.length;
		byte[] bs = new byte[8];
		
		for (int i = 3, j = csLength - 1; j >= 0; j--, i--) {
			byte[] byteForm = makeByteFromShortInt((int) cs[j]);
			int ite = 0;
			bs[(i << 1) + ite] = byteForm[ite++];
			bs[(i << 1) + ite] = byteForm[ite];
		}
		return bs;
    }
    
    /**
     * Takes an integer as argument and returns the byte representation 
     * of the short form (2 bytes) of the integer (4 bytes).
     * 
     * @param i The integer value of which to get the byte form.
     * @return The byte[] value of the integer in short format.
     */
    private static final byte[] makeByteFromShortInt(int i) {
		// integer has 4 bytes so first two bytes makes the short type.
		return new byte[] { /* (byte)(i>>24), (byte)(i>>16), */(byte) (i >> 8),	(byte) i };
	}
    
    /**
     * Takes a 64 bit salt and another 64 bit byte array input
     * and returns a 128 bit master key in a byte array. 
     * 
     * @param salt A 64 bit (8 bytes) salt in a byte array.
     * @param input A 64 bit (8 bytes) input in a byte array. 
     * @return The 128 bit master key in a byte array.
     */
    public static byte[] generateMasterKey(byte[] salt, byte[] input) {
		byte[] mKey = new byte[salt.length * 2];
		boolean tweek = true;
		int mask1 = 0xaf;
		int mask2 = 0xfa;
		for (int i = 0; i < mKey.length; i++, tweek ^= true) {
			if (tweek) {
				mKey[i] = (byte) ((salt[i / 2] | input[i / 2]) ^ mask1);
			} else {
				mKey[i] = (byte) ((input[(i - 1) / 2] & salt[(i - 1) / 2]) ^ mask2);
			}
		}
		return mKey;
    }
	
	public static byte[] generateSHA1Hash(int pin, byte[] salt) throws NoSuchAlgorithmException {
		byte[] pinBytes = Crypto.getBytesFromDigitsOfInt(pin);
		byte[] pinWithSalt = new byte[pinBytes.length + salt.length];
		for (int i = 0; i < pinWithSalt.length; i++) {
			pinWithSalt[i] = i < pinBytes.length ? pinBytes[i] : salt[i - salt.length];
		}
		
		MessageDigest digester = MessageDigest.getInstance("SHA1");
		digester.reset();
		digester.update(pinWithSalt);
		return digester.digest();
	}
	
	public static String getEncodedEncryptedData(String plain, String secretKey) {
		byte[] encryptedBytes = Crypto.encrypt(secretKey.getBytes(), plain.getBytes());
		return new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
	}

	public static String getDecryptedDecodedData(String encryptedData, String secretKey) {
		byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
		return new String(Crypto.decrypt(secretKey.getBytes(), decodedBytes));
	}

	public static SSLSocketFactory newSSLSocketFactory() {
		KeyStore keyStore = null;
		TrustManagerFactory tmf;
		try {
			tmf = TrustManagerFactory.getInstance("X509");
			tmf.init(keyStore);
	
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);
			return context.getSocketFactory();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
