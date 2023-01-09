# Object-Oriented Programming Assignment 2 Part 2
## About
The goal of this project is to extend the functionality of Java's Concurrency Framework.  
To do so, we created Task and CustomExecuter classes.     
## Task  
The Task class is a generic task with a Type that returns a result and may throw an exception.
Each task has a priority used for scheduling, inferred from the integer value of the tasks Type.  
Each Task has a TaskType data member to determinate its priority.  
TaskType is an Enum with three possible values:  
Computationl Task - the maximum priority  
IO-Bound Task - the normal priority  
Unknown Task - the minimum priority  
## CustomExecutor  
The CustomExecutor class is a custom thread pool class that defines a method for submitting a generic task to a priority queue, and a method for submitting a generic task created by a 
Callable and a Type, passed as arguments.  
To execute the Tasks by their type, we used PriorityBlockingQueue to keep the Tasks ordered by priority.  