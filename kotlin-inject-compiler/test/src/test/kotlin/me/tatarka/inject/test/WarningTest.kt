package me.tatarka.inject.test

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isSuccess
import me.tatarka.inject.ProjectCompiler
import me.tatarka.inject.Target
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

class WarningTest {

    @TempDir
    lateinit var workingDir: File

    @ParameterizedTest
    @EnumSource(Target::class)
    fun warns_on_implicit_assisted_params(target: Target) {
        val projectCompiler = ProjectCompiler(target, workingDir)

        assertThat {
            projectCompiler.source(
                "MyComponent.kt",
                """
                import me.tatarka.inject.annotations.Component
                import me.tatarka.inject.annotations.Inject
                import me.tatarka.inject.annotations.Provides
                import me.tatarka.inject.annotations.Assisted
                
                @Inject class Bar
                @Inject class Foo(val bar: Bar, assisted: String)
                
                @Component abstract class MyComponent {
                    abstract fun foo(): (String) -> Foo
                }
                """.trimIndent()
            ).compile()
        }.isSuccess().warnings().all {
            contains("Implicit assisted parameters are deprecated and will be removed in a future version.")
            contains("Annotate the following with @Assisted: [assisted: String]")
        }
    }
}