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
