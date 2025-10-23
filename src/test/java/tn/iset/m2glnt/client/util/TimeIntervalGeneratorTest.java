package tn.iset.m2glnt.client.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;

import static org.assertj.core.api.Assertions.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Iterator;

class TimeIntervalGeneratorTest {
    private static final LocalTime SIX = LocalTime.of(6, 0, 0);
    private static final LocalTime SEVEN = LocalTime.of(7, 0, 0);
    private static final LocalTime EIGHT = LocalTime.of(8, 0, 0);
    private static final LocalTime NINE = LocalTime.of(9, 0, 0);
    private static final LocalTime TEN = LocalTime.of(10, 0, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0, 0);
    private static final Duration ONE_HOUR = Duration.ofHours(1);
    private static final Duration ONE_QUARTER = Duration.ofMinutes(15);

    private TimeIntervalGenerator sixToTenByHours;
    private TimeIntervalGenerator nineToNoonByQuarters;

    @BeforeEach
    void testSetUp() {
        sixToTenByHours = new TimeIntervalGenerator(SIX, TEN, ONE_HOUR);
        nineToNoonByQuarters = new TimeIntervalGenerator(NINE, NOON, ONE_QUARTER);
    }


    @Test
    void testGetIntervalDuration() {
        assertThat(sixToTenByHours.getIntervalDuration()).isEqualTo(ONE_HOUR);
        assertThat(nineToNoonByQuarters.getIntervalDuration()).isEqualTo(ONE_QUARTER);
    }

    @Test
    void testGetStartTime() {
        assertThat(sixToTenByHours.getStartTime()).isEqualTo(SIX);
        assertThat(nineToNoonByQuarters.getStartTime()).isEqualTo(NINE);
    }

    @Test
    void testGetEndTime() {
        assertThat(sixToTenByHours.getEndTime()).isEqualTo(TEN);
        assertThat(nineToNoonByQuarters.getEndTime()).isEqualTo(NOON);
    }

    @Test
    void testGetTimeIntervals() {
        assertThat(nineToNoonByQuarters.getTimeIntervals())
                .hasSize(12)
                .contains(new TimeInterval(NINE, NINE.plus(ONE_QUARTER)),
                            new TimeInterval(TEN, TEN.plus(ONE_QUARTER)));
        assertThat(sixToTenByHours.getTimeIntervals())
                .hasSize(4)
                .containsExactly(new TimeInterval(SIX, SEVEN), new TimeInterval(SEVEN, EIGHT),
                        new TimeInterval(EIGHT, NINE), new TimeInterval(NINE, TEN));
    }

    @Test
    void testGetNumberOfIntervals() {
        assertThat(sixToTenByHours.getNumberOfIntervals()).isEqualTo(4);
        assertThat(nineToNoonByQuarters.getNumberOfIntervals()).isEqualTo(12);
    }

    @Test
    void testGetTimeIndex() {
        assertThat(sixToTenByHours.getTimeIndex(EIGHT)).isEqualTo(2);
        assertThat(nineToNoonByQuarters.getTimeIndex(NINE)).isEqualTo(0);
    }

    @Test
    void testGetStartTimesOfIntervals() {
        assertThat(sixToTenByHours.getStartTimesOfIntervals())
                .hasSize(4)
                .containsExactly(SIX, SEVEN, EIGHT, NINE);
        assertThat(nineToNoonByQuarters.getStartTimesOfIntervals())
                .hasSize(12)
                .contains(NINE, TEN, TEN.plus(ONE_QUARTER), TEN.plus(ONE_QUARTER.multipliedBy(3)));
    }

    @Test
    void testIterator() {
        Iterator<TimeInterval> iterator = nineToNoonByQuarters.iterator();
        for (int i = 0; i < 12; i++) {
            assertThat(iterator.hasNext()).isTrue();
            TimeInterval timeInterval = iterator.next();
            LocalTime expectedStart = NINE.plus(ONE_QUARTER.multipliedBy(i));
            LocalTime expectedEnd = expectedStart.plus(ONE_QUARTER);
            assertThat(timeInterval.start()).isEqualTo(expectedStart);
            assertThat(timeInterval.end()).isEqualTo(expectedEnd);
        }
    }
}