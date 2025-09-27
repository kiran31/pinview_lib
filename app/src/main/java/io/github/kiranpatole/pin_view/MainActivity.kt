package io.github.kiranpatole.pin_view

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.kiranpatole.pinview.PinView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- 1. Password PinView with Error Button ---
        val pinViewPassword = findViewById<PinView>(R.id.pinViewPassword)
        val errorButton = findViewById<Button>(R.id.errorButton)

        pinViewPassword.setOnPinEnteredListener { pin ->
            // Simulate a network call or verification
            if (pin == "1234") {
                Toast.makeText(this, "Success! PIN is 1234", Toast.LENGTH_SHORT).show()
                pinViewPassword.clearPin()
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                // Show the error state
                pinViewPassword.showErrorState()
            }
        }

        // The button can also trigger the error state externally
        errorButton.setOnClickListener {
            pinViewPassword.showErrorState(1500) // Show error for 1.5 seconds
        }


        // --- 2. Text (All Caps) PinView ---
        val pinViewText = findViewById<PinView>(R.id.pinViewText)
        pinViewText.setPinText("123dfc")
        pinViewText.setOnPinEnteredListener { pin ->
            Toast.makeText(this, "Entered Text: $pin", Toast.LENGTH_SHORT).show()
            // Clear the pin for reuse in the demo
            pinViewText.clearPin()
            // *** THIS IS THE SECONDARY FIX ***
            // The line that reset the listener has been removed, so this PinView
            // will now work correctly every time it's used, not just once.
        }


        // --- 3. Custom Dimension PinView ---
        val pinViewCustom = findViewById<PinView>(R.id.pinViewCustom)
        pinViewCustom.setOnPinEnteredListener { pin ->
            Toast.makeText(this, "Custom PinView Entered: $pin", Toast.LENGTH_SHORT).show()
            // Clear the pin for reuse in the demo
            pinViewCustom.clearPin()
        }
    }
}