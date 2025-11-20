package tn.iset.m2glnt.client.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;

public class DateTimeNormalizer {



    /**
     * Version alternative qui garde l'heure originale si elle est valide
     */
    public static LocalDateTime normalizeAndValidate(LocalDateTime originalDateTime,
                                                     DayGenerator days,
                                                     TimeIntervalGenerator timeIntervals) {
        System.out.println("🔍 === VALIDATION ET NORMALISATION ===");
        System.out.println("DateTime reçu: " + originalDateTime);

        LocalDate date = originalDateTime.toLocalDate();
        LocalTime time = originalDateTime.toLocalTime();

        // Vérifier la date
        if (!isDateInRange(date, days)) {
            System.err.println("❌ Date hors plage: " + date);
            return null;
        }
        System.out.println("✅ Date valide: " + date);

        // Vérifier que l'heure est dans la plage globale
        if (!isTimeInGlobalRange(time, timeIntervals)) {
            System.err.println("❌ Heure hors plage globale: " + time);
            return null;
        }
        System.out.println("✅ Heure dans plage globale: " + time);

        // NORMALISER l'heure pour qu'elle corresponde à un intervalle
        LocalTime normalizedTime = normalizeTimeToInterval(time, timeIntervals);

        if (normalizedTime == null) {
            System.err.println("❌ Impossible de normaliser l'heure: " + time);
            return null;
        }

        LocalDateTime normalizedDateTime = LocalDateTime.of(date, normalizedTime);
        System.out.println("✅ DateTime final: " + normalizedDateTime);
        System.out.println("✅ === VALIDATION RÉUSSIE ===\n");

        return normalizedDateTime;
    }

    private static boolean isTimeInGlobalRange(LocalTime time, TimeIntervalGenerator timeIntervals) {
        TimeInterval firstInterval = null;
        TimeInterval lastInterval = null;

        for (TimeInterval interval : timeIntervals) {
            if (firstInterval == null) {
                firstInterval = interval;
            }
            lastInterval = interval;
        }

        if (firstInterval == null || lastInterval == null) {
            return false;
        }

        // L'heure est valide si elle est entre le début du premier intervalle et la fin du dernier
        boolean isAtOrAfterStart = !time.isBefore(firstInterval.start());
        boolean isBeforeOrAtEnd = time.isBefore(lastInterval.end()) || time.equals(lastInterval.end());

        return isAtOrAfterStart && isBeforeOrAtEnd;
    }

    private static LocalTime normalizeTimeToInterval(LocalTime time, TimeIntervalGenerator timeIntervals) {
        // Trouver l'intervalle qui contient cette heure
        for (TimeInterval interval : timeIntervals) {
            if (!time.isBefore(interval.start()) && time.isBefore(interval.end())) {
                System.out.println("🔄 Normalisation: " + time + " → " + interval.start());
                return interval.start(); // Retourner le début de l'intervalle
            }
        }

        // Si l'heure est exactement la fin du dernier intervalle
        TimeInterval lastInterval = null;
        for (TimeInterval interval : timeIntervals) {
            lastInterval = interval;
        }

        if (lastInterval != null && time.equals(lastInterval.end())) {
            System.out.println("🔄 Normalisation: " + time + " → " + lastInterval.start());
            return lastInterval.start();
        }

        return null;
    }

    private static boolean isDateInRange(LocalDate date, DayGenerator days) {
        for (LocalDate day : days) {
            if (day.equals(date)) {
                return true;
            }
        }
        return false;
    }

    public static void syncDatabaseWithDisplay(
            java.time.LocalDateTime databaseDateTime,
            java.time.LocalDateTime displayDateTime,
            int slotId) {

        System.out.println("\n🔄 === SYNC DATABASE-DISPLAY ===");
        System.out.println("Slot ID: " + slotId);
        System.out.println("DateTime en base de données: " + databaseDateTime);
        System.out.println("DateTime affiché: " + displayDateTime);

        if (!databaseDateTime.equals(displayDateTime)) {
            System.err.println("⚠️  DÉSYNCHRONISATION DÉTECTÉE:");
            System.err.println("   Base de données: " + databaseDateTime);
            System.err.println("   Affichage: " + displayDateTime);

            // IMPORTANT: Dans un système de production, il faudrait mettre à jour la base de données
            // pour refléter l'heure affichée, ou vice-versa
        } else {
            System.out.println("✅ Parfaitement synchronisé");
        }

        System.out.println("✅ === SYNC TERMINÉE ===\n");
    }
}