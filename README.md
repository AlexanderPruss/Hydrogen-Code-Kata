# Hydrogen Code Kata

This is a RESTful Spring Boot API that stores data in MongoDB. 
It was built in a time-limited code kata challenge and then shared with and graded by other participants. 

The goal of the kata was to create an API that contained several insurance modules and allowed 
users to create insurance policies based on these modules. Some choices of technology and design were pre-selected 
by the kata, namely that the app had to be a RESTful API with Java on Spring Boot with Spring Data.

The rest of the ReadME, and any code comments, were meant to give context to the developer inspecting my application.

## Building and starting the app

You can build and run the app either from your favorite IDE, or directly with Gradle -

`./gradlew clean bootRun`

You can run tests from the IDE or with Gradle:

`./gradlew clean test`

## Using the API

#### Database

This application needs to connect to a MongoDB to run. It's configured to search for a MongoDB instance on the default 
local port, `localhost:27017`. You can of course reconfigure the app to connect to any other instance of MongoDB.

##### But why not Docker-Compose to launch the app with a DB?

I don't think it's the responsibility of the application to provide the runtime of its own data source, except in unit and 
integration tests. Once the app is deployed to the cloud it will be scaling independently of its data source anyway.

#### Data Migrations

The app will automatically attempt to execute a data migration when it's started, unless the migration has already been
executed. This migration creates the four insurance modules used in the application. See the `migrations` package for more
details.

#### Data Model

There are three main domain objects in the app:

An `InsuranceModule` describes a kind of insurance that can be purchased.

A `Customer` purchases insurances from different modules.

A `Policy` is a contract between a `Customer` and an `InsuranceModule`.

#### Creating Users and Policies

To create (or delete) insurance policies, you need to first create a `Customer`. A customer is created by logging in to the app at 
the `POST /login` endpoint.

Once a `Customer` is created, you can create or update policies by PUTing to the 

`PUT /customer/${customerId}/policies`

endpoint. You'll need the ID of the InsuranceModule that you want to create a policy for; you can find all the insurance 
modules at 

`GET /insurance-modules`

#### Swagger

A Swagger API is made available at `localhost:8080/swagger-ui.html`. 

## Design Notes

#### Spring Data

I try to limit the use of Spring Data in my projects, there's too much magic going on there for my liking.
I think it's a particularly bad choice for NoSQL, ala MongoDB. That said, I chose to use Spring Data to connect 
to Mongo just to show that I know how to push Spring Data buttons.

#### User Handling

While many things in this example application are far from production ready, the user handling is obviously not correct.
Ideally we'd have an Auth workflow with different rights. For instance, it's currently not possible to create additional
insurance modules, but it's reasonable to expect an Admin/Superuser to be allowed to do so.

#### REST vs GraphQL

I'm increasingly a proponent of GraphQL, but I chose to implement this POC as a RESTful API because I feel that Java
and Spring are poor choices for a GraphQL API.

#### Dockerfile

A very basic dockerfile is present, it assumed that the app has already been packaged with `./gradlew bootJar`. 
Further improvements to the Dockerfile (external configuration and such) depend on the context of how this is deployed,
and so are currently absent.