package com.example.kobbajs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.w3c.dom.Text
import kotlin.math.max

class MainActivity : AppCompatActivity()  {

    private lateinit var angleButtonOne: ImageView
    private lateinit var angleButtonTwo: ImageView

    private lateinit var firstArc: EditText
    private lateinit var secondArc: EditText

    private lateinit var clearButton: ImageView

    private lateinit var result : TextView
    private lateinit var resultButton: ImageView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Скрыть системные кнопки и шторку
        hideSystemUI()

        // Инициализация UI
        angleButtonOne = findViewById(R.id.messureAngleOne)
        angleButtonTwo = findViewById(R.id.messureAngleTwo)

        firstArc = findViewById(R.id.angleDiffOne)
        secondArc = findViewById(R.id.angleDiffTwo)

        clearButton = findViewById(R.id.clearAll)

        result = findViewById(R.id.resultText)
        resultButton = findViewById(R.id.calculateButton)

        // Проверяем значение угла, если измерили его, и подставляем в соответсвующее текстовое поле
        val extras = this.intent.extras
        if (extras != null) {
            val angleType = intent.getStringExtra("valueToPass")
            val angle = intent.getStringExtra("angle")

            if (angleType.toString() == "AngleOne") {
                firstArc.setText(angle.toString())
            } else {
                secondArc.setText(angle.toString())
            }

            if (extras != null && extras.containsKey("firstValue")) {
                val firstValue = intent.getStringExtra("firstValue")
                firstArc.setText(firstValue.toString())
            } else if (extras != null && extras.containsKey("secondValue")) {
                val secondValue = intent.getStringExtra("secondValue")
                secondArc.setText(secondValue.toString())
            }
        }

        // Активация кнопок
        angleButtonOne.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    angleButtonOne.scaleX = 0.9f
                    angleButtonOne.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Действие при отпускании
                    angleButtonOne.scaleX = 1.0f
                    angleButtonOne.scaleY = 1.0f
                    startMessure("AngleOne")
                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }

        angleButtonTwo.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    angleButtonTwo.scaleX = 0.9f
                    angleButtonTwo.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Действие при отпускании
                    angleButtonTwo.scaleX = 1.0f
                    angleButtonTwo.scaleY = 1.0f
                    startMessure("AngleTwo")
                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }

        resultButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    resultButton.scaleX = 0.9f
                    resultButton.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    resultButton.scaleX = 1.0f
                    resultButton.scaleY = 1.0f

                    // Действие при отпускании
                    val firstArcText = firstArc.text.toString()
                    val secondArcText = secondArc.text.toString()

                    if (firstArcText != "" && secondArcText !="") {
                        val firstArcNumber = firstArcText.toInt()
                        val secondArcNumber = secondArcText.toInt()
                        val max = maxOf(firstArcNumber, secondArcNumber)
                        val min = minOf(firstArcNumber, secondArcNumber)

                        if (0.75 * max - min > 0.5) {
                            result.text = getString(R.string.result_one)
                        } else {
                            result.text = getString(R.string.result_two)
                        }

                    } else {
                        result.text = getString(R.string.result_none)
                    }

                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }

        clearButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    clearButton.scaleX = 0.9f
                    clearButton.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Действие при отпускании
                    clearButton.scaleX = 1.0f
                    clearButton.scaleY = 1.0f

                    firstArc.text.clear()
                    secondArc.text.clear()

                    result.text = ""

                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }
    }

    private fun startMessure(angleType: String) {
        val intent = Intent(this, AngleMessure::class.java)
        intent.putExtra("valueToPass", angleType)

        if (firstArc.text.toString() != "" && angleType == "AngleTwo") {
            intent.putExtra("firstValue", firstArc.text.toString())
        } else if (secondArc.text.toString() != "" && angleType == "AngleOne") {
            intent.putExtra("secondValue", secondArc.text.toString())
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Новый API для Android 11+
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Старый API для устройств до Android 11
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }
}