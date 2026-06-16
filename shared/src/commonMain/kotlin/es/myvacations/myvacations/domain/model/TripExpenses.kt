package es.myvacations.myvacations.domain.model

data class TripExpenses(
    val id: String,
    val name: String,
    val icon: ExpenseIcon,
    val amount: Double
)
{

}