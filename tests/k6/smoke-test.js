import http from "k6/http";
import { check } from "k6";

export const options = {
  vus: 1,
  iterations: 1,
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<1500"],
  },
};

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const endpoints = [
    "/test/db",
    "/api/users",
    "/api/pets",
    "/api/vaccines",
    "/api/medications",
    "/api/appointments",
    "/api/reminders",
  ];

  endpoints.forEach((path) => {
    const response = http.get(`${baseUrl}${path}`);
    check(response, {
      [`${path} status is 200`]: (r) => r.status === 200,
    });
  });
}
