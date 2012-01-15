mvn dependency:copy-dependencies
cp target/*.jar ./target/dependency
java -classpath './target/dependency/*' org.rendershark.http.HttpServer ./src/main/resources/rendershark.properties