                                                                    Architecture Overview
![Service](https://user-images.githubusercontent.com/7619618/132039293-4acb870d-55cc-4fe1-a239-f8a5d6f055bc.jpeg)

The architecture consists of following -  
Eureka Server - 
1. This is service registry server. All the micro services register themselves into Eureka server and the Eureka server knows all the client services running on each port and IP addresses. 
2. Need for Eureka server - In production when applications are deployed on cloud platforms, then all servers or containers use dynamic IPs. So we can’t store IPs and port of all the services in config files. 
3. Whenever service A wants to talk to  service B, they first talk to Eureka Server. Then Eureka provides the running instances of service B.
4. Whenever a new instance of a microservices comes up, it would register itself with Eureka server.
5. For service discovery, Eureka uses a central registry to maintain network locations of all micro services.
6. If for some reason the IP address and port of any service changes, new values will be immediately registered in the service registry. 
7. To enable eureka server you need to add @EnableEurekaServer annotation in the main class.
8. Whenever Eureka starts up, it will try to register itself as a client. So make sure to add this property in application.properties - “eureka.client.register-with-eureka=false”.
9. The services registering to Eureka (called Eureka clients) fetch the information from server and caches it locally. They use that info to contact other services. This property needs to be set for all the eureka clients.-“eureka.client.fetch-registry=true”.
10. To register any service with Eureka you need to add @EnableDiscoveryClient in the main class.



Gateway Service - 
1. Used spring cloud gateway to implement api gateway which is recommended by Spring. Zuul which was widely used before for implementing gateway has been put into maintenance mode by Netflix, so can’t use that.
2. In a micro services architecture only api gateway IP url is available to clients and then all the calls are internally mapped to the required micro service.
3. Gateway contacts the eureka server to get the location of the service and then the calls the particular service.
4. Refer to the application.properties in the gateway service to see how to implement the routes.
5. Make sure to set this property in application.properties of gateway service. “spring.cloud.gateway.discovery.locator.enabled=true”.



Config Service - 
1. Spring cloud config service acts as a centralized configuration server for all the services. 
2. Use of config server is that, in case of any configurational changes for any service, that particular service should get the changes without having to restart the service.
3. Git repository uri needs to be added in the application.properties file of config server.
4. Each service’s config properties files are put in the git repo. 
5. Properties files in git repo should be of the format “<application-name>-<profile>.yml/properties”, where application-name is the value for “spring.application.name” in the application.properties. For example, item-dev.properties file is for dev profile and item.properties is for default profile.
6. To access the properties values from config server hit this url - “http://<config-server-port>/item/dev” for dev profile and  “http://<config-server-port>/item/default” for default profile.
7. For item service to get configurational changes from config server this property needs to be added - “spring.cloud.config.uri=http://<config-server-port>.
8. In the code, the db connection properties for item service are put in item-dev.properties. So in item service make sure to put the config server uri in the bootstrap.properties file instead of application.properties file because bootrap.properties get loaded first when the app is run.
9. You can also get configuration changes by exposing a rest end point. Refer to ConfigClient class in item service. 
10. Once you change any config property in item-dev.properties, you need to commit this change in git repo. And then you need to call actuator’s refresh endpoint from item service to get the changes in the item service. Dont forget to add @RefreshScope annotation(refer to ConfigClient in item service project). To call the actuator’s refresh endpoint hit this url - “http://<item-service-port>/actuator/refresh”. So in this way you can get config changes in item service without having to restart the application.
11. *** Also dont forget to add actuator dependency in all the services and config server. Also set this property in application/boostrap.properites - “management.endpoints.web.exposure.include=*”. This enables the actuator endpoints.


Spring Cloud bus
1. There is a problem with above approach for getting config changes. The issue is once we change any value in config properties file, we have to hit actuator’s refresh endpoint for each service separately. This approach doesn’t make sense if there are lot of service in the whole architecture.
2. Spring cloud bus solves this problem. After implementing you just have to call actuator’s “bus-refresh” endpoint from any one random service and the changes will propagate to all the services which are subscribed to bus.
3. Spring cloud bus uses lightweight message broker to broadcast config changes across services.
4. In the code I have used RabbitMq but kafka and redis can also be used.
5. For setting up RabbitMq locally, it is recommended running RabbitMq as a docker image using this command in terminal - “docker pull rabbitmq:3-management”. And then to run “docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management”. 
6. After this you can access RabbitMq dashboard on “http://localhost:15672,”. Default username is “guest” and password also “guest”.
7. Add the spring-cloud-starter-bus-amqp dependency in all the config clients and config server.
8. In all the config clients add the rabbitmq credentials. Refer to bootstrap.properties file in item service.
9. Thats all you need to do to enable the bus. Then change any value in config properties file and hit the actuator’s “bus-refresh” endpoint in any one of the service. The changes will propagate to all the services subscribed to the bus.


HashiCorp Vault to store sensitive info like username and password.
1. For item service, I have stored db username and password in vault.
2. First setup the vault and start the server using “vault server -dev”. 
3. Add required vault config properties in bootstrap.properties file. (Refer to bootstrap.properties in item service). If you are getting values from vault in you service put the vault config properties in bootstrap.properties and not in application.properties. 


Item Service - 
1. This service is exposing some rest end points and performing basic CRUD operation on postgres db.


Feign Service - 
1. This service is doing 2 things. It is communication with item service in synchronous as well as asynchronous manner.
2. Synchronous communication is implemented using open feign library. 
3. Asynchronous communication is implemented using event driven.
4. One use case where synchronous communication between micro services can be used is - If there is an order service and an inventory service. Inventory service contains the stock of each product. So before placing an order a call needs to be made to inventory service to get the stock of the product. In this case order service needs to wait for response before proceeding to place order.
5. Implementing feign to make rest calls is very easy. Refer to controller class and ItemServiceClient interface in feign service project. 
6. All you have to do is add @FeignClient(name=“item”). Make sure the name value here is same as the application name of item service. 
7. And then add mappings in the interface same as in item service project. 
8. Both sync and async have been implemented in getItemsById function in Controller class of feign service.
9. For event driven communication feign service is acting as producer and item service is the consumer.
10. Consumer function is implemented in SpringItemServiceApplication class in item service.


Circuit breaker - 
1. While doing synchronous communication between micro services, circuit breaker is needed to create a fallback function, in case if item service is down. 
2. So we can create a fallback function which will get implemented instead of getting service down error.
3. It makes the rest communication more resilient.
4. To implement circuitbreaker I have used Resilience4J library. This is recommended by spring. Previously Hystrix was widely used but it has been put in maintenance mode by Netflix.


Why use open feign instead of RestTemplate class to implement synchronous communication - 
1. Feign allows us to abstract the mechanics of calling a rest service. Once feign is integrated all we have to do is make a function call.
2. The actual implementation of making a rest call is handled at runtime by feign.
3. Urls are not hardcoded.
4. We can use Eureka client id(which isn nothing but spring application name) instead of url.


Distributed tracing using sleuth and zipkin - 
1. In micro services architecture, an incoming request could be going through multiple other services. In that case if there is some issue like latency, then it gets difficult to identify where the issue is.
2. So distributed tracing is implemented. The benefit is any request will have the same trace id across its whole journey. So it will become easier to identify the problem.
3. Only sleuth and zipkin dependencies and some properties needs to be added. Refer to application/bootstrap.properties of item and feign service. 
4. Install and set up zipkin. Head over to its default location - http://localhost:9411 to see the logs.






