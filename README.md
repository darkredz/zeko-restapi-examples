# Zeko Rest API Example Project
Example project in Kotlin language using [Zeko Rest API framework](https://github.com/darkredz/zeko-restapi-framework)

## Getting Started
Clone the project, cd to the folder.

Run the shell script to start the RESTful backend in development mode(hot reload enabled)
```shell script
./run.sh
```

Open browser and go to http://localhost:9999

Now, you should be able to test your API endpoints now with browser, [Postman](https://www.postman.com) or tool of your choice

After running app is compiled, you will also find swagger.json under api-doc folder. 
Just import it into Postman and start playing around 


## Brief Explanation
You should import the database [zeko_test.sql](https://github.com/darkredz/zeko-restapi-examples/blob/master/zeko_test.sql) into your MySQL server for the demo to work.

This projects uses [Koin](https://insert-koin.io/) for dependency injection.

[BootstrapVerticle](https://github.com/darkredz/zeko-restapi-examples/blob/master/src/main/kotlin/io/zeko/restapi/examples/BootstrapVerticle.kt) is the main entry file and all dependencies needed are created here. 
[Jackson](https://github.com/FasterXML/jackson) is set to use snake case naming strategy when encoding JSON.
It is also set to convert date objects to ISO date time string. 

Example usage of [SendGrid](https://sendgrid.com/) mail service can be found under user registration, which sends email in a circuit breaker strategy.
You can uncomment/comment the code in the bootstrap class to switch to [Mandrill](https://mandrillapp.com/)

[RestApiVerticle](https://github.com/darkredz/zeko-restapi-examples/blob/master/src/main/kotlin/io/zeko/restapi/examples/RestApiVerticle.kt) contains all the routes and cronjobs of the project. 
You will find error handler, JWT auth and token refresh here.

SQL queries are executed with [Jasync](https://github.com/jasync-sql/jasync-sql) async MySQL driver, 
however you can change to use [Hikari-CP](https://github.com/brettwooldridge/HikariCP) or [Vert.x JDBC client](https://vertx.io/docs/vertx-jdbc-client/kotlin/) in [DB class](https://github.com/darkredz/zeko-restapi-examples/blob/master/src/main/kotlin/io/zeko/restapi/examples/DB.kt)
