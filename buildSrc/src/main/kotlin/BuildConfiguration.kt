import org.gradle.api.JavaVersion

object BuildConfiguration {
    const val COMPILE_SDK = 35
    const val MIN_SDK = 26
    const val TARGET_SDK = 35

    val SOURCE_COMPATIBILITY = JavaVersion.VERSION_20
    val TARGET_COMPATIBILITY = JavaVersion.VERSION_20
    const val JVM_TARGET = "20"
}