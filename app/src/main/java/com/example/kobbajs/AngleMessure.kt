package com.example.kobbajs

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class AngleMessure : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gravity: FloatArray? = null
    private var magnetic: FloatArray? = null
    private var magneticField: Sensor? = null

    private lateinit var angleTextOne: TextView
    private lateinit var angleTextTwo: TextView
    private lateinit var angleTextResult: TextView

    // Инициализация переменной для режима измерения
    var mode = "First angle"

    // Инициализация переменной для результата

    var resultAngle = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_angle_messure)

        // Получаем данные из Intent
        val angleType = intent.getStringExtra("valueToPass")

        val extras = intent.extras
        var firstValue = ""
        var secondValue = ""
        if (extras != null && extras.containsKey("firstValue")) {
            firstValue = intent.getStringExtra("firstValue").toString()
        } else if (extras != null && extras.containsKey("secondValue")) {
            secondValue = intent.getStringExtra("secondValue").toString()
        }

        // Скрыть системные кнопки и шторку
        hideSystemUI()

        // Инициализация UI
        angleTextOne = findViewById(R.id.angle_text_one)
        angleTextTwo = findViewById(R.id.angle_text_two)
        angleTextResult = findViewById(R.id.result_text)

        val backButton: ImageView = findViewById(R.id.back_button)
        val clearButton: ImageView = findViewById(R.id.clear_button)
        val saveButton: ImageView = findViewById(R.id.save_button)


        // Инициализация сенсоров
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Логика кнопки "назад"
        backButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    backButton.scaleX = 0.9f
                    backButton.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Действие при отпускании
                    backButton.scaleX = 1.0f
                    backButton.scaleY = 1.0f

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }

        // Логика кнопки "очистить"
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

                    angleTextOne.text = ""
                    angleTextTwo.text = "0"
                    angleTextResult.text = ""

                    mode = "First angle"

                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }

        // Логика кнопки "сохранить"
        saveButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    saveButton.scaleX = 0.9f
                    saveButton.scaleY = 0.9f
                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Действие при отпускании
                    saveButton.scaleX = 1.0f
                    saveButton.scaleY = 1.0f

                    when (mode) {

                        "First angle" -> mode = "Second angle"


                        "Second angle" -> {
                            if (angleTextOne.text.toString() != "" && angleTextTwo.text.toString() != "") {
                                val angleOne = angleTextOne.text.toString().toInt()
                                val angleTwo = angleTextTwo.text.toString().toInt()
                                val max = maxOf(angleOne, angleTwo)
                                val min = minOf(angleOne, angleTwo)

                                resultAngle = max - min

                                angleTextResult.text = String.format(getString(R.string.result_text) + " " + resultAngle.toString() + "°")
                                mode = "End"
                            }
                        }

                        "End" -> {
                            val intent = Intent(this, MainActivity::class.java)


                            intent.putExtra("valueToPass", angleType)
                            intent.putExtra("angle", resultAngle.toString())

                            if (firstValue != "") {
                                intent.putExtra("firstValue", firstValue)
                            } else if (secondValue != "") {
                                intent.putExtra("secondValue", secondValue)
                            }

                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                        }
                    }

                    true // Указываем, что событие обработано
                }

                else -> false
            }
        }
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

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magneticField?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> magnetic = event.values
        }

        if (gravity != null && magnetic != null) {
            val rotationMatrix = FloatArray(9)
            val inclinationMatrix = FloatArray(9)

            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravity, magnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                // Вычисляем угол наклона
                val pitch = Math.toDegrees(orientation[1].toDouble()).roundToInt()

                // Ограничиваем угол от -90 до 90 градусов
                val limitedAngle = pitch.coerceIn(-90, 90)

                // Обновляем UI
                when (mode) {
                    "First angle" -> angleTextOne.text = String.format(limitedAngle.toString())
                    "Second angle" -> angleTextTwo.text = String.format(limitedAngle.toString())
                }

            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не требуется обрабатывать в данном случае
    }
}