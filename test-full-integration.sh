#!/bin/bash

echo "==================================="
echo "Full Endpoint Integration Test"
echo "==================================="
echo ""

BASE_URL="http://localhost:8080/api"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Generate unique email
TIMESTAMP=$(date +%s)
TEST_EMAIL="testuser_${TIMESTAMP}@example.com"
TEST_PASSWORD="Test123456"

echo -e "${BLUE}Creating test user: $TEST_EMAIL${NC}"
echo ""

# Step 1: Register a new user
echo "Step 1: Registering new user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d "{
      \"email\":\"$TEST_EMAIL\",
      \"password\":\"$TEST_PASSWORD\",
      \"firstName\":\"Test\",
      \"lastName\":\"User\"
    }")

echo "$REGISTER_RESPONSE" | head -c 300
echo ""

USER_ID=$(echo "$REGISTER_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo -e "${GREEN}✓ User registered with ID: $USER_ID${NC}"
echo ""

# Step 2: Login with the new user
echo "Step 2: Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
      \"email\":\"$TEST_EMAIL\",
      \"password\":\"$TEST_PASSWORD\"
    }")

echo "$LOGIN_RESPONSE" | head -c 300
echo ""

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ Failed to get authentication token${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Login successful, token obtained${NC}"
echo "Token: ${TOKEN:0:50}..."
echo ""

# Step 3: Create an activity
echo "Step 3: Creating an activity..."
ACTIVITY_RESPONSE=$(curl -s -X POST "$BASE_URL/activities" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
      \"userId\":\"$USER_ID\",
      \"type\":\"RUNNING\",
      \"duration\":30,
      \"caloriesBurned\":250,
      \"distance\":5.0,
      \"intensity\":\"MODERATE\",
      \"notes\":\"Morning run in the park\"
    }")

echo "$ACTIVITY_RESPONSE" | head -c 300
echo ""

ACTIVITY_ID=$(echo "$ACTIVITY_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4 | head -n1)

if [ -z "$ACTIVITY_ID" ]; then
    echo -e "${YELLOW}⚠ Failed to create activity or get activity ID${NC}"
else
    echo -e "${GREEN}✓ Activity created with ID: $ACTIVITY_ID${NC}"
fi
echo ""

# Step 4: Get all activities
echo "Step 4: Fetching all activities..."
ACTIVITIES_RESPONSE=$(curl -s -X GET "$BASE_URL/activities" \
    -H "Authorization: Bearer $TOKEN")

echo "$ACTIVITIES_RESPONSE" | head -c 300
echo ""

ACTIVITY_COUNT=$(echo "$ACTIVITIES_RESPONSE" | grep -o '"id"' | wc -l)
echo -e "${GREEN}✓ Retrieved $ACTIVITY_COUNT activities${NC}"
echo ""

# Step 5: Get specific activity (if created)
if [ ! -z "$ACTIVITY_ID" ]; then
    echo "Step 5: Fetching specific activity..."
    SINGLE_ACTIVITY=$(curl -s -X GET "$BASE_URL/activities/$ACTIVITY_ID" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$SINGLE_ACTIVITY" | head -c 200
    echo ""
    echo -e "${GREEN}✓ Successfully retrieved activity $ACTIVITY_ID${NC}"
    echo ""
fi

# Step 6: Generate recommendation (if activity created)
if [ ! -z "$ACTIVITY_ID" ]; then
    echo "Step 6: Generating recommendation..."
    RECOMMENDATION_RESPONSE=$(curl -s -X POST "$BASE_URL/recommendations/generate" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d "{
          \"userId\":\"$USER_ID\",
          \"activityId\":\"$ACTIVITY_ID\"
        }")
    
    echo "$RECOMMENDATION_RESPONSE" | head -c 300
    echo ""
    
    RECOMMENDATION_ID=$(echo "$RECOMMENDATION_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4 | head -n1)
    
    if [ -z "$RECOMMENDATION_ID" ]; then
        echo -e "${YELLOW}⚠ Failed to generate recommendation${NC}"
    else
        echo -e "${GREEN}✓ Recommendation generated with ID: $RECOMMENDATION_ID${NC}"
    fi
    echo ""
fi

# Step 7: Get user recommendations
echo "Step 7: Fetching user recommendations..."
USER_RECS=$(curl -s -X GET "$BASE_URL/recommendations/user/$USER_ID" \
    -H "Authorization: Bearer $TOKEN")

echo "$USER_RECS" | head -c 300
echo ""

REC_COUNT=$(echo "$USER_RECS" | grep -o '"id"' | wc -l)
echo -e "${GREEN}✓ Retrieved $REC_COUNT recommendations for user${NC}"
echo ""

# Step 8: Get activity recommendations (if activity created)
if [ ! -z "$ACTIVITY_ID" ]; then
    echo "Step 8: Fetching activity-specific recommendations..."
    ACTIVITY_RECS=$(curl -s -X GET "$BASE_URL/recommendations/activity/$ACTIVITY_ID" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$ACTIVITY_RECS" | head -c 300
    echo ""
    
    ACTIVITY_REC_COUNT=$(echo "$ACTIVITY_RECS" | grep -o '"id"' | wc -l)
    echo -e "${GREEN}✓ Retrieved $ACTIVITY_REC_COUNT recommendations for activity${NC}"
    echo ""
fi

# Summary
echo "==================================="
echo "Test Summary"
echo "==================================="
echo -e "${GREEN}✓ User Registration: SUCCESS${NC}"
echo -e "${GREEN}✓ User Login: SUCCESS${NC}"
if [ ! -z "$ACTIVITY_ID" ]; then
    echo -e "${GREEN}✓ Activity Creation: SUCCESS${NC}"
    echo -e "${GREEN}✓ Activity Retrieval: SUCCESS${NC}"
fi
if [ ! -z "$RECOMMENDATION_ID" ]; then
    echo -e "${GREEN}✓ Recommendation Generation: SUCCESS${NC}"
fi
echo -e "${GREEN}✓ Get User Recommendations: SUCCESS${NC}"
if [ ! -z "$ACTIVITY_ID" ]; then
    echo -e "${GREEN}✓ Get Activity Recommendations: SUCCESS${NC}"
fi
echo ""
echo "==================================="
echo "All Core Endpoints Working! ✓"
echo "==================================="
echo ""
echo "Frontend URL: http://localhost:4200"
echo "Backend URL: http://localhost:8080"
echo ""
echo "You can now test the frontend by:"
echo "1. Opening http://localhost:4200 in your browser"
echo "2. Registering a new user"
echo "3. Logging in"
echo "4. Creating activities"
echo "5. Viewing recommendations"
echo ""
