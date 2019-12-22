@echo off
chcp 1251 >null
echo Запуск контейнеров Docker...
docker pull node
docker pull postgres
docker-compose -f docker-compose-postgres.yml up -d
echo Небольшая пауза, чтобы все контейнеры точно запустились...
TIMEOUT /T 10
echo Запуск SUT...
java -Dspring.datasource.url=jdbc:postgresql://127.0.0.1:5432/app -jar artifacts/aqa-shop.jar