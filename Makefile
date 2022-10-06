clean:
	./gradlew clean

build: clean
	./gradlew build -x test
