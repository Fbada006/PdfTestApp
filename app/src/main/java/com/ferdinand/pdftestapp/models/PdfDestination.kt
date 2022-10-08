package com.ferdinand.pdftestapp.models

/**
 * The destination informs the [com.ferdinand.pdftestapp.viewmodel.PdfViewModel] of the current screen the user is in and uses
 * this information to refresh the list displayed depending on the screen.
 *
 * If the user is in the [com.ferdinand.pdftestapp.ui.main.PdfListFragment] and clicks on the star icon, then the viewmodel only
 * updates the main list whereas a star/unstar action in the [com.ferdinand.pdftestapp.ui.search.SearchFragment] will update
 * only the currently searched list.
 * */
sealed class PdfDestination {
    object MainScreen : PdfDestination()
    object SearchScreen : PdfDestination()
}