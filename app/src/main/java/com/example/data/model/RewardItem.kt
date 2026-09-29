package com.example.data.model

data class RewardBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requiredStars: Int,
    val category: String
)

val REWARD_BADGES: List<RewardBadge> = listOf(
    RewardBadge(
        id = "first_steps",
        title = "المستكشف الصغير",
        description = "تعلم وتعرف على أول ٥ أرقام",
        icon = "🌟",
        requiredStars = 5,
        category = "استكشاف"
    ),
    RewardBadge(
        id = "number_artist",
        title = "خطاط الأرقام",
        description = "تمرن واكتب الأرقام على اللوحة",
        icon = "🎨",
        requiredStars = 15,
        category = "كتابة"
    ),
    RewardBadge(
        id = "balloon_master",
        title = "صياد البالونات",
        description = "فرقع البالونات الصحيحة بمهارة",
        icon = "🎈",
        requiredStars = 25,
        category = "ألعاب"
    ),
    RewardBadge(
        id = "counting_champ",
        title = "بطل العد السريع",
        description = "أتقن عد الحيوانات والفواكه بدقة",
        icon = "🏆",
        requiredStars = 40,
        category = "عد"
    ),
    RewardBadge(
        id = "math_wizard",
        title = "العبقري الصغير",
        description = "حل مسائل الجمع والطرح البسيطة",
        icon = "🚀",
        requiredStars = 60,
        category = "حساب"
    ),
    RewardBadge(
        id = "super_star",
        title = "تاج المعرفة",
        description = "أكمل رحلة الأرقام وأصبح بطلاً حقيقياً",
        icon = "👑",
        requiredStars = 80,
        category = "تفوق"
    )
)
