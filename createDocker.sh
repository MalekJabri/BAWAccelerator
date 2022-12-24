docker stop baw-accelerator
docker container rm baw-accelerator
docker rmi -f baw-accelerator:latest
docker build -t baw-accelerator:latest .
