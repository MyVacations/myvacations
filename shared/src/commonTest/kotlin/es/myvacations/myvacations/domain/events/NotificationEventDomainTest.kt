package es.myvacations.myvacations.domain.events

import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.presentation.utils.TravelIcon
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationEventDomainTest {

    private fun createTestTrip(
        mainBudget: Double,
        expenseAmounts: List<Double>
    ): TripDomain {
        val expenses = expenseAmounts.mapIndexed { index, amount ->
            TripExpensesDomain(
                id = "e_$index",
                name = "Expense $index",
                icon = TravelIcon.RESTAURANT,
                amount = amount
            )
        }
        return TripDomain(
            id = "test-trip",
            title = "Test Trip",
            place = Country.SPAIN,
            startDate = LocalDate(2026, 7, 18),
            endDate = LocalDate(2026, 7, 25),
            mainCost = 0.0,
            mainBudget = mainBudget,
            optionalExpenses = expenses,
            cover = TripCover.BARCELONA,
            favourite = false
        )
    }

    data class BudgetTestCase(
        val description: String,
        val mainBudget: Double,
        val expenses: List<Double>,
        val expectedNotificationType: NotificationType
    )

    @Test
    fun verifyBudgetCases() {
        val testCases = listOf(
            BudgetTestCase(
                description = "Normal budget with plenty remaining",
                mainBudget = 1000.0,
                expenses = listOf(500.0),
                expectedNotificationType = NotificationType.BUDGET_OK
            ),
            BudgetTestCase(
                description = "Just above the BUDGET_LOW threshold (15.1% remaining)",
                mainBudget = 1000.0,
                expenses = listOf(849.0), // 15.1% remaining
                expectedNotificationType = NotificationType.BUDGET_OK
            ),
            BudgetTestCase(
                description = "Exact BUDGET_LOW threshold boundary (15.0% remaining)",
                mainBudget = 1000.0,
                expenses = listOf(850.0), // Exactly 15.0% remaining
                expectedNotificationType = NotificationType.BUDGET_LOW
            ),
            BudgetTestCase(
                description = "Just below the BUDGET_LOW threshold (14.9% remaining)",
                mainBudget = 1000.0,
                expenses = listOf(851.0), // 14.9% remaining
                expectedNotificationType = NotificationType.BUDGET_LOW
            ),
            BudgetTestCase(
                description = "Budget exactly exhausted (0% remaining)",
                mainBudget = 1000.0,
                expenses = listOf(1000.0),
                expectedNotificationType = NotificationType.BUDGET_FINISHED
            ),
            BudgetTestCase(
                description = "Budget exceeded (negative remaining)",
                mainBudget = 1000.0,
                expenses = listOf(1001.0),
                expectedNotificationType = NotificationType.BUDGET_EXCEEDED
            ),
            BudgetTestCase(
                description = "mainBudget is 0.0 with no expenses",
                mainBudget = 0.0,
                expenses = emptyList(),
                expectedNotificationType = NotificationType.BUDGET_FINISHED
            ),
            BudgetTestCase(
                description = "mainBudget is 0.0 with some expenses",
                mainBudget = 0.0,
                expenses = listOf(10.0),
                expectedNotificationType = NotificationType.BUDGET_EXCEEDED
            ),
            BudgetTestCase(
                description = "Multiple expenses summing up exactly to the BUDGET_LOW threshold (15.0% remaining)",
                mainBudget = 100.0,
                expenses = listOf(40.0, 30.0, 15.0), // Total 85.0, 15.0% remaining
                expectedNotificationType = NotificationType.BUDGET_LOW
            ),
            BudgetTestCase(
                description = "Multiple expenses summing up exactly to BUDGET_FINISHED threshold",
                mainBudget = 100.0,
                expenses = listOf(50.0, 25.0, 25.0), // Total 100.0, 0% remaining
                expectedNotificationType = NotificationType.BUDGET_FINISHED
            )
        )

        for (case in testCases) {
            val trip = createTestTrip(case.mainBudget, case.expenses)
            val actual = trip.currentBudgetNotificationType()
            assertEquals(
                case.expectedNotificationType,
                actual,
                "Failed case '${case.description}': expected ${case.expectedNotificationType} but got $actual"
            )
        }
    }
}
