package com.github.saschawiegleb.ek.api;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Key {
    private static final String _key = "þ_\\ö©×ÂE¬WÜßÄüT_î~qC-Ò%¼}äaè>½";

    public static String decrypt() {
        try {
            byte[] encrypted = _key.getBytes(StandardCharsets.ISO_8859_1);
            byte[] keyBytes = new String("keyBytes").getBytes();
            byte[] ivBytes = new String("ivBytess").getBytes();

            SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] decrypted = new byte[cipher.getOutputSize(encrypted.length)];
            int dec_len = cipher.update(encrypted, 0, encrypted.length, decrypted, 0);
            dec_len += cipher.doFinal(decrypted, dec_len);

            return new String(decrypted, StandardCharsets.ISO_8859_1).trim();
        } catch (Exception e) {
            return "";
        }
    }
}
