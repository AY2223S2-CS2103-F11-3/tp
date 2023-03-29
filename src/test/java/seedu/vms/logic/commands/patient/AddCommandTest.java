package seedu.vms.logic.commands.patient;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.vms.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import seedu.vms.commons.core.GuiSettings;
import seedu.vms.commons.core.Retriever;
import seedu.vms.commons.core.ValueChange;
import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.commons.exceptions.UnexpectedChangeException;
import seedu.vms.logic.CommandMessage;
import seedu.vms.logic.parser.ParseResult;
import seedu.vms.logic.parser.exceptions.ParseException;
import seedu.vms.model.GroupName;
import seedu.vms.model.IdData;
import seedu.vms.model.Model;
import seedu.vms.model.ReadOnlyUserPrefs;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.appointment.AppointmentManager;
import seedu.vms.model.keyword.Keyword;
import seedu.vms.model.keyword.KeywordManager;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.PatientManager;
import seedu.vms.model.patient.ReadOnlyPatientManager;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeManager;
import seedu.vms.testutil.PatientBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPatient_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_patientAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPatientAdded modelStub = new ModelStubAcceptingPatientAdded();
        Patient validPatient = new PatientBuilder().build();

        CommandMessage commandResult = new AddCommand(validPatient).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, validPatient), commandResult.getMessage());
        assertEquals(Arrays.asList(validPatient), modelStub.patientsAdded);
    }

    @Test
    public void equals() {
        Patient alice = new PatientBuilder().withName("Alice").build();
        Patient bob = new PatientBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different patient -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPatient(Patient patient) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPatientManager(ReadOnlyPatientManager newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyPatientManager getPatientManager() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePatient(int id, boolean isForce) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteAppointment(int id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void markAppointment(int id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void unmarkAppointment(int id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPatient(int id, Patient editedPatient) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAppointment(int id, Appointment editedAppointment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableMap<Integer, IdData<Patient>> getFilteredPatientList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPatientList(Predicate<Patient> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPatient(int id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAppointment(Appointment appointment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableMap<String, VaxType> getFilteredVaxTypeMap() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public VaxTypeManager getVaxTypeManager() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addKeyword(Keyword keyword) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredKeywordList(Predicate<Keyword> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteKeyword(int id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableMap<Integer, IdData<Appointment>> getFilteredAppointmentMap() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getFilteredAppointmentMap'");
        }

        @Override
        public ObservableMap<Integer, IdData<Keyword>> getFilteredKeywordList() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getFilteredKeywordList'");
        }

        @Override
        public KeywordManager getKeywordManager() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getKeywordManager'");
        }

        @Override
        public AppointmentManager getAppointmentManager() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getAppointmentManager'");
        }

        @Override
        public void setVaxTypeManager(VaxTypeManager manager) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setVaxTypeManager'");
        }

        @Override
        public void setAppointmentManager(AppointmentManager manager) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setAppointmentManager'");
        }

        @Override
        public void setKeywordManager(KeywordManager manager) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setKeywordManager'");
        }

        @Override
        public ParseResult parseCommand(String userCommand) throws ParseException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'parseCommand'");
        }

        @Override
        public void setPatientFilters(Collection<Predicate<Patient>> filters) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setVaccinationFilters'");
        }

        @Override
        public void setVaccinationFilters(Collection<Predicate<VaxType>> filters) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setVaccinationFilters'");
        }

        @Override
        public List<IdData<Appointment>> validatePatientChange(ValueChange<IdData<Patient>> change) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'validatePatientChange'");
        }

        @Override
        public void handlePatientChange(ValueChange<IdData<Patient>> change) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'handlePatientChange'");
        }

        @Override
        public List<IdData<Appointment>> validateVaccinationChange(ValueChange<VaxType> change) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'validateVaccinationChange'");
        }

        @Override
        public void handleVaccinationChange(ValueChange<VaxType> change) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'handleVaccinationChange'");
        }

        @Override
        public ObjectProperty<VaxType> detailedVaxTypeProperty() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'detailVaxTypeProperty'");
        }

        @Override
        public void setDetailedVaxType(VaxType vaxType) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setDetailedVaxType'");
        }

        @Override
        public ObjectProperty<IdData<Patient>> detailedPatientProperty() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'detailedPatientProperty'");
        }

        @Override
        public void setDetailedPatient(IdData<Patient> patient) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'setDetailedPatient'");
        }

        @Override
        public void bindVaccinationDisplayList(ObservableList<VaxType> displayList) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'bindDisplayList'");
        }

        @Override
        public VaxType getVaccination(Retriever<String, VaxType> retriever) throws IllegalValueException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getVaccination'");
        }

        @Override
        public ValueChange<VaxType> addVaccination(VaxType vaxType) throws IllegalValueException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'addVaccination'");
        }

        @Override
        public ValueChange<VaxType> editVaccination(String name, VaxType newValue) throws IllegalValueException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'editVaccination'");
        }

        @Override
        public ValueChange<VaxType> deleteVaccination(GroupName vaxName, boolean isForce)
                throws IllegalValueException, UnexpectedChangeException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'deleteVaccination'");
        }
    }

    /**
     * A Model stub that always accept the patient being added.
     */
    private class ModelStubAcceptingPatientAdded extends ModelStub {
        final ArrayList<Patient> patientsAdded = new ArrayList<>();

        @Override
        public void addPatient(Patient patient) {
            requireNonNull(patient);
            patientsAdded.add(patient);
        }

        @Override
        public ReadOnlyPatientManager getPatientManager() {
            return new PatientManager();
        }
    }

}
