rm -rf lib/*
cp /Users/simon/.m2/repository/com/github/brotherlogic/javaserver/javaserver/$1/javaserver-$1.jar ./
mvn deploy:deploy-file -Durl=file:///Users/simon/code/card_interface/lib/ -Dfile=javaserver-$1.jar  -DgroupId=com.github.brotherlogic.javaserver -DartifactId=javaserver -Dpackaging=jar -Dversion=$1
rm javaserver-$1.jar
