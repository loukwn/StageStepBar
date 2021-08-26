package com.loukwn.stagestepbarexample

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import com.loukwn.stagestepbar.StageStepBar
import com.loukwn.stagestepbarexample.databinding.ActivityExampleBinding

class ExampleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareFields()
        setupListeners()
    }

    private val availableColors by lazy {
        listOf(
            getColorFromAttr(R.attr.colorPrimary),
            getColorFromAttr(R.attr.colorSecondary),
            Color.GRAY,
            Color.MAGENTA,
            Color.GREEN,
            Color.RED,
            Color.YELLOW
        )
    }

    private var filledTrackDefaultColorSelectionIndex = 0
    private var unfilledTrackDefaultColorSelectionIndex = 2
    private var filledThumbDefaultColorSelectionIndex = 0
    private var unfilledThumbDefaultColorSelectionIndex = 2

    private var currentStage = 0
    private var currentStep = 0

    @SuppressLint("SetTextI18n")
    private fun prepareFields() {
        binding.stepsInStagesTextInputEditText.setText("5,5,5")
        binding.nullStateCheckBox.isChecked = true
        binding.currentStateStageTextInputLayout.isEnabled = false
        binding.currentStateStepTextInputLayout.isEnabled = false
        binding.currentStateStageTextInputEditText.setText("0")
        binding.currentStateStepTextInputEditText.setText("0")
        binding.animationDurationInputEditText.setText("500")

        val orientationItems = listOf("Horizontal", "Vertical")
        val orientationAdapter =
            ArrayAdapter(this, R.layout.list_item, orientationItems)
        binding.orientationDropDown.setText("Horizontal")
        binding.orientationDropDown.setAdapter(orientationAdapter)

        val hDirectionItems = listOf("Auto", "Left to Right", "Right to Left")
        val hDirectionAdapter = ArrayAdapter(this, R.layout.list_item, hDirectionItems)
        binding.horizDirectionDropDown.setText("Auto")
        binding.horizDirectionDropDown.setAdapter(hDirectionAdapter)

        val vDirectionItems = listOf("Bottom to top", "Top to Bottom")
        val vDirectionAdapter = ArrayAdapter(this, R.layout.list_item, vDirectionItems)
        binding.verticalDirectionDropDown.setText("Bottom to Top")
        binding.verticalDirectionDropDown.setAdapter(vDirectionAdapter)

        val drawableItems = listOf("Default Shape", "User Provided")
        val filledTrackAdapter = ArrayAdapter(this, R.layout.list_item, drawableItems)
        binding.filledTrackDropDown.setText("Default Shape")
        binding.filledTrackDropDown.setAdapter(filledTrackAdapter)

        val unfilledTrackAdapter = ArrayAdapter(this, R.layout.list_item, drawableItems)
        binding.unfilledTrackDropDown.setText("Default Shape")
        binding.unfilledTrackDropDown.setAdapter(unfilledTrackAdapter)

        val filledThumbAdapter = ArrayAdapter(this, R.layout.list_item, drawableItems)
        binding.filledThumbDropDown.setText("Default Shape")
        binding.filledThumbDropDown.setAdapter(filledThumbAdapter)

        val unfilledThumbAdapter = ArrayAdapter(this, R.layout.list_item, drawableItems)
        binding.unfilledThumbDropDown.setText("Default Shape")
        binding.unfilledThumbDropDown.setAdapter(unfilledThumbAdapter)

        binding.thumbSizeSeekBar.progress = 50
        binding.thumbSizeValue.text = "${DEFAULT_THUMB_SIZE_DP}dp"
        binding.filledTrackSizeSeekBar.progress = 50
        binding.filledTrackSizeValue.text = "${DEFAULT_FILLED_TRACK_SIZE_DP}dp"
        binding.unfilledTrackSizeSeekBar.progress = 50
        binding.unfilledTrackSizeValue.text = "${DEFAULT_UNFILLED_TRACK_SIZE_DP}dp"

        binding.filledThumbDefaultColorView.setBackgroundColor(availableColors[filledThumbDefaultColorSelectionIndex])
        binding.stageStepBar.setFilledThumbToNormalShape(availableColors[filledThumbDefaultColorSelectionIndex])
        binding.unfilledThumbDefaultColorView.setBackgroundColor(availableColors[unfilledThumbDefaultColorSelectionIndex])
        binding.stageStepBar.setUnfilledThumbToNormalShape(availableColors[unfilledThumbDefaultColorSelectionIndex])
        binding.filledTrackDefaultColorView.setBackgroundColor(availableColors[filledTrackDefaultColorSelectionIndex])
        binding.stageStepBar.setFilledTrackToNormalShape(availableColors[filledTrackDefaultColorSelectionIndex])
        binding.unfilledTrackDefaultColorView.setBackgroundColor(availableColors[unfilledTrackDefaultColorSelectionIndex])
        binding.stageStepBar.setUnfilledTrackToNormalShape(availableColors[unfilledTrackDefaultColorSelectionIndex])
    }

    @SuppressLint("SetTextI18n")
    private fun setupListeners() {
        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.stepsInStagesTextInputEditText.doOnTextChanged { text, _, _, _ ->
            try {
                val stepsConfig = text.toString().split(",").map {
                    it.trim().toInt()
                }
                if (stepsConfig.isEmpty()) {
                    binding.stepsInStagesTextInputLayout.error =
                        "At least one number must be specified"
                }

                binding.stepsInStagesTextInputLayout.error = null
                binding.stageStepBar.setStageStepConfig(stepsConfig)
            } catch (e: NumberFormatException) {
                binding.stepsInStagesTextInputLayout.error = "Comma separated list of integers"
            }
        }

        binding.currentStateStageTextInputEditText.doOnTextChanged { text, _, _, _ ->
            try {
                currentStage = text!!.toString().toInt()

                binding.stageStepBar.setCurrentState(
                    StageStepBar.State(
                        stage = currentStage,
                        step = currentStep,
                    )
                )
                binding.currentStateStageTextInputLayout.error = null
            } catch (e: Exception) {
                binding.currentStateStageTextInputLayout.error = "Positive integer required"
            }
        }

        binding.currentStateStepTextInputEditText.doOnTextChanged { text, _, _, _ ->
            try {
                currentStep = text!!.toString().toInt()

                binding.stageStepBar.setCurrentState(
                    StageStepBar.State(
                        stage = currentStage,
                        step = currentStep,
                    )
                )

                binding.currentStateStepTextInputLayout.error = null
            } catch (e: Exception) {
                binding.currentStateStepTextInputLayout.error = "Positive integer required"
            }
        }

        binding.nullStateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.currentStateStageTextInputLayout.isEnabled = !isChecked
            binding.currentStateStepTextInputLayout.isEnabled = !isChecked

            if (isChecked) {
                binding.stageStepBar.setCurrentState(null)
            } else {
                binding.stageStepBar.setCurrentState(
                    StageStepBar.State(
                        stage = currentStage,
                        step = currentStep,
                    )
                )
            }
        }

        binding.animateToggleButton.setOnCheckedChangeListener { _, isChecked ->
            binding.animationDurationTextInputLayout.isEnabled = isChecked
            binding.stageStepBar.setAnimate(isChecked)
        }

        binding.animationDurationInputEditText.doOnTextChanged { text, _, _, _ ->
            val animationDuration = if (text.isNullOrEmpty()) 0 else text.toString().toInt()
            binding.stageStepBar.setAnimationDuration(animationDuration.toLong())
        }

        binding.orientationDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.stageStepBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                        height =
                            resources.getDimensionPixelOffset(R.dimen.stageStepBarSmallDimension)
                        width =
                            resources.getDimensionPixelOffset(R.dimen.stageStepBarLargeDimension)
                    }

                    binding.mainSeparator.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = binding.scrollContainer.id
                        bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                        width = 0
                        height = resources.getDimensionPixelOffset(R.dimen.mainSeparatorSize)
                    }

                    binding.scrollContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        startToEnd = ConstraintLayout.LayoutParams.UNSET
                        topToTop = ConstraintLayout.LayoutParams.UNSET
                        topToBottom = binding.stageStepBar.id
                    }

                    binding.stageStepBar.setOrientation(StageStepBar.Orientation.Horizontal)
                }
                1 -> {
                    binding.stageStepBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.UNSET
                        height =
                            resources.getDimensionPixelOffset(R.dimen.stageStepBarLargeDimension)
                        width =
                            resources.getDimensionPixelOffset(R.dimen.stageStepBarSmallDimension)
                    }

                    binding.mainSeparator.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        startToStart = binding.scrollContainer.id
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.UNSET
                        height = 0
                        width = resources.getDimensionPixelOffset(R.dimen.mainSeparatorSize)
                    }

                    binding.scrollContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        startToStart = ConstraintLayout.LayoutParams.UNSET
                        startToEnd = binding.stageStepBar.id
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = ConstraintLayout.LayoutParams.UNSET
                    }

                    binding.stageStepBar.setOrientation(StageStepBar.Orientation.Vertical)
                }
            }
        }

        binding.horizDirectionDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> binding.stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Auto)
                1 -> binding.stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Ltr)
                2 -> binding.stageStepBar.setHorizontalDirection(StageStepBar.HorizontalDirection.Rtl)
            }
        }

        binding.verticalDirectionDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> binding.stageStepBar.setVerticalDirection(StageStepBar.VerticalDirection.Btt)
                1 -> binding.stageStepBar.setVerticalDirection(StageStepBar.VerticalDirection.Ttb)
            }
        }

        binding.filledTrackDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.stageStepBar.setFilledTrackToNormalShape(availableColors[filledTrackDefaultColorSelectionIndex])
                    setColorSelectorClickable(binding.filledTrackDefaultColorView, true)
                }
                1 -> {
                    binding.stageStepBar.setFilledTrackToCustomDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.gradient_drawable
                        )!!
                    )
                    setColorSelectorClickable(binding.filledTrackDefaultColorView, false)
                }
            }
        }

        binding.unfilledTrackDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.stageStepBar.setUnfilledTrackToNormalShape(availableColors[unfilledTrackDefaultColorSelectionIndex])
                    setColorSelectorClickable(binding.unfilledTrackDefaultColorView, true)
                }
                1 -> {
                    binding.stageStepBar.setUnfilledTrackToCustomDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.gradient_drawable_2
                        )!!
                    )
                    setColorSelectorClickable(binding.unfilledTrackDefaultColorView, false)
                }
            }
        }

        binding.filledThumbDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.stageStepBar.setFilledThumbToNormalShape(availableColors[filledThumbDefaultColorSelectionIndex])
                    setColorSelectorClickable(binding.filledThumbDefaultColorView, true)
                }
                1 -> {
                    binding.stageStepBar.setFilledThumbToCustomDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.custom_shape_drawable
                        )!!
                    )
                    setColorSelectorClickable(binding.filledThumbDefaultColorView, false)
                }
            }
        }

        binding.unfilledThumbDropDown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    binding.stageStepBar.setUnfilledThumbToNormalShape(availableColors[unfilledThumbDefaultColorSelectionIndex])
                    setColorSelectorClickable(binding.unfilledThumbDefaultColorView, true)
                }
                1 -> {
                    binding.stageStepBar.setUnfilledThumbToCustomDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.custom_shape_drawable_2
                        )!!
                    )
                    setColorSelectorClickable(binding.unfilledThumbDefaultColorView, false)
                }
            }
        }

        binding.filledTrackDefaultColorView.setOnClickListener {
            filledTrackDefaultColorSelectionIndex =
                (filledTrackDefaultColorSelectionIndex + 1) % availableColors.size
            binding.filledTrackDefaultColorView.setBackgroundColor(availableColors[filledTrackDefaultColorSelectionIndex])
            binding.stageStepBar.setFilledTrackToNormalShape(availableColors[filledTrackDefaultColorSelectionIndex])
        }

        binding.unfilledTrackDefaultColorView.setOnClickListener {
            unfilledTrackDefaultColorSelectionIndex =
                (unfilledTrackDefaultColorSelectionIndex + 1) % availableColors.size
            binding.unfilledTrackDefaultColorView.setBackgroundColor(availableColors[unfilledTrackDefaultColorSelectionIndex])
            binding.stageStepBar.setUnfilledTrackToNormalShape(availableColors[unfilledTrackDefaultColorSelectionIndex])
        }

        binding.filledThumbDefaultColorView.setOnClickListener {
            filledThumbDefaultColorSelectionIndex =
                (filledThumbDefaultColorSelectionIndex + 1) % availableColors.size
            binding.filledThumbDefaultColorView.setBackgroundColor(availableColors[filledThumbDefaultColorSelectionIndex])
            binding.stageStepBar.setFilledThumbToNormalShape(availableColors[filledThumbDefaultColorSelectionIndex])
        }

        binding.unfilledThumbDefaultColorView.setOnClickListener {
            unfilledThumbDefaultColorSelectionIndex =
                (unfilledThumbDefaultColorSelectionIndex + 1) % availableColors.size
            binding.unfilledThumbDefaultColorView.setBackgroundColor(availableColors[unfilledThumbDefaultColorSelectionIndex])
            binding.stageStepBar.setUnfilledThumbToNormalShape(availableColors[unfilledThumbDefaultColorSelectionIndex])
        }


        binding.showThumbsToggleButton.setOnCheckedChangeListener { _, isChecked ->
            binding.stageStepBar.setThumbsVisible(isChecked)
        }

        binding.thumbSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = binding.thumbSizeSeekBar.progress * 2 * DEFAULT_THUMB_SIZE_DP / 100
                binding.stageStepBar.setThumbSize(value.dpToPx(this@ExampleActivity))
                binding.thumbSizeValue.text = "${value}dp"
            }
        })

        binding.filledTrackSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value =
                    binding.filledTrackSizeSeekBar.progress * 2 * DEFAULT_FILLED_TRACK_SIZE_DP / 100
                binding.stageStepBar.setCrossAxisFilledTrackSize(value.dpToPx(this@ExampleActivity))
                binding.filledTrackSizeValue.text = "${value}dp"
            }
        })

        binding.unfilledTrackSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value =
                    binding.unfilledTrackSizeSeekBar.progress * 2 * DEFAULT_UNFILLED_TRACK_SIZE_DP / 100
                binding.stageStepBar.setCrossAxisUnfilledTrackSize(value.dpToPx(this@ExampleActivity))
                binding.unfilledTrackSizeValue.text = "${value}dp"
            }
        })
    }

    private fun setColorSelectorClickable(view: View, isClickable: Boolean) {
        view.isClickable = isClickable
        view.isFocusable = isClickable
        view.isFocusableInTouchMode = isClickable
    }

    companion object {
        private const val DEFAULT_THUMB_SIZE_DP = 20
        private const val DEFAULT_FILLED_TRACK_SIZE_DP = 6
        private const val DEFAULT_UNFILLED_TRACK_SIZE_DP = 6
    }
}

private fun Int.dpToPx(context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}
