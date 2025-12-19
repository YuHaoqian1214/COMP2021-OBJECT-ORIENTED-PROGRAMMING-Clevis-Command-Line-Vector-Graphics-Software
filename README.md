# Clevis: Command Line Vector Graphics Software

## Description
This repository contains the source code and user manual for **Clevis**, a Command Line Vector Graphics Software developed in Java as part of the COMP2021 Object-Oriented Programming group project at The Hong Kong Polytechnic University (PolyU HK) in Fall 2025. The project implements a CLI tool for creating and manipulating vector graphics, including shapes like lines, circles, rectangles, and squares. It supports operations such as grouping/ungrouping shapes, calculating bounding boxes, moving shapes, checking intersections, and more.

The tool follows the Model-View-Controller (MVC) pattern and includes unit tests for the model package. This project demonstrates object-oriented principles like inheritance, polymorphism, and error handling.

**Note:** This is an academic project and does not include advanced features like persistent storage or export to vector formats (e.g., SVG). For full requirements and design details, refer to the project description PDF (not included in this repo).

## Features
- **Shape Creation:** Draw rectangles, lines, circles, and squares with unique names and coordinates.
- **Grouping and Ungrouping:** Combine multiple shapes into groups and disassemble them.
- **Deletion:** Remove individual shapes or entire groups.
- **Bounding Box Calculation:** Compute the minimum bounding box for any shape or group.
- **Movement:** Translate shapes or groups by specified offsets.
- **Shape Detection:** Identify the topmost shape covering a given point (based on Z-index and distance threshold).
- **Intersection Check:** Determine if two shapes intersect via their bounding boxes.
- **Listing:** Display details of individual shapes or all shapes in Z-order with indentation for groups.
- **Logging:** Record commands in HTML and TXT formats (specified at launch).
- **Quit Command:** Gracefully terminate the session.
- **Bonus Features (if implemented):** GUI demonstration, undo/redo support.
- **Error Handling:** Robust checks for invalid commands, duplicate names, undefined shapes, etc.

All numeric outputs are rounded to 2 decimal places.

## Setup
### Requirements
- Java SE Development Kit (JDK) 21
- IntelliJ IDEA Community Edition (2024.2 or compatible) for development and testing (recommended, as the project is structured as an IntelliJ project).

## Usage
Launch the application from the command line with logging file paths:

```
java hk.edu.polyu.comp.comp2021.clevis.Application -html path/to/log.html -txt path/to/log.txt
```

Once running, enter commands in the CLI prompt. Examples:

- Create a rectangle: `rectangle rect1 0.0 0.0 10.0 5.0`
- Create a line: `line line1 1.0 1.0 5.0 5.0`
- Group shapes: `group group1 rect1 line1`
- Move a shape: `move rect1 2.0 3.0`
- Check intersection: `intersect rect1 line1`
- List all shapes: `listAll`
- Quit: `quit`

For detailed command syntax and examples, refer to the User Manual section below or the `user_manual.pdf` file in the repository.

## User Manual
The user manual is available as `user_manual.pdf` in the root of the repository. It includes:
- An introduction to the system.
- Detailed descriptions of all supported commands with examples.
- Troubleshooting tips for common errors.

## License
This project is for educational purposes only and is not licensed for commercial use. All rights reserved by the group members and PolyU HK. If reusing code, please cite the source appropriately.
