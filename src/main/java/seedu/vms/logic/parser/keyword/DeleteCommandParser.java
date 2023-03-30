package seedu.vms.logic.parser.keyword;

import static seedu.vms.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.vms.logic.commands.keyword.DeleteCommand;
import seedu.vms.logic.parser.ArgumentMultimap;
import seedu.vms.logic.parser.CommandParser;
import seedu.vms.logic.parser.ParserUtil;
import seedu.vms.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and deletes a Keyword object.
 */
public class DeleteCommandParser implements CommandParser {
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public DeleteCommand parse(ArgumentMultimap argsMap) throws ParseException {
        try {
            String keyword = ParserUtil.parseKeyword(argsMap.getPreamble());
            return new DeleteCommand(keyword);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }
}
