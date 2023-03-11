package seedu.vms.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.vms.commons.core.LogsCenter;
import seedu.vms.commons.exceptions.DataConversionException;
import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.commons.util.FileUtil;
import seedu.vms.commons.util.JsonUtil;
import seedu.vms.model.patient.ReadOnlyPatientManager;

/**
 * A class to access PatientManager data stored as a json file on the hard disk.
 */
public class JsonPatientManagerStorage implements PatientManagerStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonPatientManagerStorage.class);

    private Path filePath;

    public JsonPatientManagerStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getPatientManagerFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyPatientManager> readPatientManager() throws DataConversionException {
        return readPatientManager(filePath);
    }

    /**
     * Similar to {@link #readPatientManager()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyPatientManager> readPatientManager(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializablePatientManager> jsonPatientManager = JsonUtil.readJsonFile(
                filePath, JsonSerializablePatientManager.class);
        if (!jsonPatientManager.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonPatientManager.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void savePatientManager(ReadOnlyPatientManager patientManager) throws IOException {
        savePatientManager(patientManager, filePath);
    }

    /**
     * Similar to {@link #savePatientManager(ReadOnlyPatientManager)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void savePatientManager(ReadOnlyPatientManager patientManager, Path filePath) throws IOException {
        requireNonNull(patientManager);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializablePatientManager(patientManager), filePath);
    }

}
