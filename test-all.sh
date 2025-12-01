#!/bin/bash
set -e # Stops the script if any test fails

echo "🧪 Testing Auth Service..."
(cd auth-service && ./mvnw test)

echo "🧪 Testing Identity Service..."
(cd identity-service && ./mvnw test)

echo "🧪 Testing Matching Service..."
(cd matching-service && ./mvnw test)

echo "🧪 Testing Chat Service..."
(cd chat-service && ./mvnw test)

echo "✅ ALL TESTS PASSED!"