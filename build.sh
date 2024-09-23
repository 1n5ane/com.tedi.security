#!/bin/bash
./gradlew clean build; cp -r config/ ./build/libs/; cp -r ssl/ ./build/libs/