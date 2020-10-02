dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.sparkjava:spark-core:2.9.2")
}

tasks {
    jar {
       manifest {
           attributes["Main-Class"] = "me.ihdeveloper.humans.service.Main"
       }
    }
}
