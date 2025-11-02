package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s name or tags match the given keywords.
 */
public class PersonMatchesKeywordsPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<String> tagKeywords;
    private final String statusKeyword;
    private final String phoneKeyword;
    private final String emailKeyword;
    private final String addressKeyword;

    /**
     * Constructs a predicate that matches a {@code Person}
     *
     * @param nameKeywords   keywords to check against the person's name
     * @param tagKeywords    keywords to check against the person's tags
     * @param statusKeyword  keywords to check against the person's status
     * @param phoneKeyword   keywords to check against the person's phone
     * @param emailKeyword   keywords to check against the person's email
     * @param addressKeyword keywords to check against the person's address
     */
    public PersonMatchesKeywordsPredicate(List<String> nameKeywords, List<String> tagKeywords, String statusKeyword,
            String phoneKeyword, String emailKeyword, String addressKeyword) {
        this.nameKeywords = nameKeywords;
        this.tagKeywords = tagKeywords;
        this.statusKeyword = statusKeyword;
        this.phoneKeyword = phoneKeyword;
        this.emailKeyword = emailKeyword;
        this.addressKeyword = addressKeyword;
    }

    @Override
    public boolean test(Person person) {
        boolean matchesName = nameKeywords.isEmpty() || nameKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));

        boolean matchesTag = tagKeywords.isEmpty() || tagKeywords.stream()
                .anyMatch(keyword -> person.getTags().stream()
                        .anyMatch(tag -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword)));

        boolean matchesStatus = statusKeyword == null || statusKeyword.isEmpty()
                || statusKeyword.equalsIgnoreCase(person.getStatus().name());

        boolean matchesPhone = phoneKeyword == null || phoneKeyword.isEmpty()
                || phoneKeyword.equalsIgnoreCase(person.getPhone().value);

        boolean matchesEmail = emailKeyword == null || emailKeyword.isEmpty()
                || emailKeyword.equalsIgnoreCase(person.getEmail().value);

        boolean matchesAddress = addressKeyword == null || addressKeyword.isEmpty()
                || person.getAddress().value.toLowerCase().contains(addressKeyword.toLowerCase());

        return matchesName && matchesTag && matchesStatus && matchesPhone && matchesEmail && matchesAddress;
    }

    /**
     * Returns the status keyword used for filtering.
     *
     * @return The status keyword, or null if no status filter is applied.
     */
    public String getStatusKeyword() {
        return statusKeyword;
    }

    /**
     * Returns the tag keywords used for filtering.
     *
     * @return The list of tag keywords.
     */
    public List<String> getTagKeywords() {
        return tagKeywords;
    }

    /**
     * Returns the phone keyword used for filtering.
     *
     * @return The phone keyword, or null if no phone filter is applied.
     */
    public String getPhoneKeyword() {
        return phoneKeyword;
    }

    /**
     * Returns the email keyword used for filtering.
     *
     * @return The email keyword, or null if no email filter is applied.
     */
    public String getEmailKeyword() {
        return emailKeyword;
    }

    /**
     * Returns the address keyword used for filtering.
     *
     * @return The address keyword, or null if no address filter is applied.
     */
    public String getAddressKeyword() {
        return addressKeyword;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PersonMatchesKeywordsPredicate)) {
            return false;
        }

        PersonMatchesKeywordsPredicate otherPredicate = (PersonMatchesKeywordsPredicate) other;
        return nameKeywords.equals(otherPredicate.nameKeywords)
                && tagKeywords.equals(otherPredicate.tagKeywords)
                && (statusKeyword == null ? otherPredicate.statusKeyword == null
                        : statusKeyword.equals(otherPredicate.statusKeyword))
                && (phoneKeyword == null ? otherPredicate.phoneKeyword == null
                        : phoneKeyword.equals(otherPredicate.phoneKeyword))
                && (emailKeyword == null ? otherPredicate.emailKeyword == null
                        : emailKeyword.equals(otherPredicate.emailKeyword))
                && (addressKeyword == null ? otherPredicate.addressKeyword == null
                        : addressKeyword.equals(otherPredicate.addressKeyword));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("tagKeywords", tagKeywords)
                .add("statusKeyword", statusKeyword)
                .add("phoneKeyword", phoneKeyword)
                .add("emailKeyword", emailKeyword)
                .add("addressKeyword", addressKeyword)
                .toString();
    }

}
