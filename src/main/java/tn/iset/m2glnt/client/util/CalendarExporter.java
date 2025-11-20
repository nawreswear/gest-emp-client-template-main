package tn.iset.m2glnt.client.util;

import tn.iset.m2glnt.client.model.Slot;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarExporter {

    public static void exportToCSV(List<Slot> slots, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Description,Date,Début,Fin,Durée");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Slot slot : slots) {
                writer.printf("%d,%s,%s,%s,%s,%s%n",
                        slot.id(),
                        escapeCsv(slot.description()),
                        slot.startDateTime().toLocalDate().format(dateFormatter),
                        slot.startDateTime().toLocalTime().format(timeFormatter),
                        slot.startDateTime().plus(slot.duration()).toLocalTime().format(timeFormatter),
                        formatDuration(slot.duration())
                );
            }
            System.out.println("✅ Export CSV réussi: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Erreur export CSV: " + e.getMessage());
        }
    }

    public static void exportToHTML(List<Slot> slots, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Export Calendrier</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        table { border-collapse: collapse; width: 100%; }
                        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                        th { background-color: #E85B8A; color: white; }
                        tr:nth-child(even) { background-color: #f2f2f2; }
                        .header { color: #E85B8A; margin-bottom: 20px; }
                    </style>
                </head>
                <body>
                    <h1 class="header">📅 Calendrier des Cours</h1>
                    <table>
                        <tr>
                            <th>ID</th><th>Description</th><th>Date</th><th>Heure Début</th><th>Heure Fin</th><th>Durée</th>
                        </tr>
                """);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Slot slot : slots) {
                writer.printf("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
                    """,
                        slot.id(),
                        slot.description(),
                        slot.startDateTime().toLocalDate().format(dateFormatter),
                        slot.startDateTime().toLocalTime().format(timeFormatter),
                        slot.startDateTime().plus(slot.duration()).toLocalTime().format(timeFormatter),
                        formatDuration(slot.duration())
                );
            }

            writer.println("""
                    </table>
                    <p><em>Généré le %s</em></p>
                </body>
                </html>
                """.formatted(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

            System.out.println("✅ Export HTML réussi: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Erreur export HTML: " + e.getMessage());
        }
    }

    private static String escapeCsv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String formatDuration(java.time.Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return hours + "h" + (minutes > 0 ? minutes + "m" : "");
    }
}