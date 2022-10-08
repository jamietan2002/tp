package foodwhere.logic.parser;

import static foodwhere.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import foodwhere.commons.core.Messages;
import foodwhere.logic.commands.AddCommand;
import foodwhere.logic.commands.ClearCommand;
import foodwhere.logic.commands.EditCommand;
import foodwhere.logic.commands.ExitCommand;
import foodwhere.logic.commands.FindCommand;
import foodwhere.logic.commands.HelpCommand;
import foodwhere.logic.commands.ListCommand;
import foodwhere.logic.commands.SDeleteCommand;
import foodwhere.logic.parser.exceptions.ParseException;
import foodwhere.model.stall.NameContainsKeywordsPredicate;
import foodwhere.model.stall.Stall;
import foodwhere.testutil.EditStallDescriptorBuilder;
import foodwhere.testutil.StallBuilder;
import foodwhere.testutil.StallUtil;
import foodwhere.testutil.TypicalIndexes;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Stall stall = new StallBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(StallUtil.getAddCommand(stall));
        assertEquals(new AddCommand(stall), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        SDeleteCommand command = (SDeleteCommand) parser.parseCommand(
                SDeleteCommand.COMMAND_WORD + " " + TypicalIndexes.INDEX_FIRST_STALL.getOneBased());
        assertEquals(new SDeleteCommand(TypicalIndexes.INDEX_FIRST_STALL), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Stall stall = new StallBuilder().build();
        EditCommand.EditStallDescriptor descriptor = new EditStallDescriptorBuilder(stall).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + TypicalIndexes.INDEX_FIRST_STALL.getOneBased() + " "
                + StallUtil.getEditStallDescriptorDetails(descriptor));
        assertEquals(new EditCommand(TypicalIndexes.INDEX_FIRST_STALL, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), () ->
                        parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class,
                Messages.MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}