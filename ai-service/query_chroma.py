import os
from dotenv import load_dotenv
import chromadb
from sentence_transformers import SentenceTransformer

load_dotenv()

CHROMA_HOST = os.getenv("CHROMA_HOST", "localhost")
CHROMA_PORT = int(os.getenv("CHROMA_PORT", 8000))

# Embedding modeli
model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

# ChromaDB bağlantısı
client = chromadb.HttpClient(host=CHROMA_HOST, port=CHROMA_PORT)
collection = client.get_collection("symptom_collection")

# Kullanıcı girişi
query = input("Semptom gir: ")

# Embedding üret
query_embedding = model.encode([query]).tolist()

# ChromaDB sorgusu
result = collection.query(
    query_embeddings=query_embedding,
    n_results=1
)

dept_id = result["metadatas"][0][0]["department_id"]
doc = result["documents"][0][0]

print("Tahmin edilen bölüm ID:", dept_id)
print("Eşleşen semptom:", doc)