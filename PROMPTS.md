-----Prompt 1 — MVC skeleton------
I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.

Prompt 2 —
Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.

Prompt 3 — Build the View
Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.

Prompt 4 — Wire the Controller
Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.

Prompt: 5 — Basic model testing
Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.

Prompt: 6 — Game State Management (Pause and Reset)
Add a pause and reset feature to the game. Update GameModel to include an isPaused boolean and a reset() method that clears bullets and restores initial positions. Update GameController to toggle pause with the 'P' key and reset with the 'R' key. Ensure the game loop only updates the model when the game is active and not paused. Update GameView to display a "PAUSED" overlay.

Result: Successfully implemented the pause and reset states.

Self-Correction during implementation: I noticed the HUD (Score and Lives) would "squish" or overlap when the PAUSED text appeared. I fixed this by updating the View to use dynamic screen coordinates (getWidth() and getHeight()) so the UI stays stable on all screen sizes.

Prompt: 7 — Advanced Alien Tiers and Health
Upgrade the alien system from simple blocks to a multi-tiered system based on the provided UI reference. Create a custom Alien class in the Model that stores health and type. Assign different health values to rows (Top rows: 5 hits, Bottom rows: 1 hit). Update the View to color-code these aliens (Red, Magenta, Blue, Green) so the player can identify their strength. Update collision logic so aliens only disappear when their health reaches zero.

Result:
The multi-tiered system is working, but testing revealed two major gameplay issues:

Alien Speed: Because the logic became more complex, the aliens were moving way too fast across the screen. I added an alienMoveTimer to the Model to slow them down to a playable speed.

Health Feedback: It was frustrating to shoot a 5-hit alien without seeing progress. I added a green health bar indicator in the View that shrinks as they take damage to provide better player feedback.

## Debugging: UI and Logic Refinement

Error observed:
HUD text misalignment and unplayable alien movement speed.

What I understood:
The HUD needed dynamic positioning to handle window changes. The alien movement was tied too closely to the frame rate, requiring a separate timer to regulate speed. Multi-hit enemies require visual health bars so the player knows the collision logic is working.

Follow-up prompt:
"Fix the alien movement speed by adding a move timer. Update the HUD to use dynamic positioning so it doesn't squish. Add a green health bar indicator above multi-hit aliens."

Result:
Code updated across all three files. The game is now balanced and the UI is stable.

Prompt: 8
Implement a reward system for defeating high-tier aliens. When a Red (Tier 4) alien is fully destroyed, instantly grant the player +2 lives. When a Pink/Magenta (Tier 3) alien is fully destroyed, instantly grant the player +1 life. No falling object or collection mechanic is needed — the lives update immediately in the HUD the moment the alien's health reaches zero. Only GameModel.java needs changes.
Result:
Instant life grants are working. Killing a Red alien adds +2 lives and killing a Pink alien adds +1 life, reflected immediately in the HUD.
Errors during implementation:
Initially implemented a falling PowerUp ball that the player had to physically collect. This was scrapped for two reasons: the ball would fall off screen before the player could reach it, meaning lives were never actually granted, and the collection mechanic added unnecessary complexity. Removed the PowerUp class, list, and all falling/collision logic entirely.
Self-Correction:
Replaced the drop-and-collect system with direct lives += 2 and lives += 1 inside the if (a.health <= 0) block in checkCollisions(). Since the life is granted at the exact moment the alien dies, there is nothing to miss. The HUD updates instantly on the next repaint cycle.

Prompt: 9
Add a temporary weapon upgrade to the game. When a Blue (Tier 2) alien is fully destroyed, it has a 30% chance to drop a falling Blue Orb. If the player's ship touches the orb, a 10-second triple shot timer activates. During this time, firing shoots three bullets at once in a spread pattern instead of one. Once the timer expires, the weapon returns to normal. Update GameModel, GameView, and ModelTester.
Result:
Triple shot is working. Blue aliens drop cyan orbs at 30% chance, catching the orb activates triple shot for 10 seconds, and the weapon resets automatically when the timer runs out.

Prompt: 10
Once all 55 standard aliens are cleared, a Giant Boss Alien spawns at the top center of the screen. The Boss is 80x40 pixels, orange colored, has 50 health points, and moves in a zig-zag pattern bouncing off the left and right walls while staying in the upper half of the screen. The Boss fires bullets more aggressively than standard aliens. Once the Boss is defeated, the player earns +500 bonus score and a green "VICTORY!" message displays along with the final score. The game loop stops updating on victory just like game over. Update GameModel, GameView, and ModelTester.
Result:
Boss wave and victory state are working. Clearing all aliens spawns the orange Boss, it zig-zags across the upper screen, and defeating it triggers the VICTORY screen with final score displayed underneath.

Errors during implementation:
The Boss took too long to kill with just one bullet at a time and felt unfair. Fixed by making every hit on the Boss have a 40% chance to drop a Blue Orb, using the exact same mechanic as Blue aliens. This rewards the player for landing shots and gives them a way to activate triple shot to burn the Boss down faster.
Self-Correction:
Added the Blue Orb drop directly inside the boss collision block in checkCollisions(). No new classes or systems needed since the BlueOrb and triple shot mechanic already existed from Prompt 9.

promot: 11 Movement & Firing Refinement
player movement and firing felt janky because both only triggered on keypress. Fixed by tracking leftHeld, rightHeld, and spaceHeld booleans in GameController. The game loop now checks these every tick for smooth continuous movement and firing while keys are held. Reduced movement speed from 15 to 5 per tick since movement now runs every frame. Only GameController.java was changed.
Result:
Player ship moves smoothly when holding arrow keys and fires continuously when holding spacebar.

Prompt: 12 — Visual Polish
Improve the overall look of the game. Add a starfield background with 100 randomly placed stars. Replace the player rectangle with a triangle shaped spaceship with wings, a cockpit, and an engine glow. Replace each alien tier with a unique hand drawn shape — Red aliens get horns and tentacles, Pink aliens get antennae and a smile, Blue aliens get tentacles and cyan eyes, Green aliens get legs and dot eyes. Replace the Boss rectangle with a large oval with spikes and an angry face. Only GameView.java was changed.
Result:
Game looks significantly more polished. Every entity on screen is now visually distinct and recognizable instead of being colored rectangles.
Self-Correction:
Switched from Graphics to Graphics2D with antialiasing enabled so curved shapes like ovals and alien eyes render smoothly instead of looking jagged.
