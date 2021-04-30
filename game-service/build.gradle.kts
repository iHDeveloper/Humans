dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.projectreactor.netty:reactor-netty-core:1.0.6")
    implementation("io.projectreactor.netty:reactor-netty-http:1.0.6")
}

tasks {
    jar {
       manifest {
           attributes["Main-Class"] = "me.ihdeveloper.humans.service.Main"
       }
    }
}
