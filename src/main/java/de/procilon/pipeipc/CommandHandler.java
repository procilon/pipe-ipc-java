package de.procilon.pipeipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.procilon.pipeipc.messages.Dialog;
import de.procilon.pipeipc.messages.ErrorMessages;

/**
 * handle the command procedure:
 * 
 * listen for a command on the given channel, parse the command, transform the
 * request and response channels into streams and perform the operation.
 * 
 * @author fichtelmannm
 *
 */
public class CommandHandler implements Runnable {
    private static final ObjectMapper json = new ObjectMapper().configure(Feature.AUTO_CLOSE_SOURCE, false);

    private final String channelName;

    /**
     * DI constructor
     * 
     * @param channelName the name of the command channel
     */
    public CommandHandler(String channelName) {
	this.channelName = channelName;
    }

    @Override
    public void run() {
	try (ByteChannel commandChannel = PipeIPC.openIpcChannel(channelName)) {
	    InputStream commandStream = Channels.newInputStream(commandChannel);
	    try {
		IpcCommand command = json.readValue(commandStream, IpcCommand.class);
		System.out.println(Dialog.RECEIVED_COMMAND.render(command));

		ByteChannel requestChannel = PipeIPC.openIpcChannel(command.getRequestPipe());
		ByteChannel responseChannel = PipeIPC.openIpcChannel(command.getResponsePipe());

		IpcCommandExecutor executor = ExecutorRegistry.INSTANCE.find(command.getType()).orElseThrow(
			() -> new IllegalStateException(ErrorMessages.MISSING_EXECUTOR.render(command.getType())));

		executor.execute(command.getArguments(), () -> Channels.newInputStream(requestChannel),
			() -> Channels.newOutputStream(responseChannel));
	    } catch (Exception e) {
		try (Writer errorWriter = Channels.newWriter(commandChannel, StandardCharsets.UTF_8)) {
		    errorWriter.write("failed to process request: " + e.getMessage() + "\n");
		    e.printStackTrace();
		}
	    }
	} catch (IOException e) {
	    System.err.println("failed to read from command pipe");
	    e.printStackTrace();
	}
	System.out.println(Dialog.FINISHED_COMMAND.render(channelName));
    }
}
