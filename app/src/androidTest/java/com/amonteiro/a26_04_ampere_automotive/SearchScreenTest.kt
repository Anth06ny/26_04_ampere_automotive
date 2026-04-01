package com.amonteiro.a26_04_ampere_automotive

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.amonteiro.a26_04_ampere_automotive.ui.screens.SearchScreen
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    //Création de l'environnement  de Test pour Compose
    @get:Rule
    val composeTestRule = createComposeRule()

    //Si besoin du context pour les resources de l'application par exemple
    //val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun testErrorStateDisplayed() {
        //On charge un ViewModel configuré dans un état
        val viewModel = MainViewModelTest().apply { errorState() }
        //On charge le composable à tester
        composeTestRule.setContent {
            SearchScreen(mainViewModel = viewModel)
        }

        // Message d'erreur visible
        composeTestRule.onNodeWithText(MainViewModelTest.ERROR_MESSAGE_TEST).assertIsDisplayed()

        // Vérifie que l'indicateur de chargement n'est pas visible
        val semantic = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        assertTrue(composeTestRule.onAllNodes(semantic, true).fetchSemanticsNodes().isEmpty())

        //La liste n'affiche aucun élément
        composeTestRule.onAllNodesWithText("Nice", substring = true, ignoreCase = true).assertCountEquals(viewModel.dataList.value.size)
    }


    @Test
    fun testLoadingStateDisplayed() {
        val viewModel = MainViewModelTest().apply { loadingState() }
        composeTestRule.setContent {
            SearchScreen(mainViewModel = viewModel)
        }

        // Message d'erreur non visible
        composeTestRule.onNodeWithText(MainViewModelTest.ERROR_MESSAGE_TEST).assertDoesNotExist()

        // Vérifie que l'indicateur de chargement est visible
        val semantic = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        assertTrue(composeTestRule.onAllNodes(semantic, true).fetchSemanticsNodes().isNotEmpty())

        //La liste n'affiche aucun élément
        composeTestRule.onAllNodesWithText("Nice", substring = true, ignoreCase = true).assertCountEquals(viewModel.dataList.value.size)
    }

    @Test
    fun testSuccessStateDisplayed() {
        val viewModel = MainViewModelTest().apply { successState() }
        composeTestRule.setContent {
            SearchScreen(mainViewModel = viewModel)
        }

        // Message d'erreur non visible
        composeTestRule.onNodeWithText(MainViewModelTest.ERROR_MESSAGE_TEST).assertDoesNotExist()

        // Vérifie que l'indicateur de chargement n'est pas visible
        val semantic = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        assertTrue(composeTestRule.onAllNodes(semantic, true).fetchSemanticsNodes().isEmpty())

        //La liste affiche des éléments
        composeTestRule.onAllNodesWithText("Nice", substring = true, ignoreCase = true).assertCountEquals(viewModel.dataList.value.size)

        // Vérifie que le titre de chaque élément sont affichés
        viewModel.dataList.value.forEach {
            composeTestRule.onNodeWithText(it.name, substring = true, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun testSearchWeathersSuccess() {
        //On charge un ViewModel configuré dans un état
        val viewModel = MainViewModelTest()
        //On charge le composable à tester
        composeTestRule.setContent {
            SearchScreen(mainViewModel = viewModel)
        }

        //Injecte "Nice" dans le TextField
        composeTestRule.onNodeWithText("Votre recherche ici")
            //Plus propre
            //.onNodeWithText(context.getText(R.string.searchtext_placeholder).toString())
            //ou avec le tag si déclaré dans le composable
            //.onNodeWithTag("SearchBar")
            .performTextReplacement("Toulouse")

        //Simule un clic sur le bouton
        composeTestRule.onNodeWithText("Load data").performClick()

        // Message d'erreur non visible
        composeTestRule.onNodeWithText(MainViewModelTest.ERROR_MESSAGE_TEST).assertDoesNotExist()

        // Vérifie que l'indicateur de chargement n'est pas visible
        val semantic = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        assertTrue(composeTestRule.onAllNodes(semantic, true).fetchSemanticsNodes().isEmpty())

        //Regarde s'il y a bien le bon nombre de résultat. 2 résultats + TextField
        composeTestRule.onAllNodesWithText("Toulouse", substring = true, ignoreCase = true).assertCountEquals(viewModel.dataList.value.size + 1)
    }
}