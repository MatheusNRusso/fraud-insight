import pandas as pd
from .model_loader import LoadedModel
from .schemas import FraudInput, FraudResponse, FraudItem


def fraud_label(p: float, thresholds: dict, suspect_threshold: float = 0.5) -> str:
    optimal = float(thresholds.get("OPTIMAL", 0.9))

    if p >= optimal:
        return "TRUE_FRAUD"
    if p >= float(suspect_threshold):
        return "SUSPECT"
    return "LEGIT"


def predict_one(model: LoadedModel, dto: FraudInput, suspect_threshold: float = 0.5) -> FraudResponse:
    X = pd.DataFrame([dto.model_dump()], columns=model.features).astype(float)
    proba = round(float(model.pipeline.predict_proba(X)[:, 1][0]), 4)

    optimal = float(model.thresholds.get("OPTIMAL", 0.9))

    return FraudResponse(
        prob_fraud=proba,
        fraud_label=fraud_label(proba, model.thresholds, suspect_threshold=suspect_threshold),
        model_version=model.version,
        threshold_optimal=optimal
    )


def predict_many(model: LoadedModel, items: list[FraudInput], suspect_threshold: float = 0.5) -> list[FraudItem]:
    X = pd.DataFrame([it.model_dump() for it in items], columns=model.features).astype(float)
    probas = model.pipeline.predict_proba(X)[:, 1].astype(float).tolist()

    results: list[FraudItem] = []
    for p in probas:
        p = round(float(p), 4)
        results.append(
            FraudItem(
                probability=p,
                label=fraud_label(p, model.thresholds, suspect_threshold=suspect_threshold),
            )
        )
    return results
