mvn clean install dependency:copy-dependencies
cd target
    mkdir rendershark
    mkdir rendershark/bin
    mkdir rendershark/lib
    cp rendershark*.jar rendershark/lib
    cp dependency/* rendershark/lib
    cp ../src/main/scripts/* rendershark/bin