package com.loukwn.stagestepbar_example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.loukwn.stagestepbar.StageStepBar
import com.loukwn.stagestepbar_example.ExampleViewViewModel.Companion.DEFAULT_FILLED_TRACK_SIZE_DP
import com.loukwn.stagestepbar_example.ExampleViewViewModel.Companion.DEFAULT_THUMB_SIZE_DP
import com.loukwn.stagestepbar_example.ExampleViewViewModel.Companion.DEFAULT_UNFILLED_TRACK_SIZE_DP
import com.loukwn.stagestepbar_example.databinding.ActivityExampleBinding

internal class ExampleViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleBinding
    private val viewModel: ExampleViewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareFields()
        listenToChanges()
        setupListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun prepareFields() {
        binding.stepsInStagesTextInputEditText.setText("5,5,5")
        binding.nullStateCheckBox.isChecked = true
        binding.currentStateStageTextInputLayout.isEnabled = false
        binding.currentStateStepTextInputLayout.isEnabled = false
        binding.currentStateStageTextInputEditText.setText("2")
        binding.currentStateStepTextInputEditText.setText("3")
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

        binding.filledThumbDefaultColorView.setBackgroundColor(viewModel.firstFilledThumbColor)
        binding.stageStepBar.setFilledThumbToNormalShape(viewModel.firstFilledThumbColor)
        binding.unfilledThumbDefaultColorView.setBackgroundColor(viewModel.firstUnfilledThumbColor)
        binding.stageStepBar.setUnfilledThumbToNormalShape(viewModel.firstUnfilledThumbColor)
        binding.filledTrackDefaultColorView.setBackgroundColor(viewModel.firstFilledTrackColor)
        binding.stageStepBar.setFilledTrackToNormalShape(viewModel.firstFilledTrackColor)
        binding.unfilledTrackDefaultColorView.setBackgroundColor(viewModel.firstUnfilledTrackColor)
        binding.stageStepBar.setUnfilledTrackToNormalShape(viewModel.firstUnfilledTrackColor)

        binding.stageStepBar.setDrawTracksBehindThumbs(true)
        binding.drawTracksBehindThumbsToggleButton.isChecked = true
    }

    @SuppressLint("SetTextI18n")
    private fun listenToChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is Event.AnimateChanged -> {
                        binding.animationDurationTextInputLayout.isEnabled = event.animate
                        binding.stageStepBar.setAnimate(event.animate)
                    }
                    is Event.AnimationDurationChanged -> {
                        binding.stageStepBar.setAnimationDuration(event.animationDuration)
                    }
                    is Event.CurrentStateChanged -> {
                        when (event.change) {
                            is CurrentStateChange.Invalid -> {
                                if (event.change.isForStage) {
                                    binding.currentStateStageTextInputLayout.error =
                                        event.change.error
                                } else {
                                    binding.currentStateStepTextInputLayout.error =
                                        event.change.error
                                }
                            }
                            is CurrentStateChange.Valid -> {
                                binding.currentStateStageTextInputLayout.error = null
                                binding.currentStateStepTextInputLayout.error = null
                                binding.stageStepBar.setCurrentState(event.change.state)
                                binding.currentStateStageTextInputLayout.isEnabled =
                                    event.change.state != null
                                binding.currentStateStepTextInputLayout.isEnabled =
                                    event.change.state != null
                            }
                        }
                    }
                    is Event.DrawTracksBehindThumbsChanged -> {
                        binding.stageStepBar.setDrawTracksBehindThumbs(event.enabled)
                    }
                    is Event.FilledThumbSetToCustom -> {
                        binding.stageStepBar.setFilledThumbToCustomDrawable(event.drawable)
                        setColorSelectorClickable(binding.filledThumbDefaultColorView, false)
                    }
                    is Event.FilledThumbSetToDefault -> {
                        binding.stageStepBar.setFilledThumbToNormalShape(event.color)
                        binding.filledThumbDefaultColorView.setBackgroundColor(event.color)
                        setColorSelectorClickable(binding.filledThumbDefaultColorView, true)
                    }
                    is Event.FilledTrackCrossAxisSizeChanged -> {
                        binding.stageStepBar.setCrossAxisFilledTrackSize(
                            event.sizeInPixel.dpToPx(
                                this@ExampleViewActivity
                            )
                        )
                        binding.filledTrackSizeValue.text = "${event.sizeInPixel}dp"
                    }
                    is Event.FilledTrackSetToCustom -> {
                        binding.stageStepBar.setFilledTrackToCustomDrawable(event.drawable)
                        setColorSelectorClickable(binding.filledTrackDefaultColorView, false)
                    }
                    is Event.FilledTrackSetToDefault -> {
                        binding.stageStepBar.setFilledTrackToNormalShape(event.color)
                        binding.filledTrackDefaultColorView.setBackgroundColor(event.color)
                        setColorSelectorClickable(binding.filledTrackDefaultColorView, true)
                    }
                    is Event.HorizontalDirectionChanged -> {
                        binding.stageStepBar.setHorizontalDirection(event.hDirection)
                    }
                    is Event.OrientationChanged -> {
                        when (event.orientation) {
                            StageStepBar.Orientation.Horizontal -> changeUiToHorizontal()
                            StageStepBar.Orientation.Vertical -> changeUiToVertical()
                        }
                        binding.stageStepBar.setOrientation(event.orientation)
                    }
                    is Event.ShowThumbsChanged -> binding.stageStepBar.setThumbsVisible(event.enabled)
                    is Event.StepsInStagesConfigChanged -> {
                        when (event.change) {
                            is StepsInStagesChange.Invalid -> {
                                binding.stepsInStagesTextInputLayout.error = event.change.error
                            }
                            is StepsInStagesChange.Valid -> {
                                binding.stageStepBar.setStageStepConfig(event.change.config)
                                binding.stepsInStagesTextInputLayout.error = null
                            }
                        }
                    }
                    is Event.ThumbSizeChanged -> {
                        binding.stageStepBar.setThumbSize(event.sizeInPixel.dpToPx(this@ExampleViewActivity))
                        binding.thumbSizeValue.text = "${event.sizeInPixel}dp"
                    }
                    is Event.UnfilledThumbSetToCustom -> {
                        binding.stageStepBar.setUnfilledThumbToCustomDrawable(event.drawable)
                        setColorSelectorClickable(binding.unfilledThumbDefaultColorView, false)
                    }
                    is Event.UnfilledThumbSetToDefault -> {
                        binding.stageStepBar.setUnfilledThumbToNormalShape(event.color)
                        binding.unfilledThumbDefaultColorView.setBackgroundColor(event.color)
                        setColorSelectorClickable(binding.unfilledThumbDefaultColorView, true)
                    }
                    is Event.UnfilledTrackCrossAxisSizeChanged -> {
                        binding.stageStepBar.setCrossAxisUnfilledTrackSize(
                            event.sizeInPixel.dpToPx(
                                this@ExampleViewActivity
                            )
                        )
                        binding.unfilledTrackSizeValue.text = "${event.sizeInPixel}dp"
                    }
                    is Event.UnfilledTrackSetToCustom -> {
                        binding.stageStepBar.setUnfilledTrackToCustomDrawable(event.drawable)
                        setColorSelectorClickable(binding.unfilledTrackDefaultColorView, false)
                    }
                    is Event.UnfilledTrackSetToDefault -> {
                        binding.stageStepBar.setUnfilledTrackToNormalShape(event.color)
                        binding.unfilledTrackDefaultColorView.setBackgroundColor(event.color)
                        setColorSelectorClickable(binding.unfilledTrackDefaultColorView, true)
                    }
                    is Event.VerticalDirectionChanged -> {
                        binding.stageStepBar.setVerticalDirection(event.vDirection)
                    }
                    null -> {}
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupListeners() {
        binding.closeBtn.setOnClickListener { finish() }

        binding.stepsInStagesTextInputEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.stepsInStagesChanged(text.toString())
        }

        binding.currentStateStageTextInputEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.currentStageChanged(text.toString())
        }

        binding.currentStateStepTextInputEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.currentStepChanged(text.toString())
        }

        binding.nullStateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.stateNullToggled(isChecked)
        }

        binding.animateToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.animateToggled(isChecked)
        }

        binding.animationDurationInputEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.animationDurationChanged(text.toString())
        }

        binding.orientationDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.orientationChanged(position)
        }

        binding.horizDirectionDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.horizontalDirectionChanged(position)
        }

        binding.verticalDirectionDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.verticalDirectionChanged(position)
        }

        binding.filledTrackDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.filledTrackDropdownSelected(position)
        }

        binding.unfilledTrackDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.unfilledTrackDropdownSelected(position)
        }

        binding.filledThumbDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.filledThumbDropdownSelected(position)
        }

        binding.unfilledThumbDropDown.setOnItemClickListener { _, _, position, _ ->
            viewModel.unfilledThumbDropdownSelected(position)
        }

        binding.filledTrackDefaultColorView.setOnClickListener { viewModel.filledTrackColorViewClicked() }
        binding.unfilledTrackDefaultColorView.setOnClickListener { viewModel.unfilledTrackColorViewClicked() }
        binding.filledThumbDefaultColorView.setOnClickListener { viewModel.filledThumbColorViewClicked() }
        binding.unfilledThumbDefaultColorView.setOnClickListener { viewModel.unfilledThumbColorViewClicked() }

        binding.thumbSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.thumbSizeChanged(progress)
            }
        })

        binding.filledTrackSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.filledTrackSizeChanged(progress)
            }
        })

        binding.unfilledTrackSizeSeekBar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListenerAdapter() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.unfilledTrackSizeChanged(progress)
            }
        })

        binding.showThumbsToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showThumbsToggled(isChecked)
        }

        binding.drawTracksBehindThumbsToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.drawTracksBehindThumbsChanged(isChecked)
        }
    }

    private fun changeUiToHorizontal() {
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
    }

    private fun changeUiToVertical() {
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
            topToBottom = binding.toolbar.id
            endToEnd = ConstraintLayout.LayoutParams.UNSET
            height = 0
            width = resources.getDimensionPixelOffset(R.dimen.mainSeparatorSize)
        }

        binding.scrollContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.UNSET
            startToEnd = binding.stageStepBar.id
            topToBottom = binding.toolbar.id
        }
    }

    private fun setColorSelectorClickable(view: View, isClickable: Boolean) {
        view.isClickable = isClickable
        view.isFocusable = isClickable
        view.isFocusableInTouchMode = isClickable
    }
}

