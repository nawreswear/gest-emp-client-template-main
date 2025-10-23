package tn.iset.m2glnt.client.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tn.iset.m2glnt.client.util.DateInterval;
import tn.iset.m2glnt.client.util.DayGenerator;

import java.time.LocalDate;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.*;
class DayGeneratorTest {
    private static final LocalDate DATE_06_01_2025 = LocalDate.of(2025, 1, 6);
    private static final LocalDate DATE_11_01_2025 = LocalDate.of(2025, 1, 11);
    private static final LocalDate DATE_06_06_2026 = LocalDate.of(2026, 6, 6);
    private static final LocalDate DATE_10_07_2026 = LocalDate.of(2026, 7, 10);
    private DayGenerator generator06_01_to_11_01_2025;
    private DayGenerator generator06_06_to_10_07_2026;

    @BeforeEach
    void setUp() {
        generator06_01_to_11_01_2025 = new DayGenerator(new DateInterval(DATE_06_01_2025, DATE_11_01_2025));
        generator06_06_to_10_07_2026 = new DayGenerator(new DateInterval(DATE_06_06_2026, DATE_10_07_2026));
    }

    @Test
    void iterator() {
        Iterator<LocalDate> dayIterator = generator06_06_to_10_07_2026.iterator();
        for (int i = 0; i < 34; i++) {
            assertThat(dayIterator.hasNext()).isTrue();
            LocalDate date = dayIterator.next();
            assertThat(date).isEqualTo(DATE_06_06_2026.plusDays(i));
        }
    }

    @Test
    void getNumberOfDays() {
        assertThat(generator06_01_to_11_01_2025.getNumberOfDays()).isEqualTo(5);
        assertThat(generator06_06_to_10_07_2026.getNumberOfDays()).isEqualTo(34);
    }

    @Test
    void getStartDate() {
        assertThat(generator06_01_to_11_01_2025.getStartDate()).isEqualTo(DATE_06_01_2025);
        assertThat(generator06_06_to_10_07_2026.getStartDate()).isEqualTo(DATE_06_06_2026);
    }

    @Test
    void getEndDate() {
        assertThat(generator06_06_to_10_07_2026.getEndDate()).isEqualTo(DATE_10_07_2026);
        assertThat(generator06_01_to_11_01_2025.getEndDate()).isEqualTo(DATE_11_01_2025);
    }

    @Test
    void getDayIndex() {
        assertThat(generator06_01_to_11_01_2025.getDayIndex(LocalDate.of(2025, 1, 6)))
                .isEqualTo(0);
        assertThat(generator06_01_to_11_01_2025.getDayIndex(LocalDate.of(2025, 1, 8)))
                .isEqualTo(2);
        assertThat(generator06_06_to_10_07_2026.getDayIndex(LocalDate.of(2025, 1, 6)))
                .isEqualTo(-1);
    }

    @Test
    void testToString() {
        assertThat(generator06_01_to_11_01_2025.toString())
                .isEqualTo("DayGenerator{interval=DateInterval[start=2025-01-06, end=2025-01-11]}");
    }
}