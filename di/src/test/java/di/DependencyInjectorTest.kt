package di

import com.boogiwoogi.di.DefaultInstanceContainer
import com.boogiwoogi.di.DefaultModule
import com.boogiwoogi.di.DiInjector
import com.boogiwoogi.di.Inject
import com.boogiwoogi.di.InstanceContainer
import com.boogiwoogi.di.Module
import com.boogiwoogi.di.Qualifier
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DependencyInjectorTest {

    interface FakeInterface

    @Test
    fun `생성자에 인자가 있는 경우 자동으로 의존성 주입을 수행한다`() {
        // given
        data class ClassB(val data: String = "")
        class ClassA(val arg1: ClassB)

        val injector = DiInjector(
            applicationContainer = DefaultInstanceContainer(),
            module = DefaultModule()
        )

        // when
        val instanceA = injector.inject<ClassA>()
        val actual = instanceA.arg1

        // then
        val expected = ClassB()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `생성자의 인자의 생성자에 인자가 있는 경우 자동으로 의존성 주입을 수행한다`() {
        // given
        data class ClassF(val data: String = "")
        data class ClassE(val arg1: ClassF)
        data class ClassD(val arg1: ClassE)

        val injector = DiInjector(
            applicationContainer = DefaultInstanceContainer(),
            module = DefaultModule()
        )

        // when
        val instanceD = injector.inject<ClassD>()
        val actual = instanceD.arg1

        // then
        val expected = ClassE(ClassF())

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `생성자의 인자에 Ineject annotation이 붙어있는 프로퍼티의 경우 Container로부터 해당 프로퍼티 타입의 인스턴스를 가져온다`() {
        // given
        data class ClassB(val data: String = "")
        class ClassA(@Inject val arg1: ClassB)

        val container: InstanceContainer = mockk(relaxed = true)

        every {
            container.find(parameter = any())
        } returns ClassB()

        val injector = DiInjector(
            applicationContainer = container,
            module = DefaultModule()
        )

        // when
        val instanceA = injector.inject<ClassA>()
        val actual = instanceA.arg1

        // then
        val expected = ClassB()

        assertThat(actual).isEqualTo(expected)
        verify {
            container.find(parameter = any())
        }
    }

    @Test
    fun `생성자의 인자에 Inject annotation이 붙어있는 프로퍼티가 Container에 존재하지 않으면 해당 프로퍼티를 module을 통해 바로 생성한다`() {
        // given
        data class ClassB(val data: String = "")
        class ClassA(@Inject val arg1: ClassB)

        val container: InstanceContainer = mockk(relaxed = true)
        val module: Module = mockk(relaxed = true)

        every {
            container.find(parameter = any())
        } returns null

        every {
            module.provideInstanceOf(clazz = ClassB::class)
        } returns ClassB()

        val injector = DiInjector(
            applicationContainer = container,
            module = module
        )

        // when
        val instanceA = injector.inject<ClassA>()
        val actual = instanceA.arg1

        // then
        val expected = ClassB()

        assertThat(actual).isEqualTo(expected)
        verify {
            module.provideInstanceOf(clazz = ClassB::class)
        }
    }

    @Test
    fun `생성자의 인자에 Inject annotation이 붙어있는 프로퍼티가 Container에도 존재하지 않고 module을 통해 생성할 수 없는 경우 예외가 발생한다`() {
        // given
        data class ClassB(val data: String = "")
        class ClassA(@Inject val arg1: ClassB)

        val container: InstanceContainer = mockk(relaxed = true)
        val module: Module = mockk(relaxed = true)

        every {
            container.find(parameter = any())
        } returns null

        every {
            module.provideInstanceOf(clazz = ClassB::class)
        } returns null

        val injector = DiInjector(
            applicationContainer = container,
            module = module
        )

        // then
        assertThrows<IllegalArgumentException> {
            injector.inject<ClassA>()
        }
    }

    @Test
    fun `파라미터가 아닌 클래스 내부에 위치한 @Inject annotation이 붙어있는 프로퍼티에 대한 의존성 주입을 수행할 수 있다`() {
        // given
        data class ClassB(val data: String = "")
        class ClassA {
            @Inject
            var property: ClassB? = null
        }

        val instanceA = ClassA()

        val injector = DiInjector(
            applicationContainer = DefaultInstanceContainer(),
            module = DefaultModule(),
        )

        // when
        injector.inject(instanceA)
        val actual = instanceA.property

        // then
        val expected = ClassB()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `같은 인터페이스에 대한 구현체가 여러개가 있어도 Qualifier 어노테이션 타입으로 의존성 주입이 이루어진다`() {
        // given
        class FakeImpl1 : FakeInterface
        class FakeImpl2 : FakeInterface

        class ClassA(
            @Qualifier("FakeImpl1")
            val fake1: FakeInterface,
            @Qualifier("FakeImpl2")
            val fake2: FakeInterface
        )

        val module: Module = mockk(relaxed = true)

        every {
            module.provideInstanceOf("FakeImpl1")
        } returns FakeImpl1()

        every {
            module.provideInstanceOf("FakeImpl2")
        } returns FakeImpl2()

        val injector = DiInjector(
            applicationContainer = DefaultInstanceContainer(),
            module = module
        )

        // when
        val instanceA = injector.inject<ClassA>()
        val actual1 = instanceA.fake1
        val actual2 = instanceA.fake2

        // then
        assertAll(
            {
                assertTrue(actual1 is FakeImpl1)
                assertTrue(actual2 is FakeImpl2)
            }
        )
    }

    @Test
    fun `Qualifier에 선언한 타입의 인스턴스가 Container에 존재하지 않고 module에서 생성이 가능하지 않다면 예외가 발생한다`() {
        // given
        class FakeImpl1 : FakeInterface
        class FakeImpl2 : FakeInterface

        class ClassA(
            @Qualifier("FakeImpl1")
            val fake1: FakeInterface,
            @Qualifier("FakeImpl2")
            val fake2: FakeInterface
        )

        val module: Module = mockk(relaxed = true)

        every {
            module.provideInstanceOf("FakeImpl1")
        } returns null

        every {
            module.provideInstanceOf("FakeImpl2")
        } returns null

        val injector = DiInjector(
            applicationContainer = DefaultInstanceContainer(),
            module = module
        )

        // then
        assertThrows<IllegalArgumentException> {
            injector.inject<ClassA>()
        }
    }
}
