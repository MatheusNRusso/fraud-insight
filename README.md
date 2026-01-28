# Fraud Insight — Spring Boot API + Machine Learning (FastAPI)

Projeto que demonstra uma **integração completa entre uma API Java (Spring Boot)** e um **serviço de Machine Learning (Python/FastAPI)** para **detecção de fraude em transações financeiras**.

✅ API REST funcional
✅ Serviço ML funcional
✅ Predição **unitária**, **batch via JSON** e **batch via CSV**
✅ Persistência controlada (apenas requisições unitárias)
✅ Contratos claros entre Java (camelCase) e ML (snake_case)
✅ Swagger/OpenAPI + Actuator

---

## 🧠 Visão Geral

O projeto é composto por **dois serviços independentes**, que se comunicam via HTTP:

### 1️⃣ `fraudriskapi` — Java / Spring Boot

Responsável por:

- Receber requisições de predição:
  - unitária
  - batch via JSON
  - batch via CSV
- Validar DTOs (Bean Validation)
- Consumir o serviço de Machine Learning
- Persistir **somente predições unitárias**
- Manter rastreabilidade (input completo em JSON)
- Expor documentação (Swagger) e healthcheck (Actuator)

---

### 2️⃣ `ml-fraud-service` — Python / FastAPI

Responsável por:

- Receber payloads com features numéricas (`v1` … `v28`)
- Executar inferência do modelo treinado
- Suportar:
  - inferência unitária
  - inferência em lote
  - upload de CSV
- Retornar:
  - probabilidade de fraude
  - rótulo (fraude / legítimo)
  - versão do modelo
  - threshold utilizado

---

## 🧱 Stack Tecnológica

### Backend (API)

- Java 21
- Spring Boot
  - Web
  - Validation
  - Data JPA
  - WebFlux (WebClient)
  - Actuator
- PostgreSQL (com suporte a H2)
- Swagger / OpenAPI (springdoc)

### Machine Learning

- Python 3.11
- FastAPI
- Pydantic
- Scikit-learn
- Pandas
- Modelo serializado (`joblib`)

---

## 📌 Endpoints Principais

### API Java — `fraudriskapi` (porta **8082**)

| Método | Endpoint                       | Descrição                             |
|------|--------------------------------|---------------------------------------|
| POST | `/api/fraud/predict`           | Predição unitária (persiste no banco) |
| POST | `/api/fraud/predict/batch`     | Predição batch via JSON               |
| POST | `/api/fraud/predict/batch/csv` | Predição batch via CSV                |
| GET  | `/actuator/health`             | Healthcheck                           |
| GET  | `/swagger-ui/index.html`       | Documentação interativa               |

---

### ML Service — `ml-fraud-service` (porta **8000**)

| Método | Endpoint             | Descrição               |
|------|----------------------|-------------------------|
| GET  | `/health`            | Status do modelo        |
| POST | `/predict`           | Predição unitária       |
| POST | `/predict/batch`     | Predição batch via JSON |
| POST | `/predict/batch/csv` | Predição batch via CSV  |

---

## ▶️ Como Rodar Localmente

### 1️⃣ Subir o PostgreSQL (opcional, recomendado)

```bash
docker run --name fraud-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=frauddb \
  -p 5432:5432 -d postgres:15
```

> 💡 Também é possível utilizar **H2 em memória** ajustando o profile da aplicação.

---

### 2️⃣ Rodar o ML Service (Python)

```bash
cd ml-fraud-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

---

### 3️⃣ Rodar a API Java (Spring Boot)

```bash
cd fraudriskapi
./mvnw spring-boot:run
```

A API ficará disponível em:

* [http://localhost:8082](http://localhost:8082)
* Swagger: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)

---

## ✅ Teste Rápido (cURL)

### Predição Unitária

```bash
curl -s -X POST http://localhost:8082/api/fraud/predict \
  -H "Content-Type: application/json" \
  -d '{
    "time": 0,
    "v1": 1.2,
    "v2": -0.3,
    "v3": 0.8,
    "...": "...",
    "v28": 0.3,
    "amount": 120.50
  }'
```

### Resposta Esperada

```json
{
  "probFraud": 0.2758,
  "fraudLabel": "LEGIT",
  "modelVersion": "1.0.0",
  "thresholdOptimal": 0.9
}
```

---

### Predição Batch via CSV

```bash
curl -s -X POST http://localhost:8082/api/fraud/predict/batch/csv \
  -F "file=@sample.csv;type=text/plain"
```

```json
{
  "meta": {
    "model_version": "1.0.0",
    "total": 2,
    "threshold_optimal": 0.9
  },
  "results": [
    { "probability": 0.02, "label": "LEGIT" },
    { "probability": 1.0, "label": "TRUE_FRAUD" }
  ]
}
```

---

## 🗃️ Persistência

A API **persiste apenas predições unitárias**, armazenando:

* Input completo em JSON (auditoria / rastreio)
* Probabilidade
* Rótulo
* Versão do modelo
* Threshold utilizado
* Origem da predição (enum)

Batch via JSON e CSV **não são persistidos por decisão arquitetural**.

---

## 🔐 Observações Técnicas Importantes

* API Java usa **camelCase** (padrão Java)
* Serviço ML usa **snake_case**
* Contratos são explícitos e validados
* Integração Java ↔ Python validada com testes reais (`curl`)

---

## 👤 Autor

**Matheus N. Russo**
Backend Developer | Java | Spring Boot | APIs | ML Integration


