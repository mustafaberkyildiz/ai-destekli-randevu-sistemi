import os
from dotenv import load_dotenv
from fastapi import FastAPI
from pydantic import BaseModel
import chromadb
from sentence_transformers import SentenceTransformer

load_dotenv()

app = FastAPI()

class SymptomRequest(BaseModel):
    symptom: str

DEPARTMENT_MAP = {
    5: "Kardiyoloji",
    6: "Nöroloji",
    7: "Endokrinoloji",
    8: "Nefroloji",
    9: "Enfeksiyon Hastalıkları",
    10: "Göğüs Hastalıkları",
    12: "Psikiyatri",
    13: "Dermatoloji",
    14: "Genel Cerrahi",
    15: "Ortopedi",
    16: "Acil Servis",
    17: "Kulak Burun Boğaz",
    19: "Göz Hastalıkları",
    20: "Üroloji",
    21: "Kadın Hastalıkları ve Doğum",
    23: "Çocuk Sağlığı ve Hastalıkları",
    24: "Fizik Tedavi ve Rehabilitasyon",
    25: "Beyin ve Sinir Cerrahisi",
    26: "Dahiliye",
}

CHROMA_HOST = os.getenv("CHROMA_HOST", "localhost")
CHROMA_PORT = int(os.getenv("CHROMA_PORT", 8000))

model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")
client = chromadb.HttpClient(host=CHROMA_HOST, port=CHROMA_PORT)
collection = client.get_collection("symptom_collection")

@app.get("/")
def home():
    return {"message": "AI symptom service is running"}

@app.post("/predict")
def predict(request: SymptomRequest):
    query_embedding = model.encode([request.symptom]).tolist()

    result = collection.query(
        query_embeddings=query_embedding,
        n_results=1
    )

    dept_id = result["metadatas"][0][0]["department_id"]
    matched_symptom = result["documents"][0][0]
    department_name = DEPARTMENT_MAP.get(dept_id, "Bilinmeyen Bölüm")

    return {
    "department_id": dept_id,
    "department_name": department_name,
    "matched_symptom": matched_symptom
    }