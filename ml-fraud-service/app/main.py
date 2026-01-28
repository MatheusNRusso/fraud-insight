from fastapi import FastAPI, HTTPException, UploadFile, File
from pathlib import Path

from .settings import settings
from .model_loader import load_model, LoadedModel
from .schemas import (
    FraudInput,
    FraudResponse,
    FraudBatchResponse,
    FraudItem,
    BatchMeta,
    HealthResponse,
)
from .service import predict_one, predict_many
from .tools.csv_tools import read_csv_bytes, ensure_columns, df_to_inputs


app = FastAPI(
    title="ML Fraud Service",
    version="1.0.0",
    description="Serviço de inferência de fraude (Logistic Regression) com batch e suporte a CSV.",
)

MODEL: LoadedModel | None = None


@app.on_event("startup")
def _startup() -> None:
    global MODEL
    MODEL = load_model(Path(settings.ARTIFACT_PATH))


@app.get("/health", response_model=HealthResponse, tags=["health"])
def health() -> HealthResponse:
    if MODEL is None:
        raise HTTPException(status_code=503, detail="Model not loaded")
    return HealthResponse(
        status="ok",
        model_version=MODEL.version,
        threshold_optimal=float(MODEL.thresholds.get("OPTIMAL", settings.OPTIMAL_THRESHOLD)),
    )


@app.post("/predict", response_model=FraudResponse, tags=["predict"])
def predict(dto: FraudInput) -> FraudResponse:
    if MODEL is None:
        raise HTTPException(status_code=503, detail="Model not loaded")
    try:
        return predict_one(MODEL, dto, suspect_threshold=settings.SUSPECT_THRESHOLD)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))


@app.post("/predict/batch", response_model=FraudBatchResponse, tags=["predict"])
def predict_batch(items: list[FraudInput]) -> FraudBatchResponse:
    if MODEL is None:
        raise HTTPException(status_code=503, detail="Model not loaded")
    try:
        results = predict_many(MODEL, items, suspect_threshold=settings.SUSPECT_THRESHOLD)
        return FraudBatchResponse(
            meta=BatchMeta(model_version=MODEL.version, total=len(results), threshold_optimal=float(MODEL.thresholds.get("OPTIMAL", 0.9))),
            results=results,
        )
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))


@app.post("/predict/batch/csv", response_model=FraudBatchResponse, tags=["predict"])
async def predict_batch_csv(file: UploadFile = File(...)) -> FraudBatchResponse:
    if MODEL is None:
        raise HTTPException(status_code=503, detail="Model not loaded")

    try:
        content = await file.read()
        df = read_csv_bytes(content)

        # valida contrato de colunas exatamente
        ensure_columns(df, MODEL.features)

        inputs = df_to_inputs(df, MODEL.features)
        results = predict_many(MODEL, inputs, suspect_threshold=settings.SUSPECT_THRESHOLD)

        return FraudBatchResponse(
            meta=BatchMeta(model_version=MODEL.version, total=len(results), threshold_optimal=float(MODEL.thresholds.get("OPTIMAL", 0.9))),
            results=results,
        )
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))
