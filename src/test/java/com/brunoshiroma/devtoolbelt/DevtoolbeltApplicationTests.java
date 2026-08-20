package com.brunoshiroma.devtoolbelt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.ByteArrayOutputStream;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DevtoolbeltApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testIndex() {
		var result = restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
		Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
		Assertions.assertThat(result.getBody()).contains("Password", "SHA", "QR Code Crypto");
	}

	@Test
	void testQrCodeCryptoPage() {
		var result = restTemplate.getForEntity("http://localhost:" + port + "/qrcode-crypto", String.class);
		Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
		Assertions.assertThat(result.getBody()).contains("QR code crypto data transfer", "Generate QR code", "Read QR code");
	}

}
