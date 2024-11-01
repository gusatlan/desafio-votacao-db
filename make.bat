@ECHO OFF
call .\compile.bat

docker build -t votacao-img:1.0.0 .
docker build -t votacao-img:latest .

del *.log
rmdir /s /q build

ECHO ON