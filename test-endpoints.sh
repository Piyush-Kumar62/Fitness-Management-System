#!/bin/bash

echo "==================================="
echo "Backend & Frontend Connection Test"
echo "==================================="
echo ""

BASE_URL="http://localhost:8080/api"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test results counter
PASSED=0
FAILED=0

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local token=$5
    
    echo -e "${YELLOW}Testing:${NC} $description"
    echo "  Endpoint: $method $endpoint"
    
    if [ -z "$token" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $token" \
            -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
        echo -e "  ${GREEN}✓ PASSED${NC} (HTTP $http_code)"
        ((PASSED++))
        echo "$body" | head -c 200
        echo ""
    else
        echo -e "  ${RED}✗ FAILED${NC} (HTTP $http_code)"
        ((FAILED++))
        echo "$body" | head -c 200
        echo ""
    fi
    echo ""
}

# Test 1: Health Check
echo "1. Health Check"
test_endpoint "GET" "/actuator/health" "Backend Health Status" "" ""

# Test 2: Register User (should fail with validation)
echo "2. User Registration (should fail - missing fields)"
test_endpoint "POST" "/auth/register" "Register without required fields" '{"email":"test@example.com"}' ""

# Test 3: Register User (valid request - may fail if user exists)
echo "3. User Registration (valid request)"
REGISTER_DATA='{
  "email":"testuser_'$(date +%s)'@example.com",
  "password":"Test123456",
  "firstName":"Test",
  "lastName":"User"
}'
test_endpoint "POST" "/auth/register" "Register new user" "$REGISTER_DATA" ""

# Test 4: Login (should fail - invalid credentials)
echo "4. User Login (invalid credentials)"
test_endpoint "POST" "/auth/login" "Login with invalid credentials" '{"email":"invalid@example.com","password":"wrong"}' ""

# Test 5: Activities endpoint without auth (should fail with 401/403)
echo "5. Get Activities (without authentication)"
test_endpoint "GET" "/activities" "Get activities without token" "" ""

# Test 6: Recommendations endpoint without auth (should fail with 401/403)
echo "6. Generate Recommendation (without authentication)"
test_endpoint "POST" "/recommendations/generate" "Generate recommendation without token" '{"userId":"test","activityId":"test"}' ""

echo "==================================="
echo "Test Summary"
echo "==================================="
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo "Total: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
else
    echo -e "${YELLOW}⚠ Some tests failed. This is expected for authentication-protected endpoints.${NC}"
fi

echo ""
echo "==================================="
echo "Frontend Status"
echo "==================================="
if curl -s http://localhost:4200 > /dev/null; then
    echo -e "${GREEN}✓ Frontend is running on http://localhost:4200${NC}"
else
    echo -e "${RED}✗ Frontend is not responding${NC}"
fi

echo ""
echo "==================================="
echo "Backend Status"
echo "==================================="
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}✓ Backend is running on http://localhost:8080${NC}"
else
    echo -e "${RED}✗ Backend is not responding${NC}"
fi

echo ""
echo "==================================="
echo "Available Endpoints Summary"
echo "==================================="
echo "Authentication:"
echo "  POST /api/auth/register - Register new user"
echo "  POST /api/auth/login - Login user"
echo ""
echo "Activities (requires auth):"
echo "  GET  /api/activities - Get all user activities"
echo "  GET  /api/activities/{id} - Get activity by ID"
echo "  POST /api/activities - Create new activity"
echo ""
echo "Recommendations (requires auth):"
echo "  POST /api/recommendations/generate - Generate recommendation"
echo "  GET  /api/recommendations/user/{userId} - Get user recommendations"
echo "  GET  /api/recommendations/activity/{activityId} - Get activity recommendations"
echo ""
echo "==================================="
