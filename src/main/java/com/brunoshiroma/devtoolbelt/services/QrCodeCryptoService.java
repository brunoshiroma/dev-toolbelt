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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class QrCodeCryptoService {

    private static final int QR_CODE_SIZE = 512;

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
}
