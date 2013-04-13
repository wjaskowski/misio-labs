# Przykladowy agent do zadania 'zagubiony Wumpus'. Agent porusza sie losowo.

import random
from action import Action

# nie zmieniac nazwy klasy
class Agent:

    # nie zmieniac naglowka konstruktora, tutaj agent dostaje wszystkie informacje o srodowisku
    def __init__(self, p, pj, pn, height, width, areaMap):

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

    def _update_hist(self):
        for row in self.hist:
            for i,_ in enumerate(row):
                row[i] += random.uniform(-0.1,0.1)
                row[i] = min(row[i], 1)
                row[i] = max(row[i], 0)

    # nie zmieniac naglowka metody, tutaj agent dokonuje obserwacji swiata
    # sensor przyjmuje wartosc True gdy agent ma uczucie stania w jamie
    def sense(self, sensor):
        self._update_hist()
        return dir

    # nie zmieniac naglowka metody, tutaj agent decyduje w ktora strone sie ruszyc,
    # funkcja MUSI zwrocic jedna z wartosci [Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT]
    def move(self):
        dir = random.choice([Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT])
        self._update_hist()
        return dir

    # nie zmieniac naglowka metody, tutaj agent udostepnia swoj histogram (ten z filtru
    # histogramowego), musi to byc tablica (lista list, krotka krotek...) o wymarach takich jak
    # plansza, pobranie wartosci agent.histogram()[y][x] zwraca prawdopodobienstwo stania na polu
    # w wierszu y i kolumnie x
    def histogram(self):
        return self.hist
