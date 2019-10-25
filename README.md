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

## 2. Creating the communication channels

This library implements the receiving end of the communication. This means that it expects the initiator (in this case we on the commandline) to create and manage the channels for the command JSONs, input and output. In this tutorial we use Unix domain sockets to send messages between different commandline windows.

Open 3 terminal windows: one for sending a command JSON, that tells our executor what we want to do, one for sending an input message and one for receiving the resulting output message.

In each window use netcat to create the respective socket in the tmp directory. This will start a process in each window.

```
nc -lkU /tmp/command.sock
nc -lkU /tmp/input.sock
nc -lkU /tmp/output.sock
```

## 3. Run PipeIPC via command line

Open an additional terminal window and navigate into the project directory. After this you can start the PipeIPC with the following commands:

### Variant 1: use mavens exec plugin to build the classpath

```
mvn exec:java -Dexec.mainClass=de.procilon.pipeipc.PipeIPC -Dexec.args=de.procilon.pipeipc.sample.EchoExecutor
```

### Variant 2: use mavens dependency plugin to copy all dependencies into a single folder and run the code using plain java

```
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" de.procilon.pipeipc.PipeIPC de.procilon.pipeipc.sample.EchoExecutor
```

At the start the application will expect the location of the command pipe. Enter the full path of our command socket `/tmp/command.sock` and press `Enter`. The application output should look like this:

```
Class de.procilon.pipeipc.sample.EchoExecutor loaded
IpcCommandExecutor registered for type "echo": de.procilon.pipeipc.sample.EchoExecutor

Enter location to command pipe: /tmp/command.sock
you can now write the command json to /tmp/command.sock
```

## 4. Giving Commands

We have now successfully connected one of our terminal windows to the java application: the window, where we created the command.sock via netcat. For the sake of this tutorial this will be our command window.

With the help of our command window we can now send instructions to our PipeIPC. Depending on what command we send, input will be processed differently. Commands are expected to have this structure:

`
{
  "type": <this decides which registered executor will process your input>,
  "requestPipe": <the full path to the pipe that is responsible for input>,
  "responsePipe": <the full path to the pipe that is responsible for the resulting output>,
  "arguments": <an object that contains additional arguments needed for the selected command>
 }
`

In this tutorial we want to use the `EchoExecutor`. This simple executor takes input data and pushes it unchanged to the output stream. The `EchoExecutor` is registered under the type `echo`. To connect the echo executor with our input and output windows we need the following command JSON:

```
{"type":"echo","requestPipe":"/tmp/input.sock","responsePipe":"/tmp/output.sock","arguments":{}}
```

Enter it into the command console and press enter. The following output should appear in the PipeIPC console:

```
command received: IpcCommand [type=echo, requestPipe=/tmp/input.sock, responsePipe=/tmp/output.sock, arguments={}]
[EchoExecutor] received echo command
```

## 5. Processing Data

Now the `EchoExecutor` will take input data from our input window and print the result to our output window. To try this out, write "Hello World!" into the input window and press enter. The same text should appear in the output window.

# Your own Executor

To create your very own executor, you create a new class that implements the `IpcCommandExecutor` interface. After doing this, you can start `PipeIPC` with your new executor. You can even register more than one at the same time:

`java -cp $CLASSPATH de.procilon.pipeipc.PipeIPC foo.FooExecutor bar.BarExecutor baz.BazExecutor`
