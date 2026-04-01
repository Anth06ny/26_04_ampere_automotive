package com.amonteiro.a26_04_ampere_automotive.automotive

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherAPI
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyScreen(carContext: CarContext)  : Screen(carContext) {

    var items = emptyList<WeatherEntity>()
    var errorMessage: String? = null
    var loading = true

    init {// Action à l'arrivée sur l'écran
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                items = WeatherAPI.loadWeathers("Toulouse")
                invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Une erreur est survenue"
            }
            loading = false
            invalidate() // Rafraîchir l'écran
        }
    }


    //Utilisation de ListTemplate
    override fun onGetTemplate(): Template {

        val header = Header.Builder().setTitle("Météo").setStartHeaderAction(Action.BACK).build()

        // Chargement en cours
        if (loading) {
            return MessageTemplate.Builder("Chargement...")
                .setHeader(header).build()
        }

        // Erreur
        if (!errorMessage.isNullOrBlank()) {
            return MessageTemplate.Builder(errorMessage!!)
                .setHeader(header).build()
        }

        //Création de la liste
        val itemListBuilder = SearchTemplate.Builder( object : SearchTemplate.SearchCallback {
            //onSearchTextChanged possible pour une détéction à chaque caractère

            override fun onSearchSubmitted(text: String) {
                loading = true
                invalidate() // Rafraîchir l'écran
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        items = WeatherAPI.loadWeathers(text)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        errorMessage = e.message ?: "Une erreur est survenue"
                    }
                    loading = false
                    invalidate() // Rafraîchir l'écran
                }
            }
        })

        val itemList = ItemList.Builder()
        //Création des lignes
        items.forEach { item ->
            val row = Row.Builder()
                .setTitle(item.name)
                .addText(item.getResume())
                .setOnClickListener {
                    CarToast.makeText(carContext, item.getResume(), CarToast.LENGTH_LONG).show()


                }
                .build()

            itemList.addItem(row)
        }
        itemListBuilder
            .setItemList(itemList.build())

        return itemListBuilder
            .build()
    }
}