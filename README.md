# Order Book Service

This repository contains the Order Book Service, a Spring Boot application that provides RESTful APIs for managing order books and user authentication.

## Table of Contents

- [Order Book Service](#order-book-service)
    - [Table of Contents](#table-of-contents)
    - [Getting Started](#getting-started)
        - [Prerequisites](#prerequisites)
        - [Installation](#installation)
    - [Running the Application](#running-the-application)
    - [Running Tests](#running-tests)
    - [API Endpoints](#api-endpoints)
        - [User Authentication](#user-authentication)
        - [Order Book](#order-book)
        - [Trade History](#trade-history)
        - [Limit Order](#limit-order)
    - [Contributing](#contributing)
    - [License](#license)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/order-book-service.git
    cd order-book-service
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Running the Application

To run the application, use the following command:
```sh
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`.

## Running Tests

To run the unit tests, use the following command:
```sh
mvn test
```

## API Endpoints

### User Authentication

- **POST /api/user/login**: Authenticates a user and returns a JWT token.
    - **Request Body**:
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```
    - **Response**:
      ```json
      {
        "token": "string"
      }
      ```

### Order Book

- **GET /api/\<currencyPair\>/orderbook**: Retrieves the current state of the order book for the specified currency pair.
    - **Path Parameter**:
        - `currencyPair`: The currency pair for which the order book is requested (e.g., BTCZAR).
    - **Response**:
      ```json
      {
        "bids": [
          {
            "side": "SELL",
            "quantity": 0.02352094,
            "price": 1205649,
            "currencyPair": "BTCZAR"
          },
          {
            "side": "SELL",
            "quantity": 0.552,
            "price": 1205653,
            "currencyPair": "BTCZAR"
          }
        ],
        "asks": [
          {
            "side": "BUY",
            "quantity": 0.11498758,
            "price": 1204532,
            "currencyPair": "BTCZAR"
          },
          {
            "side": "BUY",
            "quantity": 0.05,
            "price": 1164656,
            "currencyPair": "BTCZAR"
          }
        ],
        "lastChange": "2024-07-26T11:45:53.463699Z"
      }
      ```

### Trade History

- **GET /api/\<currencyPair\>/trades**: Retrieves the trade history for the specified currency pair.
    - **Path Parameter**:
        - `currencyPair`: The currency pair for which the trade history is requested (e.g., BTCZAR).
    - **Query Parameters**:
        - `skip`: The number of records to skip (default is 0).
        - `limit`: The maximum number of records to return (default is 10).
    - **Response**:
        ```json
        {
          "trades": [
            {
              "id": 1,
              "price": 1015459,
              "quantity": 0.56879135,
              "currencyPair": "BTCZAR",
              "tradedAt": "2022-10-11T13:44:24.571Z",
              "takerSide": "SELL",
              "quoteVolume": 570680.8748647
            }
          ]
        }
        ```

### Limit Order

- **POST /api/order/limit**: Creates a new limit order.
    - **Request Body**:
      ```json
      {
        "currencyPair": "string",
        "quantity": "number",
        "price": "number",
        "side": "BUY or SELL"
      }
      ```
    - **Response**:
      ```json
      {
        "message": "Limit order created successfully."
      }
      ```

## Contributing

No contributions are accepted - this is test assignment.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.