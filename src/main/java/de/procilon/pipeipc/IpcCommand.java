package de.procilon.pipeipc;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data for an IPC command.
 * 
 * @author fichtelmannm
 *
 */
public class IpcCommand {
    private String type;
    private String requestPipe;
    private String responsePipe;
    private JsonNode arguments;

    /**
     * Provides the type.
     *
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * Sets the type
     *
     * @param type the type to set
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * Provides the requestPipe.
     *
     * @return the requestPipe
     */
    public String getRequestPipe() {
	return requestPipe;
    }

    /**
     * Sets the requestPipe
     *
     * @param requestPipe the requestPipe to set
     */
    public void setRequestPipe(String requestPipe) {
	this.requestPipe = requestPipe;
    }

    /**
     * Provides the responsePipe.
     *
     * @return the responsePipe
     */
    public String getResponsePipe() {
	return responsePipe;
    }

    /**
     * Sets the responsePipe
     *
     * @param responsePipe the responsePipe to set
     */
    public void setResponsePipe(String responsePipe) {
	this.responsePipe = responsePipe;
    }

    /**
     * Provides the arguments.
     *
     * @return the arguments
     */
    public JsonNode getArguments() {
	return arguments;
    }

    /**
     * Sets the arguments
     *
     * @param arguments the arguments to set
     */
    public void setArguments(JsonNode arguments) {
	this.arguments = arguments;
    }

    @Override
    public String toString() {
	return "IpcCommand [type=" + type + ", requestPipe=" + requestPipe + ", responsePipe=" + responsePipe
		+ ", arguments=" + arguments + "]";
    }
}
