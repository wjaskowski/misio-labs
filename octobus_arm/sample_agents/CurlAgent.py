import random 
from array import array

class Agent:
    "A template agent acting randomly"
    
    #name should contain only letters, digits, and underscores (not enforced by environment)
    __name = 'Python_Random'
    
    def __init__(self, stateDim, actionDim, agentParams):
        "Initialize agent assuming floating point state and action"
        self.__stateDim = stateDim
        self.__actionDim = actionDim
        self.__action = array('d',[0 for x in range(actionDim)])
        #we ignore agentParams because our agent does not need it.
        #agentParams could be a parameter file needed by the agent.
        random.seed()
       
    def __randomAction(self):
        for i in range(self.__actionDim):
            self.__action[i] = random.random() 
    
    def __curlAction(self):
        for i in range(self.__actionDim):
            if (i%3 == 2):
                self.__action[i] = 1
            else:
                self.__action[i] = 0
            
    def start(self, state):
        "Given starting state, agent returns first action"
        self.__randomAction()
        return self.__action
    
    def step(self, reward, state):
        "Given current reward and state, agent returns next action"
#        self.__randomAction()
        self.__curlAction()
        return self.__action
    
    def end(self, reward):
        pass
    
    def cleanup(self):
        pass
    
    def getName(self):
        return self.__name
    
            
