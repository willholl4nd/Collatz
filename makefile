run: BestCollatz
	java BestCollatz
	
BestCollatz: *.java
	javac *.java

clean: 
	rm *.class

flushFiles:
	rm -rf *.class info.txt completed\ collatz
