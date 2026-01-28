#!/bin/bash

echo "Testing Profile Endpoint"
echo "========================"

BASE_URL="http://localhost:8080/api"
TIMESTAMP=$(date +%s)
TEST_EMAIL="testuser_${TIMESTAMP}@example.com"
TEST_PASSWORD="Test123456"

# Register
echo "1. Registering user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\",\"firstName\":\"Test\",\"lastName\":\"User\"}")

USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "User ID: $USER_ID"

# Login
echo ""
echo "2. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}")

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token received: ${TOKEN:0:50}..."

# Test profile endpoint
echo ""
echo "3. Getting profile..."
PROFILE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$BASE_URL/users/profile" \
    -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$PROFILE_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$PROFILE_RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | head -c 300
echo ""

if [ "$HTTP_CODE" == "200" ]; then
    echo "✓ Profile endpoint working!"
else
    echo "✗ Profile endpoint failed with status $HTTP_CODE"
fi

# Test update profile
echo ""
echo "4. Updating profile..."
UPDATE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X PUT "$BASE_URL/users/profile" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"firstName\":\"Updated\",\"lastName\":\"Name\"}")

HTTP_CODE=$(echo "$UPDATE_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$UPDATE_RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | head -c 300
echo ""

if [ "$HTTP_CODE" == "200" ]; then
    echo "✓ Profile update working!"
else
    echo "✗ Profile update failed with status $HTTP_CODE"
fi
