package seedu.vms.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {
    public static final String DELIMITER = "--";

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n");
    public static final Prefix PREFIX_PHONE = new Prefix("p");
    public static final Prefix PREFIX_EMAIL = new Prefix("e");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a");
    public static final Prefix PREFIX_TAG = new Prefix("t");
    public static final Prefix PREFIX_DOB = new Prefix("d");
    public static final Prefix PREFIX_BLOODTYPE = new Prefix("b");

}
