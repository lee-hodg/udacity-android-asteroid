# Asteroid Radar

This is an Android App written in Kotlin as part of the Udacity Android Kotlin developer
nanodegree.

## Functionality

It uses the [NASA near earth close approaches API](https://api.nasa.gov/)
to get asteroids that come close to the earth and display information
about them such as if they are deemed hazardous or not.

It also displays the NASA picture of the day.

## Screenshots

 - ![Asteroid Screenshot 1](https://github.com/lee-hodg/udacity-android-asteroid/blob/master/screenshots/screen_1.png)
 - ![Asteroid Screenshot 2](https://github.com/lee-hodg/udacity-android-asteroid/blob/master/screenshots/screen_2.png)
 - ![Asteroid Screenshot 3](https://github.com/lee-hodg/udacity-android-asteroid/blob/master/screenshots/screen_3.png)
 - ![Asteroid Screenshot 4](https://github.com/lee-hodg/udacity-android-asteroid/blob/master/screenshots/screen_4.png)


## Features

- Room database with live data binding
- Picasso image caching
- Retrofit
- Separation of concerns with different models for network, database and domains and a repository pattern
- Accessibility for everyone (tested with scanner and talkback)
- Multiple language support
- Updates the database offline with a worker once per day when certain battery and network
constraints are met.
- Coroutines so expensive queries do not block the main UI thread
