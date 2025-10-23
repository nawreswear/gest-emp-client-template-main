package tn.iset.m2glnt.client.viewer.presenter.dialog;

import javafx.util.StringConverter;

import java.time.Duration;

public class DurationStringConverter extends StringConverter<Duration> {
    @Override
    public String toString(Duration object) {
        if (object == null)
            return "";
        return object.toString().substring(2).toLowerCase();
    }

    @Override
    public Duration fromString(String string) {
        return Duration.parse(("PT"+string).toUpperCase());
    }
}
