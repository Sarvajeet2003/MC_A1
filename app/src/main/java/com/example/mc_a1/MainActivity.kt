package com.example.mc_a1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val journeyManager = JourneyManager(this)
        journeyManager.initialize()

        val nextStopButton: Button = findViewById(R.id.nextStopButton)
        nextStopButton.setOnClickListener {
            journeyManager.moveToNextStop()
        }

        val toggleButton: ToggleButton = findViewById(R.id.toggleButton)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            journeyManager.updateDistanceUnit(isChecked)
        }

        val gifImageView: ImageView = findViewById(R.id.gifImageView)
        Glide.with(this)
            .asGif()
            .load(R.drawable.car_animation)
            .into(gifImageView)
    }
}

class JourneyManager(private val activity: AppCompatActivity) {
    private var isDistanceInKm = true
    private lateinit var distanceCoveredTextView: TextView
    private lateinit var distanceLeftTextView: TextView
    private lateinit var journeyProgressBar: ProgressBar
    private lateinit var progressTextView: TextView
    private lateinit var sampleStops: List<Stop>
    private var totalDistance: Int = 0
    private val currentStopIndex = AtomicInteger(0)

    data class Stop(val name: String, val distance: Int)

    fun initialize() {
        distanceCoveredTextView = findViewById(R.id.distanceCoveredTextView)
        distanceLeftTextView = findViewById(R.id.distanceLeftTextView)
        journeyProgressBar = findViewById(R.id.journeyProgressBar)
        progressTextView = findViewById(R.id.progressTextView)

//        sampleStops = getSampleStops(useLazyList = true)
        sampleStops = getSampleStops()
        totalDistance = calculateTotalDistance(sampleStops)

        updateDistanceTextViews()
        updateProgress(0)
    }

    fun moveToNextStop() {
        if (currentStopIndex.get() < sampleStops.size) {
            val currentStop = sampleStops[currentStopIndex.getAndIncrement()]
            val distanceCovered = currentStop.distance
            updateDistanceTextViews()
            updateProgress(distanceCovered)
        } else {
            println("Journey completed!")
        }
    }

    fun updateDistanceUnit(isKm: Boolean) {
        isDistanceInKm = isKm
        updateDistanceTextViews()
    }

    private fun updateProgress(distanceCovered: Int) {
        val progress = if (currentStopIndex.get() < sampleStops.size - 1) {
            (distanceCovered.toDouble() / totalDistance.toDouble() * 100).toInt()
        } else {
            100
        }
        journeyProgressBar.progress = progress
        progressTextView.text = "Progress: $progress%"
    }

    private fun updateDistanceTextViews() {
        val currentStop = sampleStops[currentStopIndex.get()]
        val distanceCovered = currentStop.distance
        val distanceLeft = totalDistance - distanceCovered

        val distanceCoveredText = if (isDistanceInKm) {
            "Distance Covered: $distanceCovered km"
        } else {
            val distanceCoveredMiles = distanceCovered * 0.621371
            "Distance Covered: $distanceCoveredMiles miles"
        }

        val distanceLeftText = if (isDistanceInKm) {
            "Distance Left: $distanceLeft km"
        } else {
            val distanceLeftMiles = distanceLeft * 0.621371
            "Distance Left: $distanceLeftMiles miles"
        }

        val nextStopsText = buildString {
            for (i in currentStopIndex.get() until sampleStops.size) {
//                append("${sampleStops[i].name} (${sampleStops[i].distance} km)\n")
                append("${sampleStops[i].name} \n")
                if (i < sampleStops.size - 1) {
                    append(" -> ")
                }
            }
        }

        val nextStop = if (currentStopIndex.get() < sampleStops.size - 1) {
            "Current Stop: $nextStopsText\n"
        } else {
            "Final Stop Reached"
        }

        distanceCoveredTextView.text = "$distanceCoveredText\n$nextStop"
        distanceLeftTextView.text = distanceLeftText
    }

    private fun calculateTotalDistance(stops: List<Stop>): Int {
        if (stops.isEmpty()) {
            return 0
        }
        return stops.last().distance
    }

    fun getSampleStops(useLazyList: Boolean = false): List<Stop> {
        if (useLazyList) {
            // Create a lazy list of stops
            val lazyStops = generateSequence(0) { it + 1 }
                .map { index ->
                    when (index) {
                        0 -> Stop("Delhi", 0)
                        1 -> Stop("Jaipur, Rajasthan", 280)
                        2 -> Stop("Ajmer, Rajasthan", 415)
                        3 -> Stop("Ahmedabad, Gujarat", 945)
                        4 -> Stop("Mumbai, Maharashtra", 1475)
                        5 -> Stop("Pune, Maharashtra", 2005)
                        6 -> Stop("Hubli, Karnataka", 2155)
                        7 -> Stop("Belgaum, Karnataka", 2315)
                        8 -> Stop("Hubli, Karnataka", 2480)
                        9 -> Stop("Bangalore, Karnataka", 2570)
                        else -> null
                    }
                }
                .takeWhile { it != null }
                .mapNotNull { it }
            return lazyStops.toList()
        } else {
            return listOf(
                Stop("Delhi", 0),
                Stop("Agra, Uttar Pradesh", 150),
                Stop("Jaipur, Rajasthan", 280),
                Stop("Kota, Rajasthan", 350),
                Stop("Ajmer, Rajasthan", 415),
                Stop("Udaipur, Rajasthan", 550),
                Stop("Ahmedabad, Gujarat", 945),
                Stop("Vadodara, Gujarat", 1100),
                Stop("Surat, Gujarat", 1300),
                Stop("Mumbai, Maharashtra", 1475),
                Stop("Thane, Maharashtra", 1600),
                Stop("Pune, Maharashtra", 2005),
                Stop("Satara, Maharashtra", 2150),
                Stop("Kolhapur, Maharashtra", 2250),
                Stop("Belgaum, Karnataka", 2315),
                Stop("Dharwad, Karnataka", 2400),
                Stop("Hubli, Karnataka", 2480),
                Stop("Davanagere, Karnataka", 2550),
                Stop("Tumkur, Karnataka", 2600),
                Stop("Hosur, Tamil Nadu", 2650),
                Stop("Bangalore, Karnataka", 2700),
                Stop("Electronic City, Karnataka", 2720),
                Stop("Mysuru, Karnataka", 2800),
                Stop("Ooty, Tamil Nadu", 3000),
                Stop("Coimbatore, Tamil Nadu", 3100),
                Stop("Salem, Tamil Nadu", 3200),
                Stop("Erode, Tamil Nadu", 3300),
                Stop("Tiruppur, Tamil Nadu", 3400),
                Stop("Coonoor, Tamil Nadu", 3500),
                Stop("Palakkad, Kerala", 3600),
                Stop("Thrissur, Kerala", 3700),
                Stop("Kochi, Kerala", 3800),
                Stop("Alappuzha, Kerala", 3900),
                Stop("Kollam, Kerala", 4000),
                Stop("Trivandrum, Kerala", 4100),
                Stop("Kanyakumari, Tamil Nadu", 4200)
            )
        }
    }

    private inline fun <reified T : android.view.View> findViewById(id: Int): T {
        return activity.findViewById(id)
    }
}