# UserSwitch

[English](README.md) | **Deutsch**

Eine kleine Android-App für den schnellen Zugriff auf den nativen Benutzerwechsler von GrapheneOS.

UserSwitch wurde entwickelt, um ein einfaches **1×1-App-Symbol** zum Wechseln von Android-Benutzern bereitzustellen, ohne das größere Multiuser-Systemwidget auf dem Startbildschirm verwenden zu müssen.

## Funktionen

- Einfaches 1×1-App-Symbol
- Öffnet den nativen Benutzerwechsler von GrapheneOS / Android
- Verwendet intern das originale Multiuser-Systemwidget
- Kein Root erforderlich
- Kein Shizuku erforderlich
- Kein ADB erforderlich
- Kein Bedienungshilfen-Dienst erforderlich
- Kein Netzwerkzugriff
- Keine privilegierten App-Berechtigungen
- Unterstützung für monochrome Material-You-Icons

## Funktionsweise

UserSwitch führt den eigentlichen Benutzerwechsel nicht selbst durch.

Stattdessen hostet die App das vorhandene Multiuser-Systemwidget und löst dessen Benutzerwechsel-Aktion aus. Der eigentliche Benutzerwechsel wird dadurch weiterhin von der privilegierten Android-Systemkomponente ausgeführt.

Dadurch bleibt die App klein und benötigt keine zusätzlichen privilegierten Zugriffe.

## Erster Start

Beim ersten Start kann Android einmalig nach der Erlaubnis fragen, das Multiuser-Systemwidget mit UserSwitch zu verbinden.

Nach dieser einmaligen Einrichtung öffnet ein Tipp auf das UserSwitch-Symbol direkt den nativen Benutzerwechsler.

## Installation

Die aktuelle APK kann hier heruntergeladen werden:

[GitHub Releases](https://github.com/1260er/UserSwitch/releases)

Das Repository kann außerdem in **Obtainium** eingebunden werden, um über neue Versionen informiert zu werden und Updates zu installieren.

## Kompatibilität

UserSwitch wurde für **GrapheneOS** entwickelt und dort getestet.

Da die App das Multiuser-Systemwidget von GrapheneOS / AOSP verwendet, kann die Funktion auf anderen Android-Systemen nicht garantiert werden.

## Datenschutz

UserSwitch:

- sammelt keine persönlichen Daten
- enthält keine Analyse- oder Telemetriedienste
- benötigt keinen Internetzugriff
- kommuniziert nicht mit externen Diensten

Alle Funktionen werden ausschließlich lokal auf dem Gerät ausgeführt.

## Releases

Aktuelle stabile Version: **v1.0.0**
