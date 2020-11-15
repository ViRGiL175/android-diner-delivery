plugins {
    id("java")
    id("org.springframework.boot") version "2.3.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

group = "ru.commandos"
version = "1.1.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

// Fix for encoding problems
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.test {
    useJUnit()
}
