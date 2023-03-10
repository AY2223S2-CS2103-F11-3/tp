package seedu.vms.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.vms.commons.core.LogsCenter;
import seedu.vms.commons.exceptions.DataConversionException;
import seedu.vms.model.ReadOnlyUserPrefs;
import seedu.vms.model.UserPrefs;
import seedu.vms.model.patient.ReadOnlyPatientManager;
import seedu.vms.model.vaccination.VaxTypeManager;
import seedu.vms.storage.vaccination.VaxTypeStorage;

/**
 * Manages storage of PatientManager data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private PatientManagerStorage patientManagerStorage;
    private VaxTypeStorage vaxTypeStorage;
    private UserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given {@code PatientManagerStorage} and {@code UserPrefStorage}.
     */
    public StorageManager(
            PatientManagerStorage patientManagerStorage,
            VaxTypeStorage vaxTypeStorage,
            UserPrefsStorage userPrefsStorage) {
        this.patientManagerStorage = patientManagerStorage;
        this.vaxTypeStorage = vaxTypeStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    // ================ PatientManager methods ==============================

    @Override
    public Path getPatientManagerFilePath() {
        return patientManagerStorage.getPatientManagerFilePath();
    }

    @Override
    public Optional<ReadOnlyPatientManager> readPatientManager() throws DataConversionException, IOException {
        return readPatientManager(patientManagerStorage.getPatientManagerFilePath());
    }

    @Override
    public Optional<ReadOnlyPatientManager> readPatientManager(Path filePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return patientManagerStorage.readPatientManager(filePath);
    }

    @Override
    public void savePatientManager(ReadOnlyPatientManager patientManager) throws IOException {
        savePatientManager(patientManager, patientManagerStorage.getPatientManagerFilePath());
    }

    @Override
    public void savePatientManager(ReadOnlyPatientManager patientManager, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        patientManagerStorage.savePatientManager(patientManager, filePath);
    }

    // ================ Vax Type methods ==============================

    @Override
    public VaxTypeManager loadDefaultVaxTypes() throws RuntimeException {
        logger.fine("Attempting to load default vaccination types");
        return vaxTypeStorage.loadDefaultVaxTypes();
    }

    @Override
    public VaxTypeManager loadUserVaxTypes() throws IOException {
        return vaxTypeStorage.loadUserVaxTypes();
    }

    @Override
    public void saveVaxTypes(VaxTypeManager manager) throws IOException {
        vaxTypeStorage.saveVaxTypes(manager);
    }

}
