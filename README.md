

# AREN (ARgumentation Et Numérique)

The AREN project (in French, ARgumentation Et Numérique ; argumentation and digital) aims to develop a digital platform for online debates that promotes the development of people' argumentative skills and their critical thinking, through a citizenship education perspective. This platform allows the simultaneous participation of a large number of people in a debate, offers time to build arguments and allows to keep track of exchanges, support for reflective work. 

The project has two research components :
* A didactic component consists of experimenting with the platform in order to assess its impact and suggest changes. 
* An artificial intelligence (AI) component focuses on the automatic processing of natural language and aims to develop and evaluate an AI service for thematic classification of student interventions, facilitating their analysis and the preparation of the reflective synthesis.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

* Java Web server (Test and work on Apache-Tomcat 7.0.80+)
* Maven
* PostgreSQL

## Installation
### Database
Install and configure PostgreSQL specificaly to your OS, check the documentation, to ensure having a secure and remote accessible database.

Create the user that will manage your database. Repalce `[db_user]` and `[db_password]` by your own choices.
`CREATE USER [db_user] WITH PASSWORD '[db_password]';`
Create the database, Replace `[db_name]` by your own choice.
`CREATE DATABASE [db_name] OWNER [db_user];`

### Configuration
Download and extract the sources in the repository of your choice.

## Build
Go to the source folder and run `mvn clean install`.
A `target` folder is then created with the `aren.war` file inside.

## Deployement
Deploy the `aren.war` on the Java WebSever of your choice [Tomcat 7 tested and functional].

### First launch
On the first launche, the application will ask you for DB creentials, then the admin informations.
Once done, the context will reload so the loading of the first page may be a bit long.

### Update from 3.9.x to 3.10.x
Dump your SQL data with `pg_dump -h [db_server] -p [db_port] -a -U [db_user] [db_name] > dump.sql`  
Drop the DB with `dropdb -U [db_user] [db_name]`  
Recreate the database with  `createdb -U [db_user] [db_name]`  
Start the application like for a first deployment.  
Execute the following SQL code `ALTER TABLE documents ADD COLUMN tags text; ALTER TABLE documents ADD COLUMN proposed_tags text;`  
Import your old datas with `psql -h [db_server] -p [db_port] -a -U [db_user] -f dump.sql [db_name]` and correct the errors.  
(the notifications were not being remove, so some foreign key error may arrive, they can be ignored).  
Execute the following SQL code `ALTER TABLE documents REMOVE COLUMN tags text; ALTER TABLE documents REMOVE COLUMN proposed_tags text;`

## Documentation
The web interface is shiped with an useful *help* button. Use it if you have any issues.
The REST API documentation can be found [here](https://app.swaggerhub.com/apis-docs/aren-consortium/aren-api/3.0.0).
You can access the openapi desc directly through the application with the url `/ws/openapi.[json|yaml]`

## Running the tests
@TODO

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Vue.js](https://vuejs.org/) - Web framework used
* [Vue-i18n](https://kazupon.github.io/vue-i18n/) - Internationalization tool for Vue.js
* [Vue-router](https://router.vuejs.org/) - Single Page Website tool for Vue.js
* [http-vue-loader](https://github.com/FranckFreiburger/http-vue-loader) - Async .vue file loading for Vue.js
* [Materialize](https://materializecss.com/) - CSS framwork used
* [D3.js](https://d3js.org/) - Visualization tool

## Contributing

@TODO

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

* **Noémie-Fleur Sandillon-Rezer** - *Initial work, full-stack of v1.0.0* - [noemie-fleur.sandillon-rezer@lirmm.fr](mailto:noemie-fleur.sandillon-rezer@lirmm.fr?subject=[AREN]%20)
* **Florent Descroix** - *Complete rework, full stack since v3.0.0* - [florent.descroix@lirmm.fr](mailto:florent.descroix@lirmm.fr?subject=[AREN]%20)

See also the list of [CONTRIBUTORS](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgment
[Official website](http://www.lirmm.fr/aren)
