dependencies {
    implementation(project(":coupon-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}