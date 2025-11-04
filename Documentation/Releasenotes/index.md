## **Half a Minute IT Madness - Android Final Release V4.0.1**
**Release Notes - End of Life**

⚠️ **This is the final release of Half a Minute IT Madness for Android. The project has reached End of Life (EoL) and will no longer receive updates.**

Version 4.0.1 represents a complete rewrite focusing on modern Android development, improved stability, and quality of life improvements. This was a school project created by 2 classmates competing against teams of 4. Mobile game development was not our expertise, but we gave it our all!

### Major Technology Migration
- **Complete UI Rewrite:** Migrated from Android XML to Jetpack Compose (modern declarative UI).
- **Android SDK Update:** Updated from SDK 30-34 to SDK 33-36 (Android 13-14+).
- **Kotlin Update:** Updated to Kotlin 2.2.21 with modern coroutines. Removed all deprecated APIs.
- **Build System:** Gradle 8.13 with R8 full mode optimization. Fixed all Gradle warnings.

### Enhancements and New Features
- **APK Size Reduction:** Reduced by 77% (36 MB → 8.29 MB) through aggressive optimization, ProGuard rules, and resource cleanup.
- **Countdown Alert:** Added sound effect and red blinking timer animation for the last 5 seconds of gameplay.
- **Animations & Visual Feedback:** Green flash on correct answers (100ms), shake on wrong answers (80ms), snackbar messages (✅ +X pts, ❌ X lives), hearts display for lives (❤️), and anti-spam cooldown protection.
- **Sound System:** Added sound effects for correct/wrong answers and game completion with applause. Toggle in Settings to enable/disable.
- **Performance:** Faster startup, smoother animations, better memory usage, improved battery efficiency.
- **Code Organization:** Restructured into `/screens`, `/utils`, `/ui/theme`, `/ui/components` packages with improved separation of concerns.

### Documentation
- **Project Status:** Added End of Life notices to README, SECURITY, and CONTRIBUTING files.
- **Settings Notice:** Added project context and AI assistance disclaimer in Settings screen.
- **Localization:** Maintained support for English, Dutch, French, and German.

### Bugfixes
- **Difficulty Display:** Fixed difficulty not showing on leaderboard for Math and Guessing games - [#6](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/6) - Resolved.
- **Scoring Issues:** Fixed inconsistencies across different game modes - [#48](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/48) - Resolved.
- **Android 13+ Crashes:** Resolved various crashes, memory leaks, and theme inconsistencies.
- **Leaderboard Persistence:** Fixed leaderboard scores not saving correctly on some devices.
- **General Stability:** Improved overall app stability and performance.

### Known Limitations (Will Not Be Fixed - EoL)
- No multiplayer, cloud sync, or global leaderboards. Local leaderboards only.
- Limited game modes (Math, Language, Guessing). No background music or accessibility features.

### Development Credits
**Code Migration & Rewrite:** Anthropic Claude Code  
**Team:** [Sten Tijhuis](https://github.com/Stensel8) & [Wouter Woertman](https://github.com/waldbaan)

### End of Life Notice
No new releases, bug fixes, or security patches will be provided. The app is provided solely for archival and reference purposes. Thank you to everyone who supported this project!

**Final APK:** 8.29 MB 

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## **Half a Minute IT Madness - Android Stable Release V3.0.0**
**Release Notes**

Version 3.0.0, of Half a Minute IT Madness for Android. This release some small improvements, enhancing the overall user experience and stability of the game.

### Enhancements and New Features
- **Difficulty adjustments:** Changed game rules and the amount of points you'll be getting. This is now based on the difficulty.
- **New dice feature:** Introduced a dice roll button, this is used if you want to follow the actual game rules.
- **Wordlist changes:** Changed the wordlist and separated them. Each game will now use different words.

### Documentation
- **Localization:** Updated some strings and translations.
- **Application icon:** Updated the application icon.

### Known Issues
- **Color Display Issue:** Tiles may show unexpected colors - [#6](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/6).

### Coming Soon
- **High Score Board:** We are currently discussing and working on the implementation of a high score board. The decision to include this feature is still under consideration.

We appreciate your continued support and feedback as we strive to make Half a Minute IT Madness the best it can be. Stay tuned for more updates and enhancements in future releases!

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## **Half a Minute IT Madness - Android Stable Release V2.7**
**Release Notes**

Version 2.7, of Half a Minute IT Madness for Android. This release some small improvements, enhancing the overall user experience and stability of the game.

### Enhancements and New Features
- **UI Overhaul:** Replaced more notifications with Snackbar for a cleaner and more elegant display.
- **Crash Handling:** Added more crash handlers to reduce and manage crashes effectively.
- **Improved onBackPressed behaviour:** The application will now require you to press the back button 2 times, when inside a game, instead of 1. This is done to prevent misclicks and accidental swipe gestures.
- **Build info:** The app will now show it's current build and version info.
- **Code Optimization:** Merged the 'myaccount.java' with the newly created 'SharedPref.kt' for better readability.

### Documentation
- **Settings Storage:** Reworked the SharedPref.kt class for more efficient variable storage.
- **Localization:** Updated some strings and translations.
- **Performance:** Improved overall performance and fixed various bugs.

### Improved Crash Handling
- **Try-Catch Implementations:** In case of errors, the game will now attempt to show a warning message at the bottom of the screen instead of crashing immediately. Note that this is not guaranteed to prevent all crashes.

### Known Issues
- **Color Display Issue:** Tiles may show unexpected colors - [#6](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/6).

### Coming Soon
- **High Score Board:** We are currently discussing and working on the implementation of a high score board. The decision to include this feature is still under consideration.

We appreciate your continued support and feedback as we strive to make Half a Minute IT Madness the best it can be. Stay tuned for more updates and enhancements in future releases!

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## **Half a Minute IT Madness - Android Stable Release V2.6**
**Release Notes**

We are excited to announce the first stable release, Version 2.6, of Half a Minute IT Madness for Android. This release brings significant improvements and new features, enhancing the overall user experience and stability of the game.

### Enhancements and New Features
- **UI Overhaul:** Replaced notifications with Snackbar for a cleaner and more elegant display.
- **Crash Handling:** Added more crash handlers to reduce and manage crashes effectively.
- **New Feature:** Introduced an engaging Guessing Game.
- **Theme Update:** Updated theme colors for better consistency with the app's color scheme.
- **Code Optimization:** Application is ~97% rewritten in Kotlin 1.9.21. Future updates will replace multiple Java 17. We are transitioning from Java to Kotlin, as Kotlin offers more modern features and improved syntax.

### New Game Addition
- Added a new GuessingGame feature.

### Documentation
- **Readme Update:** Added new screenshots to the readme.md.
- **Wordlist Update:** The wordlist has been updated for a richer gaming experience.
- **Gradle Updates:** Updated the project to Android Gradle Plugin version 8.2.1 and Gradle Version 8.5.
- **Compatibility:** Made the app compatible with newer devices, ranging from SDK 30/Android 11 to Android 14 - SDK 34.
- **Dependencies:** Updated more dependencies for better performance.
- **Settings Storage:** Reworked the SharedPref.kt class for more efficient settings and variable storage.
- **Localization:** Updated some strings and translations.
- **Release Notes Display:** Changed how release notes are displayed - now opening in GitHub instead of within the app.
- **UI Enhancements:** Improved animations in settings and replaced Toast messages with Snackbar for a modern look.
- **Mode Switching:** Enhanced the transition between dark/light modes.
- **Performance:** Improved overall performance and fixed various bugs.
- **Typos:** Resolved some other typos.

### Bugfixes
- **Pause Menu Crash:** Fixed the issue where the game could crash in the Pause Menu - [#27](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/27).
- **Hard Mode Crash:** Resolved the crash issue in LanguageGame when on hard mode - [#9](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/9).

### Improved Crash Handling
- **Try-Catch Implementations:** In case of errors, the game will now attempt to show a warning message at the bottom of the screen instead of crashing immediately. Note that this is not guaranteed to prevent all crashes.

### Known Issues
- **Color Display Issue:** Tiles may show unexpected colors - [#6](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/6).

### Coming Soon
- **High Score Board:** We are currently discussing and working on the implementation of a high score board. The decision to include this feature is still under consideration.

We appreciate your continued support and feedback as we strive to make Half a Minute IT Madness the best it can be. Stay tuned for more updates and enhancements in future releases!

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## **Half a Minute IT Madness - Android Debug Release V2.4**
**Release Notes**

Version 2.4 contains:

### Bugfixes
- **High Scores Issue:** High scores not being saved - [Issue #5](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/5) - Resolved.
- **Language Saving:** Language not saving correctly - [Issue #7](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/7) - Resolved.
- **Dark Mode Flickering:** Dark mode flickering in some scenarios - [Issue #10](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/10) - Resolved.
- **Dark Mode Saving:** Dark mode not saving correctly - [Issue #11](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/11) - Resolved.
- Resolved some other typos.
- We still have a lot of known issues and bugs, but I will try to tackle them.

### Known Issues
- **Tiles Color Issue:** Tiles may show unexpected colors - [Issue #6](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/6)
- **Hard Mode Crash:** Game crashes when on hard mode - [Issue #9](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues/9)

We appreciate your continued support and feedback as we strive to make Half a Minute IT Madness the best it can be. Stay tuned for more updates and enhancements in future releases!

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## **Half a Minute IT Madness - Android Debug Release V2.3**
**Release Notes**

Version 2.3 brings a range of updates and fixes:

### Updates and Fixes
- **Language Crash Fix:** Application crash in combination with the German language is fixed!
- **General Bug Fixes:** Multiple improvements for a smoother experience.
- **Translation Logic:** Improved logic for translating words.
- **Language Update:** Belgian language removed!
- **Code Rewrite:** Application is ~35% rewritten in Kotlin 1.9.21. Future updates will replace multiple Java 17. We are transitioning from Java to Kotlin, as Kotlin offers more modern features and improved syntax.
- **Dark Mode Menus:** Reworked the dark mode context menus.
- **Release Notes Access:** Added the option to view the release notes from within the settings page.
- **Icon Update:** Changed the ic_launcher_icon.

If you have encountered any bugs, let me know via the "Issues" in GitHub!

### What's Changed
- **Merging Branches:** Merging "testing-sten" to "testing" by @Stensel8. See the pull request [here](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/pull/4).

We appreciate your continued support and feedback as we strive to make Half a Minute IT Madness the best it can be. Stay tuned for more updates and enhancements in future releases!

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
