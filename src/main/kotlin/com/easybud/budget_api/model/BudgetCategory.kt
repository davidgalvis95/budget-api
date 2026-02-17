package com.easybud.budget_api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "budget_categories")
data class BudgetCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var categoryType: CategoryType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var itemType: ItemType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var recurrencyType: RecurrencyType,

    @Column(name = "effective_date")
    var effectiveDate: LocalDate? = null,

    @Column(name = "effective_date")
    var endDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
