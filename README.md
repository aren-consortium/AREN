

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
Download and extracte the sources in the repository of your choice.

**Copy** the `application.properties` file from the `config` folder to the  `src/main/resources` folder.
Fill the empty properties, the documentation is in the file itself.

**Copy** the `persistence.xml` file from the `config` forlder to the  `src/main/resources/META-INF` folder.
Fill the following line with the `db_user`, `db_password` and `db_name` previously set.

    <property name="hibernate.connection.url" value="jdbc:postgresql://localhost:5432/db_name"/>
    <property name="hibernate.connection.username" value="db_user"/>
    <property name="hibernate.connection.password" value="db_password"/>


## Build
Go to the source folder and run `mvn clean install`.
A `target` folder is then created with the `aren.war` file inside.

## Deployement
Rename the `aren.war` file to `ROOT.war`then copy it into the Java WebSever of your choice [Tomcat 7 tested and functional].

If you do not want the software to run at the root of your serveur, you'll need to change the `path` variable of the `context.xml` file according to the name of your `war` file

### First launch
Go in your webserver folder, in the application folder.
Edit the `persistence.xml` in the `WEB-INF/classes/META-INF` folder.
Uncomment the two following lines to allow the application to build the whole database.

    <!--property name="hibernate.hbm2ddl.auto" value="drop-and-create"/-->
    <!--property name="hibernate.hbm2ddl.import_files" value="META-INF/init.sql"/-->
Restart your webserver.

**! WARNING !**
**To avoid any data loss  yous have to re-comment those previous lines. Otherwise the full database will be rebuilt at every restart of the application !**

## API super-admin credentials
The default credentials for the super-admin are **admin:password** 
Those can be easily change throught the web interface or with an API call.

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
