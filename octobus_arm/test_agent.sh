#!/bin/bash

PORT=12345
PYTHON=python2.7 # Modify to you needs
SLEEP_TIME=0.5   # You might have to increase it in case of "unable to connect to port" problems

if [ "$#" -ne 1 ]; then
    echo "usage:\n test_agent [AgentName.py]"
    exit 1
fi

AGENTFILE=$1
AGENT=`basename ${AGENTFILE%.*}`

# Create directories and remove old logs
RESDIR=results/${AGENT}
mkdir -p $RESDIR 2>/dev/null
rm *.log $RESDIR/*.log 2>/dev/null

# Kill all servers that might have not been closed properly earlier
kill `ps|grep environment/octopus-environment.jar|cut -d' ' -f1|xargs` 2>/dev/null
sleep 1

# Mean of each line (sum for each value in line)
meansum()
{
    python -c "import sys; from numpy import mean; print(mean([sum(float(r) for r in line.split()) for line in sys.stdin]))"
}

for instance in tests/*.xml; do
    test=`basename ${instance%.*}`
    printf "%-12s" "$test "

    # Run the server
    java -Djava.endorsed.dirs=environment/lib -jar environment/octopus-environment.jar external $instance $PORT &
    server_pid=$! 
    sleep $SLEEP_TIME # I need some time here to let the port be created

    # run the agent
    pushd agent/python >/dev/null
    $PYTHON agent_handler.py localhost $PORT 1 >/dev/null 2>&1
    popd >/dev/null

    # Kill the server
    kill $server_pid
    sleep $SLEEP_TIME # Waste of time, but I need to wait for OS to reclaim the port

    # Move the results to results/agent/*.log and print the sum of rewards
    logfile=$RESDIR/${test}.log
    mv *.log $logfile
    printf "%6.2f\n" `cat $logfile | meansum`
done

# Average reward over all tests
printf "\nAverage reward "
cat $RESDIR/*.log | meansum
