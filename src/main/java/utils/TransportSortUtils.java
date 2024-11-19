package main.java.utils;

import main.java.model.Transport;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TransportSortUtils {

    /**
     * Sortiert eine Liste von Transporten nach Datum in aufsteigender Reihenfolge.
     *
     * @param transports Liste der zu sortierenden Transporte.
     * @return Sortierte Liste der Transporte nach Datum.
     */
    public static List<Transport> sortTransportsByDate(List<Transport> transports) {
        return transports.stream()
                .sorted(Comparator.comparing(Transport::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Sortiert eine Liste von Transporten nach der Transportdauer in absteigender Reihenfolge.
     *
     * @param transports Liste der zu sortierenden Transporte.
     * @return Sortierte Liste der Transporte nach Dauer (absteigend).
     */
    public static List<Transport> sortTransportsByDuration(List<Transport> transports) {
        return transports.stream()
                .sorted(Comparator.comparingLong(TransportSortUtils::calculateDuration).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Berechnet die Dauer eines Transports in Minuten.
     *
     * @param transport Der zu berechnende Transport.
     * @return Die Dauer des Transports in Minuten.
     */
    private static long calculateDuration(Transport transport) {
        return java.time.Duration.between(transport.getDepartureTime(), transport.getArrivalTime()).toMinutes();
    }
}
