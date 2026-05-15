package com.petcareadmin.client;

public record ApiEnvelope<T>(boolean success, String message, T data) {
}
