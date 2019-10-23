# pipe-ipc-java
IPC entry point for local non-http inter process communication for Java applications.

# Quickstart

This library will help you to implement the receiving side of inter process communication outside of stdin, stdout and stderr. For example with Unix domain sockets.
The library contains a simple example implementation of the `IpcCommandExecutor` interface. The `EchoExecutor` writes the given input data to to the output stream referenced in the command JSON.

This quickstart guides you through starting the `EchoExecutor`, sending a command JSON, sending an input message and receiving output on the commandline.

# Unix
To see this working on a unix computer, take the following steps:

## Prerequisites
The following software is expected to be installed in order to follow this tutorial:

* git
* maven
* netcat


## 1. Project setup

Check out the code and build the classes (requires installed git and maven)

```
$ git clone https://github.com/procilon/pipe-ipc-java
$ cd pipe-ipc-java
$ mvn compile
```

## Creating the communication channels

This library implements the receiving end of the communication. This means that it expects the initiator (in this case we on the commandline) to create and manage the channels for the command JSONs, input and output. In this tutorial we use Unix domain sockets to send messages between different commandline windows.

Open 3 terminal windows: one for sending a command JSON, that tells our executor what we want to do, one for sending an input message and one for receiving the resulting output message.

In each window use netcat to create the respective socket in the tmp directory. This will start a process in each window.

```
nc -lkU /tmp/command.sock
nc -lkU /tmp/input.sock
nc -lkU /tmp/output.sock
```

3) run PipeIPC

3.1) Variant 1: use mavens exec plugin to build the classpath

```
mvn exec:java -Dexec.mainClass=de.procilon.pipeipc.PipeIPC -Dexec.args=de.procilon.pipeipc.sample.EchoExecutor
```

3.2) Variant 2: use mavens dependency plugin to copy all dependencies into a single folder and run the code using plain java

```
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" de.procilon.pipeipc.PipeIPC de.procilon.pipeipc.sample.EchoExecutor
```

4) when asked for the command pipe location, enter the full path `/tmp/command.sock` and press Enter. The output should look like this:

```
Class de.procilon.pipeipc.sample.EchoExecutor loaded
IpcCommandExecutor registered for type "echo": de.procilon.pipeipc.sample.EchoExecutor

Enter location to command pipe: /tmp/command.sock
you can now write the command json to /tmp/command.sock
```

5) now you can write the command to the command socket (the first of the 3 netcat windows). For the Echo Executor, it should look like this:

```
{"type":"echo","requestPipe":"/tmp/input.sock","responsePipe":"/tmp/output.sock","arguments":{}}
```

Enter it into the nc console and press enter. The following output should appear in the PipeIPC console:

```
command received: IpcCommand [type=echo, requestPipe=/tmp/input.sock, responsePipe=/tmp/output.sock, arguments={}]
[EchoExecutor] received echo command
```

6) Now the EchoExecutor is active and any input written into the console of `input.sock` should appear in the console output of `output.sock`
