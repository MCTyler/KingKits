package com.faris.kingkits.helpers;

public class ConfigCommand {
	private String command = "";
	private String description = "";

	/** Create a new instance of ConfigCommands
     * @param strCommand
     * @param strDescription **/
	public ConfigCommand(String strCommand, String strDescription) {
		this.command = strCommand;
		this.description = strDescription;
	}

	/** Get the config command
     * @return  **/
	public String getCommand() {
		return this.command;
	}

	/** Get the config command's config key
     * @return  **/
	public String getDescription() {
		return this.description;
	}

	/** Returns the command and config key as a String
     * @return  **/
        @Override
	public String toString() {
		return this.command + ":" + this.description;
	}

}
