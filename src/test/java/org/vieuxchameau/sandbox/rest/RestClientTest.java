package org.vieuxchameau.sandbox.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.vieuxchameau.sandbox.github.GithubUser;
import org.vieuxchameau.sandbox.security.JWTTokenGenerator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.concurrent.CompletableFuture.allOf;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test class to demo new jdk http client and
 * Java 13 text block (need to amend the pom for using them)
 */
public class RestClientTest {
    private static final String SANDBOX_ENDPOINT = "http://localhost:8080";
    private final ObjectMapper objectMapper = new JsonMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .version(HttpClient.Version.HTTP_2)
            .build();

    private CompletableFuture<Void> shouldGetGithubUser() {
        final var username = "VieuxChameau";
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/github/users/" + username))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenApply(response -> asJson(response.body(), GithubUser.class))
                .thenAccept(githubUser -> {
                    assertThat(githubUser.getLocation()).isEqualTo("Vancouver, Canada");
                    assertThat(githubUser.getLogin()).isEqualTo("VieuxChameau");
                });
    }

    private CompletableFuture<Void> shouldSayHelloWorld() {
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/hello"))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenAccept(response -> assertThat(response.body()).isEqualTo("Hello World!"));
    }

    private CompletableFuture<Void> shouldSayHelloVieuxChameau() {
        final var username = "VieuxChameau";
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/hello/" + username))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenApply(response -> asJson(response.body(), GreetingResponse.class))
                .thenAccept(response -> assertThat(response.getText()).isEqualTo("Hello VieuxChameau!"))
                ;
    }

    private CompletableFuture<Void> shouldSayHelloVieuxChameauInItalian() {
        // to test java 13 text block
        /*""
                        {
                            "name" : "VieuxChameau",
                            "language" : "it"
                        }
                        """*/
        final var body = "";
        final var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(SANDBOX_ENDPOINT + "/hellobabel"))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenApply(response -> asJson(response.body(), GreetingResponse.class))
                .thenAccept(response -> assertThat(response.getText()).isEqualTo("Ciao VieuxChameau!"));
    }

    private CompletableFuture<Void> shouldGet403WithoutToken() {
        final var username = "VieuxChameau";
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/hellosecured/" + username))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenAccept(response -> {
                    assertThat(response.statusCode()).isEqualTo(401);
                    assertThat(response.body()).contains("unauthorized");
                });
    }

    private CompletableFuture<Void> shouldSecurelySayHelloVieuxChameau() {
        final var username = "VieuxChameau";
        final var jwtTokenGenerator = new JWTTokenGenerator();
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/hellosecured/" + username))
                .header("Authorization", "Bearer " + jwtTokenGenerator.getJwtToken())
                .build();
        return httpClient.sendAsync(request, ofString())
                .thenAccept(response -> assertThat(response.body()).isEqualTo("Hello VieuxChameau"));
    }

    private CompletableFuture<Void> shouldGetDefaultExchangeRates() {
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/exchangesrates"))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenAccept(response -> assertThat(response.body()).contains("\"success\":true"));
    }

    private CompletableFuture<Void> shouldGetDefaultExchangeRatesFromEUR() {
        final var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SANDBOX_ENDPOINT + "/exchangesrates/EUR?currencies=USD"))
                .build();

        return httpClient.sendAsync(request, ofString())
                .thenAccept(response -> assertThat(response.body()).contains("Your current Subscription Plan does not support Source Currency Switching"));
    }

    public static void main(String[] args) {
        final var restClientTest = new RestClientTest();
        List<CompletableFuture<Void>> cfs = new ArrayList<>();
        cfs.add(restClientTest.shouldGetGithubUser());

        cfs.add(restClientTest.shouldSayHelloWorld());
        cfs.add(restClientTest.shouldSayHelloVieuxChameau());
//        cfs.add(restClientTest.shouldSayHelloVieuxChameauInItalian());

        cfs.add(restClientTest.shouldGet403WithoutToken());
        cfs.add(restClientTest.shouldSecurelySayHelloVieuxChameau());

        cfs.add(restClientTest.shouldGetDefaultExchangeRates());
        cfs.add(restClientTest.shouldGetDefaultExchangeRatesFromEUR());

        allOf(cfs.toArray(new CompletableFuture[0]))
                .join();
    }

    private <R> R asJson(final String json, Class<R> returnType) {
        try {
            return objectMapper.readValue(json, returnType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class GreetingResponse {
        private String text;

        public GreetingResponse() {
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
