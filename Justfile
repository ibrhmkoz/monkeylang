# Default recipe
default: build

# Build the project
build:
    mvn compile

# Run all tests
test:
    mvn test

# Run a specific test class
test-class class:
    mvn test -Dtest={{class}}

# Clean build artifacts
clean:
    mvn clean

# Clean and build
rebuild: clean build

# Package the project
package:
    mvn package

# Install to local repository
install:
    mvn install

# Run with quiet output
build-quiet:
    mvn compile -q

# Run tests with quiet output
test-quiet:
    mvn test -q

# Verify the project
verify:
    mvn verify

# Show dependency tree
deps:
    mvn dependency:tree

# Check for dependency updates
updates:
    mvn versions:display-dependency-updates
