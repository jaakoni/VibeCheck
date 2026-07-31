# Enhance Text Contrast on Home Screen

The text labels and input fields on the Home Screen currently have poor contrast and are hard to read, specifically when typing in the city, selecting dates, and viewing the category button. The goal is to explicitly make these text elements darker for better visibility.

## Proposed Changes

### Search Home Component

#### `SearchHomeScreen.kt`

- **Labels ("Location", "Timing", "Search Categories")**:
  - Update `style = MaterialTheme.typography.labelMedium` to add explicit `color = Color.Black` and `fontWeight = FontWeight.Bold` to make them stand out.
- **Location Input (`OutlinedTextField`)**:
  - Add `colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)` so the typed text is extremely visible.
  - Set the placeholder text explicitly to `Color.DarkGray`.
- **Timing Input (`OutlinedTextField`)**:
  - Update `disabledTextColor` and `disabledPlaceholderColor` in `OutlinedTextFieldDefaults.colors` to `Color.Black` and `Color.DarkGray` respectively (since the field is `enabled = false` for the date picker overlay).
- **Search Categories (`OutlinedButton`)**:
  - Change the text color inside the button from `MaterialTheme.colorScheme.onSurface` to `Color.Black`.

## Verification Plan

### Automated Tests
- Build the app using Gradle to ensure no compilation errors are introduced by the color parameter changes.

### Manual Verification
- Deploy to device/emulator to visually confirm the input text and labels are significantly darker and easier to read.