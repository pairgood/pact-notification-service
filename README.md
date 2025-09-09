# Notification Service

> **🟣 This service is highlighted in the architecture diagram below**

Email and SMS notification service for the PactMart e-commerce microservices ecosystem.

## PactMart Contract Testing

This service implements comprehensive Pact contract testing as part of the **PactMart e-commerce platform**. Contract testing ensures reliable integration between PactMart services while enabling independent development and deployment.

### What is Pact in PactMart Context?

Pact is a consumer-driven contract testing framework that:
- **Prevents Breaking Changes**: Ensures PactMart services don't break each other when deploying new versions
- **Enables Independent Development**: Teams can work on different PactMart services without coordination overhead
- **Provides Fast Feedback**: Contract tests run in seconds vs. hours for full integration tests
- **Documents Service Interactions**: Contracts serve as living documentation of PactMart service integrations

### How Pact Works in PactMart

1. **Consumer Tests**: Services that call other PactMart services create contract tests defining expected requests/responses
2. **Contract Generation**: These tests generate JSON contracts stored in PactFlow broker
3. **Provider Verification**: Services verify they satisfy all contracts from consuming PactMart services
4. **Deployment Safety**: "Can I deploy?" checks prevent incompatible versions from reaching production

### PactMart Service Roles

**Notification Service as Consumer**:
- Calls User Service to fetch user contact information (email, phone)
- Validates response fields actually used in notification processing

**Notification Service as Provider**:
- Provides notification endpoints for other PactMart services (orders, payments)
- Verifies contracts from consuming PactMart services

### Feature Branch Workflow with Pact

Our PactMart implementation uses environment-specific contract publishing:

- **Feature Branches/PRs** → Publish contracts to **test** environment in PactFlow
- **Main Branch** → Publish contracts to **production** environment in PactFlow
- **Environment Isolation** → Development work doesn't break production contracts
- **Deployment Checks** → "Can I deploy?" validates production readiness

### Contract Testing Philosophy

**Strict on Send, Liberal on Receive**:
- **Consumer Tests**: Validate all fields sent to external PactMart services
- **Response Validation**: Only validate fields actually used in business logic
- **Correlation Comments**: Every validated response field links to production code usage

### Running PactMart Contract Tests

```bash
# Run consumer contract tests (as notification service calling other services)
./gradlew test --tests "*Consumer*"

# Run provider contract tests (verifying notification service API)
./gradlew pactVerify

# Publish contracts to PactFlow broker
./gradlew pactPublish
```

### Contract Test Structure

```
src/test/java/com/ecommerce/notificationservice/contracts/
├── consumer/
│   └── UsersConsumerTest.java         # Tests calling User Service
└── provider/
    └── NotificationsProviderTest.java # Tests notification API
```

### Environment-Aware Publishing

**Feature Branch Development**:
```bash
# Automatically runs on PRs
PACT_BROKER_ENVIRONMENT=test ./gradlew pactPublish
```

**Production Deployment**:
```bash
# Automatically runs on main branch
PACT_BROKER_ENVIRONMENT=production ./gradlew pactPublish
```

### PactMart Service Naming Conventions

- **Application Name**: `PactMart` (in PactFlow)
- **Service Names**: Use pattern `pactmart-{service}` (e.g., `pactmart-notifications`)
- **Consumer/Provider Names**: Use service name without prefix (e.g., `notifications`, `users`)

### Updating PactMart Contract Tests

**When to Update Consumer Tests**:
- Adding new calls to external PactMart services
- Changing request parameters sent to other services
- Starting to use new fields from service responses

**When to Update Provider Tests**:
- Adding new notification API endpoints
- Changing notification request/response structure
- Modifying notification processing logic

**Handling Breaking Changes**:
1. Update provider implementation first
2. Verify all consumer contracts still pass
3. Update consumer contracts if needed
4. Deploy provider, then consumers

### CI/CD Integration

Our GitHub Actions pipeline handles:
- **Automated Testing**: Runs contract tests on every PR
- **Environment Publishing**: Test environment for PRs, production for main
- **Webhook Verification**: PactFlow triggers verification when contracts change
- **Deployment Safety**: Blocks production deployment if contracts incompatible

### PactFlow Integration

All contracts are published to PactFlow under the **PactMart** application with:
- **Environment Tagging**: Separate test/production contract versions
- **Webhook Triggers**: Automatic provider verification when contracts change
- **Deployment Records**: Track which versions are deployed to which environments
- **Can I Deploy**: Safety checks before production deployment

### Example Contract Tests

**Consumer Test** (Notification service calling User service):
```java
@Pact(consumer = "notifications")
public V4Pact getUserByIdPact(PactDslWithProvider builder) {
    return builder
        .given("user with id 1 exists")
        .uponReceiving("a request to get user by id")
        .path("/api/users/1")
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(LambdaDsl.newJsonBody(body -> body
            .numberType("id")         // Used in NotificationService.java
            .stringType("email")      // Used for recipient email
            .stringType("phoneNumber") // Used for SMS notifications
        ).build());
}
```

**Provider Test** (Notification service API verification):
```java
@Provider("notifications")
@PactBroker
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationsProviderTest {
    
    @State("valid order confirmation request")
    void setupValidOrderConfirmation() {
        // Setup test data for contract verification
    }
}
```

### Debugging Failed Contracts

1. **Check PactFlow**: View contract details and verification results
2. **Run Tests Locally**: `./gradlew test --tests "*Consumer*" --info`
3. **Verify Provider**: `./gradlew pactVerify --info`
4. **Check State Setup**: Ensure provider states match consumer expectations

### Manual Testing Integration

After implementing contract tests:
1. **Review Generated Contracts**: Check PactFlow for published contracts
2. **Verify Integration**: Confirm notification service works with real PactMart services
3. **Test Error Scenarios**: Validate graceful handling of service failures

---

## Service Role: Provider and Consumer
## Service Role: Provider and Consumer
This service acts as both:
- **Provider**: Offers notification endpoints consumed by Order and Payment services
- **Consumer**: Calls User Service to fetch contact information for personalized notifications

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │ Product Service │
│   (Port 8081)   │    │   (Port 8082)   │
│                 │    │                 │
│ • Authentication│    │ • Product Catalog│
│ • User Profiles │    │ • Inventory Mgmt│
│ • JWT Tokens    │    │ • Pricing       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          │ validates users      │ fetches products
          │                      │
          ▼                      ▼
    ┌─────────────────────────────────┐
    │        Order Service            │
    │        (Port 8083)              │
    │                                 │
    │ • Order Management              │
    │ • Order Processing              │
    │ • Consumes User & Product APIs  │
    └─────────────┬───────────────────┘
                  │
                  │ triggers payment
                  │
                  ▼
    ┌─────────────────────────────────┐
    │       Payment Service           │
    │       (Port 8084)               │
    │                                 │
    │ • Payment Processing            │
    │ • Gateway Integration           │
    │ • Refund Management             │
    └─────────────┬───────────────────┘
                  │
                  │ sends notifications
                  │
                  ▼
    ┌─────────────────────────────────┐
    │🟣  Notification Service         │
    │       (Port 8085)               │
    │                                 │
    │ • Email Notifications           │
    │ • SMS Notifications             │
    │ • Order & Payment Updates       │
    └─────────────────────────────────┘
                  │ All services send telemetry data
                  │
                  ▼
    ┌─────────────────────────────────┐
    │📊  Telemetry Service            │
    │       (Port 8086)               │
    │                                 │
    │ • Distributed Tracing           │
    │ • Service Metrics               │
    │ • Request Tracking              │
    │ • Performance Monitoring        │
    └─────────────────────────────────┘
```

## Features

- **Order Notifications**: Order confirmations, status updates, and cancellations
- **Payment Notifications**: Payment confirmations, failures, and refund notifications
- **Email Service**: Simulated email sending with realistic delays and failure rates
- **SMS Service**: Simulated SMS sending capabilities
- **Notification History**: Complete notification tracking and status
- **Multi-Channel**: Support for both email and SMS notifications
- **Failure Handling**: Graceful handling of notification delivery failures

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Email**: Spring Boot Starter Mail
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Java Version**: 17

## API Endpoints

### Order Notifications
- `POST /api/notifications/order-confirmation` - Send order confirmation
- `POST /api/notifications/order-status` - Send order status update
- `POST /api/notifications/order-cancellation` - Send order cancellation notice

### Payment Notifications
- `POST /api/notifications/payment-confirmation` - Send payment confirmation
- `POST /api/notifications/payment-failure` - Send payment failure notice
- `POST /api/notifications/refund-confirmation` - Send refund confirmation

### Notification Management
- `GET /api/notifications/{id}` - Get notification by ID
- `GET /api/notifications/user/{userId}` - Get notifications for user
- `GET /api/notifications` - Get all notifications

## Telemetry Integration

The Notification Service sends telemetry data to the Telemetry Service for monitoring notification delivery and communication performance:

### Telemetry Features
- **Request Tracing**: Tracing of notification processing workflows from receipt to delivery
- **Service Metrics**: Notification delivery metrics including success rates and processing times
- **Error Tracking**: Automatic detection and reporting of notification delivery failures
- **Channel Monitoring**: Separate tracking for email and SMS delivery channels
- **Business Metrics**: Notification-specific metrics like delivery rates by type and channel

### Traced Operations
- Notification creation and processing
- Email service integration and delivery
- SMS service integration and delivery
- Notification status updates and retry mechanisms
- Database operations (notification persistence and history)
- Service health checks and delivery channel validation

### Telemetry Configuration
The service is configured to send telemetry data to the Telemetry Service:
```yaml
telemetry:
  service:
    url: http://localhost:8086
    enabled: true
  tracing:
    sample-rate: 1.0
  metrics:
    enabled: true
    export-interval: 30s
  notifications:
    track-email-delivery: true
    track-sms-delivery: true
    track-failure-rates: true
```

## Running the Service

### Prerequisites
- Java 17+
- Gradle (or use included Gradle wrapper)

### Start the Service
```bash
./gradlew bootRun
```

The service will start on **port 8085**.

### Database Access
- **H2 Console**: http://localhost:8085/h2-console
- **JDBC URL**: `jdbc:h2:mem:notificationdb`
- **Username**: `sa`
- **Password**: (empty)

## Service Dependencies

### Services That Use This Service
- **Order Service**: Sends order-related notifications
- **Payment Service**: Sends payment-related notifications

### External Dependencies
- Email service providers (simulated)
- SMS service providers (simulated)

## Data Models

### Notification Entity
```json
{
  "id": 1,
  "userId": 1,
  "type": "ORDER_CONFIRMATION",
  "subject": "Order Confirmation - Order #1",
  "message": "Thank you for your order! Your order #1 has been confirmed...",
  "status": "SENT",
  "createdAt": "2024-01-15T10:30:00",
  "sentAt": "2024-01-15T10:30:15",
  "recipientEmail": "user@example.com",
  "recipientPhone": "+1234567890"
}
```

### Notification Types
- `ORDER_CONFIRMATION` - Order placed successfully
- `ORDER_STATUS_UPDATE` - Order status changed
- `ORDER_CANCELLATION` - Order cancelled
- `PAYMENT_CONFIRMATION` - Payment processed successfully
- `PAYMENT_FAILURE` - Payment processing failed
- `REFUND_CONFIRMATION` - Refund processed
- `ACCOUNT_WELCOME` - New account created
- `PASSWORD_RESET` - Password reset request

### Notification Status
- `PENDING` - Notification created, not yet sent
- `SENT` - Notification delivered successfully
- `FAILED` - Notification delivery failed
- `RETRY` - Notification marked for retry

## Notification Simulation

The service simulates realistic notification behavior:

### Email Service
- **Processing Delay**: 500ms simulation
- **Failure Rate**: 5% random failures
- **Console Output**: Detailed email sending logs

### SMS Service  
- **Processing Delay**: 300ms simulation
- **Failure Rate**: 3% random failures
- **Console Output**: SMS delivery confirmation

## Example Usage

### Send Order Confirmation
```bash
curl -X POST http://localhost:8085/api/notifications/order-confirmation \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1
  }'
```

### Send Payment Confirmation
```bash
curl -X POST http://localhost:8085/api/notifications/payment-confirmation \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "userId": 1,
    "orderId": 1
  }'
```

### Get User Notifications
```bash
curl -X GET http://localhost:8085/api/notifications/user/1
```

### Send Order Status Update
```bash
curl -X POST http://localhost:8085/api/notifications/order-status \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "status": "SHIPPED"
  }'
```

## Console Output Examples

When notifications are sent, you'll see console output like:

```
📧 Email sent successfully:
  To: User ID 1
  Subject: Order Confirmation - Order #1
  Type: ORDER_CONFIRMATION
  Message: Thank you for your order! Your order #1 has been...

📱 SMS sent successfully:
  To: +1234567890
  Message: Your order #1 has been shipped and will arrive soon!
```

## Integration with Other Services

### Order Service Integration
The Order Service automatically sends notifications for:
- New order confirmations
- Order status updates (shipped, delivered, etc.)
- Order cancellations

### Payment Service Integration
The Payment Service automatically sends notifications for:
- Successful payment confirmations
- Payment processing failures
- Refund confirmations

## Error Handling

- **Service Unavailability**: Orders and payments continue processing even if notifications fail
- **Delivery Failures**: Failed notifications are marked for potential retry
- **Network Issues**: Graceful degradation when notification channels are unavailable

## Production Considerations

In a production environment, this service would integrate with:
- **Email Providers**: SendGrid, Amazon SES, Mailgun
- **SMS Providers**: Twilio, Amazon SNS, Nexmo
- **Message Queues**: For reliable notification delivery
- **Templates**: Email and SMS template management
- **Personalization**: User-specific notification preferences

## Related Services

- **[Order Service](../order-service/README.md)**: Sends order notifications
- **[Payment Service](../payment-service/README.md)**: Sends payment notifications
- **[User Service](../user-service/README.md)**: Independent service
- **[Product Service](../product-service/README.md)**: Independent service
- **[Telemetry Service](../telemetry-service/README.md)**: Collects telemetry data from this service