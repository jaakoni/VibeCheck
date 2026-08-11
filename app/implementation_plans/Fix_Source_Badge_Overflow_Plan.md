# Implementation Plan: Fix Source Badge Text Wrapping & Layout in EventCard

## Problem Description
In `EventCard` on `SearchResultsScreen.kt`, the category tag, price tag, and source badge are placed side-by-side in a single `Row`.
When the tags contain long text (e.g., Category: "Sports & Recreation", Price: "Pricing data unavailable", Source: "Source: Ticketmaster"), the row runs out of horizontal space.
This forces the Source Badge `Box` to compress horizontally, causing its internal text ("Source: Ticketmaster") to break across 3 wrapped lines and form a squished vertical circle/oval in the bottom right.

## Proposed Changes

### `SearchResultsScreen.kt` in `EventCard`:
1. Replace `Row` with `FlowRow` (`@OptIn(ExperimentalLayoutApi::class) FlowRow`) or structure the tags cleanly with `FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp))`.
2. Add `maxLines = 1` / `softWrap = false` to the Source Badge `Text` so the text inside the badge never wraps onto multiple vertical lines.
3. Shorten "Pricing data unavailable" to "N/A" or "Price N/A" when `cost == null` to keep the UI clean, concise, and readable.
4. Ensure the Source Badge maintains a proper horizontal pill shape (`RoundedCornerShape(99.dp)`).

## Verification Plan

### Manual Verification
1. Run the app on the emulator/device.
2. Search for events in Atlanta (e.g., category "Sports & Recreation").
3. Inspect `EventCard` for "Atlanta Braves vs. Miami Marlins" (and other events).
4. Verify that the Source Badge ("Source: Ticketmaster") renders as a clean single-line horizontal pill without text wrapping or squishing.
