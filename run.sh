mvn clean compile vertx:run -Dvertx.verticle="io.zeko.restapi.example.BootstrapVerticle" \
    -Dvertx.jvmArguments="-Djava.util.logging.config.file=vertx_conf/logging.properties,-Dlogback.configurationFile=vertx_conf/logback.xml" \
    -Dvertx.runArgs="--redeploy-grace-period=5 --redeploy=src/main/kotlin/**/*"
#    -Dvertx.disableDnsResolver=true
#    -Dvertx.runArgs="-cluster"
