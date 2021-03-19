run: BestCollatz
	java BestCollatz
	
BestCollatz: 
	javac BestCollatz.java Timer.java

clean: 
	rm *.class

flushFiles:
	rm -rf *.class info.txt completed\ collatz
