# StageStepBar

![License](https://img.shields.io/github/license/loukwn/StageStepBar?style=flat-square)
[![](https://jitpack.io/v/loukwn/StageStepBar.svg?style=flat-square)](https://jitpack.io/#loukwn/StageStepBar)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Ready-green?style=flat-square)

A staged progressbar that you can use if you want finer control of the steps in between its stages. You can customize:
- Number of steps between particular stages
- Look and feel of tracks and thumbs (stages)
- Direction and Orientation of the bar
- Animation speed (or even type in Compose)

## Showcase

Best way to test how this library is configured is to check out:
- [example](./example) module if you are interested in the View library.
- [example-compose](./example-compose) module if you are interested in the Compose library.

<img src="./art/showcase.gif" width="350" />

## Installation

Add Jitpack distribution to the end of the repositories in your root `build.gradle` file.

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the relevant dependency to the list of dependencies in your module's `build.gradle` file

```groovy
dependencies {
    // Add only this one if you want to include the View version
    implementation 'com.loukwn:stagestepbar:v0.0.1'

    // Add only this one if you want to include the Compose version
    implementation 'com.loukwn:stagestepbar-compose:v0.0.1'
}
```

## Usage

### Usage as a View

#### In XML:
```xml
<com.loukwn.stagestepbar.StageStepBar
    android:id="@+id/stageStepBar"
    android:layout_width="200dp"
    android:layout_height="100dp"
    app:ssb_stageStepConfig="5,5,5"
    app:ssb_animationDuration="500"
    /*...*/
     />
```

#### In Code:
```kotlin
val stageStepBar = findViewById<StageStepBar>(R.id.stageStepBar)
stageStepBar.setStageStepConfig(listOf(6,2,7))

```

#### Configuration

All XML attributes are optional (set to their defaults).

|XML attribute|Default|Description|
|---|---|---|
| `ssb_stageStepConfig`| `"1"` | This is a list in a string form that describes the steps per stages. For example a value of `6,4,3` means 6 steps from stage 0 to 1, 4 from stage 1 to 2 and 3 steps from stage 2 to 3. <br/><br/>**In code:** `setStageStepConfig(List<Int>)` |
| `ssb_currentState`|`null`| This is a list in a string form that describes the current state. For example a value of `2,4` means go to stage 2, step 4. Both the stage and the step provided here are coerced to the largest possible value (according to `stageStepConfig`). A null value here means that nothing is filled. <br/><br/>**In code:** `setCurrentState(State?)`|
| `ssb_animate`|`true`| Whether or not the progress change will animate. <br/><br/>**In code:** `setAnimate(Boolean)`|
| `ssb_animationDuration`|`500`| The duration of the animation <br/><br/>**In code:** `setAnimationDuration(Long)`|
| `ssb_orientation`|`horizontal`| Whether the bar is `horizontal` or `vertical`. <br/><br/>**In code:** `setOrientation(Orientation)`
| `ssb_horizontalDirection`|`auto`| The direction of the bar if it is horizontal. Possible values: `auto` (based on locale). `ltr`, `rtl`. <br/><br/>**In code:** `setHorizontalDirection(HorizontalDirection)`|
| `ssb_verticalDirection`|`btt`| The direction of the bar if it is vertical. Possible values: `ttb` (Top to Bottom). `btt`, (Bottom to Top). <br/><br/>**In code:** `setVerticalDirection(VerticalDirection)`|
| `ssb_filledTrackColor`|`#000000`| The color of the track when it is filled. <br/><br/>**In code:** `setFilledTrackToNormalShape(@ColorInt Int)`|
| `ssb_unfilledTrackColor`|`#A9A9A9`| The color of the track when it is not filled. <br/><br/>**In code:** `setUnfilledTrackToNormalShape(@ColorInt Int)`|
| `ssb_filledThumbColor`|`#000000`| The color of the thumbs when they are filled. <br/><br/>**In code:** `setFilledThumbToNormalShape(@ColorInt Int)`|
| `ssb_unfilledThumbColor`|`#6F6F6F`| The color of the thumbs when they are not filled. <br/><br/>**In code:** `setUnfilledThumbToNormalShape(@ColorInt Int)`|
| `ssb_filledTrackDrawable`| - | Sets a custom drawable to the filled track. <br/><br/>**In code:** `setFilledTrackToCustomDrawable(Drawable)`|
| `ssb_unfilledTrackDrawable`| - | Sets a custom drawable to the unfilled track. <br/><br/>**In code:** `setUnfilledTrackToCustomDrawable(Drawable)`|
| `ssb_filledThumbDrawable`| - | Sets a custom drawable to the filled thumbs. <br/><br/>**In code:** `setFilledThumbToCustomDrawable(Drawable)`|
| `ssb_unfilledThumbDrawable`| - | Sets a custom drawable to the unfilled thumbs. <br/><br/>**In code:** `setUnfilledThumbToCustomDrawable(Drawable)`|
| `ssb_thumbSize`| `20dp` | The size of the thumbs (both filled and unfilled). <br/><br/>**In code:** `setThumbSize(Int) Value is expected to be in pixels.`|
| `ssb_crossAxisFilledTrackSize`| `6dp` | The height of the filled track if the bar is horizontal or its width if it is vertical. <br/><br/>**In code:** `setCrossAxisFilledTrackSize(Int) Value is expected to be in pixels.`|
| `ssb_crossAxisUnfilledTrackSize`| `6dp` | The height of the unfilled track if the bar is horizontal or its width if it is vertical. <br/><br/>**In code:** `setCrossAxisUnfilledTrackSize(Int) Value is expected to be in pixels.`|
| `ssb_showThumbs`|`true`| Whether or not to show the thumbs. <br/><br/>**In code:** `setThumbsVisible(Boolean)`|

Note: Both thumbs and tracks can either have:
- A default shape but with the color specified by the user. Calling a `set*ToNormalShape()` function takes you to this state.
- Their entire look overriden by a custom Drawable. Calling a `set*ToCustomDrawable()` function takes you to this state.

### Usage as a Composable

```Kotlin
@Composable
fun Example() {
    StageStepBar(
        modifier = Modifier...,
        config = StageStepBarConfig(
            stageStepConfig = listOf(12,45,1)
            //...
        )
    )
}

```

The full [configuration model](./stagestepbar-compose/src/main/kotlin/com/loukwn/stagestepbar_compose/data/Config.kt) is pretty much the same between the Compose and the View version. However there are differences (mostly due to things that Compose makes easier):
- Instead of specifying an `Int` in pixels for the sizes of thumbs and/or tracks, in the Compose version that is passed in `Dp`.
- Instead of just specifying a duration value for the animation, in the Compose version one can pass instead an `AnimationSpec<Float>` effectively taking advantage of other kinds of animations like `spring()` etc.
- Instead of passing a `Drawable`, in Compose one will need to pass an `ImageBitmap` (because of what the Canvas in this case expects).

These differences aside, the behavior between the two versions should be pretty much identical.

## Licence

StageStepBar is available under MIT license. See [LICENSE file](LICENSE) for more information.
