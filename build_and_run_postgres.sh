#!/bin/bash

# Define variables
IMAGE_NAME="security_postgres"
CONTAINER_NAME="security_postgres_container"
POSTGRES_PASSWORD="1234567890"
VOLUME_NAME="security_db_pgdata"
PORT=5433

# Build the Docker image
echo "Building Docker image..."
docker build --no-cache -t $IMAGE_NAME -f postgres.Dockerfile .

# Check if the container is already running, stop and remove it if it exists
if [ "$(docker ps -a | grep $CONTAINER_NAME)" ]; then
  echo "Stopping and removing existing container..."
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi

## Remove the existing Docker volume
#if [ "$(docker volume ls | grep $VOLUME_NAME)" ]; then
#  echo "Removing existing Docker volume..."
#  docker volume rm $VOLUME_NAME -f
#fi
#
## Create a new Docker volume
#echo "Creating new postgres Docker volume..."
#docker volume create $VOLUME_NAME

# Check if the Docker volume exists
if [ "$(docker volume ls -q | grep $VOLUME_NAME)" ]; then
  echo "Docker volume '$VOLUME_NAME' already exists. Not created!"
else
  echo "Creating new postgres Docker volume..."
  docker volume create $VOLUME_NAME
fi


# Run the Docker container
echo "Running Docker container..."
docker run -d \
  --name $CONTAINER_NAME \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  -p $PORT:5432 \
  -v $VOLUME_NAME:/var/lib/postgresql/data \
  $IMAGE_NAME

# Check if the container is running
if [ "$(docker ps | grep $CONTAINER_NAME)" ]; then
  echo "Docker container is running successfully!"
else
  echo "Failed to run the Docker container."
fi