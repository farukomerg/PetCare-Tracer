# Performance Testing

Bu klasordeki `k6` scriptleri backend API'sinin temel performansini olcmek icin hazirlandi.

## Dosyalar

- `tests/k6/smoke-test.js`
  Amaci backend ayakta mi ve temel endpointler cevap veriyor mu kontrol etmek.
- `tests/k6/core-load.js`
  Amaci ana listeleme endpointlerinde hafif yuk testi yapmaktir.

Grafana ile birlikte izleme icin:

- [observability.md](/C:/Users/MSI/Desktop/PetCare-Tracer/docs/observability.md)

## Calistirma

Backend calisir durumdayken:

```bash
k6 run tests/k6/smoke-test.js
k6 run tests/k6/core-load.js
```

Docker stack uzerinden:

```bash
docker compose run --rm --profile loadtest k6 run /scripts/core-load.js
```

Farkli bir URL kullanmak icin:

```bash
k6 run -e BASE_URL=http://localhost:8080 tests/k6/smoke-test.js
```

## Onerilen Raporlama

Raporunda su bilgileri paylas:

- kullanilan ortam
- test senaryosu
- VU sayisi
- ortalama cevap suresi
- p95 cevap suresi
- hata orani
