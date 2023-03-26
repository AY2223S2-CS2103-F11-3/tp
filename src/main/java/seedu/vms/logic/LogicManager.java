package seedu.vms.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import seedu.vms.commons.core.GuiSettings;
import seedu.vms.commons.core.LogsCenter;
import seedu.vms.logic.commands.Command;
import seedu.vms.logic.commands.exceptions.CommandException;
import seedu.vms.logic.parser.ParseResult;
import seedu.vms.logic.parser.exceptions.ParseException;
import seedu.vms.model.IdData;
import seedu.vms.model.Model;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.appointment.AppointmentManager;
import seedu.vms.model.keyword.KeywordManager;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.PatientManager;
import seedu.vms.model.patient.ReadOnlyPatientManager;
import seedu.vms.model.util.SampleDataUtil;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeManager;
import seedu.vms.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";

    private static final String LOAD_SUCCESS_FORMAT = "Save data for %s found";
    private static final String LOAD_DEFAULT_FORMAT = "Default data for %s will be loaded";
    private static final String LOAD_EMPTY_FORMAT = "Empty data for %s will be loaded";
    private static final String LOAD_ERROR_FORMAT = "Unable to load %s: %s";
    private static final String LOAD_DEATH_FORMAT = "Died loading %s: %s";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;

    private Consumer<List<CommandMessage>> onExecutionComplete = results -> {};

    private final LinkedBlockingDeque<String> commandQueue = new LinkedBlockingDeque<>();
    private volatile boolean isExecuting = true;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
    }


    @Override
    public void queue(String commandText) {
        commandQueue.add(commandText);
        startNext();
    }

    private synchronized void startNext() {
        if (isExecuting || commandQueue.isEmpty()) {
            return;
        }
        isExecuting = true;
        String commandText = commandQueue.poll();
        new Thread(() -> attemptProcess(
                () -> parseCommand(commandText))).start();
    }


    private void attemptProcess(Runnable process) {
        try {
            process.run();
        } catch (Throwable deathEx) {
            completeExecution(List.of(new CommandMessage(
                    deathEx.toString(),
                    CommandMessage.State.DEATH)));
        }
    }


    private void parseCommand(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            execute(model.parseCommand(commandText));
        } catch (ParseException parseEx) {
            completeExecution(List.of(new CommandMessage(
                    parseEx.getMessage(),
                    CommandMessage.State.ERROR)));
        }
    }


    private void execute(ParseResult parseResult) {
        ArrayList<CommandMessage> results = new ArrayList<>();
        parseResult.getMessage().ifPresent(results::add);
        Command command = parseResult.getCommand();

        try {
            results.add(command.execute(model));
        } catch (CommandException ex) {
            results.add(new CommandMessage(ex.getMessage(), CommandMessage.State.ERROR));
            completeExecution(results);
            return;
        }

        try {
            storage.savePatientManager(model.getPatientManager());
        } catch (IOException ioe) {
            results.add(new CommandMessage(FILE_OPS_ERROR_MESSAGE + ioe, CommandMessage.State.WARNING));
        }

        try {
            storage.saveVaxTypes(model.getVaxTypeManager());
        } catch (IOException ioe) {
            results.add(new CommandMessage(FILE_OPS_ERROR_MESSAGE + ioe, CommandMessage.State.WARNING));
        }

        try {
            storage.saveAppointments(model.getAppointmentManager());
        } catch (IOException ioe) {
            results.add(new CommandMessage(FILE_OPS_ERROR_MESSAGE + ioe, CommandMessage.State.WARNING));
        }

        try {
            storage.saveKeywords(model.getKeywordManager());
        } catch (IOException ioe) {
            results.add(new CommandMessage(FILE_OPS_ERROR_MESSAGE + ioe, CommandMessage.State.WARNING));
        }

        completeExecution(results, command.getFollowUp());
    }


    private void execute(Command command) {
        execute(new ParseResult(command));
    }


    private void completeExecution(List<CommandMessage> results) {
        completeExecution(results, Optional.empty());
    }


    private synchronized void completeExecution(List<CommandMessage> results, Optional<Command> followUp) {
        onExecutionComplete.accept(results);
        if (followUp.isPresent()) {
            new Thread(() -> attemptProcess(
                    () -> execute(followUp.get()))).start();
            return;
        }
        isExecuting = false;
        startNext();
    }


    @Override
    public void loadManagers() {
        new Thread(this::performLoadSequence).start();
    }


    private void performLoadSequence() {
        ReadOnlyPatientManager patientManager = new PatientManager();
        try {
            patientManager = storage.readPatientManager();
            sendLoadInfo(String.format(LOAD_SUCCESS_FORMAT, "patients"));
        } catch (IOException ioEx) {
            sendLoadWarning(String.format(LOAD_ERROR_FORMAT,
                    "patients", ioEx.getMessage()));
            patientManager = SampleDataUtil.getSamplePatientManager();
            sendLoadInfo(String.format(LOAD_DEFAULT_FORMAT, "patients"));
        } catch (Throwable deathEx) {
            sendLoadDeath(String.format(LOAD_DEATH_FORMAT,
                    "patients", deathEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "patients"));
        }
        model.setPatientManager(patientManager);

        VaxTypeManager vaxTypeManager = new VaxTypeManager();
        try {
            vaxTypeManager = storage.loadUserVaxTypes();
            sendLoadInfo(String.format(LOAD_SUCCESS_FORMAT, "vaccinations"));
        } catch (IOException ioEx) {
            sendLoadWarning(String.format(LOAD_ERROR_FORMAT,
                    "vaccinations", ioEx.getMessage()));
            vaxTypeManager = storage.loadDefaultVaxTypes();
            sendLoadInfo(String.format(LOAD_DEFAULT_FORMAT, "vaccinations"));
        } catch (Throwable deathEx) {
            sendLoadDeath(String.format(LOAD_DEATH_FORMAT,
                    "vaccinations", deathEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "vaccinations"));
        }
        model.setVaxTypeManager(vaxTypeManager);


        AppointmentManager appointmentManager = new AppointmentManager();
        try {
            appointmentManager = storage.loadAppointments();
            sendLoadInfo(String.format(LOAD_SUCCESS_FORMAT, "appointments"));
        } catch (IOException ioEx) {
            sendLoadWarning(String.format(LOAD_ERROR_FORMAT,
                    "appointments", ioEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "appointments"));
        } catch (Throwable deathEx) {
            sendLoadDeath(String.format(LOAD_DEATH_FORMAT,
                    "appointments", deathEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "appointments"));
        }
        model.setAppointmentManager(appointmentManager);

        KeywordManager keywordManager = new KeywordManager();
        try {
            keywordManager = storage.loadKeywords();
            sendLoadInfo(String.format(LOAD_SUCCESS_FORMAT, "keywords"));
        } catch (IOException ioEx) {
            sendLoadWarning(String.format(LOAD_ERROR_FORMAT,
                    "keywords", ioEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "keywords"));
        } catch (Throwable deathEx) {
            sendLoadDeath(String.format(LOAD_DEATH_FORMAT,
                    "keywords", deathEx.getMessage()));
            sendLoadInfo(String.format(LOAD_EMPTY_FORMAT, "keywords"));
        }
        model.setKeywordManager(keywordManager);

        isExecuting = false;
    }


    private void sendLoadInfo(String message) {
        logger.info(message);
        onExecutionComplete.accept(List.of(new CommandMessage(message)));
    }


    private void sendLoadWarning(String message) {
        logger.warning(message);
        onExecutionComplete.accept(List.of(new CommandMessage(
                message, CommandMessage.State.WARNING)));
    }


    private void sendLoadDeath(String message) {
        logger.severe(message);
        onExecutionComplete.accept(List.of(new CommandMessage(
                message, CommandMessage.State.DEATH)));
    }


    @Override
    public void setOnExecutionCompletion(Consumer<List<CommandMessage>> onExecutionComplete) {
        this.onExecutionComplete = onExecutionComplete;
    }


    @Override
    public ReadOnlyPatientManager getPatientManager() {
        return model.getPatientManager();
    }

    @Override
    public ObservableMap<Integer, IdData<Patient>> getFilteredPatientMap() {
        return model.getFilteredPatientList();
    }

    @Override
    public ObservableMap<String, VaxType> getFilteredVaxTypeMap() {
        return model.getFilteredVaxTypeMap();
    }

    @Override
    public ObservableMap<Integer, IdData<Appointment>> getFilteredAppointmentMap() {
        return model.getFilteredAppointmentMap();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }


    @Override
    public ObjectProperty<IdData<Patient>> detailedPatientProperty() {
        return model.detailedPatientProperty();
    }


    @Override
    public ObjectProperty<VaxType> detailedVaxTypeProperty() {
        return model.detailedVaxTypeProperty();
    }


    @Override
    public void bindVaccinationDisplayList(ObservableList<VaxType> displayList) {
        model.bindVaccinationDisplayList(displayList);
    }
}
