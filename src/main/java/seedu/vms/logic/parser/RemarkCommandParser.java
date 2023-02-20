package seedu.vms.logic.parser;

import seedu.vms.commons.core.Messages;
import seedu.vms.commons.core.index.Index;
import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.commons.util.CollectionUtil;
import seedu.vms.logic.commands.RemarkCommand;
import seedu.vms.logic.parser.exceptions.ParseException;
import seedu.vms.model.person.Remark;


/**
 * Parses input arguments and creates a new {@code RemarkCommand} object
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code RemarkCommand}
     * and returns a {@code RemarkCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public RemarkCommand parse(String args) throws ParseException {
        CollectionUtil.requireAllNonNull(args);
        ArgumentMultimap argMultiMap = ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_REMARK);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultiMap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    RemarkCommand.MESSAGE_USAGE), ive);
        }

        String remarkString = argMultiMap.getValue(CliSyntax.PREFIX_REMARK).orElse("");
        Remark remark = new Remark(remarkString);

        return new RemarkCommand(index, remark);
    }

}
