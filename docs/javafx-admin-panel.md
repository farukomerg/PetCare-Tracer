# JavaFX Admin Panel

Bu modül backend API'sine baglanan masaustu yonetim arayuzudur.

## Konum

- `admin-panel/petcare-admin`

## Ozellikler

- backend URL girisi
- tek tusla veri yenileme
- kullanici listesi
- evcil hayvan listesi
- randevu listesi
- hatirlatma listesi
- ozet kartlari

## Calistirma

Backend ayakta olduktan sonra IntelliJ icinden `pom.xml` acilarak JavaFX projesi calistirilabilir.

Maven plugin ile calistirmak icin proje kokunden:

```bash
backend/petcare-backend/mvnw.cmd -f admin-panel/petcare-admin/pom.xml javafx:run
```

Varsayilan backend adresi:

- `http://localhost:8080`

## Bir Sonraki Gelistirme Adimlari

- create/update/delete formlari
- grafik kartlari
- saglik ve asi sekmeleri
- giris ekrani
