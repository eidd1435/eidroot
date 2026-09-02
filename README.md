# EID ROOT

تطبيق أندرويد لتثبيت إضافات Zygisk بصلاحية الروت، من تطوير **أبو أيوب**.

## Download

[Download the latest EID ROOT APK](https://github.com/eidd1435/eidroot/releases/latest)

## Features

- فحص وطلب صلاحية الروت من Magisk أو KernelSU.
- اختيار الإضافات المطلوب تثبيتها.
- تنزيل أحدث إصدار Release تلقائيًا من GitHub.
- دعم ReZygisk وVector وNoHello وHMA-OSS Zygisk.
- قسم مستقل لتنزيل وتثبيت KernelSU Manager وGpsSetter وMagisk وHMA-OSS Manager.
- تنزيل التطبيقات وفتح مثبت أندرويد العادي بدون صلاحيات الروت.
- واجهة عربية مع عرض حالة التنزيل والتثبيت.

## Behavior

Press **Install Vector** once. The app then:

1. Checks internet connectivity.
2. Requests root through Magisk by running `su`.
3. Downloads ReZygisk to `/sdcard/Download/ReZygisk.zip`.
4. Installs ReZygisk with Magisk or KernelSU.
5. Downloads Vector to `/sdcard/Download/Vector.zip`.
6. Installs Vector with Magisk or KernelSU.
7. Downloads NoHello to `/sdcard/Download/NoHello.zip`.
8. Installs NoHello with Magisk or KernelSU.
9. Downloads HMA-OSS Zygisk to `/sdcard/Download/HMA-OSS-ZYGISK.zip`.
10. Installs HMA-OSS Zygisk with Magisk or KernelSU.
11. Shows the success message.
12. Reboots the device with root.

Magisk or KernelSU controls the permanent root permission prompt. The app requests root; the user must grant it in the installed root manager.

## Build

Open this folder in Android Studio and build the `app` module.

Minimum Android version: Android 10, API 29.
