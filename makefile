JC = javac
JFLAGS = -g
JCLASSDIR = bin/
SRC = src/
TEST = tests/
DOCPATH = docs/

PORT = 4444

.SUFFIXES: .java .class
.java.class:
						$(JC) -cp .:./bin -d $(JCLASSDIR) $(JFLAGS) $*.java

CLASSES = $(TEST)*.java\
					$(SRC)*.java



default: classes

runGreetingsServer:
		java -cp ./bin/ GreetingsServer $(PORT)

runGreetingsClient:
		java -cp ./bin/ GreetingsClient localhost $(PORT)

runEchoServer:
		java -cp ./bin/ EchoServer $(PORT)

runEchoClient:
		java -cp ./bin/ EchoClient localhost $(PORT)

runServer:
		java -cp ./bin/ Server $(PORT)

runClient:
		java -cp ./bin/ ClientApplication localhost $(PORT)


classes: $(CLASSES:.java=.class)

doc:
		javadoc -d ./docs/ $(CLASSES)

clean:
			$(RM)r $(JCLASSDIR)*
			$(RM)r $(DOCPATH)*
