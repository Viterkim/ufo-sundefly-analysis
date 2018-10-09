# Exercise 2 | Library call execution time
- Project: https://github.com/Viterkim/Single-User-Dungeon
- Picture: https://i.imgur.com/cNrn18p.png

(Made by Chris & Viktor)

Used the profiling tool from Netbeans on an older 1 sem java game we developed. (Since i needed a program to run locally).

The total time at 406,697 is me playing the game(AWT-Windows), and the actualy process that took the longest of our code, is the renderQueue.flushBuffer, which isn't our code, but a graphical library subcommand that clears the render buffer. The actual time (90ms) is not a lot, and since it's a slow java desktop application by nature it can't really be improved.

This concludes that on a simple application like this, it isn't very useful, but if you were to use it on an asyncrhonous program, like something that waits for another process, it might be more interesting. That's a good note to keep for next time.
