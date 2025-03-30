# Notes
After making a change run the following command to delete all `.class` files before compiling and running
```ps
gci -r *.class | rm
```

Do this command to delete all class files, compile, and run.
```ps
gci -r *.class | rm; javac *.java; java GameStarter
```
