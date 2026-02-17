package com.easybud.budget_api.dto

import com.easybud.budget_api.model.CategoryType
import com.easybud.budget_api.model.ItemType
import com.easybud.budget_api.model.RecurrencyType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// Category DTOs
data class CreateCategoryRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    val description: String? = null,

    @field:NotNull(message = "Category type is required")
    val categoryType: CategoryType,

    @field:NotNull(message = "Item type is required")
    val itemType: ItemType,

    @field:NotNull(message = "Recurrency type is required")
    val recurrencyType: RecurrencyType,

    val effectiveDate: LocalDate? = null,

    val endDate: LocalDate? = null

)

data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryType: CategoryType? = null,
    val itemType: ItemType? = null,
    val recurrencyType: RecurrencyType? = null,
    val effectiveDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val categoryType: CategoryType,
    val itemType: ItemType,
    val recurrencyType: RecurrencyType,
    val effectiveDate: LocalDate?,
    val endDate: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// Amount DTOs
data class CreateAmountRequest(
    @field:NotNull(message = "Category ID is required")
    val categoryId: Long,

    @field:NotNull(message = "Amount is required")
    @field:Positive(message = "Amount must be positive")
    val amount: BigDecimal,

    val note: String? = null
)

data class UpdateAmountRequest(
    val amount: BigDecimal? = null,
    val note: String? = null
)

data class AmountResponse(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val amount: BigDecimal,
    val note: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// Summary DTOs
data class BudgetSummaryResponse(
    val period: String,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val netBalance: BigDecimal,
    val incomeBreakdown: Map<String, BigDecimal>,
    val expenseBreakdown: Map<String, BigDecimal>
)
