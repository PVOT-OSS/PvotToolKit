<!--
SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
SPDX-License-Identifier: Apache-2.0
-->

# Privacy Policy — Toolkit

_Effective 2 November 2026_

Toolkit (package `org.prauga.toolkit`) is the companion app for Team
StormBreaker's kernels, published by Saalim Quadri for PVOT-OSS. This policy explains what the app does with
your data.

**In short: the app doesn't collect, send, share or sell any personal data. It has no accounts, no
ads, no analytics and no tracking. Everything it reads about your device stays on your device.**

## What the app reads on your device

- **Device and kernel information**, to show it to you: the device model, Android version and
  build, kernel version, processor layout and frequencies, GPU, display, memory, storage, battery
  state, the list of sensors, camera characteristics, media codecs, and the boot state (bootloader
  lock, verified boot, encryption, whether a `su` binary is present). All of it comes from Android's
  public APIs and from files any app can read; the app does not ask for root. These readings are
  shown on screen and are not stored or sent anywhere, except as described for the battery monitor
  below.
- **Not read:** your contacts, location, files, accounts, or any identifier such as a serial
  number, IMEI or advertising ID.

## The battery monitor

The battery monitor is off by default. If you turn it on in More, the app samples the battery level
and current in the background and shows the discharge rate in a persistent notification. To do
that it keeps the current monitoring session and the last sample in its private storage, restarts
after a reboot or an app update, and stops when you turn it off. Nothing it records leaves your
device.

## What the app keeps on your device

- **Your settings:** your theme choice.
- **The battery monitor's session and last sample**, while the monitor is on.
- **A cache of what it downloads:** the newsroom feed, the links shown in More, and news cover
  images, so it doesn't download them again every time and can show them offline.

None of this leaves your device, except through Android's own backup and device transfer if you
use them: your settings may then be backed up to your Google account, under Google's terms, or
copied to a new device. The battery monitor's data and the download cache are never included.
Clearing the app's storage or uninstalling it deletes all of it.

## What the app downloads

The app uses the internet only to show public content from the StormBreaker website:

| Service | What for |
|---|---|
| `stormbreaker.squadri.me` | The newsroom posts and their cover images, and the community, contribution, project and legal links shown in More |

These requests send nothing about you beyond what any app or browser sends to load a page: your IP
address, the time, and the app's user agent (`Toolkit-Android`). The website may log them
under its own policy, at https://stormbreaker.squadri.me/privacy/. The app never signs in anywhere
and sends no account details and nothing it read about your device.

Links in the app, such as a news post or the community pages, open in your browser and are
covered by that website's own policy.

## Permissions

- `INTERNET`, to download the content above.
- `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`, to run the battery monitor only while
  you have it turned on.
- `POST_NOTIFICATIONS`, asked when you turn the battery monitor on, for its notification only.
- `RECEIVE_BOOT_COMPLETED`, to restart the battery monitor after a reboot if it was on.

The app asks for no other permission and does not request root.

## Children

The app is not directed at children and collects no data from anyone, children included.

## Changes

If the app starts handling data differently — for example when it gains kernel controls that need
root — this policy will be updated before that version is released, with a new effective date at
the top.

## Contact

Questions about this policy: danascape@gmail.com, or open an issue at
https://github.com/PVOT-OSS/ToolKit/issues.
