package com.petcareadmin.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petcareadmin.model.AppointmentItem;
import com.petcareadmin.model.PetItem;
import com.petcareadmin.model.ReminderItem;
import com.petcareadmin.model.UserItem;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class BackendApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String baseUrl;

    public BackendApiClient(String baseUrl) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.baseUrl = sanitizeBaseUrl(baseUrl);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = sanitizeBaseUrl(baseUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<UserItem> getUsers() throws IOException, InterruptedException {
        return getData("/api/users", new TypeReference<ApiEnvelope<List<UserItem>>>() {});
    }

    public List<PetItem> getPets() throws IOException, InterruptedException {
        return getData("/api/pets", new TypeReference<ApiEnvelope<List<PetItem>>>() {});
    }

    public List<AppointmentItem> getAppointments() throws IOException, InterruptedException {
        return getData("/api/appointments", new TypeReference<ApiEnvelope<List<AppointmentItem>>>() {});
    }

    public List<ReminderItem> getReminders() throws IOException, InterruptedException {
        return getData("/api/reminders", new TypeReference<ApiEnvelope<List<ReminderItem>>>() {});
    }

    private <T> T getData(String path, TypeReference<ApiEnvelope<T>> typeReference) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("API request failed: " + response.statusCode() + " -> " + path);
        }

        ApiEnvelope<T> envelope = objectMapper.readValue(response.body(), typeReference);
        if (!envelope.success()) {
            throw new IOException("API returned failure: " + envelope.message());
        }
        return envelope.data();
    }

    private String sanitizeBaseUrl(String rawBaseUrl) {
        if (rawBaseUrl == null || rawBaseUrl.isBlank()) {
            return "http://localhost:8080";
        }
        String trimmed = rawBaseUrl.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }
}
