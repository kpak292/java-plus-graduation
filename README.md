# java-explore-with-me

## Архитектура системы

```mermaid
graph TD
    subgraph "Статистика"
        direction LR
        SS[Stats Service]
    end

    subgraph "Базы данных"
        direction LR
        MDB[(Main DB)]
        SDB[(Stats DB)]
    end

    subgraph "Основные сервисы"
        direction TB
        US[User Service]
        ES[Event Service]
        RS[Request Service]
        CS[Comment Service]
    end

    SS --> SDB

    US --> MDB
    ES --> MDB
    ES -->SS
    CS --> MDB
    RS --> MDB

    CS <--> ES
    RS <--> ES
```

## Main DB structure

```mermaid
erDiagram
    categories {
        bigint id PK
        varchar(50) name
    }

    users {
        bigint id PK
        varchar(250) name
        varchar(254) email
    }

    locations {
        bigint id PK
        double lat
        double lon
    }

    events {
        bigint id PK
        varchar(2000) annotation
        bigint category_id FK
        bigint confirmed_requests
        timestamp created_on
        varchar(7000) description
        timestamp event_date
        bigint user_id FK
        bigint location_id FK
        boolean paid
        integer participant_limit
        timestamp published_on
        boolean request_moderation
        varchar(100) state
        varchar(120) title
        bigint views
    }

    requests {
        bigint id PK
        bigint requester_id FK
        bigint event_id FK
        timestamp created_on
        varchar(100) status
    }

    compilations {
        bigint id PK
        boolean pinned
        varchar(50) title
    }

    event_compilations {
        bigint event_id FK
        bigint compilation_id FK
    }

    comments {
        bigint id PK
        bigint event_id FK
        bigint author_id FK
        varchar(255) message
    }
    
    users ||--o{ events: user_id
    users ||--o{ requests: requester_id
    events ||--o{ requests: event_id
    events ||--o{ comments: event_id
    users ||--o{ comments: author_id
    events ||--o{ event_compilations: event_id
    compilations ||--o{ event_compilations: compilation_id
    categories ||--o{ events: category_id
    locations ||--o{ events: location_id
```

## Stats DB structure

```mermaid
erDiagram
    test {
        bigint id PK
        varchar(100) app
        varchar(100) uri
        varchar(100) ip
        timestamp created_at
    }
```
## API Спецификация

Подробная спецификация API доступна в формате OpenAPI 3.0:

### Полная спецификация API
Подробная спецификация в формате OpenAPI доступна в файле:
- [Основной сервис](/https://petstore.swagger.io/?url=https://raw.githubusercontent.com/kpak292/java-plus-graduation/main/ewm-main-service-spec.json)
- [Сервис статистики](/https://petstore.swagger.io/?url=https://raw.githubusercontent.com/kpak292/java-plus-graduation/main/ewm-stats-service-spec.json)
