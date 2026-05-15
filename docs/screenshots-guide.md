# Ekran Goruntusu Rehberi

Bu dosya, proje raporu ve README icin hangi ekran goruntulerini alman gerektigini ve bunlari nereye koyacagini anlatir.

## Klasor Yapisi

Ekran goruntulerini su klasore koy:

- `docs/screenshots`

Onerilen dosya isimleri:

- `login-screen.png`
- `register-screen.png`
- `dashboard-screen.png`
- `pet-list-screen.png`
- `javafx-admin-screen.png`
- `grafana-dashboard.png`
- `prometheus-targets.png`

## README Icine Ekleme

README icinde ekran goruntulerini su formatla kullan:

```md
![Login Screen](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/screenshots/login-screen.png)
![Register Screen](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/screenshots/register-screen.png)
![Dashboard Screen](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/screenshots/dashboard-screen.png)
```

## Android Ekran Goruntusu Alma

1. Android Studio'da emulatoru baslat.
2. Uygulamayi ac.
3. Almak istedigin ekrana gel.
4. Android Studio icinde `View > Tool Windows > Running Devices` ac.
5. Acik emulator ekraninin ustunde kamera ikonuna tikla.
6. Kaydedecegin yeri `docs/screenshots` olarak sec.
7. Dosyayi uygun isimle kaydet.

Alternatif:

1. Emulator acikken sagdaki yan panelde kamera ikonuna bas.
2. `Save` de.
3. `docs/screenshots` klasorune koy.

## JavaFX Admin Panel Ekran Goruntusu Alma

1. JavaFX panelini calistir.
2. Tum ozet kartlari ve tablolar gorunur hale getir.
3. Windows'ta `Win + Shift + S` tuslarina bas.
4. Pencereyi sec.
5. Gorseli `docs/screenshots/javafx-admin-screen.png` olarak kaydet.

## Grafana Ekran Goruntusu Alma

1. Tarayicida `http://localhost:3000` ac.
2. `admin / admin123` ile giris yap.
3. `PetCare Backend Overview` dashboardunu ac.
4. Tablolar ve grafikler yuklendikten sonra `Win + Shift + S` ile goruntu al.
5. `docs/screenshots/grafana-dashboard.png` olarak kaydet.

## Prometheus Ekran Goruntusu Alma

1. Tarayicida `http://localhost:9090/targets` ac.
2. `petcare-backend` target'inin `UP` oldugunu gor.
3. `Win + Shift + S` ile goruntu al.
4. `docs/screenshots/prometheus-targets.png` olarak kaydet.

## Rapor Icin Onerilen Siralama

1. Login ekrani
2. Register ekrani
3. Dashboard
4. Pet listesi
5. JavaFX admin panel
6. Prometheus target ekrani
7. Grafana dashboard
