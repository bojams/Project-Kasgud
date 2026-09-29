@echo off
if exist bin rmdir /s /q bin
mkdir bin
javac -d bin -sourcepath src src/Main.java
if errorlevel 1 (pause & exit /b 1)
java -cp bin Main
