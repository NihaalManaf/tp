package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesKeywordsPredicate;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand = new FindCommand(
                new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_singleKeyword_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice")));
        assertParseSuccess(parser, "Alice", expectedFindCommand);
    }

    @Test
    public void parse_validNamePrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(Arrays.asList("Alice", "Bob"), List.of(), null, null, null, null));
        assertParseSuccess(parser, " n:Alice Bob", expectedFindCommand);
    }

    @Test
    public void parse_singleValidTagPrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), Arrays.asList("colleague"), null, null, null, null));
        assertParseSuccess(parser, " t:colleague", expectedFindCommand);
    }

    @Test
    public void parse_singleInvalidTagPrefix_throwsParseException() {
        // Symbols are not allowed in tags
        assertParseFailure(parser, " t:#friend",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Tag.MESSAGE_CONSTRAINTS));

        // No space allowed in tags
        assertParseFailure(parser, " t:friend colleague",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Tag.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_manyValidTagPrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), Arrays.asList("friend", "colleague"), null, null,
                        null, null));
        assertParseSuccess(parser, " t:friend t:colleague", expectedFindCommand);
    }

    public void parse_manyInvalidTagPrefix_throwsParseException() {
        assertParseFailure(parser, " t:friend t:student colleague", FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_validBothPrefixes_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("colleague"), null, null,
                        null, null));
        assertParseSuccess(parser, " n:Alice t:colleague", expectedFindCommand);
    }

    @Test
    public void parse_validStatusPrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), List.of(), "uncontacted", null, null, null));
        assertParseSuccess(parser, " s:uncontacted", expectedFindCommand);
    }

    @Test
    public void parse_validAllPrefixes_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("colleague"), "contacted",
                        null, null, null));
        assertParseSuccess(parser, " n:Alice t:colleague s:contacted", expectedFindCommand);
    }

    @Test
    public void parse_validPhonePrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, "94351253", null, null));
        assertParseSuccess(parser, " p:94351253", expectedFindCommand);
    }

    @Test
    public void parse_validEmailPrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, "alice@example.com", null));
        assertParseSuccess(parser, " e:alice@example.com", expectedFindCommand);
    }

    @Test
    public void parse_validPhoneAndEmailPrefix_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, "94351253", "alice@example.com", null));
        assertParseSuccess(parser, " p:94351253 e:alice@example.com", expectedFindCommand);
    }

    @Test
    public void parse_multipleStatusPrefix_throwsParseException() {
        assertParseFailure(parser, " s:contacted s:rejected",
                "Only one filter per field is allowed at a time!\n" + FindCommand.MESSAGE_USAGE);
    }
}
