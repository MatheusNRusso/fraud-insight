package com.fraudrisk.fraudriskapi.model;

import com.fraudrisk.fraudriskapi.model.enums.PredictionSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "fraud_predictions")
@NoArgsConstructor
public class FraudPredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inputJson;

    private double probFraud;
    private String fraudLabel;
    private String modelVersion;
    private double thresholdOptimal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PredictionSource source;

    private FraudPredictionEntity(
            String inputJson,
            double probFraud,
            String fraudLabel,
            String modelVersion,
            double thresholdOptimal,
            PredictionSource source
    ) {
        this.inputJson = inputJson;
        this.probFraud = probFraud;
        this.fraudLabel = fraudLabel;
        this.modelVersion = modelVersion;
        this.thresholdOptimal = thresholdOptimal;
        this.source = source;
    }

    public static FraudPredictionEntity of(
            String inputJson,
            double probFraud,
            String fraudLabel,
            String modelVersion,
            double thresholdOptimal,
            PredictionSource source
    ) {
        return new FraudPredictionEntity(
                inputJson,
                probFraud,
                fraudLabel,
                modelVersion,
                thresholdOptimal,
                source
        );
    }

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
