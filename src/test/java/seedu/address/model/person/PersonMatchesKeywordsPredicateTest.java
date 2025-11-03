package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstNameKeywords = Collections.singletonList("Alice");
        List<String> secondNameKeywords = Arrays.asList("Alice", "Bob");
        List<String> firstTagKeywords = Collections.singletonList("friend");
        List<String> secondTagKeywords = Arrays.asList("friend", "colleague");

        String firstAddressKeyword = "chua chu kang";
        String secondAddressKeyword = "Woodlands";

        PersonMatchesKeywordsPredicate firstPredicate = new PersonMatchesKeywordsPredicate(firstNameKeywords,
                firstTagKeywords,
                "contacted", "12345678", "alice@example.com", firstAddressKeyword);
        PersonMatchesKeywordsPredicate secondPredicate = new PersonMatchesKeywordsPredicate(secondNameKeywords,
                secondTagKeywords,
                "uncontacted", "87654321", "bob@example.com", secondAddressKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        PersonMatchesKeywordsPredicate firstPredicateCopy = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                firstTagKeywords,
                "contacted", "12345678", "alice@example.com", firstAddressKeyword);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different name keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // different tag keywords -> returns false
        PersonMatchesKeywordsPredicate differentTagPredicate = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                secondTagKeywords,
                "contacted", "12345678", "alice@example.com", firstAddressKeyword);
        assertFalse(firstPredicate.equals(differentTagPredicate));

        // different status keyword -> returns false
        PersonMatchesKeywordsPredicate differentStatusPredicate = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                firstTagKeywords,
                "uncontacted", "12345678", "alice@example.com", firstAddressKeyword);
        assertFalse(firstPredicate.equals(differentStatusPredicate));

        // different phone keyword -> returns false
        PersonMatchesKeywordsPredicate differentPhonePredicate = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                firstTagKeywords,
                "contacted", "87654321", "alice@example.com", firstAddressKeyword);
        assertFalse(firstPredicate.equals(differentPhonePredicate));

        // different email keyword -> returns false
        PersonMatchesKeywordsPredicate differentEmailPredicate = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                firstTagKeywords,
                "contacted", "12345678", "bob@example.com", firstAddressKeyword);
        assertFalse(firstPredicate.equals(differentEmailPredicate));

        // different address keyword -> returns false
        PersonMatchesKeywordsPredicate differentAddressPredicate = new PersonMatchesKeywordsPredicate(
                firstNameKeywords,
                firstTagKeywords,
                "contacted", "12345678", "alice@example.com", secondAddressKeyword);
        assertFalse(firstPredicate.equals(differentAddressPredicate));
    }

    @Test
    public void equals_nullFields() {
        // null status keywords -> returns true when both null
        PersonMatchesKeywordsPredicate firstPredicate = new PersonMatchesKeywordsPredicate(List.of("Alice"),
                List.of(),
                null, null, null, null);
        PersonMatchesKeywordsPredicate secondPredicate = new PersonMatchesKeywordsPredicate(List.of("Alice"),
                List.of(),
                null, null, null, null);
        assertTrue(firstPredicate.equals(secondPredicate));

        // null vs non-null status keyword -> returns false
        PersonMatchesKeywordsPredicate thirdPredicate = new PersonMatchesKeywordsPredicate(List.of("Alice"),
                List.of(),
                "contacted", null, null, null);
        assertFalse(firstPredicate.equals(thirdPredicate));

        // null vs non-null phone keyword -> returns false
        PersonMatchesKeywordsPredicate fourthPredicate = new PersonMatchesKeywordsPredicate(List.of("Alice"),
                List.of(),
                null, "12345678", null, null);
        assertFalse(firstPredicate.equals(fourthPredicate));

        // null vs non-null email keyword -> returns false
        PersonMatchesKeywordsPredicate fifthPredicate = new PersonMatchesKeywordsPredicate(List.of("Alice"),
                List.of(),
                null, null, "alice@example.com", null);
        assertFalse(firstPredicate.equals(fifthPredicate));
    }

    @Test
    public void test_nameMatches_returnsTrue() {
        // One name keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(
                Collections.singletonList("Alice"), List.of(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple name keywords
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice", "Bob"), List.of(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching name keyword
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Bob", "Carol"), List.of(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case name keywords
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("aLIce", "bOB"), List.of(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotMatch_returnsFalse() {
        // Non-matching name keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Carol"),
                List.of(),
                null, null, null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_tagMatches_returnsTrue() {
        // One tag keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(),
                Collections.singletonList("friend"),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend", "colleague").build()));

        // Multiple tag keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of(), Arrays.asList("friend", "colleague"),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend", "colleague").build()));

        // Only one matching tag keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of(), Arrays.asList("friend", "enemy"),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend", "colleague").build()));

        // Mixed-case tag keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of(), Arrays.asList("FrIeNd"),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend").build()));
    }

    @Test
    public void test_tagDoesNotMatch_returnsFalse() {
        // Non-matching tag keyword
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(),
                Arrays.asList("enemy"),
                null, null, null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend", "colleague").build()));
    }

    @Test
    public void test_statusMatches_returnsTrue() {
        // Exact status match
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                "CONTACTED",
                null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("contacted").build()));

        // Case-insensitive status match
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), "contacted", null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("CONTACTED").build()));

        // Mixed-case status match
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), "CoNtAcTeD", null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("contacted").build()));
    }

    @Test
    public void test_statusDoesNotMatch_returnsFalse() {
        // Different status
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                "contacted",
                null, null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("uncontacted").build()));
    }

    @Test
    public void test_phoneMatches_returnsTrue() {
        // Exact phone match
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null,
                "94351253", null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("94351253").build()));

        // Case-insensitive phone match (though phones are typically numeric)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, "94351253", null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("94351253").build()));
    }

    @Test
    public void test_phoneDoesNotMatch_returnsFalse() {
        // Different phone
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null,
                "94351253", null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("12345678").build()));
    }

    @Test
    public void test_emailMatches_returnsTrue() {
        // Exact email match
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null, null,
                "alice@example.com", null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withEmail("alice@example.com").build()));

        // Case-insensitive email match
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, "ALICE@EXAMPLE.COM",
                null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withEmail("alice@example.com").build()));
    }

    @Test
    public void test_emailDoesNotMatch_returnsFalse() {
        // Different email
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null, null,
                "alice@example.com", null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withEmail("bob@example.com").build()));
    }

    @Test
    public void test_emptyKeywords_returnsTrue() {
        // Empty name keywords (should match any name)
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(Collections.emptyList(),
                List.of(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Empty tag keywords (should match any tags)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), Collections.emptyList(),
                null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend").build()));

        // All empty/null keywords (should match any person)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Empty string status keyword (should match any status)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), "", null, null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("contacted").build()));

        // Empty string phone keyword (should match any phone)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, "", null, null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withPhone("12345678").build()));

        // Empty string email keyword (should match any email)
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, "", null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice")
                .withEmail("alice@example.com").build()));
    }

    @Test
    public void testMultipleCriteriaAllMatch_returnsTrue() {
        // All criteria match
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"),
                Arrays.asList("friend"),
                "contacted", "94351253", "alice@example.com", null);
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob")
                .withTags("friend", "colleague")
                .withStatus("contacted")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .build()));
    }

    @Test
    public void testMultipleCriteriaOneDoesNotMatch_returnsFalse() {
        // Name matches but status doesn't
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"),
                List.of(),
                "contacted", null, null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withStatus("uncontacted").build()));

        // Name and status match but tag doesn't
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("enemy"),
                "contacted", null, null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend")
                .withStatus("contacted").build()));

        // Name, status, and tags match but phone doesn't
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("friend"),
                "contacted", "94351253", null, null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend")
                .withStatus("contacted")
                .withPhone("12345678").build()));

        // Name, status, tags, and phone match but email doesn't
        predicate = new PersonMatchesKeywordsPredicate(Arrays.asList("Alice"), Arrays.asList("friend"),
                "contacted", "94351253", "alice@example.com", null);
        assertFalse(predicate.test(new PersonBuilder().withName("Alice")
                .withTags("friend")
                .withStatus("contacted")
                .withPhone("94351253")
                .withEmail("bob@example.com").build()));
    }

    @Test
    public void getStatusKeyword_returnsCorrectValue() {
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                "contacted",
                null, null, null);
        assertEquals("contacted", predicate.getStatusKeyword());

        // Null status keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, null, null);
        assertNull(predicate.getStatusKeyword());
    }

    @Test
    public void getTagKeywords_returnsCorrectValue() {
        List<String> tagKeywords = Arrays.asList("friend", "colleague");
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), tagKeywords,
                null,
                null, null, null);
        assertEquals(tagKeywords, predicate.getTagKeywords());

        // Empty tag keywords
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, null, null);
        assertEquals(List.of(), predicate.getTagKeywords());
    }

    @Test
    public void getPhoneKeyword_returnsCorrectValue() {
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null,
                "94351253", null, null);
        assertEquals("94351253", predicate.getPhoneKeyword());

        // Null phone keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, null, null);
        assertNull(predicate.getPhoneKeyword());
    }

    @Test
    public void getEmailKeyword_returnsCorrectValue() {
        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(),
                null, null,
                "alice@example.com", null);
        assertEquals("alice@example.com", predicate.getEmailKeyword());

        // Null email keyword
        predicate = new PersonMatchesKeywordsPredicate(List.of(), List.of(), null, null, null, null);
        assertNull(predicate.getEmailKeyword());
    }

    @Test
    public void toStringMethod() {
        List<String> nameKeywords = List.of("Alice", "Bob");
        List<String> tagKeywords = List.of("friend", "colleague");
        String statusKeyword = "contacted";
        String phoneKeyword = "94351253";
        String emailKeyword = "alice@example.com";

        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(nameKeywords, tagKeywords,
                statusKeyword,
                phoneKeyword, emailKeyword, null);

        String expected = PersonMatchesKeywordsPredicate.class.getCanonicalName()
                + "{nameKeywords=" + nameKeywords
                + ", tagKeywords=" + tagKeywords
                + ", statusKeyword=" + statusKeyword
                + ", phoneKeyword=" + phoneKeyword
                + ", emailKeyword=" + emailKeyword
                + ", addressKeyword=null}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void toStringMethod_withNullValues() {
        List<String> nameKeywords = List.of();
        List<String> tagKeywords = List.of();

        PersonMatchesKeywordsPredicate predicate = new PersonMatchesKeywordsPredicate(nameKeywords, tagKeywords,
                null,
                null, null, null);

        String expected = PersonMatchesKeywordsPredicate.class.getCanonicalName()
                + "{nameKeywords=" + nameKeywords
                + ", tagKeywords=" + tagKeywords
                + ", statusKeyword=null"
                + ", phoneKeyword=null"
                + ", emailKeyword=null"
                + ", addressKeyword=null}";
        assertEquals(expected, predicate.toString());
    }
}
