#!/usr/bin/env python
# -*- coding: utf-8 -*-
from numpy import random

# This is just a dummy solution that shows the required output format

M = 20
policy = random.randint(-5, 5, (M+1, M+1))

for i in range(M+1):
    for j in range(M+1):
        print("{:2d} ".format(policy[i][j]), end='')
    print()
