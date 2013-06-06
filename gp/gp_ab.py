#!/usr/bin/env python
from pyevolve import Util
from pyevolve import GTree
from pyevolve import GSimpleGA
from pyevolve import Consts
import math

rmse_accum = Util.ErrorAccumulator()

# Define GP operators
@GTree.gpdec(representation="+", color="green")
def gp_add(a, b): return a+b

#TODO: Define more GP operators...


# Define evaluation function
def eval_func(chromosome):
    #Here you have a table with values: a, b, f(a,b)
    data = [(0, 0, 0.0),
            (0, 1, 1.0),
            (0, 2, 2.0),
            (0, 3, 3.0),
            (0, 4, 4.0),
            (1, 0, 1.0),
            (1, 1, 1.4142135623730951),
            (1, 2, 2.23606797749979),
            (1, 3, 3.1622776601683795),
            (1, 4, 4.123105625617661),
            (2, 0, 2.0),
            (2, 1, 2.23606797749979),
            (2, 2, 2.8284271247461903),
            (2, 3, 3.605551275463989),
            (2, 4, 4.47213595499958),
            (3, 0, 3.0),
            (3, 1, 3.1622776601683795),
            (3, 2, 3.605551275463989),
            (3, 3, 4.242640687119285),
            (3, 4, 5.0),
            (4, 0, 4.0),
            (4, 1, 4.123105625617661),
            (4, 2, 4.47213595499958),
            (4, 3, 5.0),
            (4, 4, 5.656854249492381)]

    # Compile chromosome to python code
    code_comp = chromosome.getCompiledCode()
 
    #TODO: Compute fitness. Here is how to evaluate the chromosone for two values:
    a = 1
    b = 2
    evaluated = eval(code_comp)
    expected = data[a][b]
 
    fitness = abs(expected - evaluated)
    return fitness
 
def run():
    # Genome is Genetic Programming Tree. Use default initializator, mutator and crossover
    genome = GTree.GTreeGP()
 
    # Set tree's maximum depth and initialization method
    genome.setParams(max_depth=4, method="ramped")
 
    # Set objective function (how to evaluate an individual?)
    genome.evaluator += eval_func
 
    # Create a new algorithm with a given genome
    ga = GSimpleGA.GSimpleGA(genome)
 
    # How terminals are named? Where can I find non-terminal functions?
    ga.setParams(gp_terminals       = ['a', 'b'],
                 gp_function_prefix = "gp")

    ga.stepCallback.set(step_callback)
 
    # We want to minimize the objective function
    ga.setMinimax(Consts.minimaxType["minimize"])
    #TODO: Set the number of generations
    ga.setGenerations(1)             
    ga.setCrossoverRate(1.0)
    ga.setMutationRate(0.25)
 
    #TODO: Set the population size
    ga.setPopulationSize(10)
    
    # How often show statistics?
    ga(freq_stats=1)
 
    # Print best individual
    best = ga.bestIndividual()
 
    best.writeDotImage("best.jpg")
 
    print best
 

def step_callback(engine):
    gen = engine.getCurrentGeneration()
 
    # Every 10th generation save...
    if gen % 10 == 0:
        #... part of the population
        filename = "pop_{}.jpg".format(gen)
        GTree.GTreeGP.writePopulationDot(engine, filename, "jpg", 0, 10)
        #... the best individual
        best = engine.bestIndividual()
        best.writeDotImage("best_{}.jpg".format(gen))
    return False


if __name__ == "__main__":
    run()

