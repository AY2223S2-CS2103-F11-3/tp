package seedu.vms.logic.commands.appointment;

import static java.util.Objects.requireNonNull;
import static seedu.vms.logic.parser.CliSyntax.DELIMITER;
import static seedu.vms.logic.parser.CliSyntax.PREFIX_ENDTIME;
import static seedu.vms.logic.parser.CliSyntax.PREFIX_STARTTIME;
import static seedu.vms.logic.parser.CliSyntax.PREFIX_VACCINATION;
import static seedu.vms.model.Model.PREDICATE_SHOW_ALL_APPOINTMENTS;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import javafx.collections.ObservableMap;
import seedu.vms.commons.core.Messages;
import seedu.vms.commons.core.index.Index;
import seedu.vms.commons.util.CollectionUtil;
import seedu.vms.logic.CommandMessage;
import seedu.vms.logic.commands.Command;
import seedu.vms.logic.commands.exceptions.CommandException;
import seedu.vms.model.GroupName;
import seedu.vms.model.IdData;
import seedu.vms.model.Model;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.vaccination.Requirement;
import seedu.vms.model.vaccination.VaxType;

/**
 * Edits the details of an existing patient in the bloodType book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_GROUP = "appointment";

    public static final String MESSAGE_USAGE = COMMAND_GROUP + " " + COMMAND_WORD
            + ": Edits the details of the appointment identified "
            + "by the index number used in the displayed appointment list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + DELIMITER + PREFIX_STARTTIME + " START_TIME] "
            + "[" + DELIMITER + PREFIX_ENDTIME + " END_TIME] "
            + "[" + DELIMITER + PREFIX_VACCINATION + " VAX_GROUP]\n"
            + "Example: " + COMMAND_GROUP + " " + COMMAND_WORD + " 1 "
            + DELIMITER + PREFIX_STARTTIME + " 2024-01-01 1330 "
            + DELIMITER + PREFIX_ENDTIME + " 2024-01-01 1400";

    public static final String MESSAGE_EDIT_APPOINTMENT_SUCCESS = "Edited Appointment: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_EXISTING_PATIENT_ID = "This patient already has an existing appointment";
    public static final String MESSAGE_MISSING_VAX_TYPE = "The given vaccine is not in the vaccine manager";
    public static final String MESSAGE_PARSE_DURATION = "Please give both the starting and ending timings";
    public static final String MESSAGE_PAST_APPOINTMENT = "The appointment has already passed";
    public static final String MESSAGE_MISSING_VAX_REQ = "The Patient does not have previous appointments for the"
            + "needed vaccine";
    public static final String MESSAGE_EXIST_VAX_REQ = "The Patient already has an appointment for this vaccine dose";
    public static final String MESSAGE_EXISTING_APPOINTMENT = "This patient already has an upcoming appointment";

    private final Index index;
    private final EditAppointmentDescriptor editAppointmentDescriptor;

    /**
     * @param index                     of the appointment in the appointment
     *                                  list to edit
     * @param editAppointmentDescriptor details to edit the appointment with
     */
    public EditCommand(Index index, EditAppointmentDescriptor editAppointmentDescriptor) {
        requireNonNull(index);
        requireNonNull(editAppointmentDescriptor);

        this.index = index;
        this.editAppointmentDescriptor = new EditAppointmentDescriptor(editAppointmentDescriptor);
    }

    @Override
    public CommandMessage execute(Model model) throws CommandException {
        requireNonNull(model);
        Map<Integer, IdData<Appointment>> lastShownList = model.getAppointmentManager().getMapView();

        if (!lastShownList.containsKey(index.getZeroBased())) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPOINTMENT_DISPLAYED_INDEX);
        }

        Appointment appointmentToEdit = lastShownList.get(index.getZeroBased()).getValue();
        Appointment editedAppointment = createEditedAppointment(appointmentToEdit, editAppointmentDescriptor);

        // Checks if patient manager contains the given index
        Map<Integer, IdData<Patient>> patientList = model.getPatientManager().getMapView();
        if (!patientList.containsKey(editedAppointment.getPatient().getZeroBased())) {
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
        }

        // Checks if vaxType manager contains the vaccine to be used in the appointment
        ObservableMap<String, VaxType> vaccinationList = model.getVaxTypeManager().asUnmodifiableObservableMap();
        if (!vaccinationList.containsKey(editedAppointment.getVaccination().getName())) {
            throw new CommandException(MESSAGE_MISSING_VAX_TYPE);
        }

        // Checks if the appointment to be edited has not passed
        if (appointmentToEdit.getAppointmentEndTime().isAfter(LocalDateTime.now())) {
            throw new CommandException(MESSAGE_PAST_APPOINTMENT);
        }

        Map<Integer, IdData<Appointment>> appointmentList = model.getAppointmentManager().getMapView();
        HashSet<GroupName> history = new HashSet<>();
        for (Map.Entry<Integer, IdData<Appointment>> entry : appointmentList.entrySet()) {
            Appointment appointment = entry.getValue().getValue();
            if (appointment.getPatient().equals(editedAppointment.getPatient())) {

                // Checks for no existing next appointment
                if (appointment.getAppointmentEndTime().isAfter(LocalDateTime.now())) {
                    throw new CommandException(MESSAGE_EXISTING_APPOINTMENT);
                }

                // Adds vaccine to patient history
                if (entry.getKey() != index.getZeroBased()) {
                    history.addAll(vaccinationList.get(appointment.getVaccination().getName()).getGroups());
                }
            }
        }

        // Checks if the given patient has taken the vaccine or the necessary vaccine
        for (Requirement requirement: vaccinationList.get(editedAppointment.getVaccination().getName())
                .getHistoryReqs()) {
            if (!requirement.check(history)) {
                switch (requirement.getReqType()) {
                case ALL:
                    // Fallthrough
                case ANY:
                    throw new CommandException(MESSAGE_MISSING_VAX_REQ);
                case NONE:
                    throw new CommandException(MESSAGE_EXIST_VAX_REQ);
                default:
                    // Should not reach here
                }
            }
        }

        model.setAppointment(index.getZeroBased(), editedAppointment);

        model.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
        return new CommandMessage(String.format(MESSAGE_EDIT_APPOINTMENT_SUCCESS, editedAppointment));
    }

    /**
     * Creates and returns a {@code Appointment} with the details of
     * {@code appointmentToEdit}
     * edited with {@code editAppointmentDescriptor}.
     */
    private static Appointment createEditedAppointment(Appointment appointmentToEdit,
                                                       EditAppointmentDescriptor editAppointmentDescriptor) {
        assert appointmentToEdit != null;

        Index patientId = editAppointmentDescriptor.getPatient().orElse(appointmentToEdit.getPatient());
        LocalDateTime startTime = editAppointmentDescriptor.getStartTime()
                .orElse(appointmentToEdit.getAppointmentTime());
        LocalDateTime endTime = editAppointmentDescriptor.getEndTime()
                .orElse(appointmentToEdit.getAppointmentEndTime());
        GroupName vaccine = editAppointmentDescriptor.getVaccine().orElse(appointmentToEdit.getVaccination());

        return new Appointment(patientId, startTime, endTime, vaccine);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editAppointmentDescriptor.equals(e.editAppointmentDescriptor);
    }

    /**
     * Stores the details to edit the appointment with. Each non-empty field value will
     * replace the corresponding field value of the appointment.
     */
    public static class EditAppointmentDescriptor {
        private Index patientId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private GroupName vaccine;

        public EditAppointmentDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code allergies} is used internally.
         */
        public EditAppointmentDescriptor(EditAppointmentDescriptor toCopy) {
            setPatient(toCopy.patientId);
            setStartTime(toCopy.startTime);
            setEndTime(toCopy.endTime);
            setVaccine(toCopy.vaccine);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(patientId, startTime, endTime, vaccine);
        }

        public void setPatient(Index patientId) {
            this.patientId = patientId;
        }

        public Optional<Index> getPatient() {
            return Optional.ofNullable(patientId);
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public Optional<LocalDateTime> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }

        public Optional<LocalDateTime> getEndTime() {
            return Optional.ofNullable(endTime);
        }

        public void setVaccine(GroupName vaccine) {
            this.vaccine = vaccine;
        }

        public Optional<GroupName> getVaccine() {
            return Optional.ofNullable(vaccine);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditAppointmentDescriptor)) {
                return false;
            }

            // state check
            EditAppointmentDescriptor e = (EditAppointmentDescriptor) other;

            return getPatient().equals(e.getPatient())
                    && getStartTime().equals(e.getStartTime())
                    && getEndTime().equals(e.getEndTime())
                    && getVaccine().equals(e.getVaccine());
        }
    }
}
