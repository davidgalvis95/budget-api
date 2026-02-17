package com.easybud.budget_api.repository

import com.easybud.budget_api.model.BudgetAmount
import com.easybud.budget_api.model.BudgetCategory
import com.easybud.budget_api.model.CategoryType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface BudgetCategoryRepository : JpaRepository<BudgetCategory, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<BudgetCategory>
    fun findByCategoryType(categoryType: CategoryType): List<BudgetCategory>
    fun existsByNameIgnoreCase(name: String): Boolean
}

@Repository
interface BudgetAmountRepository : JpaRepository<BudgetAmount, Long> {
    fun findByCategoryId(categoryId: Long): List<BudgetAmount>

    @Query("SELECT a FROM BudgetAmount a WHERE a.category.effectiveDate <= :endDate AND (a.category.endDate IS NULL OR a.category.endDate >= :startDate)")
    fun findByCategoryPeriodBetween(startDate: LocalDate, endDate: LocalDate): List<BudgetAmount>
}
