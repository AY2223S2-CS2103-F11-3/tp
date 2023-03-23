package seedu.vms.model.appointment;

import static seedu.vms.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.util.Objects;

import seedu.vms.commons.core.index.Index;
import seedu.vms.commons.util.AppUtil;
import seedu.vms.model.GroupName;

/**
 * Represents an Appointment in the vaccine management system.
 */
public class Appointment implements Comparable<Appointment> {
    public static final String MESSAGE_DURATION_CONSTRAINTS =
            "Start time must be before the end time";
    private final Index patientId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final GroupName vaccine;
    private boolean isCompleted;

    /**
     * Every field must be present and not null.
     */
    public Appointment(Index patientId, LocalDateTime startTime, LocalDateTime endTime, GroupName vaccine) {
        requireAllNonNull(patientId, startTime, endTime, vaccine);
        AppUtil.checkArgument(isValidDuration(startTime, endTime), MESSAGE_DURATION_CONSTRAINTS);

        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vaccine = vaccine;
        isCompleted = false;
    }

    public LocalDateTime getAppointmentTime() {
        return startTime;
    }

    public LocalDateTime getAppointmentEndTime() {
        return endTime;
    }

    public GroupName getVaccination() {
        return vaccine;
    }

    public Index getPatient() {
        return patientId;
    }

    public boolean getStatus() {
        return isCompleted;
    }

    public void mark() {
        assert !isCompleted;

        isCompleted = true;
    }

    public void unmark() {
        assert isCompleted;

        isCompleted = false;
    }

    public static boolean isValidDuration(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime.isBefore(endTime);
    }

    @Override
    public int compareTo(Appointment other) {
        return startTime.compareTo(other.startTime);
    }

    /**
     * Returns true if both appointments have the same patient and data fields.
     * This defines a stronger notion of equality between two appointments.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Appointment)) {
            return false;
        }

        Appointment otherAppointment = (Appointment) other;
        return otherAppointment.getPatient() == (getPatient())
                && otherAppointment.getAppointmentTime().equals(getAppointmentTime())
                && otherAppointment.getAppointmentEndTime().equals(getAppointmentEndTime())
                && otherAppointment.getVaccination().equals(getVaccination());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(patientId, startTime, endTime, vaccine);
    }

    @Override
    public String toString() {
        return getPatient().getOneBased()
                + " has an appointment at "
                + startTime;
    }
}
