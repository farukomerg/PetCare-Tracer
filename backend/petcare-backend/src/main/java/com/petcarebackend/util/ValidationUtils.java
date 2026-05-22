package com.petcarebackend.util;

/**
 * Ortak validasyon yardımcı metotlarını barındıran utility sınıfı.
 * Tüm servis sınıflarındaki isBlank() tekrarını ortadan kaldırır (DRY prensibi).
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class — instantiation engellenmiştir
    }

    /**
     * Verilen String değerin null ya da boş/boşluk karakterlerinden oluşup oluşmadığını döndürür.
     *
     * @param value kontrol edilecek String
     * @return değer null ya da blank ise {@code true}
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Verilen String değeri null değilse trim'ler; null ise null döndürür.
     *
     * @param value trim yapılacak String
     * @return trim edilmiş değer ya da null
     */
    public static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
