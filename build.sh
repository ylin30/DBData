#!/bin/bash

YELLOW_GREEN='\033[1;32m'
NC='\033[0m'

echo -e "${YELLOW_GREEN=}build Tides ...$NC\n\n"
mvn clean package assembly:single -DskipTests
