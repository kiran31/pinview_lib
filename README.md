Got it ✅ — here’s a **single, clean, copy-paste-ready `README.md` file** you can drop into your repo right away.

No extra explanations — just a polished README file:

---

````markdown
# 🔢 PinView for Android

A highly customizable, lightweight, and easy-to-use **PinView** library for Android, written in Kotlin.  
It provides a single view to handle PIN/OTP entry, removing the need for managing multiple `EditText` views.

> **Tip:** Add a GIF or video of the demo app in action here for better visibility.

---

## ✨ Features

- **Custom Pin Count** – Display any number of boxes.
- **Flexible Shapes** – Choose between `square`, `rounded_square`, and `circle`, or provide your own `Drawable`.
- **Size Control** – Specify exact width and height for each box.
- **Custom Input Types** – Accepts number, text, or textCapCharacters.
- **Password Mode** – Hide input with a custom character for password entry.
- **Cursor Support** – Display a blinking cursor for better UX.
- **Error States** – Programmatically show an error state with custom colors.
- **Rich Styling** – Customize colors, text size, spacing, stroke width, and corner radius.
- **Lightweight** – A single view with no external dependencies.
- **Paste Support** – Automatically handles pasting from the clipboard.

---

## ⚙️ Setup

### 1️⃣ Add JitPack Repository

Add the JitPack repository to your **root `settings.gradle.kts`** file:

```kotlin
dependencyResolutionManagement {
    repositories {
        // Other repositories
        maven { url = uri("https://jitpack.io") }
    }
}
````

### 2️⃣ Add the Dependency

In your **app-level `build.gradle.kts`** file, add:

```kotlin
dependencies {
    implementation("com.github.kiran31:pinview_lib:1.0.0")
}
```

---

## 🛠️ Usage

### XML Example

```xml
<io.github.kiranpatole.pinview.PinView
    android:id="@+id/pinView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pinCount="4"
    app:pinShape="rounded_square"
    app:boxWidth="48dp"
    app:boxHeight="48dp"
    app:cursorVisible="true"
    app:password="true"
    app:passwordChar="●" />
```

### Kotlin Example

```kotlin
val pinView = findViewById<PinView>(R.id.pinView)

// Get the entered PIN
val enteredPin = pinView.getText()

// Clear the PIN
pinView.clear()

// Show error state
pinView.showError(true)
```

---

## 🧩 Customization Options

| Attribute       | Type      | Description                          |
| --------------- | --------- | ------------------------------------ |
| `pinCount`      | Integer   | Number of boxes to display           |
| `pinShape`      | Enum      | `square`, `rounded_square`, `circle` |
| `boxWidth`      | Dimension | Width of each box                    |
| `boxHeight`     | Dimension | Height of each box                   |
| `password`      | Boolean   | Whether to mask input                |
| `passwordChar`  | String    | Character used for masking           |
| `cursorVisible` | Boolean   | Show/hide blinking cursor            |
| `errorColor`    | Color     | Color of box border in error state   |

---

## 📸 Demo

> Add a GIF or short video demo here for better visibility.
> You can record this using [ScreenToGif](https://www.screentogif.com/) or Android Studio's emulator screen recorder.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to fork this repo, create a branch, and submit a PR.

---

## ⭐ Support

If you like this library, please **star ⭐ the repository** — it helps others discover it!

---

## 📜 License

```
MIT License

Copyright (c) 2025 Kiran Patole

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions: 
Would you like me to also generate a **sample GIF layout storyboard** (so you can quickly record a short animation for the README + LinkedIn post)?
```
