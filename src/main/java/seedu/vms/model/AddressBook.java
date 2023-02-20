package seedu.vms.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableMap;
import seedu.vms.model.person.Person;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final IdDataMap<Person> personIdMap = new IdDataMap<>();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.personIdMap.setValues(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        this.personIdMap.setDatas(newData.getPersonMap().values());
    }

    //// person-level operations

    public boolean contains(int id) {
        return personIdMap.containts(id);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        personIdMap.add(p);
    }

    public void addPerson(IdData<Person> data) {
        personIdMap.add(data);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(int id, Person editedPerson) {
        requireNonNull(editedPerson);

        personIdMap.set(id, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(int id) {
        personIdMap.remove(id);
    }

    //// util methods

    @Override
    public String toString() {
        return personIdMap.asUnmodifiableObservableMap().toString();
        // TODO: refine later
    }

    @Override
    public ObservableMap<Integer, IdData<Person>> getPersonMap() {
        return personIdMap.asUnmodifiableObservableMap();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && personIdMap.asUnmodifiableObservableMap()
                        .equals(((AddressBook) other).personIdMap.asUnmodifiableObservableMap()));
    }

    @Override
    public int hashCode() {
        return personIdMap.hashCode();
    }
}
