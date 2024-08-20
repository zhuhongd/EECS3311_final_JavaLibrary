# YULibraryApp

**Overview**

This project involves designing a comprehensive library management system for York University. The system is intended to manage book rentals, track overdue items, facilitate online subscriptions, and handle various user roles including students, faculty, and library management. The project focuses on translating user requirements into a robust and scalable Java-based software solution.

**Project Structure**
- Use Case Diagrams: These diagrams map out the interactions between users (students, faculty, non-faculty staff) and the library management system.
- Activity Diagrams: Detailed breakdowns of user actions, illustrating the step-by-step processes involved in interacting with the system.
- Sequence Diagrams: Visual representations of the flow of events within the system, showing how different components interact over time.
- Class Diagram: The structural blueprint of the system, detailing the classes, attributes, methods, and relationships necessary for the system’s architecture.
- 
**Features**
- User Registration: Allows students, faculty, and non-faculty staff to register and access the system.
- Book Rentals and Returns: Facilitates the rental of physical items and manages overdue penalties.
- Online Book Access: Provides access to virtual copies of textbooks for the duration of a course.
- Subscription Management: Allows users to subscribe to and manage online newsletters.
- Search and Recommendations: Implements a search feature that suggests books based on text similarity.
- Course and Textbook Tracking: For faculty, the system tracks courses and notifies them of new textbook editions.

**Technologies Used**

Java: The primary programming language used to implement the system.
UML Diagrams: Used for system modeling and design.
Version Control: Git was used to manage project versions and collaboration among team members.

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
