package com.boogiwoogi.di.injectionexample.fragment.sharedViewModel

class DefaultExampleRepository : ExampleRepository {

    override fun fetchData(): List<Int> = listOf(
        1, 2, 3, 4, 5
    )
}
