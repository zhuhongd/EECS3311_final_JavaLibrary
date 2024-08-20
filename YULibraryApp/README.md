# YULibraryApp

# IntelliJ IDEA Setup

## Launch

Start IntelliJ IDEA. If not installed, download from [JetBrains](https://www.jetbrains.com/idea/download/).

## Clone Repository

On the "Welcome" screen, click "Get from VCS." Paste `https://github.com/MishaKhvatov/YULibraryApp` into the "URL" field
and click "Clone."

## Initial Project Setup with Gradle

IntelliJ will detect the Gradle project and begin the import process. Monitor the progress in the bottom toolbar:

- "Gradle: Building..." indicates the start of the build.
- "Gradle: Building Model..." means Gradle is analyzing project structure.
- "Gradle: Configuring projects..." signifies the application of your `build.gradle.kts` configurations.

## Run Your Project

After setup, navigate the project files and develop. To run, use the green play button next to the main method or test
cases to create a run configuration and execute.

# Eclipse Setup

## Install Buildship Gradle Plugin

If not installed, go to `Help -> Eclipse Marketplace` and search for "Buildship."

## Clone GitHub Repository

In the terminal, navigate to your project directory and run:
`git clone https://github.com/MishaKhvatov/YULibraryApp`

## Import Project

In Eclipse, go to `File > Import > Gradle > Existing Gradle Project` and select the cloned directory.

## Build

Click "Next" with default options and wait for the build to complete. Click "Finish" afterward.

Folders `src/main/java` and `src/test/java` should have icons indicating they are source folders. Run the project using
the green "run" button.
