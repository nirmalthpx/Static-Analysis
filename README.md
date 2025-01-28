# Static-Analysis
Code Analysis Tool to find out all the possible function call sequences started from a user event

**Solution 1**
The process aims to find all possible function call sequences triggered by a user event. It involves parsing the Android manifest to identify activities, analyzing UI layout files for elements and event handlers, and examining Java source code to locate methods and calls. These results are then used to construct a call graph of potential sequences. This approach was implemented in a python script and tested on an Android application.

**Solution 2**
Improved the code by switching from the Javalang Python module to JavaParser, making it easier to detect loops and branches. Improvements include support for nested directories, better error handling, and the ability to manage multiple activities in one file and handle complex method calls.

**Solution 3**
After receiving feedback on function call sequences, I realized my code was not maintaining the correct call order, especially for chained methods. I revised it but encountered confusion around handling branches and loops. I realized that possible call sequences could be statically analyzed. To improve, I explored representing sequences as flow graphs using Graphviz and began using DOT language to diagrammatically express nodes and edges, focusing on correcting call ordering with insights from JavaParser's AST structure.

**Solution 4**
Implemented the solution using SootUp, finding it well-suited for handling nested loops and logical statements in the control-flow graph (CFG). The process involved converting source code into bytecode, transforming it into Intermediate Representation (IR) with SootUp, developing a call graph, and converting it into DOT notation for CFG creation with Graphviz. 

**Solution 5**
Focused on my research into the User Transaction Graph (UTG) and Function Graph, highlighting their importance in method flow analysis for optimization. I emphasized exploration and analysis covering topics such as dead code detection, recursion detection, error propagation paths, potential performance bottlenecks, code dependency, and security vulnerabilities.

