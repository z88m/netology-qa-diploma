@echo off
chcp 1251 >nul
echo Запуск контейнеров Docker (node, mysql)...
docker pull node
docker pull mysql
docker-compose -f docker-compose-mysql.yml up -d
echo Небольшая пауза, чтобы все контейнеры точно запустились...
TIMEOUT /T 10
echo Запуск SUT...
java -Dspring.datasource.url=jdbc:mysql://127.0.0.1:3306/app -jar artifacts/aqa-shop.jar