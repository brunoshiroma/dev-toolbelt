package com.brunoshiroma.devtoolbelt.controllers;

import com.brunoshiroma.devtoolbelt.services.QrCodeCryptoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
public class QrCodeCryptoController {

    private final QrCodeCryptoService qrCodeCryptoService;

    public QrCodeCryptoController(QrCodeCryptoService qrCodeCryptoService) {
        this.qrCodeCryptoService = qrCodeCryptoService;
    }

    @PostMapping(value = "/api/qrcode-crypto/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenerateQrCodeResponse> generate(@RequestBody GenerateQrCodeRequest request) {
        final var data = requireText(request.data(), "Data");
        final var salt = requireText(request.salt(), "Salt");
        final var otp = requireText(request.otp(), "OTP");

        try {
            final var payload = qrCodeCryptoService.encrypt(data, salt, otp);
            final var qrCodeImage = Base64.getEncoder().encodeToString(qrCodeCryptoService.generateQrCode(payload));

            return ResponseEntity.ok(new GenerateQrCodeResponse(payload, "data:image/png;base64," + qrCodeImage));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/api/qrcode-crypto/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ReadQrCodeResponse> read(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("salt") String salt,
                                                   @RequestParam("otp") String otp) {
        requireFile(file);

        try {
            final var payload = qrCodeCryptoService.readQrCode(file);
            final var data = qrCodeCryptoService.decrypt(payload, requireText(salt, "Salt"), requireText(otp, "OTP"));

            return ResponseEntity.ok(new ReadQrCodeResponse(payload, data));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage(), e);
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, name + " is required");
        }

        return value.trim();
    }

    private static void requireFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "QR code image is required");
        }
    }

    public record GenerateQrCodeRequest(String data, String salt, String otp) {
    }

    public record GenerateQrCodeResponse(String encryptedPayload, String qrCodeDataUrl) {
    }

    public record ReadQrCodeResponse(String encryptedPayload, String data) {
    }
}
