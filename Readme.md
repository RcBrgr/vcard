# vCard Kotlin Library

[![CI & Release](https://github.com/RcBrgr/vcard/actions/workflows/release.yml/badge.svg)](https://github.com/RcBrgr/vcard/actions/workflows/release.yml)
[![Latest Release](https://img.shields.io/github/v/release/RcBrgr/vcard)](https://github.com/RcBrgr/vcard/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A lightweight, modern Kotlin library for parsing, creating, and manipulating vCard files (`.vcf`). This library aims to provide a type-safe and intuitive API for working with vCard data in any Kotlin or Java project.

## Features

- **Parse vCard Files**: Effortlessly parse vCard data from strings or files.
- **Create vCards Programmatically**: A simple and fluent API for building vCard objects from scratch.
- **Immutable Data Structures**: All parsed vCard models are immutable, ensuring thread safety.
- **Supports Multiple vCard Versions**: Designed to handle common vCard versions (e.g., 3.0, 4.0).
- **100% Kotlin**: Written in idiomatic Kotlin, offering null-safety and modern language features.

## Getting Started

### Installation

This project is not yet on Maven Central. You can use it in your project via [JitPack](https://jitpack.io/).

**1. Add the JitPack repository to your `build.gradle.kts` file:**
```kotlin
repositories {
mavenCentral()
maven { url = uri("https://jitpack.io") }
}
```

**2. Add the dependency:**
Replace `TAG` with the latest version from the [releases page](https://github.com/RcBrgr/vcard/releases).

```kotlin
dependencies {
implementation("com.github.RcBrgr:vcard:TAG")
}
```

## Usage

Here are some basic examples of how to use the library.

### Parsing a vCard

```kotlin
val vcardString = """
BEGIN:VCARD
VERSION:3.0
FN:John Doe
EMAIL;TYPE=INTERNET:john.doe@example.com
TEL;TYPE=WORK,VOICE:(111) 555-1212
END:VCARD
"""

try {
val vcard = VCardParser.parse(vcardString)
println("Successfully parsed vCard for ${vcard.formattedName}")
println("Email: ${vcard.email}")
} catch (e: Exception) {
println("Failed to parse vCard: ${e.message}")
}
```

### Creating a new vCard

```kotlin
val newVCard = VCardBuilder()
.version(VCardVersion.V3_0)
.formattedName("Jane Smith")
.addEmail("jane.smith@example.com", type = EmailType.INTERNET)
.addPhoneNumber("+1-202-555-0156", type = PhoneType.HOME)
.build()

// Get the vCard as a string
val vcardString = newVCard.toVCardString()
println(vcardString)
```

## Building from Source

To build the project locally, you'll need JDK 17 or higher.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/RcBrgr/vcard.git
    cd vcard
    ```

2.  **Build the project:**
    This command will assemble the library JAR.
    ```bash
    ./gradlew build
    ```

3.  **Run checks:**
    To run the linter (`ktlint`) and all tests, use the following command:
    ```bash
    ./gradlew ktlintCheck test
    ```

## Automation & CI/CD

This repository uses GitHub Actions for continuous integration and release management. On every push to the `main` branch:
1.  The commit message is validated against the **Conventional Commits** standard.
2.  The `ktlintCheck` and `test` Gradle tasks are executed.
3.  If all checks pass, a new version tag is automatically created and pushed to the repository based on the commit messages.

## Contributing

Contributions are welcome! If you'd like to help improve the library, please follow these steps:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/my-new-feature`).
3.  Make your changes.
4.  Commit your changes using the **Conventional Commits** specification. This is mandatory as our release process depends on it.
    - `feat:` for new features (triggers a minor version bump).
    - `fix:` for bug fixes (triggers a patch version bump).
    - `docs:`, `style:`, `refactor:`, `test:`, `chore:` for other changes (do not trigger a release).
    - Add `BREAKING CHANGE:` in the footer for major version bumps.
5.  Push to the branch (`git push origin feature/my-new-feature`).
6.  Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.