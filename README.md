# pipe-ipc-java
IPC entry point for local non-http interprocess communication

# Quickstart

The library contains a simple IpcCommandExecutor implementation `EchoExecutor` that writes the given input data to its output stream.

To see this working on a unix computer, take the following steps:

0) required installed software:

* git
* maven
* netcat

1) check out the code and build the classes (requires installed git and maven)

```
$ git clone https://github.com/procilon/pipe-ipc-java
$ cd pipe-ipc-java
$ mvn compile
```

2) run netcat to create the command socket, the input socket and the output socket - since each command is non-stopping command, a new terminal should be used for each command.

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
