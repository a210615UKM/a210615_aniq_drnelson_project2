# Slide Presentation Guide — Humanity Demo Video

These slides are used **only** for the SDG Problem Explanation (intro) and the Code Explanation (ending).  
For the middle part (7-Screen Demo, Sensor & API Demo, Persistence Demo) you do **NOT** use slides — you show the live video/screen recording from your laptop.

> File format: `.svg` (open in any browser, then screenshot, OR import directly into PowerPoint/Canva).  
> Each slide is 1280 x 720 (16:9), ready for video.

---

## PART 1 — SDG PROBLEM EXPLANATION  [0:00 – 0:25]

### 🖼️ Slide 1 — `Slide1_Title.svg`
**Show while you say:**
> "Assalamualaikum and hi everyone. My name is Muhammad Aniq Haikal, matric number A210615. My project is called Humanity, and it addresses SDG 1: No Poverty."

*(Title + your name + matric. Show for the opening line only.)*

---

### 🖼️ Slide 2 — `Slide2_Problem.svg`
**Show while you say:**
> "Poverty remains a serious issue in Malaysia and globally. Many people struggle due to unemployment, lack of community support, and limited access to basic needs. At the same time, people who want to help often don't know where to donate or how to find trusted organizations."

*(The 3 problem cards + the red "where to help" box.)*

---

### 🖼️ Slide 3 — `Slide3_Solution.svg`
**Show while you say:**
> "Humanity solves this by combining donation, job opportunities, and volunteer activities into one platform — making it easy for users to give, work, and volunteer to reduce poverty."

*(The 3 pillars: Donate / Jobs / Volunteer.)*

---

## ▶️ PART 2 — LIVE DEMO  [0:25 – 2:10]
**NO SLIDES HERE.** Switch to your laptop screen recording / emulator video:
- 7-Screen Demo (navigation flow)
- Sensor & API Demo (GPS + Google Maps + Pledge API)
- Persistence Demo (Room + Firestore side by side)

---

## PART 3 — CODE EXPLANATION  [2:10 – 2:30]

### 🖼️ Slide 4 — `Slide4_Code_API.svg`
**Show while you say:**
> "Finally, a quick look at the code. Here is my Retrofit interface — PledgeApiService — which defines the API call to fetch live donation campaigns from Pledge. It uses a GET request and returns the data as a list."

*(Shows the real `getCampaigns()` function: the GET request, Bearer auth header, and the `Result<List<Campaign>>` return.)*

---

### 🖼️ Slide 5 — `Slide5_Code_Sensor.svg`
**Show while you say:**
> "And here is my sensor implementation — the LocationRepository uses FusedLocationProviderClient to get the device's current GPS coordinates. This data is then used to sort nearby donations."

*(Shows the real `getCurrentLocation()` function using FusedLocationProviderClient + PRIORITY_HIGH_ACCURACY.)*

---

### 🖼️ Slide 6 — `Slide6_ThankYou.svg`
**Show while you say:**
> "That's it for my demo. Humanity helps reduce poverty by connecting people to donations, jobs, and volunteering — all in one app. Thank you for watching."

*(Closing / thank-you slide.)*

---

## Quick Flow Summary
| Order | Slide | Section | Time |
|-------|-------|---------|------|
| 1 | Slide1_Title | Intro / name | 0:00–0:08 |
| 2 | Slide2_Problem | Poverty problem | 0:08–0:18 |
| 3 | Slide3_Solution | Humanity solution | 0:18–0:25 |
| — | *(LIVE VIDEO)* | Demo + Sensor/API + Persistence | 0:25–2:10 |
| 4 | Slide4_Code_API | Retrofit / API code | 2:10–2:18 |
| 5 | Slide5_Code_Sensor | Sensor code | 2:18–2:25 |
| 6 | Slide6_ThankYou | Closing | 2:25–2:30 |

---

## How to use the SVG files
**Option A (fastest):** Double-click an `.svg` to open it in your browser → press `PrtSc` or use Snipping Tool to capture, paste into your video editor.

**Option B (PowerPoint):** Insert → Pictures → choose the `.svg` (PowerPoint supports SVG directly).

**Option C (convert to PNG):** If your editor needs PNG, open the SVG in browser and screenshot, or use any free online SVG-to-PNG converter.
