from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    APP_NAME: str = "ml-fraud-service"
    APP_ENV: str = "local"

    # caminho do artefato
    ARTIFACT_PATH: str = "artifacts/fraud_model_production.joblib"

    # threshold de "ótimo" (true fraud) — default 0.9
    OPTIMAL_THRESHOLD: float = 0.9

    # opcional: nível intermediário pra demo/monitoramento
    SUSPECT_THRESHOLD: float = 0.5


settings = Settings()
