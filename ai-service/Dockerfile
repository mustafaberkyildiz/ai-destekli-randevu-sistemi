FROM python:3.10-slim
WORKDIR /app

# Bağımlılıkları yükle
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Kodları kopyala
COPY . .

# FastAPI servisini 8001 portundan dışa aç
EXPOSE 8001

# --host 0.0.0.0 = uygulamanın container dışından gelen isteklere kapılarını açmasını sağlamaktır.
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8001"]