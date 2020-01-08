
build: MyStaticWebServer.class

# If you add other java classes, just copy this rule and follow the pattern
MyStaticWebServer.class: MyStaticWebServer.java
	javac $^

# Used by the CI Server to run your server on the test data.
launch: MyStaticWebServer.class
	(cd data; java -cp .. MyStaticWebServer &)


# Used by the CI server to run my unit tests
test: build launch
	java -cp lib/junit-4.11.jar:lib/hamcrest-all-1.3.jar:. org.junit.runner.JUnitCore StaticHTTPServerTest


