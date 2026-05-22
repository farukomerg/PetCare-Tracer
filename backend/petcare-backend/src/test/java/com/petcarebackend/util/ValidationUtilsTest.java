package com.petcarebackend.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * [RED] ValidationUtils için TDD testleri.
 * Bu testler ValidationUtils sınıfı yazılmadan önce kırmızı (fail) durumda olacak,
 * implementasyon tamamlandıktan sonra yeşile (pass) dönecektir.
 */
class ValidationUtilsTest {

    @Test
    void isBlankReturnsTrueForNull() {
        assertTrue(ValidationUtils.isBlank(null));
    }

    @Test
    void isBlankReturnsTrueForEmptyString() {
        assertTrue(ValidationUtils.isBlank(""));
    }

    @Test
    void isBlankReturnsTrueForWhitespaceOnly() {
        assertTrue(ValidationUtils.isBlank("   "));
    }

    @Test
    void isBlankReturnsFalseForNonEmptyString() {
        assertFalse(ValidationUtils.isBlank("petcare"));
    }

    @Test
    void trimOrNullReturnsNullForNull() {
        assertNull(ValidationUtils.trimOrNull(null));
    }

    @Test
    void trimOrNullTrimsWhitespace() {
        assertEquals("boncuk", ValidationUtils.trimOrNull("  boncuk  "));
    }
}
