plugins {
    java
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies{
    implementation(project(":plugin-api"))
    implementation(libs.javalin.core)
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks {
    shadowJar {
        val shadebase = "de.chojo.pluginjam.libs"
        //relocate("io.javalin", "$shadebase.javalin")
        //relocate("org.eclipse", shadebase)
        mergeServiceFiles()
        archiveFileName.set("PluginJam.jar")
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}
