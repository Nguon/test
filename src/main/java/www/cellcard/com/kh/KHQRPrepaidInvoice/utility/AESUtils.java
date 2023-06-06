package  www.cellcard.com.kh.KHQRPrepaidInvoice.utility;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

	private static final String AES_SPEC 					= "AES/CBC/PKCS5Padding";
	private static final String AES_KEY_SPEC 				= "AES";
	private static final String AES_KEY_GENERATOR_SPEC 		= "SHA-256";
	private static final String AES_IV_GENERATOR_SPEC 		= "MD5";
	
	
	/**
	 * Generate 32 byte key
	 * 
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static Key generateKey(final String salt) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance(AES_KEY_GENERATOR_SPEC);
		return new SecretKeySpec(md.digest(salt.getBytes()), AES_KEY_SPEC);
	}
	
	
	/**
	 * Generate 16 byte IV
	 * 
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static IvParameterSpec generateIV(final String salt) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance(AES_IV_GENERATOR_SPEC);
		return new IvParameterSpec(md.digest(salt.getBytes()));
	}
	

	public static String encrypt(final String plainText, final String salt) throws Exception {

		try {

			final Key key = generateKey(salt);
			final IvParameterSpec iv = generateIV(salt);

			final Cipher cipher = Cipher.getInstance(AES_SPEC);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);

			final byte[] encodeTextBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			
			return Base64.getEncoder().encodeToString(encodeTextBytes);

		} catch (Exception e) {
			throw e;
		}
	}

	public static String decrypt(final String cipherText, final String salt) throws Exception {

		try {
			final Key key = generateKey(salt);
			final IvParameterSpec iv = generateIV(salt);

			final Cipher cipher = Cipher.getInstance(AES_SPEC);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			
			final byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);
			final byte[] decodeTextBytes = cipher.doFinal(cipherTextBytes);

			return new String(decodeTextBytes, StandardCharsets.UTF_8);

		} catch (Exception e) {
			throw e;
		}
	}

}
