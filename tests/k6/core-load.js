import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "20s", target: 5 },
    { duration: "40s", target: 15 },
    { duration: "20s", target: 0 },
  ],
  thresholds: {
    http_req_failed: ["rate<0.02"],
    http_req_duration: ["p(95)<2000"],
  },
};

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const responses = [
    http.get(`${baseUrl}/api/pets`),
    http.get(`${baseUrl}/api/vaccines`),
    http.get(`${baseUrl}/api/medications`),
    http.get(`${baseUrl}/api/appointments`),
  ];

  responses.forEach((response) => {
    check(response, {
      "status is 200": (r) => r.status === 200,
    });
  });

  sleep(1);
}
