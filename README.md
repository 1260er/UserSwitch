# UserSwitch

**English** | [Deutsch](README.de.md)

A tiny Android app that provides fast access to the native user switcher on GrapheneOS.

UserSwitch was created to provide a simple **1×1 launcher icon** for switching Android users without requiring the larger system multiuser widget on the home screen.

## Features

- Simple 1×1 launcher icon
- Opens the native GrapheneOS / Android user switcher
- Uses the original system Multiuser widget internally
- No root required
- No Shizuku required
- No ADB required
- No accessibility service
- No network access
- No privileged app permissions
- Material You monochrome icon support

## How it works

UserSwitch does not switch Android users itself.

Instead, it hosts the existing system Multiuser widget and triggers its user-switch action. The actual user switch is therefore still performed by the privileged Android system component.

This keeps the app small and avoids granting additional privileged access.

## First launch

On the first launch, Android may ask for permission to bind the system Multiuser widget to UserSwitch.

After this one-time setup, tapping the UserSwitch icon opens the native user switcher directly.

## Installation

Download the latest APK from:

[GitHub Releases](https://github.com/1260er/UserSwitch/releases)

The repository can also be added to **Obtainium** for update notifications and installation of future releases.

## Compatibility

UserSwitch is designed and tested for **GrapheneOS**.

Because it relies on the GrapheneOS / AOSP system Multiuser widget, compatibility with other Android distributions is not guaranteed.

## Privacy

UserSwitch:

- does not collect personal data
- does not contain analytics or telemetry
- does not require Internet access
- does not communicate with external services

Everything happens locally on the device.

## Releases

Current stable release: **v1.0.0**
