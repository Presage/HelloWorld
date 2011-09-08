## Presage2 - HelloWorld

This is a demonstration of the basics of running agents in [Presage2]. Agents move around randomly in a basic world logging other agents they see, and performing a simple 'hello world' message exchange with other agents in the network.

### Running the simulation

```sh
# clone the source
git clone git://github.com/Presage/HelloWorld.git

# compile it
cd HelloWorld
mvn compile

# run with desired parameters
mvn exec:java -Dexec.mainClass=uk.ac.imperial.presage2.helloworld.HelloWorldSimulation -Dexec.args="uk.ac.imperial.presage2.helloworld.HelloWorldSimulation xSize=100 ySize=100 agentCount=10 finishTime=10"
```

This will run the simulation in a world of size 100x100 with 10 agents. The simulation will run for 10 time steps.


  [Presage2]: http://github.com/Presage/Presage2
