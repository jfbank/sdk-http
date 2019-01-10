package com.jfai.afs.http;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 * Class for encrypting/decrypting the parameters
 * 
 */
public class EncryptionUtil {
	
	/**
	 * Logger instantiation
	 */
	//private static final Logger LOG = Logger.getLogger(EncryptionUtil.class);
	
	/**
	 * A specification of the key material that constitutes a cryptographic key.
	 */
	private static KeySpec keyspecification;
	/**
	 * Instance of SecretKeyFactory
	 */
	private static SecretKeyFactory keyfactory;
	/**
	 * Instance of Cipher is created
	 */
	private static Cipher encrypt;
	/**
	 * Variable for encoded string of sequence of bytes using the named charset
	 */
	private static byte[] arrayBytes;
	/**
	 * Instance of SecretKey is created
	 */
	private static SecretKey key;

	/**
	 * instance of deciphper cipher is created
	 */
	private static Cipher decrypt;
	/**
	 * Constants for encryption/decryption
	 * it should be 24 bit by default
	 */
	private static final String ENCRYPTION_KEY = "ABCDEDFJDKAJKFDJLFA";//自己定
	/**
	 * Unicode format
	 */
	private static final String UNICODE = "UTF8";
	/**
	 * DESede Encryption
	 */
	public static final String ENCRYPTION_SCHEME = "DESede";

	/**
	 * Static method to form the specifications for encrypting/decrypting the
	 * data
	 */
	static {
		try {
			arrayBytes = ENCRYPTION_KEY.getBytes(UNICODE);
			keyspecification = new DESedeKeySpec(arrayBytes);
			keyfactory = SecretKeyFactory.getInstance(ENCRYPTION_SCHEME);
			encrypt = Cipher.getInstance(ENCRYPTION_SCHEME);
			decrypt = Cipher.getInstance(ENCRYPTION_SCHEME);
			key = keyfactory.generateSecret(keyspecification);
			encrypt.init(Cipher.ENCRYPT_MODE, key);
			decrypt.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor to prevent instantiation
	 */
	protected EncryptionUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Method to mask the input string
	 * 
	 * @param unencryptedString
	 * @return
	 */
	public static String mask(String unencryptedString) {
		String encryptedString = "";
		try {
			byte[] plainText = unencryptedString.getBytes(UNICODE);
			byte[] encryptedText;
			encryptedText = encrypt.doFinal(plainText);
			//System.out.println("encryptedText" + encryptedText);
			encryptedString = new String(Base64.encodeBase64(encryptedText));
			//System.out.println("encryptedString" + encryptedString);
			Base64 encoder = new Base64();
			byte[] encodedBytes = encoder.encode(encryptedString.getBytes());
			encryptedString = new String(encodedBytes);
			//System.out.println("encryptedString" + encryptedString);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return encryptedString;
	}
	
	
	
	

	/**
	 * Method to unmask the encrypted string
	 * 
	 * @param encryptedString
	 * @return
	 */
	public static String unmask(String encryptedString) {
		String decryptedText = null;
		try {
			Base64 decoder = new Base64();
//			LOG.info("开始进行解密:"+encryptedString);
			byte[] decodedBytes = decoder.decode(encryptedString.getBytes());
			encryptedString = new String(decodedBytes,"UTF-8");
//			LOG.info("初步解密:"+encryptedString);
			byte[] encryptedText = Base64.decodeBase64(encryptedString
					.getBytes());
			byte[] plainText = decrypt.doFinal(encryptedText);
			decryptedText = new String(plainText,"UTF-8");
//			LOG.info("解密结果:"+decryptedText);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedText;
	}

	/**
	 * @return the keySpec
	 */
	public static KeySpec getKeySpec() {
		return keyspecification;
	}

	/**
	 * @param keySpec
	 *            the keySpec to set
	 */
	public static void setKeySpec(KeySpec keySpec) {
		EncryptionUtil.keyspecification = keySpec;
	}

	/**
	 * @return the secretKeyFactory
	 */
	public static SecretKeyFactory getSecretKeyFactory() {
		return keyfactory;
	}

	/**
	 * @param secretKeyFactory
	 *            the secretKeyFactory to set
	 */
	public static void setSecretKeyFactory(SecretKeyFactory secretKeyFactory) {
		EncryptionUtil.keyfactory = secretKeyFactory;
	}

	/**
	 * @return the arrayBytes
	 */
	public static byte[] getArrayBytes() {
		return arrayBytes;
	}

	/**
	 * @param arrayBytes
	 *            the arrayBytes to set
	 */
	public static void setArrayBytes(byte[] arrayBytesObj) {
		if (arrayBytesObj == null) {
			EncryptionUtil.arrayBytes = new byte[0];
		} else {
			EncryptionUtil.arrayBytes = new byte[arrayBytesObj.length];
			System.arraycopy(arrayBytesObj, 0, EncryptionUtil.arrayBytes, 0,
					arrayBytesObj.length);
		}
	}

	/**
	 * @return the key
	 */
	public static SecretKey getKey() {
		return key;
	}

	/**
	 * @return the decrpytCipher
	 */
	public static Cipher getDecrpytCipher() {
		return decrypt;
	}

	/**
	 * @param decrpytCipher
	 *            the decrpytCipher to set
	 */
	public static void setDecrpytCipher(Cipher decrpytCipher) {
		EncryptionUtil.decrypt = decrpytCipher;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public static void setKey(SecretKey key) {
		EncryptionUtil.key = key;
	}

	/**
	 * @return the encryptCipher
	 */
	public static Cipher getEncryptCipher() {
		return encrypt;
	}

	/**
	 * @param encryptCipher
	 *            the encryptCipher to set
	 */
	public static void setEncryptCipher(Cipher encryptCipher) {
		EncryptionUtil.encrypt = encryptCipher;
	}

}
