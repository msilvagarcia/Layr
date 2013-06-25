# Layr - The Fast Web Prototyping
[![Build Status](https://travis-ci.org/miere/Layr.png?branch=master)](https://travis-ci.org/miere/Layr)

## What is Layr?

Its an Open Source focused on easy web routing, fast code maintence and free-logic markup.

## Install on your local maven repository

Type on your Terminal/Console:
```bash
git clone git://github.com/miere/Layr.git
cd Layr/layr-parent
mvn clean install
```

## Configure Layr for JEE Environment
Include the following snippet on your ```pom.xml``` file:
```xml
<dependency>
    <groupId>org.layr</groupId>
    <artifactId>layr-jee</artifactId>
    <version>3.0</version>
</dependency>
```
Include the following snippet on your ```web.xml``` file:
```xml
<filter-mapping>
    <filter-name>layr.jee.Application</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
## Documentation
Documentation still on earlier development. For now, you can follow some examples on:
- [Understanding Layr Routing](https://github.com/miere/Layr/wiki/Understanding-Layr-Routing)
- [This Routing Implementation](https://github.com/miere/Layr/blob/3.0/layr-core/tests/layr/routing/sample/HelloResource.java)
- [Another Routing Implementation](https://github.com/miere/Layr/blob/3.0/layr-core/tests/layr/routing/sample/HomeResource.java)
- [Full JEE Sample](https://github.com/miere/Layr/tree/3.0/samples/layr-jee-sample)
