package com.github.saschawiegleb.ek.api;

import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

public class KeyTest {

	@Test
	public void test() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		String in = Key.decrypt();

		byte[] input = in.getBytes(StandardCharsets.ISO_8859_1);

		byte[] keyBytes = new String("keyBytes").getBytes();
		byte[] ivBytes = new String("ivBytess").getBytes();

		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		// create the cipher with the algorithm you choose
		// see javadoc for Cipher class for more info, e.g.
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] encrypted = new byte[cipher.getOutputSize(input.length)];
		int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
		enc_len += cipher.doFinal(encrypted, enc_len);

		System.out.println(new String(encrypted, StandardCharsets.ISO_8859_1).trim());

		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] decrypted = new byte[cipher.getOutputSize(enc_len)];
		int dec_len = cipher.update(encrypted, 0, enc_len, decrypted, 0);
		dec_len += cipher.doFinal(decrypted, dec_len);

		String out = new String(decrypted, StandardCharsets.ISO_8859_1).trim();
		assertTrue(in.equalsIgnoreCase(out));
	}

	@Test
	public void test2() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		String in = Key.decrypt();

		byte[] keyBytes = new String("keyBytes").getBytes();
		byte[] ivBytes = new String("ivBytess").getBytes();

		SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

		byte[] encrypted = "þ_\\ö©×ÂE¬WÜßÄüT_î~qC-Ò%¼}äaè>½".getBytes(StandardCharsets.ISO_8859_1);

		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] decrypted = new byte[cipher.getOutputSize(encrypted.length)];
		int dec_len = cipher.update(encrypted, 0, encrypted.length, decrypted, 0);
		dec_len += cipher.doFinal(decrypted, dec_len);

		String out = new String(decrypted, StandardCharsets.ISO_8859_1).trim();
		assertTrue(in.equalsIgnoreCase(out));
	}

	@Test
	public void test3() {
		System.out.println(Key.decrypt());
	}
}
