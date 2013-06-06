#!/usr/bin/env python
from pyevolve import Util
from pyevolve import GTree
from pyevolve import GSimpleGA
from pyevolve import Consts
import math

rmse_accum = Util.ErrorAccumulator()

# Define GP operators
def gp_add(a, b): return a+b
def gp_sub(a, b): return a-b
def gp_mul(a, b): return a*b
def gp_sqrt(a):   return math.sqrt(abs(a))
   
# Define evaluation function
def eval_func(chromosome):
   global rmse_accum
   rmse_accum.reset()

   # Compile chromosome to python code
   code_comp = chromosome.getCompiledCode()
   
   for a in xrange(0, 5):
      for b in xrange(0, 5):
          for c in xrange(0, 5):
             # Evaluate python code
             evaluated     = eval(code_comp)
            
             # Target function
             target        = math.sqrt(math.sqrt(a) + 3*b + c*c)
             rmse_accum   += (target, evaluated)

   fitness = rmse_accum.getRMSE()

   fitness += chromosome.getNodesCount() / 300.0;

   return fitness

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

def main_run():
   # Genome is Genetic Programming Tree. Use default initializator, mutator and crossover
   genome = GTree.GTreeGP()

   # Set tree's maximum depth and initialization method
   genome.setParams(max_depth=5, method="ramped")

   # Set objective function (how to evaluate an individual?)
   genome.evaluator += eval_func

   # Create a new algorithm with a given genome
   ga = GSimpleGA.GSimpleGA(genome)

   # How terminals are named? Where can I find non-terminal functions?
   ga.setParams(gp_terminals       = ['a', 'b', 'c'],
                gp_function_prefix = "gp")

   ga.stepCallback.set(step_callback)

   # We want to minimize the objective function
   ga.setMinimax(Consts.minimaxType["minimize"])
   ga.setGenerations(100)
   ga.setCrossoverRate(1.0)
   ga.setMutationRate(0.15)
   ga.setPopulationSize(4000)
   
   # How often show statistics?
   ga(freq_stats=1)


   # Print best individual
   best = ga.bestIndividual()

   best.writeDotImage("best.jpg")

   print best

if __name__ == "__main__":
   #main_run()
   x = []
   for a in xrange(0, 5):
      for b in xrange(0, 5):
          for c in xrange(0, 5):
             x.append([a,b,c,math.sqrt(math.sqrt(a) + 3*b + c*c)])
   import pprint
   pprint.pprint(x)
