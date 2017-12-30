# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).


--------
### [0.2.0](N/A) - 2017-12-30
#### Added
* Upgrade to Java 9
* Upgrade to JUnit 5
* Expanded DataProxy functionality to support setting individual array indexes and adding elements to arrays
* Unit tests for DataProxy

#### Changed
* Updated dependencies, multiple dependencies up to latest version
* Moved and added unit tests and moved xml/json test files into `rsc/` directory
* Cleaned up some old code warnings and new ones from Java 9 upgrade

#### Removed
* Removed `XmlTag` in favor of `DataElement` everywhere


--------
###[0.1.1](https://github.com/TeamworkGuy2/DataTransfer/commit/4ccd383e1452ee4339f3567efb1aaaa30697f90c) - 2016-10-07
#### Changed
*  Updated dependencies:
  * jsimple-types latest version (package typeInfo -> twg2.primitiveIoTypes)
  * jtext-util latest version (StringConvert -> StringEscape)


--------
###[0.1.0](https://github.com/TeamworkGuy2/DataTransfer/commit/df848e6fbc2235ca9a658e8d010282ea9621c50a) - 2015-08-21
__There are commits earlier than this, but they were not version tracked, so this is version 0.1.0__
#### Changed
* Nested existing files and packages inside new `twg2.io.serialize` package
* Moved data transferable read/write related files into new `reader` and `writer` packages.
* Changed some interfaces and added/removed some files like DataProxy to reuse code with serialization project.
