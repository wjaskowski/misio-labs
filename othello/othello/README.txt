Contact: wojciech.jaskowski@cs.put.poznan.pl

Requires 
* Java 1.7
* Maven (http://maven.apache.org/)

Building
--------
mvn install

Running
-------
java -jar target/othello-1.0-single.jar

Eclipse
-------
File -> Import -> Existing Maven Projects 

In case of errors, use Eclipse Quick Fix (ctrl+1)

Where to start?
--------------
* You must implement your own BoardMoveEvaluator<OthelloBoard> (see ExampleMoveEvaluator for an example).

* For learning the evaluation function, you probably need to hack a bit Othello class
