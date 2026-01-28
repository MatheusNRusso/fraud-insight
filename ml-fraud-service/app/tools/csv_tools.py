from io import BytesIO
import pandas as pd
from typing import List
from ..schemas import FraudInput


def read_csv_bytes(content: bytes) -> pd.DataFrame:
    return pd.read_csv(BytesIO(content))


def ensure_columns(df: pd.DataFrame, expected: list[str]) -> None:
    missing = [c for c in expected if c not in df.columns]
    extra = [c for c in df.columns if c not in expected]
    if missing:
        raise ValueError(f"CSV com colunas faltando: {missing}")
    if extra:
        raise ValueError(f"CSV com colunas desconhecidas: {extra}")


def df_to_inputs(df: pd.DataFrame, expected: list[str]) -> List[FraudInput]:
    # garante ordem e tipo
    df = df[expected].copy()

    items: List[FraudInput] = []
    for row in df.to_dict(orient="records"):
        items.append(FraudInput(**row))
    return items
