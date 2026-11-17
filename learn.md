| Django ORM                      | Spring Boot JPA | Database relationship |
| ------------------------------- | --------------- | --------------------- |
| `ForeignKey`                  | `@ManyToOne`  | Many-to-One           |
| `OneToOneField`               | `@OneToOne`   | One-to-One            |
| `ManyToManyField`             | `@ManyToMany` | Many-to-Many          |
| Reverse ForeignKey relationship | `@OneToMany`  | One-to-Many           |

Run

export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

./mvnw spring-boot:run

SHOW DATABASES;

USE lostfound;

show tables;
DESCRIBE items;

set -a
source .env
set +a




# Application

APP_DEBUG=false
APP_ENV=prod
PORT=10000
APP_LOG_LEVEL=INFO
SQL_DEBUG=false




```shell
# Application
APP_DEBUG=true
APP_ENV=dev
PORT=8080
APP_LOG_LEVEL=DEBUG
SQL_DEBUG=true



DB_URL=jdbc:mysql://lostfound-y1qr56.db.enzonic.com:3306/main?sslMode=REQUIRED
DB_USERNAME=u_csabzelkvb
DB_PASSWORD=woiZim1YB4HdzuNrdz6gfBKxTo0JdQNz

JWT_SECRET='GoWTvi9nZqPrEYfuE5h0rn2SY3RP3FFqT7IGHZICwfI'
```
