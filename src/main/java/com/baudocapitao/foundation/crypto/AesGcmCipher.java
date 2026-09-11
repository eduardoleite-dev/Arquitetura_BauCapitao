package com.baudocapitao.foundation.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AesGcmCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec key;
    private final SecureRandom secureRandom;

    public AesGcmCipher(byte[] keyBytes) {
        if (keyBytes == null || !(keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32)) {
            throw new IllegalArgumentException("A chave AES deve ter 16, 24 ou 32 bytes");
        }
        this.key = new SecretKeySpec(keyBytes.clone(), "AES");
        this.secureRandom = new SecureRandom();
    }

    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criptografar o valor", exception);
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null) {
            return null;
        }
        try {
            byte[] input = Base64.getDecoder().decode(encryptedText);
            if (input.length <= IV_LENGTH) {
                throw new IllegalArgumentException("Ciphertext inválido");
            }
            byte[] iv = Arrays.copyOfRange(input, 0, IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(input, IV_LENGTH, input.length);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível descriptografar o valor", exception);
        }
    }
}
