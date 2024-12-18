<ins>Gruppenprojekt MLG5008 - Flixbus</ins>

**Beschreibung der Anwendung:**

Die Anwendung die in diesem Projekt erstellt wurde, stellt ein Buchungssystem für den Vertrieb von Transport-Dienstleistungen dar. Ziel ist dabei, dass die Anwendung angebotene Transporte konsistent verwaltet und bei Verfügbarkeit Kunden zum Kauf zur Verfügung stellt, wobei Transporte hinzukommen und entfallen können sowie Kunden bereits gebuchte Tickets stornieren können.

Die Nutzung der Anwendung erfordert zunächst erstmal eine Anmeldung, wodurch die Berechtigungen des Users festgelegt werden. Sollte kein Account bestehen, kann einer erstellt werden. Es gibt zwei Klassen von Usern: Administrator und Kunde. Unabhängig von der Klasse kann ein User folgende Funktionalität nutzen:

- Anzeigen aller Reiseziele
- Anzeigen aller angebotenen Transporte
- Sortieren aller Transporte nach Datum oder Fahrtdauer
- Filtern der angebotenen Transporte nach den Kriterien Abfahrts- und/oder Ankunftsort oder maximaler Ticketpreis

Kunden haben ein Guthaben und könne sich mit diesem Guthaben Plätze bei einem angebotenen Transport zwischen zwei Orten kaufen. Falls es noch verfügbare Plätze gibt und das Guthaben reicht, wird ein Platz auf dem Transport reserviert, ein Ticket erstellt und das Guthaben entsprechend reduziert. Eine Kunde kann ein bereits gekauftes Ticket aber wieder stonieren und entsprechend Guthaben zurückerstattet bekommen und sein Guthaben auch auffüllen. Somit ergeben sich folgende zusätzliche Funktionalitäten für den Kunden:

- Guthaben abfragen und aufladen
- Ticket für Transport kaufen
- Alle gebuchten Tickets anzeigen
- Ticket stornieren

Administratoren können Transporte in der Anwendung erstellen und verwalten: sie können neue Transporte zwischen zwei Orten sowie neue Orte hinzufügen und Transporte aus dem System entfernen, allerdings nur solche die sie selber irgendwann erstellt haben.  Nach erfolgter Anmeldung ergeben sich somit folgende zusätzliche Funktionalitäten für Administratoren:

- Hinzufügen eines Ortes
- Hinzufügen eines neuen Transportes zwischen zwei Orten
- Anzeigen aller Tickets für einen ausgewählten Transport
- Annulierung eines verwalteten Transports (mit entsprechender Stornierung aller gebuchten Tickets)

Transporte können sowohl für Züge als auch Busse erstellt werden, der Unterschied besteht in der Tatsache dass Busse nur eine Klasse an Sitzplätzen haben, während Züge zwei Klassen bieten (1. und 2. Klasse). Entsprechend gibt es auch eine Unterscheidung bei den Tickets in Bus- und Zugtickets.

**Klassendiagramm:**

![bild](https://github.com/user-attachments/assets/d6439761-b7dc-4296-88bb-0d998eb8eda5)



