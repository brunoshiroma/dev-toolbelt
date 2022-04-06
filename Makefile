clean:
	./gradlew clean

build-java:
	./gradlew build

build-java-github:
	./gradlew clean buildGitHubActions --no-daemon

build-docker: @build-java-github
	docker build .

build-docker-multistage:
	docker build . -f Dockerfile.multistage
