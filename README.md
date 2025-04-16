# Notes
## Running
After making a change run the following command to delete all `.class` files before compiling and running
```ps
gci -r *.class | rm
```

Do this command to delete all class files, compile, and run.
```ps
gci -r *.class | rm; javac *.java; java GameStarter
```

## lib Documentation
- `audio` is for all classes related to playing a sound (music or sfx)
- `input` is for classes such as keybinds
- `menus` is for navigation menus that the user will go through and see
- `network` is for all classes relating to hosting and joining game lobbies
- `objects` are for game objects
- `render` is for classes that draw with graphics 

## Server Hosting
TODO: Figure this out 