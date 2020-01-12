## Дипломная работа AQA

### Документация
[План автоматизации](https://github.com/z88m/netology-qa-diploma/blob/master/docs/Plan.md)

[Отчёт по итогам тестирования](https://github.com/z88m/netology-qa-diploma/blob/master/docs/Report.md)

[Отчет по итогам автоматизации](https://github.com/z88m/netology-qa-diploma/blob/master/docs/Summary.md)

#### Подготовка и запуск теста

_(Если ваша система по какой-то причине не воспринимает адрес 127.0.0.1, то замените его в команде на алиас '__localhost__')_

1. Клонировать репозиторий
    * ```git clone https://github.com/z88m/netology-qa-diploma.git```

1. Перейти в каталог со скачанным содержимым репозитория и скачать докер-контейнеры
    * ```cd ./netology-qa-diploma/```
    * ```docker pull mysql``` 
    * ```docker pull postgres``` 
    * ```docker pull node```
1. Запуск контейнеров Docker и эмулятора биллинга
    * ```docker-compose up -d --build```
1. Запуск SUT с поддержкой MySQL
   * ```java -Dspring.datasource.url=jdbc:mysql://127.0.0.1:3306/app -jar artifacts/aqa-shop.jar```
1. **ИЛИ** Запуск SUT с поддержкой Postgres
   * ```java -Dspring.datasource.url=jdbc:postgresql://127.0.0.1:5432/app -jar artifacts/aqa-shop.jar```
1. Запуск тестов с MySQL
   * ```gradlew -Ddb.url=jdbc:mysql://127.0.0.1:3306/app clean test allureReport```
1. **ИЛИ** Запуск тестов с Postgres
   * ```gradlew -Ddb.url=jdbc:postgresql://127.0.0.1:5432/app clean test allureReport```

Для запуска тестов из **Idea** требуется исправить адрес базы данных в файле *TestSQLHelper.java*

#### Отчёт Allure
Отчёт по тестам генерируется командой ```gradlew allureServe```

#### Окончание тестов и остановка контейнеров

   * Прервать выполнение SUT по Ctrl+C (или закрытием окна терминала)
   * Остановить контейнеры командой ```docker-compose down```
   
P.S.: Для Windows: Чтобы каждый раз не писать эти длинные команды добавлены файлы 0-7.xxx.bat
