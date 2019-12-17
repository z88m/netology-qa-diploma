## Дипломная работа AQA

#### Подготовка и запуск теста
1. Клонировать репозиторий
    * ```git clone https://github.com/z88m/netology-qa-diploma.git```

1. Перейти в каталог со скачанным содержимым репозитория и скачать докер-контейнеры
    * ```cd ./netology-qa-diploma/```
    * ```docker pull mysql``` 
    * ```docker pull postgres``` 
    * ~~docker pull node~~ - заменён на локальный node.js
1. Запуск версии с поддержкой MySQL
    * ```docker-compose -f docker-compose-mysql.yml up -d``` 
1. **ИЛИ** Запуск версии с поддержкой Postgres
    * ```docker-compose -f docker-compose-postgres.yml up -d``` 
1. Перейти в каталог с эмулятором биллинга и запустить его
    * ```cd ./artifacts/gate-simulator```
    * ```npm start```
1. Открыть новый терминал, перейти в каталог с тестируемым приложением и запустить его
    * ```cd ./netology-qa-diploma/artifacts```
    * ```java -jar aqa-shop.jar```
1.  Открыть новый терминал, перейти в каталог репозитория и запустить тест
    * ```cd ./netology-qa-diploma/```
    * ```gradlew test``` *(или через **IntelliJ IDEA**)*
