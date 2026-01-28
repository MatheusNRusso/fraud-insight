from pydantic import BaseModel, Field
from typing import List


# ✅ Entrada (single) — contrato fixo do modelo
class FraudInput(BaseModel):
    time: float
    v1: float
    v2: float
    v3: float
    v4: float
    v5: float
    v6: float
    v7: float
    v8: float
    v9: float
    v10: float
    v11: float
    v12: float
    v13: float
    v14: float
    v15: float
    v16: float
    v17: float
    v18: float
    v19: float
    v20: float
    v21: float
    v22: float
    v23: float
    v24: float
    v25: float
    v26: float
    v27: float
    v28: float
    amount: float = Field(ge=0)


# ✅ Resposta do /predict (single)
class FraudResponse(BaseModel):
    prob_fraud: float = Field(ge=0, le=1)
    fraud_label: str
    model_version: str
    threshold_optimal: float


# ✅ Item enxuto do batch
class FraudItem(BaseModel):
    probability: float = Field(ge=0, le=1)
    label: str


# ✅ Meta do batch
class BatchMeta(BaseModel):
    model_version: str
    total: int
    threshold_optimal: float | None = None


# ✅ Resposta do /predict/batch
class FraudBatchResponse(BaseModel):
    meta: BatchMeta
    results: List[FraudItem]


# ✅ Health
class HealthResponse(BaseModel):
    status: str
    model_version: str
    threshold_optimal: float
