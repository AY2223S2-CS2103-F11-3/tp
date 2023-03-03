package seedu.vms.storage.vaccination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.model.vaccination.Requirement;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeBuilder;
import seedu.vms.model.vaccination.VaxTypeStorage;


/** A JSON friendly version of {@link VaxType}. */
public class JsonAdaptedVaxType {
    private static final String MISSING_FIELD_MESSAGE_FORMAT = "Vaccination type [%s] is missing";

    private final String name;
    private final List<String> groups;
    private final Integer minAge;
    private final Integer maxAge;
    private final Integer minSpacing;
    private final List<JsonAdaptedVaxRequirement> historyReqs;
    private final List<JsonAdaptedVaxRequirement> allergyReqs;


    /** Constructs a {@code JsonAdaptedVaxType}. */
    @JsonCreator
    public JsonAdaptedVaxType(
                @JsonProperty("name") String name,
                @JsonProperty("groups") List<String> groups,
                @JsonProperty("minAge") Integer minAge,
                @JsonProperty("maxAge") Integer maxAge,
                @JsonProperty("minSpacing") Integer minSpacing,
                @JsonProperty("historyReqs") List<JsonAdaptedVaxRequirement> historyReqs,
                @JsonProperty("allergyReqs") List<JsonAdaptedVaxRequirement> allergyReqs) {
        this.name = name;
        this.groups = groups;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.minSpacing = minSpacing;
        this.historyReqs = historyReqs;
        this.allergyReqs = allergyReqs;
    }


    /**
     * Converts this JSON friendly version to an {@link VaxType} instance. The
     * type is added in to the vaccination type map in the process.
     *
     * @throws IllegalValueException if name field is missing.
     */
    public VaxType toModelType(VaxTypeStorage storage) throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "NAME"));
        }
        VaxTypeBuilder builder = VaxTypeBuilder.of(storage, name);

        if (groups != null) {
            builder = builder.setGroups(new HashSet<>(groups));
        }

        if (minAge != null) {
            builder = builder.setMinAge(minAge);
        }

        if (maxAge != null) {
            builder = builder.setMaxAge(maxAge);
        }

        if (minSpacing != null) {
            builder = builder.setMinSpacing(minSpacing);
        }

        if (historyReqs != null) {
            builder = builder.setHistoryReqs(toReqList(historyReqs));
        }

        if (allergyReqs != null) {
            builder = builder.setAllergyReqs(toReqList(allergyReqs));
        }

        return builder.build();
    }


    private List<Requirement> toReqList(List<JsonAdaptedVaxRequirement> adaptedList) throws IllegalValueException {
        ArrayList<Requirement> reqs = new ArrayList<>();
        for (JsonAdaptedVaxRequirement adapted : adaptedList) {
            reqs.add(adapted.toModelType());
        }
        return reqs;
    }
}
