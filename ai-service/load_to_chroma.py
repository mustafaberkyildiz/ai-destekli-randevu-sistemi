import os
from dotenv import load_dotenv
import chromadb
from sentence_transformers import SentenceTransformer
import mysql.connector

# .env dosyasındaki değişkenleri yükle
load_dotenv()

# Ortam değişkenlerinden veritabanı bilgilerini al (yoksa varsayılanı kullanır)
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_USER = os.getenv("DB_USERNAME", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "1234")
DB_NAME = os.getenv("DB_NAME", "proje")
DB_PORT = int(os.getenv("DB_PORT", 3306))
CHROMA_HOST = os.getenv("CHROMA_HOST", "localhost")
CHROMA_PORT = int(os.getenv("CHROMA_PORT", 8000))

# MySQL bağlantısı
conn = mysql.connector.connect(
    host=DB_HOST,
    user=DB_USER,
    password=DB_PASSWORD,
    database=DB_NAME,
    port=DB_PORT,
)

cursor = conn.cursor()
cursor.execute("SELECT id, symptom_text, department_id FROM symptom_department")
rows = cursor.fetchall()

# Embedding modeli
model = SentenceTransformer("all-MiniLM-L6-v2")

# ChromaDB bağlantısı
client = chromadb.HttpClient(host=CHROMA_HOST, port=CHROMA_PORT)

# Koleksiyon
collection = client.get_or_create_collection("symptom_collection")

documents = []
metadatas = []
ids = []

for row in rows:
    row_id, symptom_text, department_id = row
    documents.append(symptom_text)
    metadatas.append({"department_id": department_id})
    ids.append(str(row_id))

# Embedding üret
embeddings = model.encode(documents).tolist()
# hastanin girdigi semptoma gore en yakin semptom aranacagi icin 
# semptom kismini encode ediyoruz vektor haline getiriyoruz

# ChromaDB'ye ekle
collection.add(
    documents=documents,
    embeddings=embeddings,
    metadatas=metadatas,
    ids=ids
)

print(f"{len(documents)} kayıt ChromaDB'ye eklendi.")