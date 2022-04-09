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

build-native: build-java-github
	rm -rf build/native
	mkdir -p build/native
	cd build/native && jar -xvf ../libs/devtoolbelt.jar && cp -R META-INF BOOT-INF/classes && \
	native-image --no-fallback --static -H:Name=dev-toolbelt -cp BOOT-INF/classes:`find BOOT-INF/lib | tr '\n' ':'`
