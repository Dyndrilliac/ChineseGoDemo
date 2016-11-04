*******************************************************************

* Title:  Chinese Go Demo
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/12/2016

*******************************************************************

This code makes use of my [Custom Java API](https://github.com/Dyndrilliac/java-custom-api). In order to build this source, you should clone the repository for the API using your Git client, then import the project into your IDE of choice (I prefer Eclipse), and finally modify the build path to include the API project. For more detailed instructions, see the README for the API project.

This projecct forms the basis of a single-player game of Go against an AI opponent using the [American Go Association's Concise Rules](http://www.usgo.org/files/pdf/conciserules.pdf). No handicap functionality is available at this time, but it is on my todo list. Currently only the __Area scoring__ method is supported. I may look into implementing the __Territory scoring__ method in the future.

# Known Bugs

* The game's logic does not properly handle groups of adjacent stones of the same color. So for example, in order to capture a stone belonging to the AI, it must be completely surrounded by your stones. If one of its neighboring points is occupied by another stone belonging to the AI, then it is impossible to capture. This problem effects scoring and determining legal moves, too.
* The Ko rule, which makes moves that cause a repeat of previous board positions illegal, has not yet been implemented. I am still working on a viable legality test to prevent repeated board positions.
* If you capture a stone belonging to the AI, the point on the board where it was located turns gray indicating it is no longer occupied, but it becomes impossible to place a stone there. Additionally, even though you cannot make a move to that location, apparently it is still considered a legal move and so attempting to make a move there causes your turn to be lost and hands control to the AI as if you passed. Fortunately, as long a you know this is an issue, you can avoid it. There is no real need to place a stone on a point that you have captured. The AI cannot place a stone there since it is by definition surrounded by your own stones, and as long as it is completely surrounded by your stones, it will still count toward your score.