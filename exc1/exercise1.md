# Exercise 1 | 10 pmd rulesets and why
Check the reports folder for the specific files.
Used on an older 3 sem project i made with a group, since i haven't done java in a long time.

Pretty difficult to pick 10 rulesets (that's a lot!) but these sounded the most interesting. Also since i haven't been working on the project in a long time, it's pretty hard to pick the most relevant ones.

## Codesize
Basically translates to readability, this is ALWAYS important in any coding project, and result doesn't show that many red flags (it is also a small project after all). 

It does however say `FlightsService.java:62:	The method 'run()' has an NPath complexity of 480` which is very bad indeed, and the documentation cites that it should generally be below 200, or measures should be taken to reduce the readability. Maybe a refactoring of that specific file is required

## Comments
Basically points a finger for not having enough comments around your project, maybe not the most important since you might not want comments everywhere unless you have a strict policy. We didn't during the project development, but it could be a help if you were to do javadoc for the entire project

## Coupling
Coupling shows you how tightly coupled your project really is (basically what dependencies exist), or how modular your software is. This is probably one of the most important points to have well in your program. We suffer from a few method chain calls, and some "objects not created locally". This process should probably be run while the software is being written, to avoid issues later, or it's gonna be hell to refactor.

## Design
Checks for general object oriented design. Since java is forced object oriented, this is generally a good idea since there's no dodging that design pattern. 

## Imports
Checks the top of files for unnecessary imports, or "bad" imports. Generally a good idea since it clears files up.

## Junit
Checks JUnit syntax. We have an integration test, so the "JUnit tests should include assert() or fail()" doesn't really apply to us.

## Optimizations
Shows general minor optimizations for speed and variables. We got a lot of variables which never change, so we could have declared them as final. I don't know how much i would agree on that being true, but it's still nice to get an overview of the different variables.

## Strings
Shows mistakes, or common practice mistakes on string handling (which is very common). Very nice for catching .equals() mistakes, or multiple declerations of the same string. This is honestly the easiest set of rules to use, and fix. My personal favorite.

## Unnecessary
Shows argueable styling critique. Only useless parantheses, so not that useful in this case.

## Unusedcode
Shows variables which are never used (or functions), doesn't take into account that you might declare something for future use. (Still useful for cleanups though)
