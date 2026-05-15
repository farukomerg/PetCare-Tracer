# Observability

Bu kurulum ile backend Prometheus metrikleri uretir, Prometheus bunlari toplar ve Grafana hazir dashboard ile gosterir.

## Servisler

- Backend API: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

Grafana giris bilgileri:

- kullanici: `admin`
- sifre: `admin123`

## Calistirma

Kok klasorde:

```bash
docker compose up --build
```

## k6 Ile Yuk Testi

Docker profili ile:

```bash
docker compose run --rm --profile loadtest k6 run /scripts/core-load.js
```

Yerel kurulum ile:

```bash
k6 run tests/k6/core-load.js
```

## Grafana Dashboard

Hazir dashboard dosyasi:

- `monitoring/grafana/dashboards/petcare-overview.json`

Gosterilen ana veriler:

- backend ayakta mi
- uptime
- JVM heap kullanimi
- istek hizi
- p95 cevap suresi
