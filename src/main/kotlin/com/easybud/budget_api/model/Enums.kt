package com.easybud.budget_api.model

enum class ItemType {
    FIXED,
    TEMPORARY,
    SPORADIC
}

enum class CategoryType {
    INCOME,
    EXPENSE
}

enum class RecurrencyType {
    WEEKLY,
    MONTHLY,
    YEARLY,
    NONE // For sporadic items
}
