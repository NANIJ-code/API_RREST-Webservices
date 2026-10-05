package com.apirestjava.example.apirest.service;

import com.apirestjava.example.apirest.dto.ManualConversionRequest;
import com.apirestjava.example.apirest.dto.UpdateRateRequest;
import com.apirestjava.example.apirest.entity.ConversionTransaction;
import com.apirestjava.example.apirest.entity.ExchangeRate;
import com.apirestjava.example.apirest.entity.RateSource;
import com.apirestjava.example.apirest.exception.InvalidOperationException;
import com.apirestjava.example.apirest.exception.RateNotAvailableException;
import com.apirestjava.example.apirest.exception.ResourceNotFoundException;
import com.apirestjava.example.apirest.repository.ConversionTransactionRepository;
import com.apirestjava.example.apirest.repository.ExchangeRateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class CurrencyService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000000000");

    private final ConversionTransactionRepository transactionRepository;
    private final ExchangeRateRepository rateRepository;
    private final ExchangeRateClient rateClient;

    public CurrencyService(ConversionTransactionRepository transactionRepository,
                           ExchangeRateRepository rateRepository,
                           ExchangeRateClient rateClient) {
        this.transactionRepository = transactionRepository;
        this.rateRepository = rateRepository;
        this.rateClient = rateClient;
    }

    // GET /convert : taux externe (avec cache + retry), sinon taux local si la paire n'est pas couverte
    @Transactional
    public ConversionTransaction convert(BigDecimal amount, String from, String to) {
        validateAmount(amount);
        String fromCode = normalizeCurrency(from, "from");
        String toCode = normalizeCurrency(to, "to");
        validatePair(fromCode, toCode);

        BigDecimal rate;
        RateSource source;
        try {
            rate = rateClient.getRate(fromCode, toCode);
            source = RateSource.EXTERNAL;
        } catch (RateNotAvailableException e) {
            ExchangeRate local = rateRepository.findByFromCurrencyAndToCurrency(fromCode, toCode)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Aucun taux disponible pour " + fromCode + " -> " + toCode
                                    + ". Enregistrez un taux manuel via POST /api/currency/convert"));
            rate = local.getRate();
            source = RateSource.MANUAL;
        }

        return saveTransaction(amount, fromCode, toCode, rate, source);
    }

    // POST /convert : taux manuel, mémorisé localement
    @Transactional
    public ConversionTransaction convertManual(ManualConversionRequest request) {
        String fromCode = normalizeCurrency(request.getFrom(), "from");
        String toCode = normalizeCurrency(request.getTo(), "to");
        validatePair(fromCode, toCode);

        BigDecimal rate = request.getRate().setScale(10, RoundingMode.HALF_UP);

        ExchangeRate stored = rateRepository.findByFromCurrencyAndToCurrency(fromCode, toCode)
                .orElseGet(() -> new ExchangeRate(fromCode, toCode, rate, LocalDateTime.now()));
        stored.setRate(rate);
        stored.setUpdatedAt(LocalDateTime.now());
        rateRepository.save(stored);

        return saveTransaction(request.getAmount(), fromCode, toCode, rate, RateSource.MANUAL);
    }

    // PUT /update-rate : met à jour un taux stocké localement
    @Transactional
    public ExchangeRate updateRate(UpdateRateRequest request) {
        String fromCode = normalizeCurrency(request.getFrom(), "from");
        String toCode = normalizeCurrency(request.getTo(), "to");

        ExchangeRate stored = rateRepository.findByFromCurrencyAndToCurrency(fromCode, toCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun taux local enregistré pour " + fromCode + " -> " + toCode
                                + ". Créez-le d'abord via POST /api/currency/convert"));

        stored.setRate(request.getRate().setScale(10, RoundingMode.HALF_UP));
        stored.setUpdatedAt(LocalDateTime.now());
        return rateRepository.save(stored);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        ConversionTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction introuvable avec l'id " + id));
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public Page<ConversionTransaction> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    // --- méthodes privées ---

    private ConversionTransaction saveTransaction(BigDecimal amount, String from, String to,
                                                  BigDecimal rate, RateSource source) {
        BigDecimal normalizedRate = rate.setScale(10, RoundingMode.HALF_UP);
        BigDecimal converted = amount.multiply(normalizedRate).setScale(4, RoundingMode.HALF_UP);

        ConversionTransaction transaction = new ConversionTransaction(
                from, to, amount, normalizedRate, converted, source, LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private String normalizeCurrency(String code, String fieldName) {
        if (code == null || !code.trim().matches("[A-Za-z]{3}")) {
            throw new InvalidOperationException(
                    "'" + fieldName + "' doit être un code devise de 3 lettres (ex. EUR, USD)");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidOperationException("Le montant doit être strictement positif");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidOperationException("Le montant est trop grand");
        }
    }

    private void validatePair(String from, String to) {
        if (from.equals(to)) {
            throw new InvalidOperationException("Les devises source et cible doivent être différentes");
        }
    }
}