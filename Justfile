# Default recipe
default: build

# Build the project (clean build to avoid stale class issues with Java 25)
build:
    mvn clean compile -q

# Run all tests (clean build)
test:
    mvn clean test -q

# Run a specific test class
test-class class:
    mvn clean test -q -Dtest={{ class }}

# Clean build artifacts
clean:
    mvn clean

# Package the project
package:
    mvn clean package -q

# Install to local repository
install:
    mvn clean install -q

# Verify the project
verify:
    mvn clean verify -q

# Show dependency tree
deps:
    mvn dependency:tree

# Check for dependency updates
updates:
    mvn versions:display-dependency-updates
