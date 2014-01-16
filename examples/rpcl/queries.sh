java -jar TweetyCLI.jar --input penguins.rpcl --parser rpclme --output probfile.tmp --writer rpclmeCondProb --options [rpcl.semantics=aggregating,rpcl.inference=lifted]
java -jar TweetyCLI.jar --input penguins.rpcl probfile.tmp --parser rpclme rpclmeCondProb --query "flies(a)" --options [rpcl.semantics=aggregating,rpcl.inference=lifted]

java -jar TweetyCLI.jar --input elephants.rpcl --parser rpclme --output probfile2.tmp --writer rpclmeProb --options [rpcl.semantics=aggregating,rpcl.inference=standard]
java -jar TweetyCLI.jar --input elephants.rpcl probfile2.tmp --parser rpclme rpclmeProb --query "likes(clyde,fred)" --options [rpcl.semantics=aggregating,rpcl.inference=standard]

java -jar TweetyCLI.jar --input cold.rpcl --parser rpclme --output probfile3.tmp --writer rpclmeProb --options [rpcl.semantics=aggregating,rpcl.inference=standard]
java -jar TweetyCLI.jar --input cold.rpcl probfile3.tmp --parser rpclme rpclmeProb --query "cold(anna)" --options [rpcl.semantics=aggregating,rpcl.inference=standard]
