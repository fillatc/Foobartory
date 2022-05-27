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


# Gitpod

To avoid installing an OpenJDK to launch the app you can use [Gitpod](https://www.gitpod.io/)
Create a free account (you can have 50 hours/month for free)

You can launch the VScode workspace with this URL https://fgitpod.io/#https://github.com/fillatc/Foobartory

You have now a fully IDE in your browser, and you can now [run the game](#run-the-game)


# Install

Java 17 was used to develop this project.

You can find the latest version of an OpenJDK 17 [here](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
(download the one corresponding to your platform)


Try the following command:
```shell
java -version
```

You should see something like this:
```shell
Picked up JAVA_TOOL_OPTIONS:  -Xmx3435m
openjdk version "17.0.2" 2022-01-18 LTS
OpenJDK Runtime Environment Corretto-17.0.2.8.1 (build 17.0.2+8-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.2.8.1 (build 17.0.2+8-LTS, mixed mode, sharing)
```


# Run to game

In your terminal at the root path of the project enter the following command:
```shell
./mvnw clean install
```
The jar Foobartory-1.0-SNAPSHOT.jar is now created.

Launch the game with this command:
```shell
java -jar ./target/Foobartory-1.0-SNAPSHOT.jar
```


# Improvement

There are many ways to improve the code, adding tests is a good place to start, within bonus a
GitHub action to run the tests.

- Use Mapped Diagnostic Context (MDC) to improve the log by adding the robot serialNumber.
- The algorithm to choose an action is basic and not optimal. The goal is to reduce the maximum the
  action switching, which has an important cost.
- Using the args to customize the game:
  - number of robots at the beginning
  - number of robots to achieve
  - changing the time speed
  - changing the log level
  - ...
- Refactoring Robot class and divide the action by example:
  - Adding those classes Mine / Assemble / Shop
- There is also a bug, when a robot wants to do an activity, it's possible that the resources are not
  available anymore, another robot took it. It happens because, between the time a robot is looking 
at the resources and the time he took it, another robot may remove it.
  - A solution can be to add a lock when a robot wants to remove resources, but it will create idle time.
  - Another solution will be to look and remove the resource in the synchronized block of code.
    and return a null or an empty Optional if the resource is not available.
- Clean of the dependencies to solve the warning.
