import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.31.0"
    id("com.diffplug.spotless") version "7.0.4"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val groupId = "com.smileidentity"
val artifactId = "smile-identity-core"
val apiVersion = "0.2.0"
project.version = findProperty("VERSION_NAME") as? String
    ?: file("VERSION").readText().trim()

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(groupId, artifactId)
    pom {
        name = "Smile ID Java SDK"
        description = "The Official Smile ID Java SDK"
        url = "https://github.com/smileidentity/smile-identity-core-java"
        licenses {
            license {
                name = "Smile ID Terms of Use"
                url = "https://usesmileid.com/terms-and-conditions"
                distribution = "repo"
            }
            license {
                name = "The MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "repo"
            }
        }
        scm {
            url = "https://github.com/smileidentity/smile-identity-core-java"
            connection = "scm:git:git://github.com/smileidentity/smile-identity-core-java.git"
            developerConnection = "scm:git:ssh://github.com/smileidentity/smile-identity-core-java.git"
        }
        developers {
            developer {
                id = "Support"
                name = "Smile Identity"
                email = "support@usesmileid.com"
                url = "https://www.usesmileid.com"
                organization = "Smile ID"
                organizationUrl = "https://usesmileid.com"
            }
            developer {
                id = "jumaallan"
                name = "Juma Allan"
                email = "juma@usesmileid.com"
                url = "https://github.com/jumaallan"
                organization = "Smile ID"
                organizationUrl = "https://usesmileid.com"
            }
        }
    }
}

tasks.named("compileJava") {
    dependsOn("createProperties")
}

tasks.register<WriteProperties>("createProperties") {
    destinationFile.set(layout.buildDirectory.file("src/main/resources/project.properties"))
    property("version", project.version)
    property("apiVersion", apiVersion)
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-moshi:3.0.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-adapters:1.14.0")
    implementation("com.serjltt.moshi:moshi-lazy-adapters:2.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.7.1")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("junit:junit:4.13.1")
}