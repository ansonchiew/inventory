-------------------------------
Building the project with Maven
-------------------------------

The project has to be built in JDK 8.

Ensure Maven is installed in the machine.

Clone the project from Github

git clone https://github.com/ansonchiew/inventory.git

Navigate to the root folder of the project and enter the following command:

mvn clean install

The execution jar file will be created in target folder: inventory-1.0.jar
Unit tests will be executed and coverage reports can be accessed at target/site/jacoco/index.html

------------------------
Running the Application
------------------------

Navigate to the root folder of the project and enter the following command:

mvn spring-boot:run

During application starts up, the database schema at src/main/resources/schema.sql will be executed with some sample data (data.sql) populated into category and sub_category tables in db2 in-memory database.

-------------------------
Shut down the Application
-------------------------

Execute the following command from the terminal:
curl -X POST localhost:8080/actuator/shutdown

-------------------------
Accessing the Application
-------------------------

Access the following URL from a browser:
http://localhost:8080/inventory/home

User can perform actions on inventory create, update (quantity), browse and delete. 

----------------
Technology Stack
----------------

Front end: HTML5 and jQuery
Back end: Spring Boot, Spring MVC, Apache Tiles, REST API and db2

-----------
Assumptions
-----------

A category can have multiple sub-categories but a sub-category can only belong to a category.

All names for category, sub-category and inventory are unique.

The inventory quantity cannot be negative.

All the inventory records are stored in in-memory database and will be cleared upon application shut down.

-------------
Design Choice
-------------

The request methods used for update and delete are POST and GET respectively, as PUT and DELETE will not work in browsers due to W3C HTML 5.0 specification. 

Validation exceptions are thrown from the service layer with their messages propagated to the views by the controller. 

The category and sub-category lists are populated in the drop-down lists in the inventory creation form to prevent user from entering unknown data.

Each word in the inventory name receiving from the create request is captialised for saving to prevent duplicate in different letter case.

Inventory class implements Comparable for sorting the return list by name.

The primary key of sub_category table is a composite key consisting of sub_category_id and category_id, to ensure data uniqueness referencing from the inventory table.

Apache Tiles is used to maintain a consistent layout in the web UI.

-------------------
Unit Tests Coverage
--------------------

All execution paths in Controller, Validator, Services and Repositories.

Edge cases like blank entry, negative quantity, wrong category of sub-category, unknown sub-category, duplicate name and binding error etc.

Integration tests for classes in the service layer. 

Repository and integration tests also execute the schema.sql and data.sql that are executed during application start up. The populated data are used in the tests.
 
---------------------------------
Locations of JSP and static files
---------------------------------

JSP, tiles.xml	:  src/main/webapp/WEB-INF/
css, js			:  src/main/resources/static/
