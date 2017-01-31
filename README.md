## Mini Golf
##### The game has 3D graphics, realistic physics engine and an AI. You can built your own course and play in single, multi player mode and AI. Both the AI and physics engine are proprietary and were built from ground up 

##### Some videos to first see what does it look like: 
- Course creator (no lag ):  https://vid.me/tpDE
- Single player mode: https://vid.me/n0p5
- Multi player mode: https://vid.me/hlQO
- Against medium AI : https://vid.me/MTcy
- Against hard AI: https://vid.me/euYz


###
#### You can find the jar file in root directory as: MiniGolf.jar

- please note: 
I have experienced some issue when running the game as a jar file and course creation, 
specifically adding a golf ball and a hole is extremely laggy - the problem is not in source code, but somewhere in gradle build... 



To use the project (Intellij Idea):
- libgdx only uses gradle so even though I had some issues with deploying it, 
 you should be able to just import it as a gradle project



There are quite a few fixes left to be made, but as I have other projects I can't get the time:
- when ball is too fast is should not fall into the hole
- the issues with extremely laggy course creator (probably some links in gradle)
- the folder structure is really messy as gradle has issues with files packaging
