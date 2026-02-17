package com.easybud.budget_api.controller

import com.easybud.budget_api.dto.*
import com.easybud.budget_api.model.CategoryType
import com.easybud.budget_api.service.BudgetAmountService
import com.easybud.budget_api.service.BudgetCategoryService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/categories")
class BudgetCategoryController(
    private val categoryService: BudgetCategoryService
) {

    @PostMapping
    fun createCategory(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> {
        val category = categoryService.createCategory(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(category)
    }

    @GetMapping
    fun getAllCategories(
        @RequestParam(required = false) type: CategoryType?
    ): ResponseEntity<List<CategoryResponse>> {
        val categories = if (type != null) {
            categoryService.getCategoriesByType(type)
        } else {
            categoryService.getAllCategories()
        }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<CategoryResponse> {
        val category = categoryService.getCategoryById(id)
        return ResponseEntity.ok(category)
    }

    @GetMapping("/search")
    fun searchCategories(@RequestParam name: String): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryService.searchCategories(name)
        return ResponseEntity.ok(categories)
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        val category = categoryService.updateCategory(id, request)
        return ResponseEntity.ok(category)
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/amounts")
class BudgetAmountController(
    private val amountService: BudgetAmountService
) {

    @PostMapping
    fun createAmount(@Valid @RequestBody request: CreateAmountRequest): ResponseEntity<AmountResponse> {
        val amount = amountService.createAmount(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(amount)
    }

    @GetMapping
    fun getAllAmounts(
        @RequestParam(required = false) categoryId: Long?
    ): ResponseEntity<List<AmountResponse>> {
        val amounts = if (categoryId != null) {
            amountService.getAmountsByCategory(categoryId)
        } else {
            amountService.getAllAmounts()
        }
        return ResponseEntity.ok(amounts)
    }

    @GetMapping("/{id}")
    fun getAmountById(@PathVariable id: Long): ResponseEntity<AmountResponse> {
        val amount = amountService.getAmountById(id)
        return ResponseEntity.ok(amount)
    }

    @PutMapping("/{id}")
    fun updateAmount(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAmountRequest
    ): ResponseEntity<AmountResponse> {
        val amount = amountService.updateAmount(id, request)
        return ResponseEntity.ok(amount)
    }

    @DeleteMapping("/{id}")
    fun deleteAmount(@PathVariable id: Long): ResponseEntity<Void> {
        amountService.deleteAmount(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/summary")
    fun getBudgetSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<BudgetSummaryResponse> {
        val summary = amountService.getBudgetSummary(startDate, endDate)
        return ResponseEntity.ok(summary)
    }
}
