# Clustering-Problem
Creating new algorithms through genetic programing to solve the automatic grouping problem.

For this project a genetic programming library called ECJ was used, this allows to make the evolution of the programs, but it is necessary to program everything related to the specific problem, the "ec" folder, contains the library files.

Then I explain a little about the project:

To launch the experiments write a Scipt where the classpath is defined, other important libraries of the project, the location of java, the ec.Evolve (which is the point of entry to ECJ), and the parameters with which to work. In these parameters you must define the problem that will be evaluated in the evolution, in this case "MyProblem".

MyProblem has 3 main functions: Setup, Evaluate and Describe. In Setup the data is loaded, in Evaluate the instances are evaluated and the best individual is stored when it is found, finally in Describe the outputs are ordered.

The class Instances is the main class of the project, there the parameters of the instance are written, the functions and specific terminals designed for the problem, in addition to the Fitness function, among others.

It is also necessary to create the Point class, where the characteristics of the problem data are defined, as well as a specific evaluation function for each of the functions and terminals created.
