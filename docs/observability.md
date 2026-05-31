# Gözlemlenebilirlik (Observability) Rehberi

> **Araçlar:** Prometheus · Grafana · Spring Boot Actuator · Micrometer

---

## Mimarisi

```
Spring Boot API
  └── /actuator/prometheus  ──scrape(15s)──►  Prometheus :9090
                                                    │
                                                    └──datasource──►  Grafana :3000
                                                                          │
                                                                          └──► PetCare Dashboard
```

---

## Hızlı Başlangıç

```bash
docker compose up --build
```

| Servis | URL |
|--------|-----|
| Backend Health | http://localhost:8080/actuator/health |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 (admin / admin123) |

---

## Spring Boot Actuator Endpoint'leri

| Endpoint | Açıklama |
|----------|----------|
| `/actuator/health` | `{"status":"UP"}` — sağlık kontrolü |
| `/actuator/prometheus` | Prometheus format metrikleri |
| `/actuator/metrics` | Tüm metrik isimleri (JSON) |
| `/actuator/metrics/{name}` | Belirli bir metrik değeri |

**`application.properties` konfigürasyonu:**
```properties
management.endpoints.web.exposure.include=health,prometheus,metrics
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

---

## Prometheus Yapılandırması

**`monitoring/prometheus/prometheus.yml`:**
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: "petcare-backend"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["backend:8080"]
```

### Faydalı PromQL Sorguları

```promql
# Toplam istek sayısı (dakika başına rate)
rate(http_server_requests_seconds_count[1m])

# Endpoint bazında p95 yanıt süresi
histogram_quantile(0.95,
  rate(http_server_requests_seconds_bucket[5m])
)

# Hata istekleri (4xx + 5xx)
rate(http_server_requests_seconds_count{status=~"4..|5.."}[1m])

# JVM heap kullanımı (MB)
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# Aktif DB bağlantı sayısı
hikaricp_connections_active

# JVM GC pause süresi
rate(jvm_gc_pause_seconds_sum[1m])

# CPU kullanımı (0-1 arası)
process_cpu_usage
```

---

## Grafana Dashboard

Dashboard **otomatik olarak** provision edilir — elle oluşturmanız gerekmez.

**`monitoring/grafana/provisioning/datasources/prometheus.yml`:**
```yaml
apiVersion: 1
datasources:
  - name: PetCare Prometheus
    type: prometheus
    url: http://prometheus:9090
    isDefault: true
```

**`monitoring/grafana/provisioning/dashboards/dashboard.yml`:**
```yaml
apiVersion: 1
providers:
  - name: PetCare Dashboards
    type: file
    options:
      path: /var/lib/grafana/dashboards
```

### Dashboard Panelleri

| Panel | Metrik | Tip |
|-------|--------|-----|
| HTTP İstek Oranı | `rate(http_server_requests_seconds_count[1m])` | Zaman serisi |
| p95 Yanıt Süresi | `histogram_quantile(0.95, ...)` | Gauge |
| Hata Oranı (4xx/5xx) | `rate(...{status=~"4..|5.."}[1m])` | Stat |
| JVM Heap | `jvm_memory_used_bytes{area="heap"}` | Zaman serisi |
| CPU Kullanımı | `process_cpu_usage * 100` | Gauge |
| Aktif DB Bağlantı | `hikaricp_connections_active` | Stat |

---

## k6 + Grafana Birlikte Kullanımı

k6 testi çalışırken Grafana'yı açık tutun — metriklerin anlık değişimini izleyin:

```bash
# Terminal 1 — sistem çalışıyor
docker compose up

# Terminal 2 — yük testi
k6 run tests/k6/stress-test.js

# Tarayıcı — anlık izleme
http://localhost:3000
```

Stres testi sırasında Grafana'da:
- **HTTP istek oranı** → VU artışıyla artar
- **p95 yanıt süresi** → 50+ VU'da belirgin şekilde yükselir
- **JVM heap** → Artan yükle bellek kullanımı artar
- **Hata oranı** → 120+ VU'da görünür hale gelir

---

## Sorun Giderme

| Sorun | Kontrol |
|-------|---------|
| Prometheus hedef DOWN | `http://localhost:9090/targets` — backend erişilebilir mi? |
| Grafana boş grafik | Prometheus datasource bağlantısını test et |
| Actuator 404 | `application.properties` expose ayarları kontrol et |
| Docker metrikleri yok | `docker compose ps` — tüm servisler UP mu? |
