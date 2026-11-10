# StudyMate AI
> Intelligent Desktop Study Assistant powered by **Google Gemini** and **JavaFX**.

---

## Quick Start (How to Run)

### Option 1: One-Click Launch (Recommended)
Simply double-click **`run.bat`** in this folder!

### Option 2: From Terminal
```powershell
.\mvnw.cmd javafx:run
```

### Option 3: In Visual Studio Code
1. Press `Ctrl + Shift + P` -> **Tasks: Run Task** -> **Run StudyMate AI**.

---

## Setting Up Your Gemini API Key

You can get a free Gemini API key in 30 seconds at [Google AI Studio](https://aistudio.google.com/app/apikey).

You have **3 ways** to provide the API key:
1. **In-App GUI (Easiest)**: Launch the app, click **API Settings** in the sidebar (or the bottom status badge), paste your key, click **Test Connection**, and click **Save & Connect**.
2. **Local `.env` or `config.properties`**: Create or edit `.env` or `config.properties` in this directory:
   ```properties
   GOOGLE_API_KEY=your_gemini_api_key_here
   GEMINI_MODEL=gemini-3.5-flash
   ```
3. **Environment Variable**:
   ```powershell
   [System.Environment]::SetEnvironmentVariable("GOOGLE_API_KEY", "your_gemini_api_key_here", "User")
   ```
