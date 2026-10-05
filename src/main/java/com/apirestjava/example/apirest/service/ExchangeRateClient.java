package com.apirestjava.example.apirest.service;

import com.apirestjava.example.apirest.exception.ExternalServiceUnavailableException;
import com.apirestjava.example.apirest.exception.RateNotAvailableException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Component
public class ExchangeRateClient {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateClient.class);

    // Réponse de l'API : {"amount":1.0,"base":"EUR","date":"...","rates":{"USD":1.08}}
    @JsonIgnoreProperties(ignoreUnknown = true)
    record RatesResponse(Map<String, BigDecimal> rates) {
    }

    private final WebClient webClient;
    private final long timeoutMs;
    private final int maxAttempts;
    private final long backoffMs;

    public ExchangeRateClient(@Value("${exchange.api.base-url}") String baseUrl,
                              @Value("${exchange.api.timeout-ms:3000}") long timeoutMs,
                              @Value("${exchange.api.max-attempts:3}") int maxAttempts,
                              @Value("${exchange.api.backoff-ms:500}") long backoffMs) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.timeoutMs = timeoutMs;
        this.maxAttempts = maxAttempts;
        this.backoffMs = backoffMs;
    }

    @Cacheable(value = "exchangeRates", key = "#root.args[0] + '-' + #root.args[1]")
    public BigDecimal getRate(String from, String to) {
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Appel API externe {} -> {} (tentative {}/{})", from, to, attempt, maxAttempts);
                return fetchRate(from, to);

            } catch (RateNotAvailableException e) {
                throw e; // inutile de réessayer : la paire n'est pas supportée

            } catch (WebClientResponseException e) {
                int status = e.getStatusCode().value();
                boolean retryable = status >= 500 || status == 429 || status == 408;
                if (!retryable) {
                    throw new RateNotAvailableException(
                            "L'API externe ne fournit pas de taux pour " + from + " -> " + to);
                }
                lastError = e;

            } catch (RuntimeException e) {
                // timeout, connexion refusée, DNS...
                lastError = e;
            }

            if (attempt < maxAttempts) {
                sleep(backoffMs * (1L << (attempt - 1))); // 500 ms, 1000 ms, 2000 ms...
            }
        }

        throw new ExternalServiceUnavailableException(
                "Service de taux de change indisponible après " + maxAttempts + " tentatives. Réessayez plus tard.",
                lastError);
    }

    private BigDecimal fetchRate(String from, String to) {
        RatesResponse response = webClient.get()
                .uri(uri -> uri.path("/latest")
                        .queryParam("base", from)
                        .queryParam("symbols", to)
                        .build())
                .retrieve()
                .bodyToMono(RatesResponse.class)
                .timeout(Duration.ofMillis(timeoutMs))
                .block();

        if (response == null || response.rates() == null || response.rates().get(to) == null) {
            throw new RateNotAvailableException(
                    "L'API externe ne fournit pas de taux pour " + from + " -> " + to);
        }
        return response.rates().get(to);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceUnavailableException("Appel interrompu", e);
        }
    }
}