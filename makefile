JC = javac
JFLAGS = -g
JCLASSDIR = bin/
SRC = src/
TEST = tests/
DOCPATH = docs/

.SUFFIXES: .java .class
.java.class:
						$(JC) -cp .:./bin -d $(JCLASSDIR) $(JFLAGS) $*.java

CLASSES = $(TEST)*.java



default: classes

runEchoServer:
		java -cp ./bin/ EchoServer 4444

runEchoClient:
		java -cp ./bin/ EchoClient localhost 4444

classes: $(CLASSES:.java=.class)

doc:
		javadoc -d ./docs/ $(CLASSES)

clean:
			$(RM)r $(JCLASSDIR)*
			$(RM)r $(DOCPATH)*
