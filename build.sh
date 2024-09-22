#!/bin/bash
./gradlew build; cp -r config/ ./build/libs/; cp -r ssl/ ./build/libs/