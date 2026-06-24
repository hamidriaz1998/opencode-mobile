---
name: Nexus Automata
colors:
  surface: '#161312'
  surface-dim: '#161312'
  surface-bright: '#3c3837'
  surface-container-lowest: '#100e0d'
  surface-container-low: '#1e1b1a'
  surface-container: '#221f1e'
  surface-container-high: '#2d2928'
  surface-container-highest: '#383433'
  on-surface: '#e9e1df'
  on-surface-variant: '#d0c2cd'
  inverse-surface: '#e9e1df'
  inverse-on-surface: '#33302e'
  outline: '#998d97'
  outline-variant: '#4d444d'
  surface-tint: '#eeb3f2'
  primary: '#ffd4ff'
  on-primary: '#4a1e52'
  primary-container: '#edb2f1'
  on-primary-container: '#704076'
  inverse-primary: '#7d4c84'
  secondary: '#efb2f4'
  on-secondary: '#4b1c54'
  secondary-container: '#64336c'
  on-secondary-container: '#dda1e2'
  tertiary: '#d8ea90'
  on-tertiary: '#2a3400'
  tertiary-container: '#bcce77'
  on-tertiary-container: '#49580c'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffd5ff'
  primary-fixed-dim: '#eeb3f2'
  on-primary-fixed: '#33053c'
  on-primary-fixed-variant: '#63356a'
  secondary-fixed: '#ffd6ff'
  secondary-fixed-dim: '#efb2f4'
  on-secondary-fixed: '#33033e'
  on-secondary-fixed-variant: '#64336c'
  tertiary-fixed: '#d9eb91'
  tertiary-fixed-dim: '#bdcf78'
  on-tertiary-fixed: '#171e00'
  on-tertiary-fixed-variant: '#3e4c00'
  background: '#161312'
  on-background: '#e9e1df'
  surface-variant: '#383433'
typography:
  display-lg:
    fontFamily: Roboto Flex
    fontSize: 57px
    fontWeight: '400'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Roboto Flex
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Roboto Flex
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  title-md:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0.15px
  body-lg:
    fontFamily: Roboto Flex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Roboto Flex
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  code-md:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  gutter: 16px
  margin-mobile: 16px
  margin-tablet: 24px
  container-padding: 20px
---

## Brand & Style

The brand personality is precise, technical, and high-performance, tailored specifically for developers and engineers working within an autonomous workshop environment. The interface leverages a refined **Corporate / Modern** aesthetic with **Minimalist** and **Developer-centric** undertones. It evokes a sense of control and deep focus, utilizing a "terminal-refined" approach where clean typography meets high-contrast code visualization.

The design emphasizes:
- **Technical Excellence:** Clean lines and monospaced accents that feel like an IDE.
- **Deep Focus:** A rich dark palette to reduce eye strain during long technical sessions.
- **High Utility:** Intentional use of color only where it signifies action or status change.
- **Fluidity:** Smooth Material 3 transitions and clear elevated surfaces to represent a sophisticated, automated ecosystem.

## Colors

The color system is built on a deep "Onyx" foundation to ensure maximum contrast for technical data. 

- **Core Palette:** The primary `edb2f1` (Lavender) acts as a high-visibility beacon for interactive elements. 
- **Surface Hierarchy:** Layering is achieved through varying shades of charcoal and obsidian, moving from `background` (lowest) to `surface-variant` (highest).
- **Semantic Diffing:** Specialized colors for code and logic changes are provided. `diff-added` uses a deep emerald tint, while `diff-removed` utilizes a muted ruby, ensuring clear differentiation in automated logs or script reviews without being jarring in a dark environment.

## Typography

This design system uses a dual-font strategy to balance readability with technical flavor.

- **Roboto (Sans-Serif):** Used for all primary interface elements, headings, and body copy. It provides the standard Material 3 clarity required for mobile navigation.
- **JetBrains Mono (Monospace):** Reserved for technical data, status labels, logs, and any variable-driven workshop output. It signals "system data" versus "interface text."
- **Scaling:** Headlines scale down by 15-20% on mobile devices to preserve screen real estate for technical logs.

## Layout & Spacing

The layout follows a **Fluid Grid** model with a base-4 vertical rhythm.

- **Mobile:** 4-column grid with 16px margins and 16px gutters.
- **Tablet:** 8-column grid with 24px margins and 24px gutters.
- **Workshop Philosophy:** In an autonomous workshop context, information density is high. While margins are 16px, internal component padding is kept tight (8px or 12px) to allow for more data visualization and log viewing. 
- **Safe Areas:** Ensure bottom navigation and floating action buttons (FABs) adhere to system gestures, with a clear 16px clearance from the device edge.

## Elevation & Depth

Visual hierarchy is established through **Tonal Layers** rather than heavy shadows, consistent with modern Material 3 dark themes.

- **Level 0 (Base):** Background `#0a0706` is the canvas.
- **Level 1 (Cards):** Surface `#141110` with a subtle 1px border of `#2b2726` to define boundaries.
- **Level 2 (Dialogs/Menus):** Surface Variant `#2b2726` with a soft ambient shadow (0px 4px 20px rgba(0,0,0,0.5)).
- **Interactions:** When an element is pressed, it uses a state layer (a semi-transparent overlay of the "On" color) to indicate depth change rather than a physical lift.

## Shapes

The shape language is "Hyper-Rounded" for structural containers but "Precision-Rounded" for functional elements.

- **Primary Containers:** Large sheets and background panels use a **32px** radius, creating a soft, approachable frame for the workshop's complex data.
- **Functional Components:** Cards, list items, and internal modules use a **16px** radius for a balanced, modern look.
- **Interactive Elements:** Buttons utilize the full pill-shape (100px) to distinguish them clearly from informational cards.

## Components

### Buttons
- **Primary:** Filled with `#edb2f1`, text in `#64336c`. 100px roundedness.
- **Secondary:** Outlined with `#edb2f1` (1px stroke), text in `#edb2f1`.
- **States:** Hover/Focus uses a 12% white overlay. Pressed uses a 16% white overlay.

### Cards & Lists
- **Cards:** Surface color `#141110`, 16px corner radius. No shadow; 1px border `#2b2726`.
- **Lists:** 16px radius on press-states. Use `JetBrains Mono` for metadata labels (e.g., timestamps or hex codes).

### Inputs & Selection
- **Input Fields:** Filled style using `#2b2726`. 8px corner radius. Bottom-line indicator in `#edb2f1` when focused.
- **Checkboxes/Radios:** Primary color `#edb2f1` for active states.
- **Chips:** Surface-variant background, 8px radius, `JetBrains Mono` text for technical tags.

### Workshop Specifics
- **Diff Viewer:** Use the defined `diff-added` and `diff-removed` background colors for line-level highlighting. Use `JetBrains Mono` for all content within these blocks.
- **Status Indicators:** Use Material Symbols Outlined. Standardize icon size at 24px for navigation and 20px for inline status.
