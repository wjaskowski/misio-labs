#!/usr/bin/env python

from os import path
import random

def generate_world(nrows, ncols, pit_prob=0.2, accurate_movement_prob=0.9,
        pit_detection_prob=0.7, regular_cell_wrong_prob=0.1):
    world = [ncols * ['.'] for i in range(nrows)]
    for i in range(nrows):
        for j in range(ncols):
            world[i][j] = 'J' if random.random() < pit_prob else '.'
    exit_pos = (random.randint(0,nrows-1), random.randint(0,ncols-1))
    world[exit_pos[0]][exit_pos[1]] = 'W'

    wumpus_pos = (random.randint(0,nrows-1), random.randint(0,ncols-1))

    return '{:.2f}\n{:.2f} {:.2f}\n{} {}\n{}\n{} {}\n'.format(
        accurate_movement_prob,
        pit_detection_prob, regular_cell_wrong_prob, 
        nrows, ncols,
        '\n'.join([''.join(row) for row in world]),
          wumpus_pos[0], wumpus_pos[1])

def produce_world(filename, **kwargs):
    world = generate_world(**kwargs)
    with open(filename, 'w') as f:
        f.write(world)

def main():
    for i in range(10):
        produce_world(path.join('test_worlds', 'map20x20_sparse_{:02d}.in'.format(i)), 
                nrows=20, ncols=20, 
                pit_prob=0.2, 
                accurate_movement_prob=0.9, 
                pit_detection_prob=0.7, 
                regular_cell_wrong_prob=0.1)

    for i in range(10):
        produce_world(path.join('test_worlds', 'map20x20_medium_{:02d}.in'.format(i)), 
                nrows=20, ncols=20, 
                pit_prob=0.5, 
                accurate_movement_prob=0.99, 
                pit_detection_prob=0.99, 
                regular_cell_wrong_prob=0.2)

    for i in range(40):
        produce_world(path.join('test_worlds', 'map10x10_easy_{:02d}.in'.format(i)), 
                nrows=10, ncols=10, 
                pit_prob=0.1, 
                accurate_movement_prob=0.99, 
                pit_detection_prob=0.99, 
                regular_cell_wrong_prob=0.01)

    for i in range(40):
        produce_world(path.join('test_worlds', 'map10x10_hard_{:02d}.in'.format(i)), 
                nrows=10, ncols=10, 
                pit_prob=0.2, 
                accurate_movement_prob=0.6, 
                pit_detection_prob=0.6, 
                regular_cell_wrong_prob=0.3)

if __name__ == '__main__':
    main()
