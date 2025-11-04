# Half-a-Minute-IT-Madness-Android

> **⚠️ END OF LIFE NOTICE**  
> This project is no longer actively maintained and has reached End of Life (EoL) as of November 2025.  
> This was a school project created by 2 classmates competing against teams of 4 - we were at a disadvantage but gave it our all! 🎮  
> No new releases are being made. The app is provided solely for archival/reference purposes. 
> Final version: **v4.0.1** - A complete rewrite with modern Android APIs and Jetpack Compose.

A fun and challenging Android game that tests your IT knowledge across various categories. Answer as many questions as you can within the 30-second time limit.

[![Codacy Security Scan](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/actions/workflows/codacy.yml/badge.svg?branch=main)](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/actions/workflows/codacy.yml)

![License](https://img.shields.io/github/license/Stensel8/Half-a-Minute-IT-Madness-Android?label=License)

![Kotlin_Used](https://img.shields.io/github/languages/top/Stensel8/Half-a-Minute-IT-Madness-Android?color=purple&label=Kotlin)

![Repo_Size](https://img.shields.io/github/repo-size/Stensel8/Half-a-Minute-IT-Madness-Android) ![Apk_Size](https://img.shields.io/badge/APK_Size-8MB-blue)




[![Stable Version](https://img.shields.io/badge/StableVersion-V4.0.1-darkgreen)](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases/tag/StableV4.0.1)
[![Beta Version](https://img.shields.io/badge/BetaVersion-V2.4-blue)](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases/tag/DebugV2.4)
![Last Commit](https://img.shields.io/github/last-commit/Stensel8/Half-a-Minute-IT-Madness-Android?label=Last%20Commit)



## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Getting Started (PC)](#getting-started-on-pc)
- [Getting Started (Mobile)](#getting-started-on-mobile)
- [Installation Guide](#installation-guide)
- [Usage Instructions](#usage-instructions)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [FAQs](#faqs)
- [License](#license)
- [Acknowledgments](#acknowledgments)



## Overview

We created an Android game with multiple minigames in which you earn points by moving forward, you will also lose lives on wrong answers. The goal is to be the first one to reach 50 points. You can play with or without a board. Scores are tracked for each round.

## Features

- Engaging gameplay with a 30-second time constraint.
- Multiple categories including math, language, and IT trivia.
- Earn points for correct answers; lose lives for incorrect ones.
- Lives displayed as heart icons in the top right corner.
- Challenge yourself to beat your high scores in each category.


## Screenshots

- **Welcome Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_welcome.webp" alt="Welcome screen" width="300">

- **Home Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_mainactivity.webp" alt="Home screen" width="300">

- **Settings Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_settings.webp" alt="Settings screen" width="300">

- **Choose Game Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_choosegame.webp" alt="Choose game screen" width="300">

- **Math Game Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_mathgame.webp" alt="Math game screen" width="300">

- **Guessing Game Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_guessinggame.webp" alt="Guessing game screen" width="300">

- **Choose Language Game Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_chooselanguagegame.webp" alt="Choose language game screen" width="300">

- **Language Game Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_languagegame.webp" alt="Language game screen" width="300">

- **Game Over Screen**
  <img src="Documentation/Screenshots/Dark/Half%20a%20Minute%20IT%20Madness_gameover.webp" alt="Game over screen" width="300">



## Getting Started on PC?

To get started with Half-a-Minute-IT-Madness, follow these steps to set up the project:


### Cloning the Repository

First, clone the repository to your local machine:

   ```bash
   git clone https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android.git
   ```
This command will create a copy of the repository in your local directory.


### Opening the Project

After cloning the repository, open the project in Android Studio:


### Launch Android Studio.

On the welcome screen, select 'Open an Existing Project'.
Navigate to the directory where you cloned the repository.
Select the Half-a-Minute-IT-Madness-Android folder and click 'OK' to open the project.

This will load the project in Android Studio, and you'll be able to view all the files and resources.


### Building and Running the App

To build and run the app on your Android device or emulator:

Connect your Android device to your computer via USB, or set up an emulator in Android Studio.
Make sure your device or emulator is selected in the target dropdown menu in Android Studio.
Click on the 'Run' button (represented by a green triangle) or press Shift + F10.
Android Studio will build the project and install the app on your device or emulator.


## Installation Guide

### Prerequisites:

- **Android Studio Hedgehog or Higher:** Ensure you have Android Studio version Hedgehog (2023.1.1) or higher installed. This version includes all necessary tools and SDKs for building the app.
- **Minimum Android SDK:** The project requires a minimum Android SDK version of 30. Make sure this SDK version is installed via the SDK Manager in Android Studio.
- **Device Storage:** At least 128MB of free space on your Android device or emulator for optimal performance.

## Getting Started on Mobile?

To get started with Half-a-Minute-IT-Madness, follow these steps to set up the project:

### Usage Instructions

To get started with Half-a-Minute-IT-Madness:

1. **Start the Game**: Open the app on your Android device.
2. **Choose a Category**: Select your preferred category from the main menu.
3. **Answer Questions**: Try to answer as many questions as possible within 30 seconds.
4. **Keep Track of Your Score**: Your score is displayed at the top of the screen.
5. **Repeat**: Play again to improve your score or try a different category for a new challenge.

For more detailed instructions, refer to our [Game Rules](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/tree/main/Documentation).


## Roadmap

We are continuously working on improving and expanding Half-a-Minute-IT-Madness. Here's what we may be working on in the near future:


- ~~**High Score Board**: Implementing a leaderboard to track top scores.~~ [fixed in 4.0.1](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases/tag/StableV4.0)
- ~~**New Difficulty Levels**: Adding the ability to choose even higher difficulty levels for a greater challenge.~~[fixed in V4.0.1](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/releases/tag/StableV4.0)
- **Improved Performance**: Continuously optimizing the game for smoother and faster gameplay.
- **Additional Categories**: Introducing new trivia categories based on user feedback.
- **Multiplayer Mode**: Developing a multiplayer feature for competitive play.

Stay tuned for these updates and more as we strive to make Half-a-Minute-IT-Madness even more exciting!


## Contributing

Contributions are welcome! If you'd like to contribute to the project, please follow the
contribution guidelines.


## FAQs

**Q: Can I play Half-a-Minute-IT-Madness on any Android device?**
A: The game is compatible with devices running Android SDK version 30 and above.

**Q: Is the game free?**
A: Yes, Half-a-Minute-IT-Madness is completely free to play.

**Q: How can I contribute to the game's development?**
A: Check out our [Contributing Guide](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/blob/main/CONTRIBUTING.md) for details on how you can contribute.

**Q: Are there any in-app purchases?**
A: No, the game does not currently feature any in-app purchases.

**Q: How can I report a bug or provide feedback?**
A: Please open an issue on our GitHub repository or contact us directly through [GitHub Discussions](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/discussions).

Have more questions? Feel free to ask them in the [Issues section](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/issues) of our GitHub repository.


## License

This project is licensed under the GNU General Public License version 3 (GPL v3).

Copyright (c) 2023-2024 by Sten Tijhuis
- [Sten Tijhuis](https://github.com/Stensel8)
- [Wouter Woertman](https://github.com/waldbaan)

For more details, see the [GPL version 3 License](https://www.gnu.org/licenses/gpl-3.0.html).


## Acknowledgments

Special thanks to the contributors who have helped make this game possible!

**Owners:**
- [Stensel8](https://github.com/Stensel8)
- [waldbaan](https://github.com/waldbaan)

**Project contributors / Inspirations:**
- [TimonZeelen](https://github.com/TimonZeelen)
- [tritshuri](https://github.com/tritshuri)
- [nassimassb](https://github.com/nassimassb)
- [OpenAI](https://openai.com)
- [Microsoft CoPilot](https://learn.microsoft.com/en-us/copilot)
- [Anthropic Claude](https://www.anthropic.com/claude) - AI assistance for v4.0.1 rewrite
- [Google Developer Training](https://github.com/google-developer-training)
- [Icon Kitchen](http://icon.kitchen)
- [TheMardy/ThirtySeconds](https://github.com/TheMardy/ThirtySeconds)
- [nassimassb/THE-30-seconds-game](https://github.com/nassimassb/THE-30-seconds-game)
- [haugeSander/Cardgame](https://github.com/haugeSander/Cardgame)

[View all contributors](https://github.com/Stensel8/Half-a-Minute-IT-Madness-Android/graphs/contributors)

---

### 📝 Important Notice

**This was a school project created by 2 classmates competing against teams of 4 students.** Mobile game development was not our area of expertise, but we took on the challenge for fun and learning. This project was largely developed with the assistance of Anthropic Claude Code for the final v4.0.1 rewrite.

**Disclaimer:** This app was made for educational purposes and entertainment. Unexpected bugs and issues may still appear. We did our best with limited mobile development experience. The project is now archived and no longer maintained.

---

For more details, see the [GPL version 3 License](https://www.gnu.org/licenses/gpl-3.0.html).

Feel free to star the repository if you like it! ⭐

