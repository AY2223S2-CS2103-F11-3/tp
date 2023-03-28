package seedu.vms.model.appointment;

import seedu.vms.commons.core.ValueChange;
import seedu.vms.commons.core.index.Index;
import seedu.vms.model.GroupName;
import seedu.vms.model.IdData;
import seedu.vms.model.StorageModel;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.vaccination.VaxType;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps all data at the patient-manager level
 * Duplicates are not allowed (by .isSameAppointment comparison)
 */
public class AppointmentManager extends StorageModel<Appointment> implements ReadOnlyAppointmentManager {

    /**
     * Constructs an empty {@code AppointmentManager}.
     */
    public AppointmentManager() {

    }

    /**
     * Creates an AppointmentManager using the appointments in the {@code toBeCopied}
     */
    public AppointmentManager(ReadOnlyAppointmentManager toBeCopied) {
        super(toBeCopied);
    }

    /**
     * Marks the appointment at the given id as completed.
     * The appointment must exist in the appointment manager.
     */
    public void mark(int id) {
        Appointment appointment = getMapView().get(id).getValue().mark();
        set(id, appointment);
    }

    /**
     * Unmarks the appointment at the given id as not completed.
     * The appointment must exist in the appointment manager.
     */
    public void unmark(int id) {
        Appointment appointment = getMapView().get(id).getValue().unmark();
        set(id, appointment);
    }


    /**
     * Validates patient changes in AppointmentManager
     * Does not delete.
     */
    public List<IdData<Appointment>> validatePatientChange(ValueChange<IdData<Patient>> change) {
        List<IdData<Appointment>> invalidAppointments = new ArrayList<>();
        if (!change.getOldValue().equals(change.getNewValue())
                && change.getOldValue().isPresent()
                && change.getNewValue().isEmpty()) {
            Index patientToDelete = Index.fromZeroBased(change.getOldValue().get().getId());
            getMapView().entrySet().stream()
                    .filter(x->x.getValue().getValue().getPatient().equals(patientToDelete))
                    .forEach(x->invalidAppointments.add(x.getValue()));
        }
        return invalidAppointments;
    }

    /**
     * Handles patient changes in AppointmentManager
     */
    public void handlePatientChange(ValueChange<IdData<Patient>> change) {
        if (!change.getOldValue().equals(change.getNewValue())
                && change.getOldValue().isPresent()
                && change.getNewValue().isEmpty()) {
            Index patientToDelete = Index.fromZeroBased(change.getOldValue().get().getId());
            getMapView().entrySet().stream()
                    .filter(x->x.getValue().getValue().getPatient().equals(patientToDelete))
                    .forEach(x->remove(x.getKey()));
        }
    }

    /**
     * Validates vaccination changes in AppointmentManager
     * Does not delete.
     */
    public List<IdData<Appointment>> validateVaccinationChange(ValueChange<VaxType> change) {
        List<IdData<Appointment>> invalidAppointments = new ArrayList<>();
        if (!change.getOldValue().equals(change.getNewValue())
                && change.getOldValue().isPresent()) {
            GroupName vaxToChange = change.getOldValue().get().getGroupName();
            getMapView().entrySet().stream()
                    .filter(x->x.getValue().getValue().getVaccination().equals(vaxToChange))
                    .forEach(x->invalidAppointments.add(x.getValue()));
        }
        return invalidAppointments;
    }

    /**
     * Handles vaccination changes in AppointmentManager
     */
    public void handleVaccinationChange(ValueChange<VaxType> change) {
        if (!change.getOldValue().equals(change.getNewValue())
                && change.getOldValue().isPresent()) {
            if (change.getNewValue().isPresent()) {
                // update
                GroupName vaxToEdit = change.getOldValue().get().getGroupName();
                GroupName editedVax = change.getNewValue().get().getGroupName();
                getMapView().entrySet().stream()
                        .filter(x->x.getValue().getValue().getVaccination().equals(vaxToEdit))
                        .forEach(x->set(x.getKey(), x.getValue().getValue().setVaccination(editedVax)));
            } else {
                // delete
                GroupName vaxToDelete = change.getOldValue().get().getGroupName();
                getMapView().entrySet().stream()
                        .filter(x->x.getValue().getValue().getVaccination().equals(vaxToDelete))
                        .forEach(x->remove(x.getKey()));
            }
        }
    }
}
