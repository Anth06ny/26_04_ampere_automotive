package com.amonteiro.a26_04_ampere_automotive.ui.screens

import android.R.attr.text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.amonteiro.a26_04_ampere_automotive.R
import com.amonteiro.a26_04_ampere_automotive.data.remote.WeatherEntity
import com.amonteiro.a26_04_ampere_automotive.ui.MainViewModel
import com.amonteiro.a26_04_ampere_automotive.ui.theme._26_04_ampere_automotiveTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

//Code affiché dans la Preview, thème claire, thème sombre
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
            or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SearchScreenPreview() {
    _26_04_ampere_automotiveTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //Jeu de donnée pour la Preview
            val mainViewModel : MainViewModel = viewModel()
            mainViewModel.loadFakeData()

            SearchScreen(modifier = Modifier.padding(innerPadding),
                mainViewModel = mainViewModel)
        }
    }
}

//Code affiché dans la Preview, thème claire, thème sombre
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
            or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SearchScreenPreview2() {
    _26_04_ampere_automotiveTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //Jeu de donnée pour la Preview
            val mainViewModel : MainViewModel = viewModel()
            mainViewModel.loadFakeData(errorMessage = "UNe erreur")

            SearchScreen(modifier = Modifier.padding(innerPadding),
                mainViewModel = mainViewModel)
        }
    }
}

//Code affiché dans la Preview, thème claire, thème sombre
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
            or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SearchScreenPreview3() {
    _26_04_ampere_automotiveTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //Jeu de donnée pour la Preview
            val mainViewModel : MainViewModel = viewModel()
            mainViewModel.loadFakeData(errorMessage = "UNe erreur", runInProgress = true)

            SearchScreen(modifier = Modifier.padding(innerPadding),
                mainViewModel = mainViewModel)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(modifier:Modifier = Modifier, mainViewModel: MainViewModel = viewModel()) {

    val searchText = rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    //Accès à une permission
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = {
            if (it) { //Permission OK
                mainViewModel.errorMessage.value = "Permission : $it"
            } else { //Permission refusée
                mainViewModel.errorMessage.value = "Il faut la permission"
            }
        })

    //val list = mainViewModel.dataList //.filter { it.title.contains(searchText.value, true) }
    //Grâce à la fonction collectAsStateWithLifecycle(), Compose peut observer les changements de la liste
    val list by mainViewModel.dataList.collectAsStateWithLifecycle()
    val runInProgress by mainViewModel.runInProgress.collectAsStateWithLifecycle()
    val errorMessage by mainViewModel.errorMessage.collectAsStateWithLifecycle()


    Column(modifier= modifier.fillMaxSize()) {

        SearchBar(searchText = searchText)

        MyError(errorMessage = errorMessage)

        AnimatedVisibility(visible = runInProgress){
            CircularProgressIndicator()
        }

        //Version officiel on remonte les événements et on utilise un by pour searchText
        //SearchBar(
        //    texte = searchText,
        //    onValueChange = {
        //        searchText = it
        //    }
        //)



        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(list.size) {
                PictureRowItem(
                    modifier = Modifier.padding(8.dp),
                    data = list[it]
                )
            }
        }

        Row(modifier = Modifier.align(CenterHorizontally)) {
            Button(
                onClick = {
                    locationPermissionState.launchPermissionRequest()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Clear filter")
            }

            Button(
                onClick = {
                    //Lancera le chargement des données dans les variables que les composables écoutent
                    mainViewModel.loadWeathers(searchText.value)
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Load data")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier, searchText: MutableState<String>) {
    //On prend en paramètre searchText afin que le composant parent puisse l'utiliser

    TextField(
        value = searchText.value,
        onValueChange = { it:String -> searchText.value = it }, //Action
        leadingIcon = { //Image d'icône
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        },
        singleLine = true,
        label = { Text("Votre recherche ici") }, //Texte d'aide qui se déplace
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)//Hauteur minimum
            .padding(8.dp)
    )
}

@Composable
fun PictureRowItem(modifier: Modifier = Modifier, data: WeatherEntity) {

    //Persiste à la recomposition
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiary)
    ) {
        AsyncImage(
            model = data.weather.firstOrNull()?.icon ?: "",
            contentDescription = data.weather.firstOrNull()?.description ?: "",
            contentScale = ContentScale.FillWidth,
            //Pour toto.png. Si besoin de choisir l'import pour la classe R, c'est celle de votre package
            //Image d'échec de chargement qui sera utilisé par la preview
            //error = painterResource(R.drawable.toto),
            //Image d'attente.
            //placeholder = painterResource(R.drawable.toto),
            onError = { println(it) },
            modifier = Modifier
                .heightIn(max = 100.dp)
                .widthIn(max = 100.dp)
        )

        Column(Modifier.weight(1f).clickable { isExpanded = !isExpanded }) {
            Text(
                text = data.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            //En mettant directement le calcul dans Text plutôt que dans une variable seul Text sera recomposé
            Text(
                text = if (isExpanded)data.getResume() else (data.getResume().take(20) + "..."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .animateContentSize()  //Pour l'animation d'agrandissement
            )
        }
    }
}

//Le composant est réutilisable avec n'importe quelle chaine de caractère
@Composable
fun MyError(
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    //permet d'afficher / masquer l'erreur avec une animation
    AnimatedVisibility(!errorMessage.isNullOrBlank()) {
        Text(
            text = errorMessage ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onError,
            modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.error)
        )
    }
}