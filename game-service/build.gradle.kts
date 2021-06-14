dependencies {
    implementation(project(":protocol"))
    implementation("io.netty:netty-all:4.1.65.Final")
}

tasks {
    jar {
       manifest {
           attributes["Main-Class"] = "me.ihdeveloper.humans.service.Main"
       }
    }
}
