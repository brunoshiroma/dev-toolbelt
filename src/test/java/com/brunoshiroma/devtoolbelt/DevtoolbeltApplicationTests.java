package com.brunoshiroma.devtoolbelt;

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

import java.util.Base64;
import java.util.Map;

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

	@Test
	void testQrCodeCryptoRoundTrip() throws Exception {
		// The payload is already encrypted by the browser; the server only handles QR image generation and reading.
		final var encryptedPayload = "dtbqr1.AAAAAAAAAAAAAAAA.AAAAAAAAAAAAAAAAAAAAAA";

		final var generateHeaders = new HttpHeaders();
		generateHeaders.setContentType(MediaType.APPLICATION_JSON);

		final var generateResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/qrcode-crypto/generate",
				new HttpEntity<>(Map.of("encryptedPayload", encryptedPayload), generateHeaders),
				String.class
		);

		Assertions.assertThat(generateResponse.getStatusCode().is2xxSuccessful()).isTrue();
		final var generateBody = JsonParserFactory.getJsonParser().parseMap(generateResponse.getBody());
		final var qrCodeDataUrl = String.valueOf(generateBody.get("qrCodeDataUrl"));

		Assertions.assertThat(qrCodeDataUrl).startsWith("data:image/png;base64,");

		final var qrCodeBytes = Base64.getDecoder().decode(qrCodeDataUrl.substring(qrCodeDataUrl.indexOf(',') + 1));
		final var fileBody = new LinkedMultiValueMap<String, Object>();
		fileBody.add("file", new ByteArrayResource(qrCodeBytes) {
			@Override
			public String getFilename() {
				return "qrcode.png";
			}
		});

		final var readHeaders = new HttpHeaders();
		readHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		final var readResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/qrcode-crypto/read",
				new HttpEntity<>(fileBody, readHeaders),
				String.class
		);

		Assertions.assertThat(readResponse.getStatusCode().is2xxSuccessful()).isTrue();
		final var readBody = JsonParserFactory.getJsonParser().parseMap(readResponse.getBody());

		Assertions.assertThat(String.valueOf(readBody.get("encryptedPayload"))).isEqualTo(encryptedPayload);
	}


}
