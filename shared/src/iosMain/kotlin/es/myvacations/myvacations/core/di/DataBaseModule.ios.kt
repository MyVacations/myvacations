package es.myvacations.myvacations.core.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataBaseModule(): List<Module> = listOf(
    module {
        single<MyVacationsDatabase> {
            val driver = NativeSqliteDriver(
                schema = MyVacationsDatabase.Schema,
                name = "myvacations.db"
            )

            MyVacationsDatabase(driver)
        }
    }
)