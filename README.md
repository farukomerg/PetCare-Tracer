<<<<<<< HEAD
# PetCare-Tracer

PetCare-Tracer, evcil hayvan sahiplerinin saglik, asi, ilac, beslenme, randevu, hatirlatma ve aktivite verilerini tek bir merkezden yonetebilmesi icin gelistirilmis cok katmanli bir takip platformudur. Proje, Ileri Java dersi kapsaminda Java tabanli backend, JavaFX admin paneli, Android istemci ve izleme/test altyapisi ile tasarlanmistir.

## Proje Amaci

Bu projenin amaci, evcil hayvan sahiplerinin daginik halde tuttugu bilgileri tek bir dijital platformda toplamak ve asagidaki ihtiyaclari karsilamaktir:

- evcil hayvan profili olusturma
- saglik gecmisi tutma
- asi kayitlarini takip etme
- ilac ve doz planlarini yonetme
- beslenme planlarini kaydetme
- veteriner randevularini saklama
- hatirlatma olusturma
- gunluk aktivite kayitlarini izleme

## Kullanilan Teknolojiler

- Java 17
- Spring Boot
- Spring JDBC
- PostgreSQL
- MongoDB
- BCrypt
- JavaFX
- Android Studio (Java)
- Docker Compose
- Prometheus
- Grafana
- k6
- JUnit + Mockito

## Sistem Mimarisi

```mermaid
flowchart LR
    A["Android Uygulamasi"] --> B["Spring Boot API"]
    C["JavaFX Admin Panel"] --> B
    B --> D["PostgreSQL"]
    B --> E["MongoDB"]
    F["k6"] --> B
    G["Prometheus"] --> B
    H["Grafana"] --> G
```

## Tamamlanan Moduller

### Backend

- Auth
- Users
- Pets
- Health Records
- Vaccines
- Vaccine Records
- Medications
- Medication Schedules
- Feeding Plans
- Appointments
- Reminders
- Activity Logs

### Istemci Tarafi

- JavaFX admin panel iskeleti
- Android login ekrani
- Android register ekrani
- Android dashboard ekrani
- Android pet listesi ekrani

### DevOps / Test

- Docker Compose kurulumu
- Prometheus metrics toplama
- Grafana dashboard
- k6 smoke test
- k6 hafif yuk testi
- temel TDD tabanli service testleri

## Proje Klasor Yapisi

- `backend/petcare-backend`
  Spring Boot backend kodlari
- `admin-panel/petcare-admin`
  JavaFX admin panel projesi
- `mobil-app/PetCareMobile`
  Android Studio mobil istemci projesi
- `db`
  PostgreSQL schema ve seed scriptleri
- `monitoring`
  Prometheus ve Grafana konfigrasyonlari
- `tests/k6`
  performans test scriptleri
- `docs`
  kurulum ve kullanim dokumanlari

## Yerel Calistirma

### Backend

```bash
cd backend/petcare-backend
mvnw.cmd spring-boot:run
```

Varsayilan backend adresi:

- `http://localhost:8080`

### Docker ile Tum Sistemi Calistirma

```bash
docker compose up --build
```

Bu komut asagidaki servisleri ayaga kaldirir:

- PostgreSQL: `localhost:5433`
- MongoDB: `localhost:27018`
- Backend API: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

Grafana giris bilgileri:

- kullanici: `admin`
- sifre: `admin123`

## Test ve Izleme

### HTTP Istekleri

- [requests.http](/C:/Users/MSI/Desktop/PetCare-Tracer/backend/petcare-backend/requests.http)

### k6 Testleri

```bash
k6 run tests/k6/smoke-test.js
k6 run tests/k6/core-load.js
```

Docker profili ile:

```bash
docker compose --profile loadtest run --rm k6 run /scripts/smoke-test.js
```

### TDD / Unit Test

Backend testleri:

```bash
cd backend/petcare-backend
mvnw.cmd test
```

Eklenen ornek test siniflari:

- `AuthServiceTest`
- `ActivityLogServiceTest`
- `FeedingPlanServiceTest`

## Android Notlari

Android Studio'da acilacak proje:

- `mobil-app/PetCareMobile`

Android emulator icin backend adresi:

- `http://10.0.2.2:8080/`

Onemli not:

- `No target device found` hatasi koddan degil, emulator veya fiziksel cihaz tanimli olmamasindan kaynaklanir.
- Bu durumda Android Studio icinde `Tools > Device Manager > Create Device` adimlariyla bir emulator olusturulmalidir.

Detayli rehber:

- [android-mobile.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/android-mobile.md)

## JavaFX Admin Panel

Calistirma:

```bash
backend/petcare-backend/mvnw.cmd -f admin-panel/petcare-admin/pom.xml javafx:run
```

Detayli not:

- [javafx-admin-panel.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/javafx-admin-panel.md)

## Dokumantasyon

- [database-setup.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/database-setup.md)
- [performance-testing.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/performance-testing.md)
- [observability.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/observability.md)
- [screenshots-guide.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/screenshots-guide.md)

## Ekran Goruntuleri

Asagidaki ekran goruntulerinin README veya rapora eklenmesi onerilir:

- Giris ekrani
- Kayit ekrani
- Dashboard
- Pet listesi
- JavaFX admin panel
- Prometheus targets
- Grafana dashboard


=======
PetCare-Tracer
Evcil hayvan sahiplerinin; saglik, asi, ilac, beslenme, randevu, hatirlatma ve aktivite verilerini tek merkezden yonetebildigi ileri Java dersi projesi.

Proje Yapisi

backend/petcare-backend

Spring Boot + JDBC + MongoDB backend

admin-panel/petcare-admin

JavaFX admin panel

db

PostgreSQL schema ve seed scriptleri

docs

kurulum ve performans test dokumani

tests/k6

performans testi scriptleri

mobil-app

Android istemci icin ayrilan alan

Login, register, dashboard ve pet listeleme iskeleti hazir


Kullanilan Teknolojiler

Java 17

Spring Boot

Spring JDBC

PostgreSQL

MongoDB

BCrypt

Docker Compose

k6

JavaFX

Prometheus

Grafana


Tamamlanan Backend Modulleri

auth

users

pets

health records

vaccines

vaccine records

medications

medication schedules

feeding plans

appointments

reminders

activity logs


Yerel Calistirma
PostgreSQL ve MongoDB servislerini ac.
petcare_tracker veritabanini db/01_schema.sql ve db/02_seed.sql ile hazirla.
Backend klasorune gir:

bash



cd backend/petcare-backend



Uygulamayi baslat:

bash



mvnw.cmd spring-boot:run



Docker Ile Calistirma
Kok klasorde:

bash



docker compose up --build



Bu kurulum su servisleri ayaga kaldirir:


PostgreSQL: localhost:5433

MongoDB: localhost:27018

Backend API: http://localhost:8080

Prometheus: http://localhost:9090

Grafana: http://localhost:3000


Temel Testler
Hazir HTTP istekleri:


requests.http


Performans testleri:

bash



k6 run tests/k6/smoke-test.js
k6 run tests/k6/core-load.js



Monitoring notlari:


observability.md


JavaFX Admin Panel
bash



backend/petcare-backend/mvnw.cmd -f admin-panel/petcare-admin/pom.xml javafx:run



Detayli notlar:


javafx-admin-panel.md


Android Mobile
Android Studio ile acilacak proje:


mobil-app/PetCareMobile


Detayli notlar:


android-mobile.md

screenshots-guide.md


Mimari Ozeti

#mermaid-_r_16v_{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;font-size:16px;fill:rgb(255, 255, 255);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#mermaid-_r_16v_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#mermaid-_r_16v_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#mermaid-_r_16v_ .error-icon{fill:rgba(255, 255, 255, 0.082);}#mermaid-_r_16v_ .error-text{fill:rgba(255, 255, 255, 0.498);stroke:rgba(255, 255, 255, 0.498);}#mermaid-_r_16v_ .edge-thickness-normal{stroke-width:1px;}#mermaid-_r_16v_ .edge-thickness-thick{stroke-width:3.5px;}#mermaid-_r_16v_ .edge-pattern-solid{stroke-dasharray:0;}#mermaid-_r_16v_ .edge-thickness-invisible{stroke-width:0;fill:none;}#mermaid-_r_16v_ .edge-pattern-dashed{stroke-dasharray:3;}#mermaid-_r_16v_ .edge-pattern-dotted{stroke-dasharray:2;}#mermaid-_r_16v_ .marker{fill:rgba(255, 255, 255, 0.498);stroke:rgba(255, 255, 255, 0.498);}#mermaid-_r_16v_ .marker.cross{stroke:rgba(255, 255, 255, 0.498);}#mermaid-_r_16v_ svg{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;font-size:16px;}#mermaid-_r_16v_ p{margin:0;}#mermaid-_r_16v_ .label{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;color:rgb(255, 255, 255);}#mermaid-_r_16v_ .cluster-label text{fill:rgb(255, 255, 255);}#mermaid-_r_16v_ .cluster-label span{color:rgb(255, 255, 255);}#mermaid-_r_16v_ .cluster-label span p{background-color:transparent;}#mermaid-_r_16v_ .label text,#mermaid-_r_16v_ span{fill:rgb(255, 255, 255);color:rgb(255, 255, 255);}#mermaid-_r_16v_ .node rect,#mermaid-_r_16v_ .node circle,#mermaid-_r_16v_ .node ellipse,#mermaid-_r_16v_ .node polygon,#mermaid-_r_16v_ .node path{fill:rgba(54, 54, 54, 0.96);stroke:rgba(255, 255, 255, 0.082);stroke-width:1px;}#mermaid-_r_16v_ .rough-node .label text,#mermaid-_r_16v_ .node .label text,#mermaid-_r_16v_ .image-shape .label,#mermaid-_r_16v_ .icon-shape .label{text-anchor:middle;}#mermaid-_r_16v_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#mermaid-_r_16v_ .rough-node .label,#mermaid-_r_16v_ .node .label,#mermaid-_r_16v_ .image-shape .label,#mermaid-_r_16v_ .icon-shape .label{text-align:center;}#mermaid-_r_16v_ .node.clickable{cursor:pointer;}#mermaid-_r_16v_ .root .anchor path{fill:rgba(255, 255, 255, 0.498)!important;stroke-width:0;stroke:rgba(255, 255, 255, 0.498);}#mermaid-_r_16v_ .arrowheadPath{fill:#e7e7e7;}#mermaid-_r_16v_ .edgePath .path{stroke:rgba(255, 255, 255, 0.498);stroke-width:2.0px;}#mermaid-_r_16v_ .flowchart-link{stroke:rgba(255, 255, 255, 0.498);fill:none;}#mermaid-_r_16v_ .edgeLabel{background-color:rgb(40, 40, 40);text-align:center;}#mermaid-_r_16v_ .edgeLabel p{background-color:rgb(40, 40, 40);}#mermaid-_r_16v_ .edgeLabel rect{opacity:0.5;background-color:rgb(40, 40, 40);fill:rgb(40, 40, 40);}#mermaid-_r_16v_ .labelBkg{background-color:rgba(40, 40, 40, 0.5);}#mermaid-_r_16v_ .cluster rect{fill:rgba(255, 255, 255, 0.03);stroke:rgba(255, 255, 255, 0.082);stroke-width:1px;}#mermaid-_r_16v_ .cluster text{fill:rgb(255, 255, 255);}#mermaid-_r_16v_ .cluster span{color:rgb(255, 255, 255);}#mermaid-_r_16v_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;font-size:12px;background:rgba(255, 255, 255, 0.082);border:1px solid rgba(255, 255, 255, 0.082);border-radius:2px;pointer-events:none;z-index:100;}#mermaid-_r_16v_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(255, 255, 255);}#mermaid-_r_16v_ rect.text{fill:none;stroke-width:0;}#mermaid-_r_16v_ .icon-shape,#mermaid-_r_16v_ .image-shape{background-color:rgb(40, 40, 40);text-align:center;}#mermaid-_r_16v_ .icon-shape p,#mermaid-_r_16v_ .image-shape p{background-color:rgb(40, 40, 40);padding:2px;}#mermaid-_r_16v_ .icon-shape rect,#mermaid-_r_16v_ .image-shape rect{opacity:0.5;background-color:rgb(40, 40, 40);fill:rgb(40, 40, 40);}#mermaid-_r_16v_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#mermaid-_r_16v_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#mermaid-_r_16v_ :root{--mermaid-font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif;}Android / JavaFX ClientSpring Boot APIPostgreSQLMongoDB



Ekran Goruntuleri

Giris ekrani: ekran goruntusu

Kayit ekrani: ekran goruntusu

Dashboard: ekran goruntusu

Pet listesi: ekran goruntusu

JavaFX admin panel: ekran goruntusu

Prometheus target ekrani: ekran goruntusu

Grafana dashboard: ekran goruntusu


Sonraki Asama

Android pet detay, saglik ve hatirlatma ekranlari

ekran goruntuleri ve sunum raporu
>>>>>>> d3edc1b1b1dfc319853159826e2d4e6332f29417
