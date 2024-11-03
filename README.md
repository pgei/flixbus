Gruppenprojekt MLG5008 - Flixbus

Beschreibung der Anwendung:

Die Anwendung die in diesem Projekt erstellt wurde, stellt ein Buchungssystem für den Vertrieb von Transport-Dienstleistungen dar. Ziel ist dabei, dass die Anwendung angebotene Transporte konsistent verwaltet und bei Verfügbarkeit Kunden zum Kauf zur Verfügung stellt, wobei Transporte hinzukommen und entfallen können sowie Kunden bereits gebuchte Tickets stornieren können.

Die Nutzung der Anwendung erfordert zunächst erstmal eine Anmeldung, wodurch die Berechtigungen des Users festgelegt werden. Sollte kein Account bestehen, kann einer erstellt werden. Es gibt zwei Klassen von Usern: Administrator und Kunde. Unabhängig von der Klasse kann ein User folgende Funktionalität nutzen:

Anzeigen aller Reiseziele
Anzeigen aller angebotenen Transporte
Filtern der angebotenen Transporte nach den Kriterien Datum, Preis, (Abfahrts-/Ankunfts-)Ort, (Abfahrts-/Ankunfts-)Zeit
Kunden haben ein Guthaben und könne sich mit diesem Guthaben Plätze bei einem angebotenen Transport zwischen zwei Orten kaufen. Falls es noch verfügbare Plätze gibt und das Guthaben reicht, wird ein Platz auf dem Transport reserviert, ein Ticket erstellt und das Guthaben entsprechend reduziert. Eine Kunde kann ein bereits gekauftes Ticket aber wieder stonieren und entsprechend Guthaben zurückerstattet bekommen und sein Guthaben auch auffüllen. Soit ergeben sich folgende zusätzliche Funktionalitäten für den Kunden:

Guthaben abfragen und aufladen
Ticket für Transport kaufen
Alle gebuchten Tickets anzeigen
Ticket stornieren
Administratoren können Transporte in der Anwendung erstellen und verwalten: sie können neue Transporte zwischen zwei Orten sowie neue Orte hinzufügen und Transporte aus dem System entfernen, allerdings nur solche die sie selber irgendwann erstellt haben. Nach erfolgter Anmeldung ergeben sich somit folgende zusätzliche Funktionalitäten für Administratoren:

Hinzufügen eines Ortes
Hinzufügen eines neuen Transportes zwischen zwei Orten
Anzeigen aller Tickets für einen ausgewählten Transport
Annulierung eines verwalteten Transports (mit entsprechender Stornierung aller gebuchten Tickets)
Klassendiagramm:

<img width="373" alt="Klassendiagramm" src="https://github.com/user-attachments/assets/83df1fa8-913b-4172-88fc-3de961018d8c">
