Here’s a clean, well-formatted **README** for your PinView library, including the XML attributes displayed in a proper table format:

--


# PinView for Android

A highly customizable, lightweight, and easy-to-use **PinView** library for Android, written in Kotlin. It provides a single view to handle PIN/OTP entry, removing the need for managing multiple `EditText` views.

*(https://github.com/user-attachments/assets/8f14131a-ac28-41d7-96c1-40f0897cfc53)*

---

## ✨ Features

* **Custom Pin Count**: Display any number of boxes.
* **Flexible Shapes**: Choose between `square`, `rounded_square`, or `circle`. Or provide your own Drawable.
* **Size Control**: Specify exact width and height for each box.
* **Custom Input Types**: Accepts `number`, `text`, or `textCapCharacters`.
* **Password Mode**: Hide input with a custom character.
* **Cursor**: Display a blinking cursor for better UX.
* **Error States**: Programmatically show an error state with custom colors.
* **Rich Styling**: Customize colors, text size, spacing, stroke width, and corner radius.
* **Lightweight**: Single view, no external dependencies.
* **Paste Support**: Automatically handles pasting from the clipboard.

---

## ⚙️ Setup

### 1. Add JitPack Repository

Add the JitPack repository to your root `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ... other repositories
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add the Dependency

Add the dependency to your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.kiranpatole:pinview_lib:1.0.0")
}
```

---

## 🛠️ Usage

### XML Examples

**Basic Number PinView (4-digit)**

```xml
<io.github.kiranpatole.pinview.PinView
    android:id="@+id/pinView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinCount="4"
    app:pinInputType="number"
    app:boxWidth="50dp"
    app:boxHeight="50dp"
    app:boxSpacing="10dp"/>
```

**Password PinView with Cursor and Rounded Boxes**

```xml
<io.github.kiranpatole.pinview.PinView
    android:id="@+id/pinViewPassword"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinCount="6"
    app:pinShape="rounded_square"
    app:boxCornerRadius="8dp"
    app:isPassword="true"
    app:passwordCharacter="*"
    app:showCursor="true"
    app:cursorColor="@color/purple_500"
    app:boxStrokeColorFocused="@color/purple_500"/>
```

### Kotlin Usage

```kotlin
val pinView = findViewById<PinView>(R.id.pinView)

pinView.setOnPinEnteredListener { pin ->
    if (pin == "1234") {
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
        pinView.showErrorState()
        pinView.clearPin()
    }
}
```

---

## 🧩 All XML Attributes

| Attribute                | Description                                               | Format    |
| ------------------------ | --------------------------------------------------------- | --------- |
| pinCount                 | The number of boxes to display.                           | integer   |
| pinShape                 | Shape of the boxes: `square`, `rounded_square`, `circle`. | enum      |
| boxWidth                 | The width of each box.                                    | dimension |
| boxHeight                | The height of each box.                                   | dimension |
| boxSpacing               | The space between each box.                               | dimension |
| pinInputType             | Input type: `number`, `text`, `textCapCharacters`.        | enum      |
| isPassword               | Hides the input if true.                                  | boolean   |
| passwordCharacter        | Character to show in password mode.                       | string    |
| showCursor               | Shows a blinking cursor if true.                          | boolean   |
| cursorColor              | The color of the cursor.                                  | color     |
| cursorWidth              | The width of the cursor.                                  | dimension |
| boxStrokeColor           | Color of the box border.                                  | color     |
| boxStrokeColorFocused    | Border color of the active box.                           | color     |
| boxStrokeColorError      | Border color in error state.                              | color     |
| boxStrokeWidth           | The width of the box border.                              | dimension |
| boxBackgroundColor       | Background color for empty boxes.                         | color     |
| boxBackgroundColorFilled | Background color for filled boxes.                        | color     |
| pinTextColor             | The color of the text inside the boxes.                   | color     |
| pinTextSize              | The size of the text.                                     | dimension |
| boxCornerRadius          | Corner radius for `rounded_square` shape.                 | dimension |
| boxBackgroundDrawable    | A custom drawable for the box background.                 | reference |

---

## 🤝 Contributing

Contributions are welcome! If you find a bug or want to add a feature, feel free to open an issue or submit a pull request.

---

## ⭐ Support

If you find this library helpful, please star ⭐ the repository — it helps others discover it!

---

## 📜 License

Copyright 2025 Kiran Patole

Licensed under the Apache License, Version 2.0 (the "License");
You may not use this file except in compliance with the License.
You may obtain a copy of the License at:

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

---
