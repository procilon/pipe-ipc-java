package de.procilon.pipeipc;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.nio.channels.ByteChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.procilon.pipeipc.messages.Dialog;
import de.procilon.pipeipc.messages.ErrorMessages;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;

/**
 * Main class for Channel (pipe/unix domain sockets) based IPC
 * 
 * @author fichtelmannm
 *
 */
public class PipeIPC {
    private static final String OS = System.getProperty("os.name", "");

    private static final boolean WIN = OS.startsWith("Windows");

    /**
     * Connect to a new channel.
     * 
     * Channel creation always happens outside of this java process.
     * 
     * @param name the name of the channel (path name)
     * @return the opened channel
     * @throws IOException if the channel could not be opened.
     */
    @SuppressWarnings("resource")
    public static ByteChannel openIpcChannel(String name) throws IOException {
	if (WIN) {
	    return new RandomAccessFile(name, "rw").getChannel();
	} else {
	    UnixSocketAddress address = new UnixSocketAddress(name);
	    return UnixSocketChannel.open(address);
	}
    }

    /**
     * the main method
     * 
     * @param args program arguments
     * @throws ReflectiveOperationException if registration of IpcCommandExecutors
     *                                      failed
     */
    public static void main(String[] args) throws ReflectiveOperationException {
	ExecutorService threadPool = Executors.newCachedThreadPool();

	for (String arg : args) {
	    Class<?> type = Class.forName(arg);
	    System.out.println(Dialog.CLASS_LOADED.render(arg));
	    if (IpcCommandExecutor.class.isAssignableFrom(type)) {
		@SuppressWarnings("unchecked")
		Constructor<? extends IpcCommandExecutor> defaultConstructor = (Constructor<? extends IpcCommandExecutor>) type
			.getConstructor();

		IpcCommandExecutor instance = defaultConstructor.newInstance();
		String commandType = instance.type().orElseThrow(
			() -> new IllegalArgumentException(ErrorMessages.MISSING_EXECUTOR_TYPE.render(type.getName())));

		ExecutorRegistry.INSTANCE.register(commandType, instance);
	    }
	}

	try (Scanner stdin = new Scanner(System.in)) {
	    do {
		System.out.println();
		System.out.print(Dialog.REQUEST_COMMAND_PIPE.render());
		String commandPipe = stdin.nextLine().trim();
		threadPool.submit(new CommandHandler(commandPipe));
		System.out.println(Dialog.COMMAND_PIPE_READY.render(commandPipe));
	    } while (stdin.hasNext());
	}
    }
}
