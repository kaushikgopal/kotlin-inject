import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    id("kotlin-inject.detekt")
    id("kotlin-inject.merge-tests")
    kotlin("jvm")
    kotlin("kapt")
    alias(libs.plugins.jmh)
}

dependencies {
    implementation(kotlin("stdlib"))
    kapt(project(":kotlin-inject-compiler:kapt"))
    implementation(project(":kotlin-inject-runtime"))
    implementation(project(":integration-tests:module"))

    kaptTest(project(":kotlin-inject-compiler:kapt"))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.javax.inject)
    testImplementation(libs.assertk)

    jmhImplementation(kotlin("stdlib"))
}

sourceSets {
    val test by getting {
        kotlin.srcDir("../common/src/test/kotlin")
        kotlin.srcDir("../common-jvm/src/test/kotlin")
        java.srcDir("../common-jvm/src/test/java")
    }
}

kapt {
    arguments {
        arg("me.tatarka.inject.enableJavaxAnnotations", "true")
        arg("me.tatarka.inject.useClassReferenceForScopeAccess", "true")
    }
}

jmh {
    // https://github.com/melix/jmh-gradle-plugin/issues/159
    duplicateClassesStrategy.set(DuplicatesStrategy.EXCLUDE)
}

val SourceSet.kotlin: SourceDirectorySet
    get() = withConvention(KotlinSourceSet::class) { kotlin }