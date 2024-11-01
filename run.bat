@ECHO OFF
call .\make.bat

docker-compose down
docker compose down

docker compose up
docker-compose up
ECHO ON
