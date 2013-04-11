import random
from world import World
from action import Action

class Env:
    """Srodowisko zagubionego Wumpusa.

    Srodowisko sklada sie z:
    - prawdopodobienstwa wykonania poprawnego ruchu (p),
    - prawdopodobienstwa wyczucia jamy gdy stoi sie w niej (pj),
    - prawdopodobienstwa wyczucia jamy gdy stoi sie poza nia (pn),
    - pozycji poczatkowej agentow umieszczanych w srodowisku (start_y, start_x: 0 <= start_y < height, 0 <= start_x < width),
    - wysokosci (height) i szerokosci (width) mapy,
    - mapy swiata (map).

    Mapa to krotka zawierajaca height lancuchow o dlugosci width znakow kazdy. Podczas odczytywania mapy najpierw podaje sie numer wiersza (wspolrzedna y) a potem numer kolumny (wspolrzedna x).

    Jesli w srodowisku umieszczony jest agent przechowywane sa takze nastepujace informacje:
    - obiekt z programem agenta (agent),
    - pozycja agenta (agent_y, agent_x: 0 <= agent_y < height, 0 <= agent_x < width),
    - liczba ruchow wykonanych przez agenta (agent_steps_counter),
    - ostatnia akcja wykonana przez agenta (agent_last_action), poczatkowo None,
    - ostatnie przemieszczenie wykonane przez agenta (agent_last_motion), poczatkowo None,
    - aktualny stan sensora agenta (agent_sensor), po osiagnieciu wyjscia None."""

    __MOTIONS = {
        Action.UP : (-1, 0),
        Action.DOWN : (1, 0),
        Action.LEFT : (0, -1),
        Action.RIGHT : (0, 1)
    }
    """Wartosci o jakie zmieniaja sie wspolrzedne agenta po wykonaniu deterministycznych akcji."""

    def __init__(self, path):
        """Tworzy srodowisko zagubionego Wumpusa na podstawie opisu z pliku. Plik powinien miec nastepujacy format:
        <p>
        <pj> <pn>
        <height> <width>
        height wierszy zawierajacych width znakow okreslonych przez pola enumeracji World
        <start_y + 1> <start_x + 1>
        Uwaga: Wspolrzedne startowe w pliku zakladaja numeracje od 1, a w srodowisku numeracja jest od 0, stad to '+ 1' przy wspolrzednych startowych.

        Argument path to sciezka do pliku zawierajacego opis srodowiska."""

        file = open(path, 'r')

        self.p = float(file.readline().strip())

        tokens = file.readline().strip().split()
        self.pj = float(tokens[0])
        self.pn = float(tokens[1])

        tokens = file.readline().strip().split()
        self.height = int(tokens[0])
        self.width = int(tokens[1])

        self.map = []
        for i in range(self.height):
            self.map.append(file.readline().strip()[:self.width])
        self.map = tuple(self.map)

        tokens = file.readline().strip().split()
        self.start_y = int(tokens[0]) - 1
        self.start_x = int(tokens[1]) - 1

        file.close()

        self.thresholds = {
            World.CAVE : self.pj,
            World.EMPTY : self.pn
        }

        self.agent = None
        self.agent_y = None
        self.agent_x = None
        self.agent_steps_counter = None
        self.agent_last_motion = None
        self.agent_last_action = None
        self.agent_sensor = None
        return

    def __str__(self):
        """Zwraca biezacy opis agenta w srodowisku."""

        return "cnt: {}; pos: ({}, {}); sen: {}; act: {}; mot: {}".format(
                self.agent_steps_counter, self.agent_y + 1, self.agent_x + 1, self.agent_sensor,
                self.agent_last_action, self.agent_last_motion)

    def __agent_field(self):
        """Zwraca znak opisujacy zwartosc pola na ktorym stoi agent."""

        return self.map[self.agent_y][self.agent_x]

    def __randomize_sensor_state(self):
        """Po wykonanym ruchu agenta aktualizuje stan jego sensora. Stan losowany jest na podstawie rozkladu prawdopodobienstwa zaleznego od zawartosci pola na ktorym stoi agent."""

        if self.is_completed():
            self.agent_sensor = None
        else:
            self.agent_sensor = random.uniform(0, 1) < self.thresholds[self.__agent_field()]

    def __randomize_agent_motion(self):
        """Realizuje ruch agenta na podstawie ostanio wybranej przez niego akcji. Ruch jest zaburzany lub nie na postawie odpowiedniego rozkladu prawdopodobienstwa."""

        motion = list(Env.__MOTIONS[self.agent_last_action])

        if random.uniform(0, 1) >= self.p:
            motion_modification = random.choice(Env.__MOTIONS.values())
            motion[0] += motion_modification[0]
            motion[1] += motion_modification[1]

        self.agent_y += motion[0]
        self.agent_x += motion[1]

        self.agent_y %= self.height
        self.agent_x %= self.width

        self.agent_last_motion = tuple(motion)
        return

    def reset(self, agent_factory):
        """Resetuje srodowisko i umieszcza w nim podanego w argumencie agenta."""

        self.agent = agent_factory(self.p, self.pj, self.pn, self.height, self.width, self.map)
        self.agent_y = self.start_y
        self.agent_x = self.start_x
        self.agent_steps_counter = 0
        self.agent_last_motion = None
        self.agent_last_action = None
        self.__randomize_sensor_state()
        return

    def step_sense(self):
        """Zmusza agenta znajdujacego sie w srodowisku do dokonania obserwacji."""

        self.agent.sense(self.agent_sensor)

    def step_move(self):
        """Zmusza agenta znajdujacego sie w srodowisku do wykonania nastepnego ruchu."""

        self.agent_steps_counter += 1
        self.agent_last_action = self.agent.move()
        self.__randomize_agent_motion()
        self.__randomize_sensor_state()
        return

    def is_completed(self):
        """Sprawdza czy agent dotarl do wyjscia."""

        return self.__agent_field() == World.EXIT

    def run(self, max_steps = None):
        """Zmusza agenta znajdujacego sie w srodowisku do wykonania kolejnych ruchow tak dlugo az znajdzie wyjscie lub wykona max_steps ruchuow (o ile zostanie okreslone)."""

        if max_steps is None:
            while not self.is_completed():
                self.step_sense()
                self.step_move()
        else:
            for i in range(max_steps):
                self.step_sense()
                self.step_move()
                if self.is_completed():
                    break
