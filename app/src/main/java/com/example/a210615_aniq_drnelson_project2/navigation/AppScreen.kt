package com.example.a210615_aniq_drnelson_project2.navigation

enum class AppScreen {

    SignUp,
    Login,
    Welcome,

    Main,
    Donate,
    Jobs,
    Volunteer,
    Profile,

    DonationDetail,
    PledgeDonation,
    DemoDonation,
    Summary,
    ThankYou,

    SupportMessage,

    AddVolunteer,

    EditProfile,

    LocationPicker;

    companion object {
        const val LOCATION_PICKER_ROUTE = "LocationPicker/{mode}"
        const val EDIT_JOB_ROUTE = "EditJob/{jobId}"

        fun locationPickerRoute(mode: String = "main"): String = "LocationPicker/$mode"
        fun editJobRoute(jobId: Int): String = "EditJob/$jobId"
    }
}
