[![Build Android Project](https://github.com/Fbada006/PdfTestApp/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Fbada006/PdfTestApp/actions/workflows/build.yml)

# PdfTestApp

This is a simple application that is built using the Model-View-ViewModel (MVVM) architecture in 100% Kotlin. It has the
following features:

1. Displays all the PDF files that the user has in the `Download` folder as long as the permissions have been granted,
   otherwise they are
   requested with an explanation of why they are needed.
2. A search functionality to allow the user to find PDF files based on the query
3. Allows the user to star/unstar items for a favorite feature
4. Allows clicking of individual items to open the PDF using the PSPDFKIT library

# Prerequisites

The app was built using Android Studio Dolphin | 2021.3.1 so try running at least that version of Android studio. As for
testing devices,
ensure they are at least running Android API 23 and above. Running UI tests can be done straight from the Android Studio
UI.

## Table of contents

- [Architecture](#architecture)
- [Libraries](#libraries)
- [Extras](#extras)

## Architecture

The app uses the MVVM architecure because it is a nice and simple architecture that makes testing and maintenance
easier. It was also chosen
because it is a popular choice meaning a new developer can pick it up easily making for smooth transitions between
teams. There is also the
added benefit of using a `ViewModel` to survive configuration changes. `Kotlin Flow` and `Coroutines` have been used to
monitor data and
state changes in the app making for a smooth user experience.

> **_NOTE ON NAVIGATION:_**  The decision to use the previous navigation component as opposed
> to [Navigation Compose](https://developer.android.com/jetpack/compose) is extremely intentional
> since there are type checks enforced. Debugging on the Navigation Compose component can be a nightmare especially when
> you pass a wrong
> type by mistake. The component will fail at run time whereas the older component will fail at compile time.
> Additionally, the
> older navigation component has the added benefit of allowing the use of parcelable types as well as the opportunity
> to have the flexibility of fragments and experience the best of both worlds. This makes for a cleaner architecture in
> my opinion.

## Libraries

This app makes use of the following libraries:

- [Jetpack](https://developer.android.com/jetpack)🚀
   - [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manage UI data to survive
     configuration changes
     and is lifecycle-aware
   - [Compose](https://developer.android.com/jetpack/compose) - Android’s modern toolkit for building native UI.
   - [Navigation](https://developer.android.com/guide/navigation) - Handle everything needed for in-app navigation
   - [Room DB](https://developer.android.com/topic/libraries/architecture/room) - Fluent SQLite database access
- [Dagger Hilt](https://dagger.dev/hilt/) - A fast dependency injector for Android and Java.
- [PSPDFKit](https://pspdfkit.com/guides/android/) - An SDK for viewing, annotating, editing, and creating PDFs
- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Library Support for coroutines
- [Material Design](https://material.io/develop/android/docs/getting-started/) - build awesome beautiful UIs.🔥🔥
- [Timber](https://github.com/JakeWharton/timber) - A logger with a small, extensible API which provides utility on top
  of Android's normal
  Log class.
- [Truth](https://truth.dev/) - A library for performing assertions in tests
- [Mockk](https://mockk.io/) - A mocking framework for testing in Android
- [Robolectric ](http://robolectric.org/) - For fast and reliable unit tests to Android.
- [Turbine](https://github.com/cashapp/turbine) - Turbine is a small testing library for `kotlinx.coroutines Flow`

## Extras

#### CI-Pipeline

The app also uses GitHub actions to build the app and runs whenever a new pull request or merge to the `master` branch
happens.
The pipeline also runs unit tests. The status of the build is displayed at the top of this file.

```
MIT License

Copyright (c) 2022 Ferdinand Bada

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```