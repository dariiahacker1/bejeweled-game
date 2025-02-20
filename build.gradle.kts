plugins {
    java
    application
}

group = "sk.tuke.kpi.kp"
version = "1.0"

application {
    mainClass.set("sk.tuke.kpi.kp.bejeweled.Bejeweled")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
