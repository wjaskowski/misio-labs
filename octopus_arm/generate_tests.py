#!/usr/bin/env python
# -*- coding: utf-8 -*-
import re, os.path
import numpy as np
from math import sin, cos, pi


with open('base_settings.xml') as f:
    lines = f.readlines()

def gen_settings(angle):
    def rotate(x, y, angle):
        return x * cos(angle) - y * sin(angle), x * sin(angle) + y * cos(angle)

    new_lines = []
    for line in lines:
        if 'position' in line and 'mass' in line:
            m = re.match(".*position='(?P<x>[0-9.]+) +(?P<y>[0-9.]+)'.*", line)
            new_x, new_y = rotate(float(m.group('x')), float(m.group('y')), angle)
            line = re.sub("position='[0-9\. ]*'", "position='{:.2f} {:.2f}'".format(new_x, new_y), line)
        new_lines.append(line)
    return "".join(new_lines)

# Generate 1000 tests: each one different initial arm angle
for angle in np.linspace(-pi/4, pi/4, 1000):
    with open(os.path.join('tests', 'test_{:.3f}.xml'.format(angle)), 'w') as f:
        f.write(gen_settings(angle))
