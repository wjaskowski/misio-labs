#!/usr/bin/env python
import subprocess
from subprocess import CalledProcessError, TimeoutExpired, DEVNULL
import sys, glob, time, re
from os import path
import pandas as pd
from pandas import DataFrame, Series
from difflib import Differ

def usage():
    return "usage:\n  test_all.py dir_with_tests program_or_dir_with_programs";


def parse_cmd():
    if len(sys.argv) < 3 or sys.argv[0] == '-h':
        print(usage())
        exit(0)

    test_dir = sys.argv[1]
    tests = glob.glob(path.join(test_dir, '*.in'))

    sol = sys.argv[2]
    programs = glob.glob(path.join(sol, '*.py')) if path.isdir(sol) else [sol]
    return programs, tests


def files_are_equal(out, correct_out):
    # Files are treated as lists of floats
    outarr = [float(x) for x in " ".join(open(out).readlines()).split()]
    corarr = [float(x) for x in " ".join(open(correct_out).readlines()).split()]

    return outarr == corarr;


def test_one(program, test):
    test_out = test.replace('.in', '.out')
    tmp = 'tmp.out'
    now = time.time()
    try:
        subprocess.check_output(['python2.7', program, test, tmp], stderr=DEVNULL, timeout=60)
    except CalledProcessError as e:
        timediff = time.time() - now
        return 'E', timediff, str(e)
    except TimeoutExpired as e:
        return 'T', float('inf'), str(e)

    timediff = time.time() - now
    return '+' if files_are_equal(tmp, test_out) else '-', timediff, ""

def get_index(program):
    return re.search('_(.+?)\.py', path.basename(program)).group(1), 

def main():
    programs, tests = parse_cmd()

    columns=["program", "test", "correct", "time"]
    df = DataFrame(columns=columns)

    # Run all programs on all tests
    for program in programs:
        print("{:30s}".format(path.basename(program)), end='', flush=True)
        for test in tests:
            res, time, err_info = test_one(program, test)
            print(res, end='', flush=True)

            # Save rsults to df
            df = df.append({
                "program": get_index(program),
                "test": path.basename(test),
                "result": res, 
                "time": time, 
                "err_info": err_info,
                "program_len": len(open(program).readlines())   # Not ellegant, but works
            }, ignore_index=True)
        print()

    df.to_csv('results.csv')

    def avg(x):
        return sum(x) / len(x)

    def app(group):
        return Series({
            'time_sum': group['time'].sum(), 
            'correct_perc': avg(group['result']=='+'),
            'program_len': group['program_len'].iget(0),
            'results': "".join(group['result']),
        })

    final = df.groupby("program").apply(app)

    final.to_csv('summary.csv')
    print(final)
    

if __name__ == '__main__':
    main()

