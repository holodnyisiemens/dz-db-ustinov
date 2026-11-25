# Питомник собак

Учебный проект по Spring Data JPA (Hibernate) + PostgreSQL: домен «Питомник собак».

## Сущности и связи

| Сущность | Назначение |
|---|---|
| `Litter` (Приплод) | Помёт щенков |
| `Dog` (Собака) | Собака питомника |
| `VeterinaryPassport` (Ветеринарный паспорт) | Паспорт собаки |
| `Exhibition` (Выставка) | Выставка собак |
| `Participation` (Участие) | Связка «собака ↔ выставка» с оценкой и местом |

Связи:

- **1:N двусторонняя** `Litter — Dog`: владеющая сторона `Dog.litter` (`@ManyToOne` + `@JoinColumn`),
  обратная `Litter.dogs` (`@OneToMany(mappedBy = "litter")`, каскад `PERSIST`/`MERGE`, `orphanRemoval = true`).
- **N:M через сущность-связку** `Dog ↔ Exhibition` через `Participation`
  (две `@ManyToOne` + собственные атрибуты `score`, `place`, уникальность пары «собака + выставка»).
- **1:1** `Dog — VeterinaryPassport`: владеющая сторона `VeterinaryPassport.dog` (`@OneToOne` + `@JoinColumn(unique = true)`),
  обратная `Dog.passport` (`@OneToOne(mappedBy = "dog")`).

## Стек

Java 17+ · Spring Boot 3.3 · Spring Data JPA (Hibernate 6) · PostgreSQL 16 (Docker Compose) · Flyway · Maven

## Как поднять проект

Требования: JDK 17+, Docker с запущенным демоном, Maven 3.9+.

```bash
# 1. PostgreSQL в Docker
docker compose up -d

# 2. Сборка и запуск приложения
mvn clean package -DskipTests
java -jar target/kennel-0.0.1-SNAPSHOT.jar
```

Либо одной командой: `mvn spring-boot:run`.

Приложение: http://localhost:8080. БД: `jdbc:postgresql://localhost:5432/kennel`
(пользователь/пароль `kennel`/`kennel`). Схему создаёт Flyway (`src/main/resources/db/migration/V1__init.sql`),
Hibernate работает в режиме `ddl-auto: validate`, `open-in-view: false`.

Остановить: `docker compose down` (данные сохранятся в volume `kennel-data`, удалить вместе с данными: `docker compose down -v`).

## Как проверить каждый пункт задания

Ниже готовый сценарий (Git Bash / Linux / PowerShell — команды одинаковые, кроме формата кавычек).
Каждый шаг ссылается на пункт таблицы самопроверки.

> Если Git Bash на Windows портит кириллицу в теле запроса (в ответе вместо «Рекс» — «????»),
> отправляйте тело из UTF-8-файла: `curl -d @dog.json -H "Content-Type: application/json" ...`
> (это особенность передачи аргументов curl.exe в Git Bash, к приложению отношения не имеет).

### 0. Подготовка данных

```bash
# приплод (1:N — владеющая/обратная сторона, пункт 2)
curl -X POST localhost:8080/api/litters -H "Content-Type: application/json" \
  -d '{"name":"Горные орлы","birthDate":"2023-04-10"}'
# => {"id":1,"name":"Горные орлы","birthDate":"2023-04-10","dogs":[]}

# собака в приплоде: каскад PERSIST + вспомогательный метод addDog (пункты 2, 13, 14)
curl -X POST localhost:8080/api/litters/1/dogs -H "Content-Type: application/json" \
  -d '{"name":"Рекс","breed":"Лабрадор","gender":"MALE","birthDate":"2023-05-01"}'
# => собака с litterId=1

# ещё собаки: напрямую, со ссылкой на приплод, и без неё
curl -X POST localhost:8080/api/dogs -H "Content-Type: application/json" \
  -d '{"name":"Белла","breed":"Лабрадор","gender":"FEMALE","birthDate":"2023-05-03","litterId":1}'
curl -X POST localhost:8080/api/dogs -H "Content-Type: application/json" \
  -d '{"name":"Гром","breed":"Овчарка","gender":"MALE","birthDate":"2022-11-20"}'
```

### 1. Все типы связей

```bash
# 1:N — обратная сторона подтянула собак через @EntityGraph (пункт 12)
curl localhost:8080/api/litters/1
# => {"id":1,...,"dogs":[Рекс, Белла]}

# 1:1 — выдача паспорта (пункт 4); assignTo синхронизирует обе стороны (пункт 14)
curl -X POST localhost:8080/api/passports -H "Content-Type: application/json" \
  -d '{"passportNumber":"AB-123456","issueDate":"2023-06-01","veterinarianName":"Иванов И.И.","dogId":1}'
# повторный паспорт той же собаке -> 409 (уже есть)
# N:M — выставка и участие (пункт 3)
curl -X POST localhost:8080/api/exhibitions -H "Content-Type: application/json" \
  -d '{"title":"Кубок гор","city":"Москва","eventDate":"2024-03-15"}'
curl -X POST localhost:8080/api/participations -H "Content-Type: application/json" \
  -d '{"dogId":1,"exhibitionId":1,"score":95,"place":2}'
curl -X POST localhost:8080/api/participations -H "Content-Type: application/json" \
  -d '{"dogId":2,"exhibitionId":1,"score":98,"place":1}'
# та же пара собака+выставка -> 409 (uq_participation_dog_exhibition)
curl -X POST localhost:8080/api/participations -H "Content-Type: application/json" \
  -d '{"dogId":1,"exhibitionId":1,"score":90,"place":3}'
# выставка с протоколом: participations + собаки одним запросом (пункт 12)
curl localhost:8080/api/exhibitions/1
# собака с паспортом: join fetch ленивых litter/passport (пункты 8, 12)
curl localhost:8080/api/dogs/1
# => {"id":1,...,"litterId":1,"litterName":"Горные орлы","passportId":1,"passportNumber":"AB-123456",...}
```

### 2. Три вида запросов

```bash
# из имени метода — derived query (пункт 6)
curl "localhost:8080/api/dogs/by-breed?breed=Лабрадор"
curl "localhost:8080/api/exhibitions/by-city?city=Москва"
curl "localhost:8080/api/passports/number/AB-123456"
curl "localhost:8080/api/participations/by-dog-name?name=Рекс"

# JPQL без nativeQuery (пункт 8)
curl "localhost:8080/api/dogs/younger-than?date=2023-01-01"
curl "localhost:8080/api/participations/results?exhibitionId=1"

# чистый SQL — nativeQuery = true (пункт 7)
curl "localhost:8080/api/litters/big?from=2020-01-01&minDogs=2"
curl "localhost:8080/api/participations/podium?exhibitionId=1"
# => [{"place":1,"dogName":"Белла","breed":"Лабрадор","score":98},{"place":2,...}]
```

### 3. Criteria API — динамические фильтры (пункт 10)

Все параметры необязательные; в `where` попадают только заполненные:

```bash
curl "localhost:8080/api/dogs/search?breed=Лабрадор&gender=FEMALE"
curl "localhost:8080/api/dogs/search?bornAfter=2023-01-01&bornBefore=2023-12-31"
curl "localhost:8080/api/dogs/search?nameContains=ре"
curl "localhost:8080/api/dogs/search"   # без фильтров — все собаки
```

### 4. orphanRemoval — удаление «сироты» (пункт 14)

```bash
# добавим собаку без паспорта и участий (=> id 4)
curl -X POST localhost:8080/api/litters/1/dogs -H "Content-Type: application/json" \
  -d '{"name":"Шторм","breed":"Лабрадор","gender":"MALE","birthDate":"2023-05-05"}'

# удаляем её из приплода
curl -X DELETE localhost:8080/api/litters/1/dogs/4

# записи в таблице dogs больше нет: orphanRemoval удалил её из БД
curl localhost:8080/api/dogs/4
# => 404 {"error":"Собака с id=4 не найдена"}
```

Собаку, у которой есть паспорт или участия, удалить так нельзя: внешний ключ вернёт
409 `DataIntegrityViolationException` (защита целостности данных).

### 5. @Version — оптимистичная блокировка (пункт 19)

```bash
curl localhost:8080/api/dogs/1        # запоминаем "version":0
curl -X PUT localhost:8080/api/dogs/1 -H "Content-Type: application/json" \
  -d '{"name":"Рекс-2","breed":"Лабрадор","gender":"MALE","version":0}'
# => 200, "version":1
curl -X PUT localhost:8080/api/dogs/1 -H "Content-Type: application/json" \
  -d '{"name":"Рекс-3","breed":"Лабрадор","gender":"MALE","version":0}'
# => 409 {"error":"Собака была изменена другим пользователем, актуальная версия: 1"}
```

### 6. Остальное (архитектура/инструменты)

- DTO наружу, сущности не покидают сервис — пакет `dto`, контроллеры принимают/возвращают только DTO (пункт 11).
- `open-in-view: false`, транзакции на сервисе с `readOnly = true` для чтения — `application.yml`, `service/*` (пункты 9, 11).
- Flyway-схема — `db/migration/V1__init.sql` (пункт 12 инструментов).
- В консоли приложения (`show-sql: true`) видно SQL: отдельные `select` для LAZY-связей
  отсутствуют в детальных запросах (загружаются join fetch / `@EntityGraph`), но появляются,
  если ленивая связь трогается вне них, — наглядно видно разницу.

## Таблица самопроверки

| Пункт задания | Где реализовано |
|---|---|
| 5 сущностей | `entity/Dog`, `entity/Litter`, `entity/VeterinaryPassport`, `entity/Exhibition`, `entity/Participation` |
| 1:N двусторонняя: владеющая `@ManyToOne` + `@JoinColumn` | `Dog.litter` — `entity/Dog.java` |
| 1:N двусторонняя: обратная `@OneToMany(mappedBy)` | `Litter.dogs` — `entity/Litter.java` |
| N:M через связку с собственными атрибутами (две `@ManyToOne`) | `Participation.dog`, `Participation.exhibition`, поля `score`, `place` — `entity/Participation.java` |
| 1:1 `@OneToOne` | `VeterinaryPassport.dog` (`@JoinColumn(unique=true)`), `Dog.passport` (`mappedBy`) |
| Свой `JpaRepository` на каждую сущность | `repository/DogRepository`, `LitterRepository`, `VeterinaryPassportRepository`, `ExhibitionRepository`, `ParticipationRepository` |
| Запрос из имени метода (derived) | `DogRepository.findByBreed`, `ExhibitionRepository.findByCity`, `VeterinaryPassportRepository.findByPassportNumber`, `ParticipationRepository.findByDogName` |
| `@Query(nativeQuery = true)` | `LitterRepository.findWithAtLeastDogsBornAfter`, `ParticipationRepository.findPodiumNative` |
| `@Query` JPQL без `nativeQuery` | `DogRepository.findDetailById`, `DogRepository.findYoungerThanWithLitter`, `ParticipationRepository.findResultsByExhibition` |
| Транзакции открывает сервис (`@Transactional`, не на репозиториях/контроллерах) | все методы `service/*Service` |
| Методы чтения `readOnly = true` | `@Transactional(readOnly = true)` на GET-методах сервисов |
| Criteria API: динамические фильтры по необязательным параметрам | `DogService.search(DogSearchCriteria)` — `CriteriaBuilder/Root/Predicate` |
| Наружу DTO, не сущности | пакет `dto/*`, контроллеры `controller/*` |
| `open-in-view: false` | `application.yml` → `spring.jpa.open-in-view` |
| Flyway для схемы | `src/main/resources/db/migration/V1__init.sql` |
| Все связи явно `LAZY` | `fetch = FetchType.LAZY` во всех ассоциациях сущностей |
| Ленивые связи через `join fetch` / `@EntityGraph`, где нужны | `DogRepository.findDetailById` (join fetch), `LitterRepository.findDetailById`, `ExhibitionRepository.findDetailById`, `VeterinaryPassportRepository.findByPassportNumber`, `ParticipationRepository.findByDogName` (`@EntityGraph`) |
| Каскады точечно `PERSIST`/`MERGE`, без `ALL` | `Litter.dogs`: `cascade = {CascadeType.PERSIST, CascadeType.MERGE}` |
| `orphanRemoval = true` (композиция) | `Litter.dogs` — проверка: `DELETE /api/litters/{id}/dogs/{dogId}` |
| Вспомогательный метод синхронизирует обе стороны связи | `Litter.addDog/removeDog`, `VeterinaryPassport.assignTo` (используются в `DogService.create`, `LitterService.addDog/removeDog`, `VeterinaryPassportService.create`) |
| Идентификаторы `SEQUENCE`, не `IDENTITY` | `@SequenceGenerator` на каждой сущности; `*_seq` создаются в `V1__init.sql` |
| `@Version` на совместно редактируемой сущности | `Dog.version`, `Participation.version`; проверка в `DogService.update` (409 при устаревшей версии) |
| `equals/hashCode` по «тип + id», без коллекций | во всех 5 сущностях |

## API

| Метод и путь | Описание | Тип запроса |
|---|---|---|
| `POST /api/litters` | Создать приплод | — |
| `GET /api/litters/{id}` | Приплод с собаками (`@EntityGraph`) | — |
| `GET /api/litters/big?from&minDogs` | Крупные приплоды | native SQL |
| `POST /api/litters/{id}/dogs` | Добавить собаку в приплод (каскад PERSIST) | — |
| `DELETE /api/litters/{id}/dogs/{dogId}` | Убрать собаку из приплода (orphanRemoval) | — |
| `POST /api/dogs` | Создать собаку (можно с `litterId`) | — |
| `GET /api/dogs/{id}` | Собака с приплодом и паспортом (JPQL join fetch) | JPQL |
| `PUT /api/dogs/{id}` | Обновить собаку (проверка `@Version`) | — |
| `GET /api/dogs/by-breed?breed=` | Собаки по породе | derived |
| `GET /api/dogs/younger-than?date=` | Собаки моложе даты (join fetch) | JPQL |
| `GET /api/dogs/search?...` | Поиск по необязательным фильтрам | Criteria API |
| `POST /api/passports` | Выдать паспорт собаке (1:1) | — |
| `GET /api/passports/number/{number}` | Паспорт по номеру | derived + `@EntityGraph` |
| `POST /api/exhibitions` | Создать выставку | — |
| `GET /api/exhibitions/{id}` | Выставка с протоколом (`@EntityGraph`) | — |
| `GET /api/exhibitions/by-city?city=` | Выставки по городу | derived |
| `POST /api/participations` | Зарегистрировать участие (N:M, оценка/место) | — |
| `GET /api/participations/by-dog-name?name=` | Участия собаки | derived + `@EntityGraph` |
| `GET /api/participations/results?exhibitionId=` | Протокол выставки | JPQL join fetch |
| `GET /api/participations/podium?exhibitionId=` | Призёры (места 1–3) | native SQL |

Даты передаются в ISO-формате: `2023-05-01`. `gender`: `MALE` / `FEMALE`.
