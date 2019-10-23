package de.procilon.pipeipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Executor to perform a task defined by an {@link IpcCommand}.
 * 
 * @author fichtelmannm
 *
 */
public interface IpcCommandExecutor {

    /**
     * Execute the task configured by the provided arguments json.
     * 
     * @param arguments the argument json
     * @param in        the binary stream of input data (which may be optional)
     * @param out       the binary stream of output data
     * @throws IOException in case of IO errors.
     */
    void execute(JsonNode arguments, Supplier<InputStream> in, Supplier<OutputStream> out) throws IOException;

    /**
     * Optionally provide a default type for which commands can be handled
     * 
     * @return an Optional yielding the default type for which commands can be
     *         handled or an empty {@link Optional} if no default type is specified.
     */
    default Optional<String> type() {
	return Optional.empty();
    }
}
