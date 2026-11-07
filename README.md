# 🏥 Yapay Zekâ Destekli Sağlık Randevu Sistemi

Java Spring Boot, React ve Python kullanılarak geliştirilen, hasta, doktor ve yönetici rollerine sahip web tabanlı sağlık randevu yönetim sistemidir.

Proje, klasik randevu yönetimi işlevlerine ek olarak hastaların belirttiği semptomları analiz ederek uygun tıbbi bölüme yönlendirilmesini sağlayan yapay zekâ destekli bir öneri mekanizması içerir.

## 🚀 Proje Özellikleri

### 👤 Hasta İşlemleri
- Kullanıcı kaydı ve JWT tabanlı kimlik doğrulama
- Doktor ve bölüm listeleme
- Uygun tarih ve saatlere göre randevu oluşturma
- Mevcut randevuları görüntüleme ve iptal etme
- Semptomlara göre uygun tıbbi bölüm önerisi alma

### 👨‍⚕️ Doktor İşlemleri
- Doktora ait randevuların görüntülenmesi
- Muayene süreçlerinin yönetilmesi
- Doktor izinlerinin oluşturulması ve yönetilmesi
- Randevu durumlarının güncellenmesi

### 🛠️ Yönetici İşlemleri
- Kullanıcı, doktor ve bölüm yönetimi
- Doktor ve bölümlerin aktif/pasif durumlarının yönetilmesi
- Randevu kayıtlarının ve raporlarının görüntülenmesi
- Sistem genelindeki yönetim işlemleri

## 🧠 Yapay Zekâ ve Anlamsal Arama

Projenin yapay zekâ bileşeni, hastaların yazdığı semptomlara göre uygun tıbbi bölümün önerilmesini amaçlar.

Bu süreçte Python tabanlı FastAPI servisi, SentenceTransformers ve ChromaDB kullanılarak anlamsal benzerlik araması gerçekleştirilir.

**Çalışma akışı:**

1. Hasta semptomlarını sisteme girer.
2. Girilen metin yapay zekâ servisine iletilir.
3. Metin, embedding modeli aracılığıyla vektörel gösterime dönüştürülür.
4. ChromaDB üzerinde semptomlarla anlamsal benzerlik araması yapılır.
5. Eşleşen semptomlar üzerinden uygun tıbbi bölüm önerilir.
6. Sonuç kullanıcı arayüzünde gösterilir.

Bu özellik tıbbi tanı koymak amacıyla değil, kullanıcıların uygun bölüme yönlendirilmesini desteklemek amacıyla geliştirilmiştir.

## 💻 Kullanılan Teknolojiler

| Katman | Teknolojiler |
|---|---|
| Backend | Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate |
| Frontend | React, JavaScript, Vite |
| Veritabanı | MySQL |
| Kimlik Doğrulama | JWT, Refresh Token |
| Yapay Zekâ Servisi | Python, FastAPI |
| Anlamsal Arama | ChromaDB, SentenceTransformers |
| API | REST API |
| Geliştirme Araçları | Maven, Git, GitHub, Postman |
| Konteynerleştirme | Docker, Docker Compose |

## 🏗️ Sistem Mimarisi

Sistem, temel olarak aşağıdaki bileşenlerden oluşur:

- **Frontend:** React ile geliştirilen kullanıcı arayüzü
- **Backend:** Spring Boot tabanlı REST API ve iş kuralları
- **Veritabanı:** MySQL ile kalıcı veri yönetimi
- **AI Service:** FastAPI ile anlamsal arama ve bölüm önerisi
- **Vector Database:** ChromaDB ile embedding verilerinin saklanması ve benzerlik araması

```text
           React Frontend
                  |
                  v
           Spring Boot API
              /       \
             v         v
          MySQL     FastAPI
                        |
                        v
                 SentenceTransformers
                        |
                        v
                     ChromaDB
```

## 🔐 Güvenlik

- Spring Security ile kimlik doğrulama ve yetkilendirme
- JWT tabanlı erişim kontrolü
- Refresh Token mekanizması
- Hasta, doktor ve yönetici rolleri için rol bazlı yetkilendirme
- Korumalı REST API endpoint'leri

## 📁 Proje Yapısı

```text
ai-destekli-randevu-sistemi/
│
├── backend/
│   └── Spring Boot Backend
│
├── ai-service/
│   └── Python FastAPI ve ChromaDB
│
├── docker-compose.yaml
├── .env.example
└── README.md
```

## 🎯 Projenin Amacı

Bu projeyle Spring Boot üzerinde güvenli REST API geliştirme, ilişkisel veritabanı yönetimi, rol bazlı yetkilendirme, frontend-backend entegrasyonu ve Python tabanlı yapay zekâ servislerinin bir arada kullanılması hedeflenmiştir.

Proje aynı zamanda farklı teknolojiler arasında servis iletişimi, modüler yazılım geliştirme ve yapay zekâ destekli arama mekanizmalarının uygulanması konularında deneyim kazanmak amacıyla geliştirilmiştir.

## 👨‍💻 Geliştirici

**Mustafa Berk Yıldız**

Bilgisayar Mühendisliği – Konya Teknik Üniversitesi

[GitHub Profilim](https://github.com/mustafaberkyildiz)
