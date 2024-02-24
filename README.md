## WOOGI-di (dependency injection)

This is the Library for helping dependency injection.

On this library, it injects dependencies by using kotlin reflection.

Here's the examples of android project applied WOOGI-di.

### FOSS (Fc online stats searching)

Fc Online is football game in South Korea. This android application provides match results to user. Specially we provides relative match result for opponents you often played with.

[GitHub - fc-online-stats-searching/foss at oldversion](https://github.com/fc-online-stats-searching/foss/tree/oldversion)

[FOSS - Apps on Google Play](https://play.google.com/store/apps/details?id=com.foss.foss)

### Sample App

[GitHub - woowacourse/android-di at boogi-woogi](https://github.com/woowacourse/android-di/tree/boogi-woogi)

## Download

```groovy
implementation 'com.github.boogi-woogi:woogi-di:1.1.0'
```

## Documentation

### DiActivity

DiActivity is abstract class that inherits `AppCompatActivity` class.

If you want to inject some dependencies on your Activity you should inherit this class.

```groovy
abstract class DiActivity : AppCompatActivity() {

    /**
     instanceContainer is literally instance container that contains instances injected this Activity.
     **/
    val instanceContainer: InstanceContainer = ActivityInstanceContainer()

    /**
     module is interface which has the way of creating instances that you want to inject on this Activity.
     **/	
    abstract val module: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInjector()
    }
    ...

    /**
     this method makes injecting dependencies by using Activity's container, module and Application's container, module
     **/
    private fun setupInjector() {
        DiApplication.injector.inject(
            target = this,
            container = instanceContainer,
            module = module,
        )
    }
}
```

### DiApplication

This is open class that inherits `Application` class.

You can declare dependency that you’ll inject like this example.

```groovy
class FossApplication : DiApplication() {

    override fun onCreate() {
        super.onCreate()

        with(injector) {
            applicationContainer.add(
                Instance<MatchRepository>(
                    DefaultMatchRepository(
                        injector.inject(module = RetrofitModule)
                    )
                )
            )
						...
				}
```

---

### Injecting on ViewModel

```kotlin
/**
 * This is key method for injecting viewModel. Using container, module of arguments and DiApplication's module and container return viewModel factory that you want.
 */
inline fun <reified VM : ViewModel> diViewModelFactory(
    container: InstanceContainer,
    module: Module
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        DiApplication.run {
            injector.inject<VM>(
                module = module,
                container = container
            )
        }
    }
}

/**
 * You can use this method on DiActivity for injecting dependencies on viewModel.
 */
@MainThread
inline fun <reified VM : ViewModel> DiActivity.diViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class,
    { viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) },
)

/**
 * You can use this method on DiFragment for injecting dependencies on viewModel.
 * ViewModel will not be shared each fragments
 */
@MainThread
inline fun <reified VM : ViewModel> DiFragment.diViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class,
    { viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) },
)

/**
 * You can use this method on DiFragment for injecting dependencies on viewModel.
 * viewModel will be shared each fragments contained on same activity
 */
@MainThread
inline fun <reified VM : ViewModel> DiFragment.diActivityViewModels(): Lazy<VM> = ViewModelLazy(
    VM::class, { requireActivity().viewModelStore },
    { diViewModelFactory<VM>(instanceContainer, module) }
)

```

**Example of Injecting ViewModel on Activity**

```kotlin
class HomeActivity : DiActivity() {
    ...
    /**
     * if there's no dependencies that Activity should know, initialize module just like this.
     */
    override val module: Module by lazy { DefaultModule() }

    private val recentMatchViewModel: RecentMatchViewModel by diViewModels()
    ...
}
```

---

### Injecting on Activity’s memeber property

```kotlin
class CartActivity : DiActivity() {

    override val module: Module by lazy { ShoppingActivityModule(this) }

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }

    private val viewModel by diViewModels<CartViewModel>()

    /**
     * You should add clarify Inject annotation that you want to inject. This property is going to be initialize on OnCreate() as I explained before.
     */
    @Inject
    private lateinit var dateFormatter: DateFormatter

		...
}
```

---

### **Same parent class, different sub class.**

Sometimes, you can face that we have to inject the dependency of same interface, and other implementations.

How to resolve this situation?  You can resolve this problem using `@Qualifier` annotation and name of sub class.

Here’s the example on [Sample App](https://github.com/woowacourse/android-di/tree/boogi-woogi).

```kotlin
class ShoppingApplicationModule(private val context: Context) : DefaultModule() {

    ...
    @Qualifier("DatabaseCartRepository")
    @Provides
    fun provideDatabaseCartRepository(): CartRepository {
        return DatabaseCartRepository(
            ShoppingDatabase.getDatabase(context).cartProductDao()
        )
    }

    @Qualifier("InMemoryCartRepository")
    @Provides
    fun provideInMemoryCartRepository(): CartRepository {
        return InMemoryCartRepository()
    }
    ...
}
```

### Test code

On test code maybe you'll be able to see various cases.
https://github.com/boogi-woogi/woogi-di/tree/main/woogidi/src/test/java/com/boogiwoogi/woogidi