package com.brunoshiroma.devtoolbelt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ObjectMapper objectMapper;

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
		final var sourceData = "secret data transfer";
		final var salt = "shared-salt";
		final var otp = "654321";

		final var generateHeaders = new HttpHeaders();
		generateHeaders.setContentType(MediaType.APPLICATION_JSON);

		final var generateResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/qrcode-crypto/generate",
				new HttpEntity<>(Map.of("data", sourceData, "salt", salt, "otp", otp), generateHeaders),
				String.class
		);

		Assertions.assertThat(generateResponse.getStatusCode().is2xxSuccessful()).isTrue();
		final var generateBody = objectMapper.readTree(generateResponse.getBody());
		final var encryptedPayload = generateBody.path("encryptedPayload").asText();
		final var qrCodeDataUrl = generateBody.path("qrCodeDataUrl").asText();

		Assertions.assertThat(encryptedPayload).startsWith("dtbqr1.");
		Assertions.assertThat(qrCodeDataUrl).startsWith("data:image/png;base64,");

		final var qrCodeBytes = Base64.getDecoder().decode(qrCodeDataUrl.substring(qrCodeDataUrl.indexOf(',') + 1));
		final var fileBody = new LinkedMultiValueMap<String, Object>();
		fileBody.add("file", new ByteArrayResource(qrCodeBytes) {
			@Override
			public String getFilename() {
				return "qrcode.png";
			}
		});
		fileBody.add("salt", salt);
		fileBody.add("otp", otp);

		final var readHeaders = new HttpHeaders();
		readHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		final var readResponse = restTemplate.postForEntity(
				"http://localhost:" + port + "/api/qrcode-crypto/read",
				new HttpEntity<>(fileBody, readHeaders),
				String.class
		);

		Assertions.assertThat(readResponse.getStatusCode().is2xxSuccessful()).isTrue();
		final var readBody = objectMapper.readTree(readResponse.getBody());

		Assertions.assertThat(readBody.path("encryptedPayload").asText()).isEqualTo(encryptedPayload);
		Assertions.assertThat(readBody.path("data").asText()).isEqualTo(sourceData);
	}


}
