package tn.iset.m2glnt.client.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tn.iset.m2glnt.client.util.DurationGenerator;

import java.time.Duration;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.*;

class DurationGeneratorTest {
    private static final Duration TEN_HOURS = Duration.ofHours(10);
    private static final Duration FOUR_HOURS = Duration.ofHours(4);
    private static final Duration ONE_HOUR = Duration.ofHours(1);
    private static final Duration ONE_QUARTER = Duration.ofMinutes(15);

    private DurationGenerator hoursUptoTenHours;
    private DurationGenerator quartersUptoFourHours;

    @BeforeEach
    void setUp() {
        hoursUptoTenHours = new DurationGenerator(ONE_HOUR, TEN_HOURS);
        quartersUptoFourHours = new DurationGenerator(ONE_QUARTER, FOUR_HOURS);
    }


    @Test
    void iterator() {
        Iterator<Duration> iterator = hoursUptoTenHours.iterator();
        for(int i = 1; i <= 10; i++){
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isEqualTo(Duration.ofHours(i));
        }
    }

    @Test
    void getDurations() {
        assertThat(hoursUptoTenHours.getDurations()).contains(ONE_HOUR, FOUR_HOURS);
        assertThat(quartersUptoFourHours.getDurations())
                .contains(ONE_QUARTER, ONE_QUARTER.multipliedBy(3), FOUR_HOURS);
    }

    @Test
    void getDurationIndex() {
        assertThat(hoursUptoTenHours.getDurationIndex(ONE_HOUR)).isEqualTo(0);
        assertThat(hoursUptoTenHours.getDurationIndex(FOUR_HOURS)).isEqualTo(3);
        assertThat(hoursUptoTenHours.getDurationIndex(ONE_QUARTER)).isEqualTo(-1);
        assertThat(quartersUptoFourHours.getDurationIndex(ONE_QUARTER)).isEqualTo(0);
        assertThat(quartersUptoFourHours.getDurationIndex(ONE_HOUR)).isEqualTo(3);
    }
}