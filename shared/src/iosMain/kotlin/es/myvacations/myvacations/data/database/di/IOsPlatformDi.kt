package es.myvacations.myvacations.data.database.di

import es.myvacations.myvacations.data.database.DatabaseFactory
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import org.koin.dsl.module

val platformModule = module {
    single {
        DatabaseFactory()
    }

    single<MyVacationsDatabase> {
        get<DatabaseFactory>().create()
    }
}