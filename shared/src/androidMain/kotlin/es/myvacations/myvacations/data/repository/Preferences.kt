package es.myvacations.myvacations.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val ActiveTripIdKey = stringPreferencesKey("active_trip_id")
val ActiveTripTitleKey = stringPreferencesKey("active_trip_title")
val ActiveTripPlaceKey = stringPreferencesKey("active_trip_place")
val ActiveTripStartDateKey = stringPreferencesKey("active_trip_startdate")
val ActiveTripEndDateKey = stringPreferencesKey("active_trip_enddate")
val ActiveTripMainCostKey = doublePreferencesKey("active_trip_maincost")
val ActiveTripMainBudgetKey = doublePreferencesKey("active_trip_mainbudget")
val ActiveTripCoverKey = stringPreferencesKey("active_trip_cover")
val ActiveTripExpensesKey = stringPreferencesKey("active_trip_expenses")
val ActiveTripFavouriteKey = booleanPreferencesKey("active_trip_favourite")
val WidgetEventPreferencesKey = stringPreferencesKey("widgetEventPreferences")