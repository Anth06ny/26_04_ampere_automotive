package com.amonteiro.a26_04_ampere_automotive.automotive

import android.app.PendingIntent
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.amonteiro.a26_04_ampere_automotive.MainActivity
import com.amonteiro.a26_04_ampere_automotive.R

class MainScreen(carContext: CarContext) : Screen(carContext) {

    private var outsideTemp = 0f
    private var fuelLevel = "-"

    private val carPropertyManager = Car.createCar(carContext)?.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager


    init {

        //Récupération de la température intérieur (Sans VHAL)
        val sensorManager = carContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        tempSensor?.let {
            sensorManager.registerListener(object : SensorEventListener {

                //Changement de valeur
                override fun onSensorChanged(event: SensorEvent) {
                    outsideTemp = event.values[0]
                    invalidate()
                }

                //Evolution du niveau de precision
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

                //On peut choisir la frequence d'actualisation
            }, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        //Niveau de fuel (VHAL)
        // Lecture du niveau de carburant (en mL)
        try {
            val prop = carPropertyManager?.getProperty<Float>(VehiclePropertyIds.FUEL_LEVEL, 0)
            val ml = prop?.value ?: 0f
            fuelLevel = "${ml / 1000} L"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onGetTemplate(): Template {

        val header = Header.Builder().setTitle("Mon Application").setStartHeaderAction(Action.APP_ICON).build()

        val icon = CarIcon.Builder(IconCompat.createWithResource(carContext, R.mipmap.ic_launcher)).build()
        val gridItemList = ItemList.Builder()
            .addItem(
                GridItem.Builder()
                    .setTitle("Automotive")
                    .setImage(icon)
                    .setOnClickListener {
                        screenManager.push(MyScreen(carContext))
                    }
                    .build())
            .addItem(
                GridItem.Builder()
                .setTitle("Mobile")
                .setImage(icon)
                .setOnClickListener {
//L'Activity doit avoir été déclaré dans l'AndroidManifest
                    val intent = Intent(carContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    PendingIntent.getActivity(carContext, 0, intent, PendingIntent.FLAG_IMMUTABLE).send()

                }
                .build())
            .addItem(
                GridItem.Builder()
                .setTitle("Température amb : $outsideTemp")
                .setImage(icon)
                .build())
            .addItem(
                GridItem.Builder()
                    .setTitle("Fuel : $fuelLevel")
                    .setImage(icon)
                    .build())
            .build()

        return GridTemplate.Builder()
            .setSingleList(gridItemList)
            .setHeader(header)
            .build()
    }

}