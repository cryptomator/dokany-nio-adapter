[![Build Status](https://github.com/cryptomator/dokany-nio-adapter/workflows/Build/badge.svg)](https://github.com/cryptomator/dokany-nio-adapter/actions?query=workflow%3ABuild)
[![Known Vulnerabilities](https://snyk.io/test/github/cryptomator/dokany-nio-adapter/badge.svg)](https://snyk.io/test/github/cryptomator/dokany-nio-adapter)

# dokany-nio-adapter
Provides directory content specified by a `java.nio.file.Path` via a [Dokany](https://dokan-dev.github.io/) filesystem.

Important notice: *Only Dokany 1.5.x is supported*. Hence, older minor (1.4.x, 1.3.x, etc.) or newer major versions (like 2.x.x) are not supported.

## Configuration parameters

This library uses the following system properties:

* `org.cryptomator.frontend.dokany.mountTimeOut` - The mount timeout threshold in milliseonds. If the mounting operation exceeds it, the mounting is aborted.

## Usage

Have a lookt at the [examples](/src/test/java/org/cryptomator/frontend/dokany/ExampleFilesystemTests.java).

## License
This project is dual-licensed under the AGPLv3 for FOSS projects as well as a commercial license for independent software vendors and resellers. If you want to use this library in applications, that are *not* licensed under the AGPL, feel free to contact our [support team](https://cryptomator.org/help/).
