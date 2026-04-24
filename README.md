I build a Space Invaders game built in Java using the MVC design pattern, which was spilt across three classes. First being gameModel which handles all game logic and data with no Swing imports.
GameView which reads from the model and draws everything the player sees. GameController handles keyboard input and runs the game loop using a Swing Timer.

Beyond the base game, I added several extensions to make it more interesting. Aliens are split into differnet tiers with different health values by useing (green bar for health)
and to differ the types of enimes/stronger ones it's the color that helps
and they are easy to spot. Defeating tougher aliens rewards bonus lives, and blue aliens have a chance to drop a a blue ball power-up that activates
triple shot for 10 seconds. 
Once all aliens are cleared, a Boss spawns that zig-zags across the screen with 50 health defeating it triggers a Victory screen. 
I also added pause and reset functionality, and a visual overhaul.
