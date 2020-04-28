# zeko-restapi-examples
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
This projects uses [Koin](https://insert-koin.io/) for dependency injection.

BootstrapVerticle is the main entry file and all dependencies needed are created here. 
[Jackson](https://github.com/FasterXML/jackson) is set to use snake case naming strategy when encoding JSON.
It is also set to convert date objects to ISO date time string. 

Example usage of SendGrid mail service can be found under user registration, which sends email in a circuit breaker strategy.

RestApiVerticle contains all the routes and cronjobs of the project. 
You will find error handler, JWT auth and token refresh here.

SQL queries are executed with [Jasync](https://github.com/jasync-sql/jasync-sql) async MySQL driver, 
however you can change to use [Hikari-CP](https://github.com/brettwooldridge/HikariCP) or [Vert.x JDBC client](https://vertx.io/docs/vertx-jdbc-client/kotlin/) in DB class
