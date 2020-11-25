plugins {
    id("java")
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

group = "ru.commandos.diner.server"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.reactivex.rxjava3:rxjava:3.0.6")
}

tasks.test {
    useJUnit()
}
