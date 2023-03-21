package seedu.vms.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;
import java.util.logging.Logger;

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
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.ReadOnlyPatientManager;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;

    private Consumer<List<CommandMessage>> onExecutionComplete = results -> {};

    private final LinkedBlockingDeque<String> commandQueue = new LinkedBlockingDeque<>();
    private volatile boolean isExecuting = false;

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
}
