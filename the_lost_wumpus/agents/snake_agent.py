# Przykladowy agent do zadania 'zagubiony Wumpus'. Agent porusza sie wezykiem.

import random
from action import Action

# nie zmieniac nazwy klasy
class Agent:

    # nie zmieniac naglowka konstruktora, tutaj agent dostaje wszystkie informacje o srodowisku
    def __init__(self, p, pj, pn, height, width, areaMap):

        self.times_moved = 0
        self.direction = Action.LEFT

        # w ten sposob mozna zapamietac zmienne obiektu
        self.p = p
        self.pj = pj
        self.pn = pn
        self.height = height
        self.width = width
        self.map = areaMap

        # w tym przykladzie histogram wypelniany jest tak aby na planszy wyszedl gradient
        self.hist = []
        for y in range(self.height):
            self.hist.append([])
            for x in range(self.width):
                self.hist[y].append(float(y + x) / (self.width + self.height - 2))

        # dopisac reszte inicjalizacji agenta
        return

    # nie zmieniac naglowka metody, tutaj agent dokonuje obserwacji swiata
    # sensor przyjmuje wartosc True gdy agent ma uczucie stania w jamie
    def sense(self, sensor):
        pass

    # nie zmieniac naglowka metody, tutaj agent decyduje w ktora strone sie ruszyc,
    # funkcja MUSI zwrocic jedna z wartosci [Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT]
    def move(self):
        if self.times_moved < self.width - 1:
            self.times_moved += 1
            return self.direction
        else:
            self.times_moved = 0
            self.direction = Action.RIGHT if self.direction == Action.LEFT else Action.LEFT
            return Action.DOWN

    # nie zmieniac naglowka metody, tutaj agent udostepnia swoj histogram (ten z filtru
    # histogramowego), musi to byc tablica (lista list, krotka krotek...) o wymarach takich jak
    # plansza, pobranie wartosci agent.histogram()[y][x] zwraca prawdopodobienstwo stania na polu
    # w wierszu y i kolumnie x
    def histogram(self):
        return self.hist
