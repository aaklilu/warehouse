## Example Warehouse Application
This is an example warehouse management application that lets users maintain products, articles that make up a product and orders (sales) of the products.

The warehouse application has the following functionalities;
* Get all products and quantity of each that is an available with the current inventory
* Remove(Sell) a product and update the inventory accordingly

### Application Architecture
There are three domains involved.

* **_Inventory:_** responsible for maintaining articles that make up a product and stock levels of them.
* **_Product:_** a product catalogue domain where the list of products available in the system are maintained and kept tracked based on sales and inventory levels.
* **_Order:_** a sales domain

#### Flow
1. The articles will be added to inventory at which point `inventory level changed` event will be fired.
   * Product domain processes the event and updates available quantity on each product that contains the updated article
2. A product is added to the catalogue.
3. An order is placed on the order domain and `order placed` event
   * The event is received and processed by inventory domain to track stock level which in turn will trigger an `inventory level changed` event.

#### TechStack
* Kotlin
* Spring Boot
* Gradle
* PostgreSql
* TestContainers
* Docker
