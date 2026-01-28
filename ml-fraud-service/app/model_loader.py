from dataclasses import dataclass
from pathlib import Path
import joblib


@dataclass(frozen=True)
class LoadedModel:
    pipeline: object
    features: list[str]
    thresholds: dict
    version: str


def load_model(artifact_path: Path) -> LoadedModel:
    artifact = joblib.load(artifact_path)

    # Estrutura do seu artefato:
    # keys = ["model", "config", "metadata"]
    config = artifact["config"]

    features = config["features"]["names"]  # ordem correta (time, v1..v28, amount)
    threshold = float(config.get("threshold", 0.9))
    version = config.get("version", "fraud-unknown")

    return LoadedModel(
        pipeline=artifact["model"],  # sklearn.pipeline.Pipeline
        features=list(features),
        thresholds={
            "OPTIMAL": threshold
        },
        version=version,
    )
