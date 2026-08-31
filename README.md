# Customized Keyboard

Put a clown nose on everything.

# Fossify Keyboard

<img alt="Logo" src="graphics/icon.webp" width="120" />

<a href="https://play.google.com/store/apps/details?id=org.fossify.keyboard"><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height=80/></a> <a href="https://f-droid.org/packages/org.fossify.keyboard/"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on-en.svg" alt="Get it on F-Droid" height=80/></a> <a href="https://apt.izzysoft.de/fdroid/index/apk/org.fossify.keyboard"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height=80/></a>

Introducing Fossify Keyboard – your go-to solution for effortless and efficient typing. Experience a seamless typing experience designed to cater to all your needs, whether chatting with friends or inserting texts, numbers, or symbols.

**📶 OFFLINE FUNCTIONALITY:**    
Fossify Keyboard operates entirely offline without internet permission, allowing you to use it anytime, anywhere, without needing an internet connection. This also provides you with more privacy, security, and stability compared to other keyboards that connect to the internet.

**🌐 MULTIPLE LANGUAGES AND LAYOUTS:**    
Choose from a wide variety of languages and keyboard layouts. Fossify Keyboard supports multiple languages, making it easy for you to switch and type in your preferred language effortlessly.

**📋 HANDY CLIPBOARD:**    
Create clips and pin frequently used ones for easy access. This feature allows you to insert your most-used texts quickly, saving you time and effort.

**📳 CUSTOMIZABLE SETTINGS:**    
Tailor your typing experience by toggling vibrations, popups on key presses, and selecting your preferred language from the list of supported ones. Personalize your keyboard settings to suit your preferences.

**🌙 MATERIAL DESIGN AND DARK THEME:**    
Enjoy a sleek, modern design with a default dark theme. Fossify Keyboard offers a visually appealing and comfortable user experience, making typing a pleasure.

**🔒 PRIVACY AND SECURITY:**    
Your privacy is our top priority. Fossify Keyboard does not collect or share any user information with third parties. Experience peace of mind knowing your typing activity remains private and secure.

**🎨 CUSTOMIZABLE COLORS:**    
Personalize your keyboard with customizable colors. Fossify Keyboard allows you to choose and adjust colors to match your style and preferences.

**🌐 OPEN-SOURCE TRANSPARENCY:**    
Fossify Keyboard is fully open-source, providing you with transparency and security. You have access to the source code for audits, ensuring a trustworthy and reliable typing tool.

Experience typing like never before – efficient, personalized, and secure. Download Fossify Keyboard now and elevate your typing experience.

➡️ Explore more Fossify apps: https://www.fossify.org    
➡️ Open-Source Code: https://www.github.com/FossifyOrg    
➡️ Join the community on Reddit: https://www.reddit.com/r/Fossify    
➡️ Connect on Telegram: https://t.me/Fossify    

<div align="center">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" width="30%">
</div>


---

## Fork changes (Yet-Another-Keyboard)

- **Bugfix pass: investigated thoroughly, no changes made.** This app has
  no `PagerAdapter`/`EventBus` usage at all - it's a single
  `InputMethodService`, a different architecture from the other Yet-
  Another apps, so those known patterns don't apply. Backspace/delete
  already correctly uses `BreakIterator` for proper Unicode grapheme
  boundary detection (so deleting handles emoji and combining characters
  correctly, not naive single-`char` deletion), gated safely behind
  `isNougatPlus()` with a sane fallback on older versions. No dictionary
  or word-prediction feature exists to check for the kind of synchronous-
  lookup typing lag that's common in predictive keyboards. Core key
  handling (`onKey`) is well-guarded against null keyboard/input
  connection states.

  One low-confidence, not-acted-on observation: `onInitializeInterface()`
  calls `registerOnSharedPreferenceChangeListener(this)` with no matching
  `unregister` call anywhere in the file, and that method can be invoked
  more than once per service instance (not just once like `onCreate()`).
  Didn't treat this as a confirmed bug and didn't change it - Android's
  own `SharedPreferences` implementation keys this specific listener type
  in a way that's both idempotent for repeat registrations of the same
  instance and doesn't hold a strong reference, unlike the `EventBus`
  registration that was a real, confirmed leak in
  Yet-Another-Voice-Recorder. Noting it here rather than either silently
  ignoring it or overstating it as a fix.

## Beautify pass (Yet-Another-Keyboard)

- **Gel-styled key-press preview bubble and popup keyboard frame.** Both
  UI elements share `minikeyboard_background.xml` (a rounded-rect
  `LayerDrawable` with a fill and stroke layer) and were tinted at
  runtime via `findDrawableByLayerId(...).applyColorFilter(...)` - a flat
  `PorterDuff` tint, no gradient possible. The key-press bubble
  specifically is arguably the single most-seen UI surface in the whole
  app short of the keys themselves, since it appears on every keypress.

  Replaced both call sites (`MyKeyboardView.showKey()` for the bubble,
  and the `changedView.id == R.id.mini_keyboard_view` branch of
  `setupKeyboard()` for the popup keyboard's own frame) with a shared
  `Context.createGelMinikeyboardBackground()` - a real gradient + stroke
  + soft highlight built fresh via `GradientDrawable`/`LayerDrawable`,
  same technique as the Messages/Gallery gel elements built earlier this
  session. Verified the look via a render simulation against a realistic
  dark keyboard background before writing the final Kotlin, not just
  reasoned about.

  Deliberately left `minikeyboard_background.xml` itself untouched -
  confirmed both call sites already fully replace the drawable at
  runtime (assigning a brand new one, not mutating the existing one in
  place), so the static XML file's actual content is irrelevant to the
  visual result and doesn't need to change.

  Kept `strokeColor` as a genuinely separate, caller-provided value
  rather than deriving it from the base color the way rim colors are in
  the other gel helpers built this session - that matches how this
  element already worked (background and stroke are two independently
  configurable colors here already), not a new design choice.

  Caught and fixed a real mistake during this edit before it shipped: an
  early version of the replacement left a stale `mPreviewText!!.background
  = previewBackground` line immediately after the new drawable
  assignment, referencing a variable that no longer existed after the
  surrounding code was replaced - caught by grepping for the removed
  variable name across the file after the edit, not assumed to be clean.

  **Not verified on a real device** - the gradient/highlight look was
  verified via render simulation at representative proportions and
  colors, not confirmed against a live keyboard render.
