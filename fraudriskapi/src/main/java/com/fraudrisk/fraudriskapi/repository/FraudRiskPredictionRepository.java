package com.fraudrisk.fraudriskapi.repository;

import com.fraudrisk.fraudriskapi.model.FraudPredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudRiskPredictionRepository extends
        JpaRepository<FraudPredictionEntity, Long> {
}
