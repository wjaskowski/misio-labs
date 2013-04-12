#!/usr/bin/env python

from environment import Env
import argparse
import os.path
import sys
import glob

# zinterpretuj i sprawdz argumentyfrom environment import Env
parser = argparse.ArgumentParser(description='The Lost Wumpus framework launcher.',
        fromfile_prefix_chars='@',
        epilog='Arguments can be loaded from file using syntax: {} @FILE'.format(
                os.path.basename(sys.argv[0])))

class ImportAgent(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        try:
            sys.path.append(os.path.dirname(values))
            exec('from ' + os.path.splitext(os.path.basename(values))[0] + ' import Agent')
            agent_factory = Agent

        except:
            msg = "can't load Agent class from '{}'".format(values)
            raise argparse.ArgumentTypeError(msg)

        else:
            namespace.agent_factory = agent_factory

def agent_module(string):
    if not os.path.isfile(string):
        msg = "can't open '{}'".format(string)
        raise argparse.ArgumentTypeError(msg)
    return string

class LoadAndAppendEnvs(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        environments = []

        for env_name_list in values:
            for env_name in env_name_list:
                try:
                    env = Env(env_name)

                except:
                    msg = "can't load environment from '{}'".format(env_name)
                    raise argparse.ArgumentTypeError(msg)

                else:
                    environments.append(env)

        namespace.environments = environments

def env_file_or_dir(string):
    if os.path.isfile(string):
        return [string]

    elif os.path.isdir(string):
        env_list = glob.glob(os.path.join(string, '*.in'))

        if len(env_list) == 0:
            msg = "directory '{}' contains no environment files".format(string)
            raise argparse.ArgumentTypeError(msg)

        return env_list

    else:
        msg = "can't open '{}': no such file or directory".format(string)
        raise argparse.ArgumentTypeError(msg)

parser.add_argument('-v', dest='visualise', action='store_const', const=True, default=False,
        help='visualise agent behavior (only first specified ENV will be used, -n flag ignored)')
parser.add_argument('-s', dest='size', metavar='S', type=int, default=50,
        help='size of each box representing map location (default: 50, ignored when -v flag is\
        absent)')
parser.add_argument('-n', dest='trials', metavar='N', type=int, default=1,
        help='number of times an agent will be placed in each environment (default: 1)')
parser.add_argument('agent_factory', metavar='AGENT', action=ImportAgent, type=agent_module,
        help='file containing Agent class')
parser.add_argument('environments', metavar='ENV', action=LoadAndAppendEnvs, type=env_file_or_dir,
        nargs='+', help='file \'*.in\' containing environment description or directory containing\
        at least one \'*.in\' file')

args = parser.parse_args()

if args.visualise:
    # w trybie wizualizacji wyswietl okno podgladu
    from visualiser import visualise
    visualise(args.agent_factory, args.environments[0], args.size)
else:
    # w zwyklym trybie uruchom agenta w kazdym srodowisku zadana liczbe razy i zlicz jego ruchy
    total_steps = 0
    for env in args.environments:
        for i in range(args.trials):
            env.reset(args.agent_factory)
            env.run(env.width * env.height * 2)
            total_steps += env.agent_steps_counter
    print total_steps
