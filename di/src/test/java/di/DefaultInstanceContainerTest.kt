
package di

import com.boogiwoogi.di.DefaultInstanceContainer
import com.boogiwoogi.di.Inject
import com.boogiwoogi.di.Instance
import com.boogiwoogi.di.InstanceContainer
import com.boogiwoogi.di.Qualifier
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.full.primaryConstructor

class DefaultInstanceContainerTest {

    lateinit var container: InstanceContainer

    @BeforeEach
    fun setup() {
        container = DefaultInstanceContainer()
    }

    @Test
    fun `kClass 타입과 일치하는 컨테이너 내부에 존재하는 인스턴스를 찾아서 반환한다`() {
        // given
        data class ClassA(val data: String = "")

        val container = DefaultInstanceContainer(
            instances = listOf(Instance(ClassA()))
        )

        // when
        val actual = container.find(ClassA::class)

        // then
        assertTrue(actual is ClassA)
    }

    @Test
    fun `kClass 타입과 일치하는 인스턴스가 없다면 null을 반환한다`() {
        // given
        data class ClassA(val data: String = "")

        val container = DefaultInstanceContainer()

        // when
        val actual = container.find(ClassA::class)

        // then
        assertNull(actual)
    }

    @Test
    fun `Qualifier annotation이 붙어있는 parameter의 인스턴스를 찾을 수 있다`() {
        // given
        data class ClassB(val data: String = "")
        data class ClassA(@Qualifier("ClassB") val arg1: ClassB)

        val container = DefaultInstanceContainer(
            instances = listOf(Instance(ClassB()))
        )

        // when
        val actual = ClassA::class
            .primaryConstructor
            ?.parameters
            ?.first()
            ?.run {
                container.find(this)
            }

        // then
        assertTrue(actual is ClassB)
    }

    @Test
    fun `Inject annotation이 붙어있는 parameter의 인스턴스를 찾을 수 있다`() {
        // given
        data class ClassB(val data: String = "")
        data class ClassA(@Inject val arg1: ClassB)

        val container = DefaultInstanceContainer(
            instances = listOf(Instance(ClassB()))
        )

        // when
        val actual = ClassA::class
            .primaryConstructor
            ?.parameters
            ?.first()
            ?.run {
                container.find(this)
            }

        // then
        assertTrue(actual is ClassB)
    }
}
