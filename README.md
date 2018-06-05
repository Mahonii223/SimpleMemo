# SimpleMemo

## About the project

### 1. Goal

The basic concept was to create a system that would allow to store simple memos, consisting of title, content and some metadata, using RESTful services. While designing the webapp, several assumptions were made:
- A memo is a set of title, content, version number, creation date and last modification date.
- The app must allow to add new memos.
- The app must allow to update memos, although all change history must be stored aswell.
- The app must allow to remove memos.
- The app must provide services to read:
  - all memos
  - specific memo
  - specific memo along with all its changelog, including deleted memos.
  
### 2. Implementation

In order to keep the database as simple as possible, while maintaining the ability to perform all tasks mentioned above, the following database was designed:
The database consists of a single table, storing id, title, content and version number of a note, aswell as columns labeled as "created", "modified", "deleted" and "threadid".
The way system treats memos resembles that of an internet forum, the first memo being the original post, and subsequent edits of it being replies to that post. The database perceives edit of a memo as separate memos, only linked by "threadid" key and date in column "created".
This not only allows to store all memos and their changes in the same table, but also makes expanding the database rather easy in case there was a need to attach more records to a specific thread, which only requires treating "treadid" as foreign key.

### 3. Technology

The webapp is based on RESTful services, handled by JAX-RS used to obtain and respond with JSON files. It utilizes standard JDBC MySQL database connector, as well as Junit, Mockito and Mariadb4j for unit and integration testing the most bug-prone components.
The entire app is build with gradle.


## Setup

IntelliJ, Eclipse or any other IDE capable of building gradle projects is required to run this project.
After downloading the repository, and adjustment to the code must be made: in main.webapp.DBConnector.java, DBURL, DBUSER and DBPASS variables must be set to database URL, username and password to allow connection.
In order to setup required "memos" table in the database, setup.sql script can be used, located in src/Test/IntegrationTests.
Once the database has been set up, gradle can be used to create a .war artifact ready to be deployed on server of your choice, however do note that a server compliant to Java EE spec such as TomEE is required.


## Usage

Once the webapp is running, the followin CURL commands can be used to test it:

To list all memos:
```curl -X GET http://serverurl/memos```

To add a memo:
``` curl -d '{ "title" : "titlehere", "content" : "contenthere"} -H "Content-Type: application/json" -X POST http://serverurl/memos```

To get a memo:
``` curl -X GET http://serverurl/memos/{id}```

Full list of available services:
- List all memos: ```/memos```, ```GET```
- Get specific memo: ```/memos/{id}```, ```GET```
- Get specific memo with its changelog (get specific thread): ```/memos/versions/{id}```, ```GET```
- Add a new memo: ```/memos/add```, ```POST```
- Update a memo (add new memo to thread): ```/memos/add/{threadid}```, ```PUT```
- Delete a memo: ```/memos/{id}```, ```DELETE```

---
#### Notes on the project (mostly on its flaws)
- As the excercise requires, all records in the database store date of creation, which is similar to all records with the same threadID. This creates some redundancy in the stored data, however it greatly simplifies some listing queries.
- The strings obtained from Json files and passed to database queries are NOT SANITIZED.
- Authentication and security aspects of the projects were ommited
