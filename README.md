# Driving School Application

[![Build Status](https://ci.appveyor.com/api/projects/status/32r7s2skrgm9ubva?svg=true)]()

Driving School Application is a third semester project for B.Sc Students at Aalborg University, Denmark. The application is running Spring, connetected between an interface and backend-server with REST. Some of the features are listed as: 
  - Create, delete and update lessons for each student.
  - Create, delete and update course for each student.
  - Dedicated store manager, handles course / additional lessons request from students.
  - Full User Management System
  - Logbook and signature support.
  - Dedicated Calendar for each user.

## Getting Started
Following guide will explain how to get the website running. The website was written using IntelliJ IDEA as IDE.

### Prerequisites

[Java 8](https://www.java.com/en/download/)

[Maven](https://maven.apache.org/download.cgi) (Is provided natively by most IDE's)

### Installing

First clone the repository.

```sh
git clone https://github.com/SW3-ds302e18-2018/DrivingSchoolApplication.git
```

Go to the directory.

```sh
cd DrivingSchoolApplication
```

Compile the project using maven.

```sh
mvn compile
```

## Built With

* [Spring](https://spring.io/) - The web framework for both MVC and REST

## License

AGPL-3.0

<!--
### Maven Dependencies

Driving School Application runs on Java 8 with Maven, and running serveral depencies for SMS/email, canvas support and many more.

| Plugin | Version |
| ------ | ------ |
| AWS | [plugins/dropbox/README.md]|
-->
