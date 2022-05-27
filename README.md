# Foobartory
An automatic production line of foobar.


# Statement
The goal is to code an automatic production line of foobar .
At the beginning, we have 2 robots, each one is able to perform these activities:
- Mining foo : occupy the robot for 1 second.
- Mining bar : keep the robot busy for a random time between 0.5 and 2 seconds.
- Assembling a foobar : keep the robot busy for 2 seconds.
    - The robot use a foo and a bar to assemble a foobar
    - The operation has a 60% chance of success.
    - In case of failure, the bar can be reused but the foo is lost.
- Sell foobar : take 10s to sell up to 5 foobar , we earn €1 per foobar sold.
- Buying a new robot: take 1s, the robot buy a new robot for €3 and 6 foo
- Moving to a new activity: occupy the robot for 5 seconds.

The game stops when you reach 30 robots

## Notes
1. A second for the robot does not have to be a real life second.
2. Each robot must operate independently, they shouldn’t remain idle
3. No need to do complex maths to solve the problem, we do not need the action
   choice to be optimal, just a working production line.
