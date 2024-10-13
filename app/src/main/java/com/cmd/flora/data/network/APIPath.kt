package com.cmd.flora.data.network

typealias Path = String

object APIPath {
    fun login(): Path = "login"
    fun forgotPass(): Path = "forgot-password"
    fun resetPass(): Path = "reset-password"
    fun refreshToken(): Path = "refresh"
    fun registerToken(): Path = "register_token"
    fun me(): Path = "me"
    fun homeNotify(): Path = "home-notify"
    fun newLetter(): Path = "newsletter"
    fun facility(): Path = "facility"
    fun tel(): Path = "tel_list"
    fun serviceHistory(): Path = "me/order-histories"
    fun information(): Path = "information"
    fun detailInformation(id: String? = null): Path = information() + id?.let { "/${it}" }
    fun logout(): Path = "logout"
    fun withdraw(): Path = "withdrawal"
    fun contactGenre(): Path = "enum/contact_genre"
    fun event(): Path = "event"
    fun prefecture(): Path = "enum/prefecture"
    fun contactFacilityList(): Path = "enum/contact_facility_list"
    fun sendContact(): Path = "contact"
    fun togglePushNotification(): Path = "toggle-push-notification"
    fun unReadNotification(): Path = "unread"
    fun getPoint(id: Int? = null): Path = "point" + id?.let { "/${it}" }
    fun updateEmail(): Path = "me/update-email"
    fun unregisterToken(): Path = "unregister-token"
}