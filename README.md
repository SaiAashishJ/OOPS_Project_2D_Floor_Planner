# 2D Floor Planner (Java OOP Project)

A simple yet powerful 2D floor planner application built using Java. This application allows users to create, customize, and manage floor plans with multiple rooms, windows, doors, and furniture. The application provides an intuitive graphical user interface (GUI) and supports saving/loading floor plans, dragging and repositioning rooms.

## Features

- **Create a new plan or open an existing plan.**
- **Add rooms** with predefined types: Bedroom, Bathroom, Kitchen, Dining Room, and Drawing Room.
  - Rooms are drawn as rectangles with user-defined dimensions (length and width).
- **Right-click** on rooms to:
  - Add windows, doors, and furniture.
  - Move/rotate the location of fixtures (window, door, furniture).
  - Delete the room.
- **Drag and drop rooms** to reposition them on the floor plan.
  - The system checks for overlaps between rooms and alerts the user.
  - **Grid snap** feature to align rooms and fixtures to the grid.
- **Save and load designs** from the local file system:
  - Save floor plans as a file.
  - Open previously saved designs for further editing.

## Installation

1. Clone the repository to your local machine:

    ```bash
    git clone https://github.com/your-username/floor-planner.git
    ```

2. Open the project in your preferred IDE (Eclipse, IntelliJ, etc.) or build using command line tools.

3. Compile and run the file to launch the application.

## How to Use

1. **Create a New Plan:**
   - Upon launching the application, you can create a new floor plan by selecting the "New Plan" option.
   
2. **Add a Room:**
   - Right-click on the canvas to choose the type of room you want to add (e.g., Bedroom, Bathroom, Kitchen, etc.).
   - Specify the dimensions (length and width) of the room.

3. **Modify Room Features:**
   - Right-click on a room to add windows, doors, or furniture.
   - You can also move these features by dragging them around within the room.

4. **Move Rooms:**
   - Drag rooms to reposition them on the floor plan. The application will alert you if rooms overlap.
   - Use the grid snapping feature to align rooms easily to a grid.

5. **Save and Open Plans:**
   - After designing your floor plan, you can save it to your local file system using the "Save" option.
   - Open any previously saved plan for further editing.

6. **Delete a Room:**
   - Right-click on a room and select the "Delete" option to remove it from the floor plan.

## Screenshots





## Technologies Used

- Java Swing (for GUI)
- Object-Oriented Programming (OOP) principles
