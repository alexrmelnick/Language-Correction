dev_checker:
	javac -d bin Checker.java **/*.java
	jar cvfm checker.jar manifestChecker.txt -C bin . -C SQLite .

dev_corrector:
	javac -d bin Corrector.java **/*.java
	jar cvfm corrector.jar manifestCorrector.txt -C bin . -C SQLite .
