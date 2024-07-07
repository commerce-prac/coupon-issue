val bootJar: org.springframework.boot.gradle.tasks.bundling.BootJar by tasks

bootJar.enabled = false

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.redisson:redisson-spring-boot-starter:3.19.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}

tasks.withType<Test> {
    useJUnitPlatform()
}