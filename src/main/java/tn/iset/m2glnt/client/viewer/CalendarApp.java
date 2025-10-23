package tn.iset.m2glnt.client.viewer;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleCalendar;
import tn.iset.m2glnt.client.service.dao.SimpleCalendarServiceDAO;
import tn.iset.m2glnt.client.viewer.controller.CalendarViewController;
import tn.iset.m2glnt.client.viewer.controller.CalendarWeekViewConfiguration;
import tn.iset.m2glnt.client.viewer.controller.PeriodCalendarViewController;

import java.time.Duration;
import java.time.LocalTime;

public class CalendarApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Calendar calendar = new SimpleCalendar(new SimpleCalendarServiceDAO());
        CalendarViewController calendarController = new PeriodCalendarViewController(calendar,
                new CalendarWeekViewConfiguration(LocalTime.of(8,0), LocalTime.of(19,0),
                        Duration.ofMinutes(60), Duration.ofHours(4), Duration.ofHours(1), Color.AQUA));
        primaryStage.setScene(calendarController.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

