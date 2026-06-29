package com.brunoshiroma.devtoolbelt.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class QrCodeCryptoService {

    private static final String PAYLOAD_PREFIX = "dtbqr1";
    private static final int PBKDF2_ITERATIONS = 65_536;
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;
    private static final int QR_CODE_SIZE = 512;

    private final SecureRandom secureRandom = new SecureRandom();

    public String encrypt(String data, String salt, String otp) {
        try {
            final var iv = new byte[GCM_IV_BYTES];
            secureRandom.nextBytes(iv);

            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, deriveKey(salt, otp), new GCMParameterSpec(GCM_TAG_BITS, iv));

            final var encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return PAYLOAD_PREFIX + "." + base64Url(iv) + "." + base64Url(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Unable to encrypt data", e);
        }
    }

    public String decrypt(String payload, String salt, String otp) {
        final var parts = payload.split("\\.");
        if (parts.length != 3 || !PAYLOAD_PREFIX.equals(parts[0])) {
            throw new IllegalArgumentException("Invalid QR code payload");
        }

        try {
            final var iv = Base64.getUrlDecoder().decode(parts[1]);
            final var encrypted = Base64.getUrlDecoder().decode(parts[2]);

            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, deriveKey(salt, otp), new GCMParameterSpec(GCM_TAG_BITS, iv));

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to decrypt QR code with the provided salt and OTP", e);
        }
    }

    public byte[] generateQrCode(String payload) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    payload,
                    BarcodeFormat.QR_CODE,
                    QR_CODE_SIZE,
                    QR_CODE_SIZE
            );

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalArgumentException("Unable to generate QR code", e);
        }
    }

    public String readQrCode(MultipartFile file) {
        try {
            final var image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) {
                throw new IllegalArgumentException("Unable to read the uploaded image");
            }

            return decodeQrCode(image);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read the uploaded QR code", e);
        }
    }

    private String decodeQrCode(BufferedImage image) {
        try {
            final var bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            final var hints = Map.of(
                    DecodeHintType.TRY_HARDER, Boolean.TRUE,
                    DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE)
            );

            return new MultiFormatReader().decode(bitmap, hints).getText();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to find a readable QR code in the uploaded image", e);
        }
    }

    private SecretKeySpec deriveKey(String salt, String otp) throws GeneralSecurityException {
        final var keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        final var keySpec = new PBEKeySpec(otp.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), PBKDF2_ITERATIONS, AES_KEY_SIZE);

        return new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
    }

    private String base64Url(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
