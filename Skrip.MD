# Video Demo Script – Humanity App (SDG 1: No Poverty)
**Name:** Muhammad Aniq Haikal Bin Mustazar  
**No. Matric:** A210615  
**Duration:** ~2 minutes 30 seconds

---

## [0:00 – 0:25] SDG Problem Explanation

"Assalamualaikum and hi everyone. My name is Muhammad Aniq Haikal, matric number A210615. My project is called Humanity, and it addresses SDG 1: No Poverty.

Poverty remains a serious issue in Malaysia and globally. Many people struggle due to unemployment, lack of community support, and limited access to basic needs. At the same time, people who want to help often don't know where to donate or how to find trusted organizations.

Humanity solves this by combining donation, job opportunities, and volunteer activities into one platform — making it easy for users to give, work, and volunteer to reduce poverty."

---

## [0:25 – 1:15] 7-Screen Demo & Navigation Flow

"Let me walk you through the app. Starting at the Sign Up screen — if a user already has an account, we can skip sign up and jump to Sign In using user data stored in local Room database.

Now we're on the Homepage. For this demo, I want to focus on the Donate feature. I tap on Donate page — here you can see all donation organizations fetched live from the Pledge API. This is real-time data showing which donations are currently open, so users won't accidentally donate to the wrong organization.

Now let me demonstrate the donation flow. I select Baby2Baby, which is still active because Pledge data is live. I fill up the donation form as required, press Pay, and we see the Summary screen. Lastly, I fill in a Support Message and send it.

After sending, a Thank You screen appears. The donation history is saved in local Room, and the Support Message is stored in Firestore so everyone can see it.

To verify — I go to the Profile screen by tapping the Profile icon. Here we can see Donation History and Support Messages the user has made. For public view, if we go back to Baby2Baby, at the bottom there's a Community Support Message section showing messages from all users."

---

## [1:15 – 1:50] Sensor & API Demo

"Now for the Sensor and API demo. Back on the Donate screen, I tap the map icon on the top navigation bar. I press Current Location — the app uses the device's GPS sensor to pick my current location, and with help from Google Maps API, it gives accurate coordinates just like Google Maps.

The donation list now shows organizations nearby my location. Since most Pledge API data is from the US, let me change location to Los Angeles and confirm. Now you can see Baby2Baby moves up in the list because it's nearby.

This combination of GPS sensor plus Google Maps API plus Pledge API gives users a location-aware donation experience."

---

## [1:50 – 2:10] Persistence Demo

"For persistence — Donation History is stored locally using Room database, and Support Messages sync to Firebase Firestore under each user's ID.

As proof, let me show side by side — on the left, the local Room database showing donation records, and on the right, Firestore console showing the same data stored in the cloud organized by user ID. This ensures data is available offline locally and synced online for community features."

---

## [2:10 – 2:30] Code Explanation

"Finally, a quick look at the code. Here is my Retrofit interface — PledgeApiService — which defines the API call to fetch live donation campaigns from Pledge. It uses a GET request and returns the data as a list.

And here is my sensor implementation — the LocationRepository uses FusedLocationProviderClient to get the device's current GPS coordinates. This data is then used to sort nearby donations.

That's it for my demo. Humanity helps reduce poverty by connecting people to donations, jobs, and volunteering — all in one app. Thank you for watching."

---

## Screen Flow Summary
1. Sign Up / Sign In Screen
2. Homepage Screen
3. Donate Screen (List of campaigns from Pledge API)
4. Location/Map Screen (GPS Sensor + Google Maps API)
5. Donation Form & Payment Screen
6. Thank You / Summary Screen
7. Profile Screen (History from Room + Firestore)

---

*End of Script*
