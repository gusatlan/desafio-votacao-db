#!/bin/bash
./compile

docker build -t votacao-img:1.0.0 .
docker build -t votacao-img:latest .

rm *.log
rm -Rvf build

