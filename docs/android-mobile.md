# Android Mobile Client

Bu modul Android Studio uzerinden Java ile gelistirilecek istemci tarafinin ilk iskeletidir.

## Konum

- `mobil-app/PetCareMobile`

## Hazirlanan Ekranlar

- Login
- Register
- Dashboard
- Pet listesi

## Teknik Yapi

- Java
- AndroidX
- Material Components
- Retrofit
- Gson Converter
- RecyclerView

## Backend Baglantisi

Varsayilan API adresi:

- `http://10.0.2.2:8080/`

Not:

- Android emulator icin `10.0.2.2` kullanildi.
- Gercek cihazda test edeceksen bu adresi kendi bilgisayarinin yerel IP adresine gore guncellemen gerekir.

## Android Studio Ile Acma

1. Android Studio ac.
2. `Open` sec.
3. `mobil-app/PetCareMobile` klasorunu sec.
4. Gradle sync tamamlandiktan sonra uygulamayi emulator uzerinde calistir.

## Android Studio'da Calistirma Sirasi

1. Android Studio ac.
2. `Open` sec.
3. `C:\Users\MSI\Desktop\PetCare-Tracer\mobil-app\PetCareMobile` klasorunu ac.
4. Proje yuklenince `Sync Project with Gradle Files` calistir.
5. Ust menuden `Tools > Device Manager` ac.
6. `Create Device` sec.
7. `Phone` kategorisinden `Pixel 6` veya `Pixel 7` sec.
8. `Next` de.
9. Cikan listeden bir Android image indir.
10. `Finish` de.
11. Device Manager ekraninda olusan emulatoru `Play` butonu ile baslat.
12. Emulator tamamen acildiktan sonra Android Studio ust cubugundaki cihaz listesinden bu emulatoru sec.
13. Sonra yesil `Run` butonuna bas.

## Uygulamanin Derlenme Durumu

Bu projede mobil taraf derlenebiliyor ve debug APK uretiyor:

- `mobil-app/PetCareMobile/app/build/outputs/apk/debug/app-debug.apk`

Bu bilgi bize sunu gosteriyor:

- kod tarafinda kritik bir derleme engeli yok
- sorun dogrudan hedef cihaz eksikliginden geliyor

## Sonraki Mobil Asamalar

- health records ekrani
- appointments ekrani
- reminders ekrani
- pet detay sayfasi
- form ekranlari ile create/update islemleri

## No Target Device Found Hatasi

Bu hata genelde koddan degil Android cihaz ortamindan kaynaklanir.

Bu projede kontrol edilen durum:

- Android SDK yolu tanimli
- fakat emulator icin `system-images` kurulumu gorunmuyor
- AVD klasorunde hazir sanal cihaz bulunmuyor
- `adb devices` ciktisi bos donuyor

Bu nedenle Android Studio uygulamayi calistiracak hedef cihaz bulamiyor.

Cozum:

1. Android Studio icinde `Tools > Device Manager` ac.
2. `Create Device` sec.
3. Ornek olarak `Pixel 6` sec.
4. `Next` de.
5. Bir `system image` indir.
6. Cihazi olusturup baslat.
7. Sonra ust bardan bu emulatoru secip `Run` de.

Eger `Create Device` adiminda image listesi bos gelirse:

1. `SDK Manager` ac.
2. `SDK Platforms` sekmesinden en az bir Android surumu sec.
3. `SDK Tools` sekmesinden gerekli emulator bilesenlerini kur.
4. Sonra tekrar `Device Manager` ekranina don.

Fiziksel telefon kullanacaksan:

1. telefonda gelistirici seceneklerini ac
2. USB debugging aktif et
3. telefonu kablo ile bagla
4. cihaz izin penceresini onayla
