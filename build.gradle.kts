import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
	war
}

group = "org.vieuxchameau"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_12
java.targetCompatibility = JavaVersion.VERSION_12

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.RELEASE"

configurations {
	all {
		exclude("org.springframework.boot", "spring-boot-starter-logging")
	}
}

val swaggerVersion = "2.9.2"
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security.oauth:spring-security-oauth2:2.3.6.RELEASE")
	implementation("org.springframework.security:spring-security-jwt:1.0.10.RELEASE")
	implementation("org.springframework.data:spring-data-redis")
	implementation("redis.clients:jedis:3.1.0")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("com.lmax:disruptor:3.4.2")
	implementation("org.springframework:spring-oxm")
//	Jaxb is no longer in the JDK since version 11
	implementation("javax.xml.bind:jaxb-api")
	implementation("org.glassfish.jaxb:jaxb-runtime")
	implementation("org.glassfish.jaxb:jaxb-core:2.3.0.1")
	implementation("io.springfox:springfox-swagger2:$swaggerVersion")
	implementation("io.springfox:springfox-swagger-ui:$swaggerVersion")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.amqp", "spring-rabbit-test")
	testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
	testImplementation("io.jsonwebtoken", "jjwt", "0.9.1")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

springBoot {
	buildInfo {
		properties {
			additional = mapOf(
				"jenkinsBuildTag" to (System.getProperties()["jenkinsBuildTag"] ?: "local"),
//                "encoding" to mapOf("source" to "UTF-8", "reporting" to "UTF-8"),
				"java" to mapOf("source" to java.sourceCompatibility, "target" to java.targetCompatibility)
			)
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "12"
	}
}
