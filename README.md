VİCDANIM - Kapsamlı Proje Analizi ve Geliştirme Planı
Harika bir fikir! Ahlaki, oyunun gerçekten bağımlılık yaratıcısı, uzun soluklu bir deneyimi dönüştürmek için ayrıntılı bir mimari ve özellik seti hazırladı.
🎯 OYUN MEKANİKLERİ VE BAĞIMLILIK SİSTEMLERİ
1️⃣ Günlük Görev ve Streak Sistemi
📅 Günlük Giriş Ödülleri:
- 1. gün: 10 Vicdan Kristali
- 7. gün: Özel rozet + 100 kristal
- 30. gün: Altın vicdan rozetleri + özel hikaye kilidi açılır

🔥 Streak Sistemi:
- Üst üste giriş yapıldıkça çarpan artar (x1.5, x2, x3)
- Streak koparsa bonus sıfırlanır → kullanıcı her gün girmek zorunda
2️⃣ Seviye ve İlerleme Sistemi
kotlin// Seviye Yapısı
data class Level(
    val id: Int,
    val name: String,
    val requiredXP: Int,
    val unlockedScenarios: List<Scenario>,
    val rewards: List<Reward>
)

// Örnek seviyeler:
Seviye 1: "Vicdan Öğrencisi" → 3 temel senaryo
Seviye 5: "Adalet Arayıcısı" → Zorlu etik ikilemler açılır
Seviye 10: "Hikmet Yolcusu" → Çoklu karakter perspektifleri
Seviye 20: "Vicdan Rehberi" → Kendi senaryonu yazabilir
```

### 3️⃣ **Çoklu Para Birimi Ekonomisi**
```
💎 Vicdan Kristali → Günlük görevlerden kazanılır
⭐ Hikmet Puanı → Doğru kararlardan
🏆 Erdem Madalyası → Özel başarılardan
🎁 Hediye Kutuları → Rastgele açılır, rozet/tema içerir
```

### 4️⃣ **Hikaye Dallanması ve Sonuç Sistemi**
```
Her senaryo 3-5 bölüme ayrılır:
├─ Bölüm 1: Olay tanıtımı
├─ Bölüm 2-4: Seçimler (her seçim farklı dala götürür)
└─ Bölüm 5: Sonuç + Vicdan Muhasebesi

Örnek:
"Kopya Kağıdı" senaryosu → 12 farklı son
"Kayıp Cüzdan" senaryosu → 8 farklı son
"Siber Zorbalık" senaryosu → 15 farklı son
```

### 5️⃣ **Sosyal Özellikler**
```
👥 Arkadaş Sistemi:
- Arkadaşlarının vicdan profillerini gör
- Karşılaştırmalı liderlik tablosu
- Haftalık turnuvalar (en etik kararlar)

💬 Vicdan Konseyi:
- Zorlu kararlarda topluluktan oy al
- %80 oy alan seçenek bonus puan verir
```

### 6️⃣ **Rozet ve Başarı Sistemi (100+ Rozet)**
```
🏅 Kategori Rozetleri:
- "Altın Kalp" → 50 empati puanı
- "Adaletin Kılıcı" → 100 adalet kararı
- "Gerçeğin Sesi" → Hiç yalan söylemeden 20 senaryo

🎖️ Özel Rozetler:
- "Gece Kuşu" → Gece 00:00-05:00 arası giriş
- "Mükemmeliyetçi" → Bir senaryoyu 5 kez farklı oyna
- "Filozof" → Günlüğüne 30 gün üst üste yaz

🎨 KULLANICI DENEYİMİ ÖZELLİKLERİ
📊 Vicdan Profil Sistemi
kotlindata class ConscienceProfile(
    val honesty: Int,        // Dürüstlük
    val justice: Int,        // Adalet
    val empathy: Int,        // Empati
    val responsibility: Int, // Sorumluluk
    val patience: Int,       // Sabır
    val courage: Int,        // Cesaret
    val wisdom: Int          // Hikmet
)

// Radar Chart ile görselleştirme
// Her hafta gelişim raporu (PDF veya ekran görüntüsü paylaşılabilir)
```

### 🎭 **Karakter ve Avatar Sistemi**
```
🧑 Kişiselleştirilebilir avatar:
- Kıyafetler (rozet ile açılır)
- Ruh hayvanı (profil analizi sonucu belirlenir)
- Başlıklar ("Adalet Savaşçısı", "Merhamet Elçisi")
```

### 📖 **Vicdan Günlüğü (Journaling)**
```
Her senaryodan sonra:
- "Neden bu kararı aldın?" sorusu
- Serbest yazı (isteğe bağlı)
- Günlük 30 günlük tutulursa "Yazar" rozeti
- Geçmiş kararlarını görebilme, pişmanlık sistemi
```

### 🌙 **Gece Modu ve Temalar**
```
🎨 Açılabilir Temalar:
- Karanlık Mod (varsayılan)
- Zen Bahçesi (500 kristal)
- Osmanlı Klasik (1000 kristal)
- Fütüristik Neon (özel rozet gerekir)
```

---

## 🧩 TEKNİK MİMARİ (Kotlin + Android)

### **Proje Yapısı**
```
app/
├── data/
│   ├── models/
│   │   ├── Scenario.kt
│   │   ├── Choice.kt
│   │   ├── UserProfile.kt
│   │   └── Badge.kt
│   ├── repository/
│   │   ├── ScenarioRepository.kt
│   │   └── UserRepository.kt
│   └── database/
│       └── ConscienceDatabase.kt (Room)
├── ui/
│   ├── home/
│   ├── scenario/
│   ├── profile/
│   ├── journal/
│   └── leaderboard/
├── utils/
│   ├── NotificationManager.kt
│   ├── StreakManager.kt
│   └── RewardCalculator.kt
└── viewmodel/
    └── GameViewModel.kt
Kullanılacak Teknolojiler
kotlin// build.gradle.kts
dependencies {
    // Jetpack Compose (Modern UI)
    implementation("androidx.compose.ui:ui:1.5.4")
    
    // Room Database (Yerel veri)
    implementation("androidx.room:room-runtime:2.6.0")
    
    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Charts (Radar grafik için)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // Firebase (Bulut kayıt, liderlik tablosu)
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    
    // Notification (Günlük hatırlatıcı)
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    
    // Lottie (Animasyonlar)
    implementation("com.airbnb.android:lottie-compose:6.1.0")
}

🚀 GELİŞTİRME AŞAMALARI
Faz 1: Temel Sistem (2-3 hafta)

✅ Oda Veritabanı ile veri modelleri
✅ 10 temel senaryo (JSON formatında)
✅ Seçim sistemi ve puan planlama
✅ Profil ekranı (radar grafiği)
✅ Günlük giriş dosyalama sistemi

Faz 2: Oyunlaştırma (2 hafta)

✅ Rozet sistemi (25 rozet)
✅ Seviye sistemi
✅ Vicdan Kristali ekonomisi
✅ Bildirim sistemi
✅ Streak takibi

Faz 3: Sosyal Özellikler (2 hafta)

✅ Firebase sistemi
✅ Liderlik tablosu
✅ Arkadaş ekleme
✅ Haftalık turnuvalar

Faz 4: İçerik ve Cilalama (2 hafta)

✅ 50+ senaryo yazım
✅ Ses ve müzik
✅ Animasyonlar (Lottie)
✅ Beta testi ve optimizatörü
