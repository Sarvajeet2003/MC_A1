package com.example.mc_a1
import android.os.Bundle
import android.view.View
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
        // Use lazy list by default
        sampleStops = getSampleStops(useLazyList = true)
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
                append(sampleStops[i].name)
                if (i < sampleStops.size - 1) {
                    append(" -> ")
                }
            }
        }
        val nextStop = if (currentStopIndex.get() < sampleStops.size - 1) {
            "Next Stops: $nextStopsText"
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
                        else -> null // Stop generating after 10 stops
                    }
                }
                .takeWhile { it != null }
                .mapNotNull { it }
            return lazyStops.toList()
        } else {
            return listOf(
                Stop("Delhi", 0),
                Stop("Jaipur, Rajasthan", 280),
                Stop("Ajmer, Rajasthan", 415),
                Stop("Ahmedabad, Gujarat", 945),
                Stop("Mumbai, Maharashtra", 1475),
                Stop("Pune, Maharashtra", 2005),
                Stop("Hubli, Karnataka", 2155),
                Stop("Belgaum, Karnataka", 2315),
                Stop("Hubli, Karnataka", 2480),
                Stop("Bangalore, Karnataka", 2570)
            )
        }
    }
    private fun <T : View> findViewById(id: Int): T {
        return activity.findViewById(id)
    }
}