package com.aman.vaak.models

/**
 * Represents the keyboard setup progress states in sequence
 */
enum class VaakSetupState {
    NEEDS_ENABLING, // Keyboard needs system enabling
    NEEDS_PERMISSIONS, // Microphone permissions needed
    NEEDS_OVERLAY_PERMISSION, // SYSTEM_ALERT_WINDOW permission needed
    NEEDS_API_KEY, // API key configuration needed
    READY_FOR_USE, // All requirements met, can use keyboard
    SETUP_COMPLETE, // Selected as current keyboard (optional)
}
