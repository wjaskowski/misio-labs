import gtk
import cairo
from world import World

class GUI(gtk.Window):
    """Okienko wizualizacji zachowania agenta w srodowisku."""

    __BOX_SIZE = 50
    """Rozmiar pojedynczej kratki swiata."""

    __MARGIN = 4
    """Margines dla etykiet i przyciskow."""

    __ACTION_LABEL_TEXT = 'Last action : {}'
    """Tresc etykiety opisujacej ostatnia wybrana akcje."""

    __MOTION_LABEL_TEXT = 'Last motion (dy, dx): {}'
    """Tresc etykiety opisujacej ostatnio wykonane przemieszczenie."""

    __SENSOR_LABEL_TEXT = 'Sensor state: {}'
    """Tresc etykiety opisujacej stan sensora jam."""

    __NSTEPS_LABEL_TEXT = 'Steps counter: {}'
    """Tresc etykiety opisujacej liczbe wykonanych ruchow."""

    def __init__(self, agent_factory, environment):
        """Inicjalizuje obiekt okna wizualizera."""

        super(GUI, self).__init__()

        self.agent_factory = agent_factory
        self.env = environment
        self.env.reset(self.agent_factory)

        self.draw_width = self.env.width * GUI.__BOX_SIZE
        self.draw_height = self.env.height * GUI.__BOX_SIZE

        self.darea = gtk.DrawingArea()
        self.darea.set_size_request(self.draw_width, self.draw_height)
        self.darea.connect("expose-event", self.expose)

        self.action_label = gtk.Label(GUI.__ACTION_LABEL_TEXT.format(self.env.agent_last_action))
        self.motion_label = gtk.Label(GUI.__MOTION_LABEL_TEXT.format(self.env.agent_last_motion))
        self.sensor_label = gtk.Label(GUI.__SENSOR_LABEL_TEXT.format(self.env.agent_sensor))
        self.nsteps_label = gtk.Label(GUI.__NSTEPS_LABEL_TEXT.format(self.env.agent_steps_counter))

        self.step_button = gtk.Button("Step")
        self.step_button.connect("clicked", self.step, None)
        self.step_button.set_size_request(self.draw_width - (2 * GUI.__MARGIN), -1)

        self.reset_button = gtk.Button("Reset")
        self.reset_button.connect("clicked", self.reset, None)
        self.reset_button.set_size_request(self.draw_width - (2 * GUI.__MARGIN), -1)

        label_height = self.action_label.size_request()[1]
        button_height = self.step_button.size_request()[1]

        self.set_title("The Lost Wumpus")
        self.resize(self.draw_width,
                self.draw_height + (5 * GUI.__MARGIN) + (3 * label_height) + (2 * button_height) + 1)
        self.set_position(gtk.WIN_POS_CENTER)
        self.connect("destroy", gtk.main_quit)

        fix = gtk.Fixed()


        y = 1
        fix.put(self.darea, 1, y)
        y += self.draw_height + GUI.__MARGIN

        fix.put(self.action_label, GUI.__MARGIN, y)
        y += label_height + GUI.__MARGIN

        fix.put(self.motion_label, GUI.__MARGIN, y)
        y += label_height + GUI.__MARGIN

        fix.put(self.sensor_label, GUI.__MARGIN, y)
        y += label_height + GUI.__MARGIN

        fix.put(self.nsteps_label, GUI.__MARGIN, y)
        y += label_height + GUI.__MARGIN

        fix.put(self.step_button, GUI.__MARGIN, y)
        y += button_height + GUI.__MARGIN

        fix.put(self.reset_button, GUI.__MARGIN, y)
        y += button_height + GUI.__MARGIN

        self.add(fix)
        self.show_all()
        return

    def __refresh(self):
        """Wymusza aktualizacje zawartosci okna."""

        self.action_label.set_text(GUI.__ACTION_LABEL_TEXT.format(self.env.agent_last_action))
        self.motion_label.set_text(GUI.__MOTION_LABEL_TEXT.format(self.env.agent_last_motion))
        self.sensor_label.set_text(GUI.__SENSOR_LABEL_TEXT.format(self.env.agent_sensor))
        self.nsteps_label.set_text(GUI.__NSTEPS_LABEL_TEXT.format(self.env.agent_steps_counter))
        self.darea.queue_draw_area(0, 0, self.draw_width, self.draw_height)

    def step(self, widget, data=None):
        """Akcja wykonywana po wcisnieciu przycisku Step"""

        self.env.step()
        self.__refresh()
        self.step_button.set_sensitive(not self.env.is_completed())

    def reset(self, widget, data=None):
        """Akcja wykonywana po wcisnieciu przycisku Reset"""

        self.env.reset(self.agent_factory)
        self.__refresh()
        self.step_button.set_sensitive(True)

    def __gradient(self, val):
        """Dostarcza gradient kolorow dla wartosci histogramu."""

        r = 2 * (1 - val) if val > 0.5 else 1
        g = 2 * val if val < 0.5 else 1;
        return cairo.SolidPattern(r, g, 0)

    def expose(self, widget, event):
        """Rysuje mape srodowiska i wiedzy agenta."""

        cr = self.darea.window.cairo_create()

        histogram = self.env.agent.histogram()

        for y in range(self.env.height):
            for x in range(self.env.width):
                cr.set_line_width(0)
                cr.set_source(self.__gradient(histogram[y][x]))
                cr.rectangle(x * GUI.__BOX_SIZE, y * GUI.__BOX_SIZE, GUI.__BOX_SIZE - 1,
                        GUI.__BOX_SIZE - 1)
                cr.fill()

        map = self.env.map

        cr.set_line_width(2)
        cr.set_source_rgb(0, 0, 0)
        for y in range(self.env.height):
            for x in range(self.env.width):

                if map[y][x] == World.CAVE:
                    cr.rectangle(x * GUI.__BOX_SIZE + 1, y * GUI.__BOX_SIZE + 1,
                            GUI.__BOX_SIZE - 3, GUI.__BOX_SIZE - 3)

                if map[y][x] == World.EXIT:
                    cr.move_to(x * GUI.__BOX_SIZE + 2, y * GUI.__BOX_SIZE + 2)
                    cr.rel_line_to(GUI.__BOX_SIZE - 4,GUI.__BOX_SIZE - 4)

                    cr.move_to(x * GUI.__BOX_SIZE + 2, y * GUI.__BOX_SIZE + GUI.__BOX_SIZE - 2)
                    cr.rel_line_to(GUI.__BOX_SIZE - 4, - GUI.__BOX_SIZE + 4)

        cr.stroke()


        xc = self.env.agent_x * GUI.__BOX_SIZE + (GUI.__BOX_SIZE / 2)
        yc = self.env.agent_y * GUI.__BOX_SIZE + (GUI.__BOX_SIZE / 2)

        cr.arc(xc, yc, 20, 0, 2*3.1415)
        cr.fill()

def visualise(agent_factory, environment):
    """Tworzy i wyswietla okienko wizualizacji."""

    GUI(agent_factory, environment)
    gtk.main()
