package com.example.keepaccount

sealed class Screen(val route: String) {
    object ItemList : Screen("item_list")
    object EventList : Screen("event_list")
    object Strip : Screen("strip")
    object BarcodeInvoice : Screen("barcode_invoice")
    object Visual : Screen("visual")

    object AddItem : Screen("add_item/{year}/{month}/{day}") {
        fun createRoute(year: String, month: String, day: String) = "add_item/$year/$month/$day"
    }

    object AddEvent : Screen("add_event?eventId={eventId}") {
        fun createRoute(eventId: Int = -1) = "add_event?eventId=$eventId"
    }

    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Int) = "event_detail/$eventId"
    }
}
