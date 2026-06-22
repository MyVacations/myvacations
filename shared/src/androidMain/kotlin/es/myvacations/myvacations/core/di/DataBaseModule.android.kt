package es.myvacations.myvacations.core.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dataBaseModule(): List<Module> = listOf(
    module {
        single<MyVacationsDatabase> {
            val driver = AndroidSqliteDriver(
                schema = MyVacationsDatabase.Schema,
                context = androidContext(),
                name = "myvacations.db"
            )

            MyVacationsDatabase(driver)
        }
    }
)