/**
 * PetCare-Tracer — k6 Kırılma (Stress / Breaking Point) Testi
 *
 * Amaç: API'nin kırılma noktasını bulmak için VU sayısını kademeli olarak artırıp
 *       hataların ve gecikmenin ne zaman kritik eşiği aştığını tespit etmek.
 *
 * Beklenen davranış:
 *   - Düşük yük  (≤20 VU): %0 hata, p95 < 500ms
 *   - Orta yük   (≤50 VU): %0 hata, p95 < 1000ms
 *   - Yüksek yük (≤100 VU): hata oranı < %5 kabul edilebilir
 *   - Kırılma    (>100 VU): sunucu zorlanmaya başlar
 *
 * Çalıştırma:
 *   k6 run tests/k6/stress-test.js
 *   docker compose --profile loadtest run --rm k6 run /scripts/stress-test.js
 */
import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend } from "k6/metrics";

// Özel metrik: hata oranı
const errorRate = new Rate("error_rate");
// Özel metrik: yanıt süresi trendi (tek endpoint için ayrı izleme)
const petListDuration = new Trend("pet_list_duration", true);

export const options = {
  stages: [
    // Isınma
    { duration: "30s", target: 10 },
    // Normal yük
    { duration: "30s", target: 25 },
    // Yoğun yük
    { duration: "30s", target: 50 },
    // Stres noktası
    { duration: "30s", target: 100 },
    // Kırılma testi — maksimum baskı
    { duration: "30s", target: 150 },
    // Soğuma — sistemin toparlanmasını izle
    { duration: "30s", target: 0 },
  ],

  thresholds: {
    // Genel başarı eşiği: hata oranı %10'un altında olmalı
    http_req_failed: ["rate<0.10"],
    // p95 yanıt süresi: 3 saniyenin altında olmalı
    http_req_duration: ["p(95)<3000"],
    // Özel metrik: hata oranı %10 altında
    error_rate: ["rate<0.10"],
    // /api/pets özel trendi
    pet_list_duration: ["p(95)<3000"],
  },
};

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

/**
 * VU başına çalışan test fonksiyonu.
 * Her iterasyonda birden fazla endpoint'e istek atılır.
 */
export default function () {
  const params = {
    headers: { "Content-Type": "application/json" },
    timeout: "10s",
  };

  // --- Endpoint 1: Evcil hayvan listesi ---
  const petRes = http.get(`${baseUrl}/api/pets`, params);
  const petOk = check(petRes, {
    "GET /api/pets — status 200": (r) => r.status === 200,
    "GET /api/pets — response not empty": (r) => r.body && r.body.length > 0,
  });
  errorRate.add(!petOk);
  petListDuration.add(petRes.timings.duration);

  // --- Endpoint 2: Aşı listesi ---
  const vaccineRes = http.get(`${baseUrl}/api/vaccines`, params);
  check(vaccineRes, {
    "GET /api/vaccines — status 200": (r) => r.status === 200,
  });
  errorRate.add(vaccineRes.status !== 200);

  // --- Endpoint 3: İlaç listesi ---
  const medRes = http.get(`${baseUrl}/api/medications`, params);
  check(medRes, {
    "GET /api/medications — status 200": (r) => r.status === 200,
  });
  errorRate.add(medRes.status !== 200);

  // --- Endpoint 4: Randevu listesi ---
  const apptRes = http.get(`${baseUrl}/api/appointments`, params);
  check(apptRes, {
    "GET /api/appointments — status 200": (r) => r.status === 200,
  });
  errorRate.add(apptRes.status !== 200);

  // --- Endpoint 5: Hatırlatma listesi ---
  const reminderRes = http.get(`${baseUrl}/api/reminders`, params);
  check(reminderRes, {
    "GET /api/reminders — status 200": (r) => r.status === 200,
  });
  errorRate.add(reminderRes.status !== 200);

  // --- Endpoint 6: Kullanıcı listesi (yazım yetkisi gerektirmez) ---
  const userRes = http.get(`${baseUrl}/api/users`, params);
  check(userRes, {
    "GET /api/users — status 200": (r) => r.status === 200,
  });
  errorRate.add(userRes.status !== 200);

  sleep(0.5);
}

/**
 * Test tamamlandığında özet yazdır.
 */
export function handleSummary(data) {
  const summary = {
    testName: "PetCare-Tracer Stress Test",
    totalRequests: data.metrics.http_reqs ? data.metrics.http_reqs.values.count : "N/A",
    errorRate: data.metrics.error_rate ? data.metrics.error_rate.values.rate.toFixed(4) : "N/A",
    p95Duration: data.metrics.http_req_duration
      ? data.metrics.http_req_duration.values["p(95)"].toFixed(2) + "ms"
      : "N/A",
    maxVUs: 150,
    timestamp: new Date().toISOString(),
  };

  return {
    stdout: JSON.stringify(summary, null, 2) + "\n",
  };
}
