package com.easybud.budget_api.service

import com.easybud.budget_api.dto.*
import com.easybud.budget_api.model.BudgetAmount
import com.easybud.budget_api.model.BudgetCategory
import com.easybud.budget_api.model.CategoryType
import com.easybud.budget_api.repository.BudgetAmountRepository
import com.easybud.budget_api.repository.BudgetCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional
class BudgetCategoryService(
    private val categoryRepository: BudgetCategoryRepository,
    private val amountRepository: BudgetAmountRepository
) {
    fun createCategory(request: CreateCategoryRequest): CategoryResponse {
        if (categoryRepository.existsByNameIgnoreCase(request.name)) {
            throw IllegalArgumentException("Category with name '${request.name}' already exists")
        }

        //The user id will get fetched from the security context for oauth2 validation
        val userId = UUID.randomUUID()

        val category = BudgetCategory(
            userId = userId,
            name = request.name,
            description = request.description,
            categoryType = request.categoryType,
            itemType = request.itemType,
            recurrencyType = request.recurrencyType,
            effectiveDate = request.effectiveDate,
            endDate = request.endDate
        )

        val saved = categoryRepository.save(category)
        return saved.toResponse()
    }

    fun getAllCategories(): List<CategoryResponse> {
        return categoryRepository.findAll().map { it.toResponse() }
    }

    fun getCategoryById(id: Long): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id $id not found") }
        return category.toResponse()
    }

    fun updateCategory(id: Long, request: UpdateCategoryRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id $id not found") }

        request.name?.let { category.name = it }
        request.description?.let { category.description = it }
        request.categoryType?.let { category.categoryType = it }
        request.itemType?.let { category.itemType = it }
        request.recurrencyType?.let { category.recurrencyType = it }
        request.effectiveDate?.let { category.effectiveDate = it }
        request.endDate?.let { category.endDate = it }

        val updated = categoryRepository.save(category)
        return updated.toResponse()
    }

    fun deleteCategory(id: Long) {
        val category = categoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Category with id $id not found") }
        
        // Delete all associated amounts first
        val amounts = amountRepository.findByCategoryId(id)
        amountRepository.deleteAll(amounts)
        
        categoryRepository.delete(category)
    }

    fun searchCategories(name: String): List<CategoryResponse> {
        return categoryRepository.findByNameContainingIgnoreCase(name).map { it.toResponse() }
    }

    fun getCategoriesByType(type: CategoryType): List<CategoryResponse> {
        return categoryRepository.findByCategoryType(type).map { it.toResponse() }
    }

    private fun BudgetCategory.toResponse() = CategoryResponse(
        id = this.id!!,
        name = this.name,
        description = this.description,
        categoryType = this.categoryType,
        itemType = this.itemType,
        recurrencyType = this.recurrencyType,
        effectiveDate = this.effectiveDate,
        endDate = this.endDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

@Service
@Transactional
class BudgetAmountService(
    private val amountRepository: BudgetAmountRepository,
    private val categoryRepository: BudgetCategoryRepository
) {
    fun createAmount(request: CreateAmountRequest): AmountResponse {
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { NoSuchElementException("Category with id ${request.categoryId} not found") }

        val amount = BudgetAmount(
            category = category,
            amount = request.amount,
            note = request.note
        )

        val saved = amountRepository.save(amount)
        return saved.toResponse()
    }

    fun getAllAmounts(): List<AmountResponse> {
        return amountRepository.findAll().map { it.toResponse() }
    }

    fun getAmountById(id: Long): AmountResponse {
        val amount = amountRepository.findById(id)
            .orElseThrow { NoSuchElementException("Amount with id $id not found") }
        return amount.toResponse()
    }

    fun getAmountsByCategory(categoryId: Long): List<AmountResponse> {
        return amountRepository.findByCategoryId(categoryId).map { it.toResponse() }
    }

    fun updateAmount(id: Long, request: UpdateAmountRequest): AmountResponse {
        val amount = amountRepository.findById(id)
            .orElseThrow { NoSuchElementException("Amount with id $id not found") }

        request.amount?.let { amount.amount = it }
        request.note?.let { amount.note = it }

        val updated = amountRepository.save(amount)
        return updated.toResponse()
    }

    fun deleteAmount(id: Long) {
        if (!amountRepository.existsById(id)) {
            throw NoSuchElementException("Amount with id $id not found")
        }
        amountRepository.deleteById(id)
    }

    fun getBudgetSummary(startDate: LocalDate, endDate: LocalDate): BudgetSummaryResponse {
        val amounts = amountRepository.findByCategoryPeriodBetween(startDate, endDate)

        val incomes = amounts.filter { it.category.categoryType == CategoryType.INCOME }
        val expenses = amounts.filter { it.category.categoryType == CategoryType.EXPENSE }

        val totalIncome = incomes.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }

        val incomeBreakdown = incomes.groupBy { it.category.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val expenseBreakdown = expenses.groupBy { it.category.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return BudgetSummaryResponse(
            period = "$startDate to $endDate",
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            netBalance = totalIncome - totalExpenses,
            incomeBreakdown = incomeBreakdown,
            expenseBreakdown = expenseBreakdown
        )
    }

    private fun BudgetAmount.toResponse() = AmountResponse(
        id = this.id!!,
        categoryId = this.category.id!!,
        categoryName = this.category.name,
        amount = this.amount,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
