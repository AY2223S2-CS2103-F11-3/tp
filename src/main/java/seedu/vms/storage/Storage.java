package seedu.vms.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.vms.commons.exceptions.DataConversionException;
import seedu.vms.model.ReadOnlyUserPrefs;
import seedu.vms.model.UserPrefs;
import seedu.vms.model.patient.ReadOnlyAddressBook;
import seedu.vms.storage.vaccination.VaxTypeStorage;

/**
 * API of the Storage component
 */
public interface Storage extends AddressBookStorage, UserPrefsStorage, VaxTypeStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

}
