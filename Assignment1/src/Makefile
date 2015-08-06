#A Makefile for the serial vs parallel assignment
#Alison Hoernle - HRNALI002
#3 August 2015

JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
	
CLASSES = \
	Driver.java \
	MedianFilter.java \
	FilterHandler.java \
	FilterHandlerParallel.java \
	
default: classes

classes: $(CLASSES:.java=.class)

run:
	java Driver
clean:
	$(RM) *.class