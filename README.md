# UBot-Lab
A small extendible Java-based coding game where the player must write a short script to navigate a robot through a maze filled with checkpoints, obstacles, and enemies.

To launch the game from the main directory:
```
java Play
```

# UBot Instructions
## Basic Instructions
Your character is the green circle. His name is UBot. You are his programmer; you must program him to win each level in the lab; don’t worry though—it’s easy. On the left of the screen you will see UBot and the current level. On the right, you will see the coding area and a run and reset button.

## Objective
![UBot](https://user-images.githubusercontent.com/48743701/112224050-ff306500-8be7-11eb-85b4-dd06b77509ad.png)
<br/>
Navigate UBot to the blue space.

## Basic Coding
Programming UBot can be as simple as giving driving directions: “Take a right here. Drive three blocks. Then turn left.”
UBot accepts five basic commands: R, L, U, D, and W.
When UBot sees R, he will move one space right. When he sees L, he moves left. When U, up. When D, down. When he sees W, he waits.
Each command must occupy its own line of text and must be capital.
To run the program in the coding area, click the Run button. To reset the level to its original position, click the Reset button.
An example program to win the first level is shown.

```
R
R
D
D
```
![Full Screenshot](https://user-images.githubusercontent.com/48743701/112224091-0e171780-8be8-11eb-9646-d54abffa57a2.png)


## Some Game Components
### Walls
![Walls](https://user-images.githubusercontent.com/48743701/112224148-24bd6e80-8be8-11eb-975a-c2a50c7b1e8b.png)
<br/>
Walls are grey. No character in the game can go through walls. Walls do not move.

### Roomba
![Roomba](https://user-images.githubusercontent.com/48743701/112224185-3272f400-8be8-11eb-9d5c-5a3521ff79a3.png)
<br/>
A roomba is a red circle with a little yellow dot at its head to indicate where it is going. When a roomba encounters something it cannot pass through, it turns right. They are harmless, but they do sometimes get in the way.

### Spike
![Spikes](https://user-images.githubusercontent.com/48743701/112224198-36067b00-8be8-11eb-8fe1-6bf5e26c22c8.png)
<br/>
Spikes are orange triangles that point in the direction they are travelling. If UBot hits one, he will be terminated. When a spike encounters something it cannot pass through, it turns around and immediately begins travelling in the opposite direction. Spikes can harm UBot. Be careful.

### Checkpoint
![Checkpoint](https://user-images.githubusercontent.com/48743701/112224206-3868d500-8be8-11eb-8ee2-4a77fe99e231.png)
<br/>
A checkpoint is a small red circle. When UBot passes over the checkpoint, it turns green, meaning it is activated. When all checkpoints are activated, the finish square will change from red to blue. 

### Others
![Spike Roomba Generator](https://user-images.githubusercontent.com/48743701/112224237-4159a680-8be8-11eb-8b86-4b22d06bf021.png)
![Spike Roomba](https://user-images.githubusercontent.com/48743701/112224240-4159a680-8be8-11eb-9bb9-760901297cc4.png)
![Generator](https://user-images.githubusercontent.com/48743701/112224241-41f23d00-8be8-11eb-84a2-8d938575a19f.png)
<br/>
Obviously these are not all the components of the game—there are more, but the ones listed are the main ones. Observe to figure out how new components operate. Experiment and find out which things are dangerous. Have fun with it.

## Comments
Because all commands for UBot are capitalized, anything written in all lower case letters is considered by the interpreter to be a comment. In other words, UBot will ignore any line of text that has no capital letters in it. The following is a comment:
```
this is a comment
```

## Looping
Sometimes it becomes more convenient to make a set of instructions and then run it over and over without stopping until the level is finished. When this is the case, we can create loops for UBot.
### The Infinite Loop
The syntax for an infinite loop is as follows:
```
LOOP
insert commands here
END
```
The loop runs until the level is completed or UBot is terminated.
The following loop tells UBot to go right forever.
```
LOOP
R
END
```

### The TIMES Loop
When we want UBot to execute a set of commands a specific number of times, we use the TIMES loop. An example is shown.
```
5 TIMES
R
END
```
In this example, UBot will go right five times.
The next example shows a more complex looping structure using infinite and TIMES loops.
```
LOOP
4 TIMES
R
U
END
W
2 TIMES
D
W
END
END
```
In this example, an infinite loop encompasses two TIMES loops. When running this script, UBot will go right, then up four times. He will wait once. Then he will go down and wait two times before he returns to the top of the loop.

## Indentation
Do not indent. The language developed for UBot discourages indentation despite the fact that this is bad style. Feel free to cringe.

## Menus
### Game Speed
The game speed menu allows the user to change the speed at which the game runs. The default setting is “Normal.”

### Level Select
This menu allows the user to choose a level to play.

### UBot
When “Show UBot” is selected, UBot will appear on the screen. When it is not selected, the level will not show UBot and will run as if he were not present.

Okay that is all there is to this game so far! I hope you enjoy playing it, or building your own levels following the Level Template. More on that to come!
