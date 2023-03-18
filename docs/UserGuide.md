---
layout: page
title: User Guide
---

Vaccination Management System (VMS) is a **desktop app for managing vaccination appointments, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, VMS can get your appointments sorted out with great efficiency.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `11` or above installed in your Computer.

1. Download the latest `vms.jar` from [here](https://github.com/AY2223S2-CS2103-F11-3/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your VMS.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar vms.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

<!--    * `list` : Lists all contacts.
   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.
   * `delete 3` : Deletes the 3rd contact shown in the current list.
   * `clear` : Deletes all contacts.
   * `exit` : Exits the app. -->

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Command line syntax

<div markdown="block" class="alert alert-info" id="CLI-presentation-format">

**:information_source: Command syntaxes presentation**<br>

* **Pink italicized bolded capitalized words** represents _placeholders_ that the reader will have to replace with a
  variable. For example, <code><var>PATIENT_ID</var></code> will represent a patient ID in commands or example outputs.
* **Backslash** (`\`) before line breaks represents a _command continuation_ where the following line break and
  backslash are to be replaced with aN EMPTY character. For example,<br>
  <pre>
  appointment add --p <var>PATIENT_ID</var> \
      --s <var>START_TIME</var> --e <var>END_TIME</var> \
      --v <var>VAX_NAME</var> \
  </pre>
  would have the same meaning as this
  <pre>
  appointment add --p <var>PATIENT_ID</var> --s <var>START_TIME</var> --e <var>END_TIME</var> --v <var>VAX_NAME</var>
  </pre>
* **Square brackets** (`[` and `]`) around arguments indicate that the argument is optional. For example,
  <br><code>[--n <var>NEW_NAME</var>]</code> would mean that <wbr><code>--n <var>NEW_NAME</var></code> is optional.
* **Three dots with no space** (`...`) <u>after</u> arguments indicates that multiple of the same type of
  argument can be repeated. For example <wbr><code>[--r <var>REQUIREMENT</var>]...</code> would mean that
  <code>--r <var>REQUIREMENT</var></code> can appear multiple times.
* **Three dots with no space** <u>before</u> and <u>after</u> an argument would indicate that a list of that argument
  is required. The elements of a list are delimited by commas (`,`) and the keyword `{EMPTY}` is used to represent an
  empty list. For example, <code>--g ...<var>GROUP</var>...</code> would mean that a list of
  <code><var>GROUP</var></code> is required. Accepted arguments may be
  <code>--g <var>GROUP_1</var>, <var>GROUP_2</var>, <var>GROUP_3</var></code> for a list of 3 groups or `--g {EMPTY}`
  for an empty list of groups.'
* **Triangle brackets** (`<` and `>`) around words represent a [type](#types).

</div>

### General command syntax

The general command line syntax is as follows:<br>

<pre>
<var>COMPONENT</var> <var>COMMAND_WORD</var> <var>PREAMBLE</var> [--<var>FLAG</var> <var>ARGUMENT</var>]...
</pre>

* <code><var>COMPONENT</var></code> is a variable of type [`<component>`](#component). It may be omitted if it is
  [`basic`](#basic---applications-basic-features).
* <code><var>PREAMBLE</var></code> is any text after <code><var>COMMAND_WORD</var></code> to the end of the command or
  the first `--` flag delimiter. Its type is command dependent and will be taken to be empty if
  <code><var>COMMAND_WORD</var></code> is immediately followed by `--`.
* <code><var>COMMAND_WORD</var></code> and <code><var>FLAG</var></code> are single word arguments that do no accept
  spaces.

##### Additional points

* `--` is used to delimit flags and cannot be present in any of the argument placeholders.
* Some arguments may require parts which are delimited by `::`.
* Leading and trailing white spaces in <code><var>ARGUMENTS</var></code> and elements in lists will be ignored.

### Types

#### `<component>`

The list of available components are given in the [components section](#components).

#### `<string>`

Strings can take on any character sequence that do not contain `--` or new line characters.

#### `<group-name>`

A non-blank character sequence consisting of only alphanumeric character and all brackets excluding triangle brackets
(`<` and `>`). The character limit is **30** characters.

#### `<integer>`

An integer value between `-2147483648` and `2147483647`.

#### `<age>`

An extension of `<integer>`, allowing only positive values (ie. `x >= 0`). Age also has a max value of `200` which is
allowed to be exceeded, provided it conforms to `<integer>` restrictions as well. All values of age that exceed the max
value will be evaluated to be equal.

#### `<bloodType>`

The list of blood types are:

* A+
* A-
* B+
* B-
* AB+
* AB-
* O+
* O-

All other values will be rejected

#### `<date>`

The supported date formats are:

* `yyyy-mm-ddThh:mm`<br>
    eg. 2023-05-03T04:45
* `yyyy-m-d hhmm` - single and double digit day and months are supported.<br>
  eg. 2023-5-3 0455
  * The following formats are also acceptable:
  * `yyyy-mm-d hhmm`
  * `yyyy-mm-dd hhmm`
  * `yyyy-m-dd hhmm`

#### `<phone-number>`

Only 8 digit Singapore numbers are allowed.

#### `<req>`

`<req>` represents a requirement. It is used to evaluate if a patient's vaccination records meets a vaccination history
requirement. For every vaccination that a patient has taken, that vaccination's groups are tested on all requirements
that the vaccination has. A check is done to check if that vaccination's groups contains any or all of the groups
within the requirement set and its truth value depends on the [type](#req-type) of the requirement. If all requirements
are satisfied, the check passes and the patient satisfies the history requirement of the vaccination and vice versa.

`<req>` arguments require 2 and only 2 parts. The general syntax is as follows:

<pre>
<var>REQ_TYPE</var> :: ...<var>REQUIREMENT</var>...
</pre>

* <code><var>REQ_TYPE</var></code> : `<req-type>` - The type of the requirement.
* <code><var>REQUIREMENT</var></code> : `<group-name>` - An element that makes up the requirement set.

#### `<req-type>`

Only the following values are allowed:

* `ALL` - all groups of the requirement set must be present to pass. Example, a vaccination requirement of `G1, G2, G3`
  will require a patient to have taken a vaccination with all 3 groups. A vaccination with `G1, G2, G3` and
  `G1, G2, G3, G4` will pass but a vaccination with `G1, G2` groups will not.
* `ANY` - at least one group within the requirement set must be present to pass. Example, a vaccination requirement of
  `G1, G2, G3` will require the patient to have taken a vaccination that has any of the 3 groups. A vaccination with
  `G1` and `G1, G3` will pass but a vaccination with `G4, G5` groups will not.
* `NONE` - none of the groups within the requirement set must be present to pass. Example, a requirement with
  `G1, G2, G3` will require the patient to not have taken any vaccination that are classified as any of the 3 groups. A
  vaccination with `G1` and `G1, G3` will fail while a vaccination with `G4, G5` will pass. A failure on this type will
  break the testing process of the patient and the patient will immediately fail the history requirement of the
  vaccination. In other words, the patient will not be able to take that vaccination.

## Components

Below shows a list of components, their supported command words and their usage.

### `basic` - Application's basic features

#### `exit` - Exit the program

```text
exit
```

#### `help` - Display help page

```text
help
```

### `patient` - Patient functionalities

##### Patient data

| Variable      | Is needed | Type                   | Accept multiple |
| ------------- | --------- | ---------------------- | --------------- |
| `name`        | YES       | `<name>`               | NO              |
| `phone`       | YES       | `<phone-number>`       | NO              |
| `dateOfBirth` | YES       | `<date>`               | NO              |
| `bloodType`   | YES       | `<bloodType>`          | NO              |
| `allergy`     | NO        | list of `<group-name>` | YES             |
| `vaccine`     | NO        | list of `<group-name>` | YES             |

#### `add` - Add a patient

```text
patient add --name <string> --phone <phone-number> --d <date> --bloodtype <string> --a <group-name> --v <group-name>
patient add --name <string> --phone <phone-number> --d <date> --bloodtype <string>
```

Example:

* `patient add --n John Doe --p 98765432 --d 2001-03-19 --b B+ --a catfur --a pollen --v covax`
* `patient add --n John Doe --p 98765432 --d 2001-03-19 --b B+`

#### `delete` - Delete a patient

```text
patient delete <integer>
```

Example:

* `patent delete 5`

#### `find` - Locate a patient

Finds patients whose names contain any of the given keywords.

```text
patient find --name <string>
```

Example:

* `patient find --name john`

#### `list` - List all patients

```text
patient list
```

### `appointment` - Appointment functionalities

#### `add` - Add an appointment

```text
appointment add --p <integer> --s <date> --e <date> --v <string>
```

Example:

* `appointment add --p 5 --s 2023-03-05 0700 --e 2023-03-05 0800 --v Mordena`

#### `edit` - Edit an appointment

```text
appointment edit 1 --p <integer> --s <date> --e <date> --v <string>
```

Example:

* `appointment edit 1 --p 5 --s 2023-03-05 0700 --e 2023-03-05 0800 --v Pfizer`

#### `delete` - Delete an appointment

```text
appointment delete <integer>
```

Example:

* `appointment delete 5`

#### `list` - List all appointments

```text
appointment list
```

### `vaccination` - Vaccination functionalities

Vaccinations are uniquely identified by their names. Below shows a table describing the attributes vaccination has.

| Attribute               | Type                 | Description                                                       | Default value |
| ----------------------- | -------------------- | ----------------------------------------------------------------- | ------------- |
| Name                    | `<group-name>`       | The name of the vaccination.                                      | -             |
| Groups                  | `...<group-name>...` | The groups the vaccination<br>classifies under                    | `empty list`  |
| Minimum<br>age          | `<age>`              | The minimum age (inclusive)<br>to take the vaccination            | `0`           |
| Maximum<br>age          | `<age>`              | The maximum age (inclusive)<br>to take the vaccination            | `200`         |
| Ingredients             | `<group-name>`       | Ingredients of the vaccination.<br>Similar to patient's allergies | `empty list`  |
| History<br>requirements | `...<req>...`        | The list of requirements to<br>take the vaccination               | `empty list`  |

#### `add` - Add a vaccination type

Adds a new vaccination type as defined in the command into the system. If any of the optional arguments are omitted,
they will be set to their default values.

##### Syntax

<pre>
vaccination add <var>VAX_NAME</var> [--g ...<var>GROUP</var>...] [--lal <var>MIN_AGE</var>] [--ual <var>MAX_AGE</var>] \
    [--a ...<var>INGREDIENT</var>...]... [--h <var>HISTORY_REQ</var>]...
</pre>

* <code><var>VAX_NAME</var></code> : `<group-name>`
* <code><var>GROUP</var></code> : `<group-name>`
* <code><var>MIN_AGE</var></code> : `<Age>`
* <code><var>MAX_AGE</var></code> : `<Age>`
* <code><var>INGREDIENT</var></code> : `<group-name>`
* <code><var>HISTORY_REQ</var></code> : `<Req>`

##### Example

```text
vaccination add Pfizer (Dose 1) --groups DOSE 1, PFIZER, VACCINATION \
    --lal 5 \
    --a allergy1, allergy2, allergy3 \
    --h NONE::DOES 1 \
```

Copy and paste:<br>
`vaccination add Pfizer (Dose 1) --groups DOSE 1, PFIZER, VACCINATION --lal 5 --i allergy1, allergy2, allergy3 --h NONE::DOES 1`
<br><br>
Output:<br>
{some ss}

##### Restrictions

* The name of the vaccination being added must not exist in the system.

#### `edit` - Edit a vaccination type

Updates the attributes of the specified vaccination to the attributes specified. If any of the optional arguments
are omitted, they will be set to what they were before.

<pre>
vaccination add <var>VAX_NAME</var> [--n <var>NEW_NAME</var>] [--g ...<var>GROUP</var>...] \
    [--lal <var>MIN_AGE</var>] [--ual <var>MAX_AGE</var>] \
    [--a ...<var>INGREDIENT</var>...]... [--h <var>HISTORY_REQ</var>]...
</pre>

* <code><var>VAX_NAME</var></code> : `<group-name>`
* <code><var>NEW_NAME</var></code> : `<group-name>`
* <code><var>GROUP</var></code> : `<group-name>`
* <code><var>MIN_AGE</var></code> : `<Age>`
* <code><var>MAX_AGE</var></code> : `<Age>`
* <code><var>INGREDIENT</var></code> : `<group-name>`
* <code><var>HISTORY_REQ</var></code> : `<Req>`

##### Example

After the vaccination add example,

```text
vaccination edit Pfizer (Dose 1) --n Pfizer (Dose 2) \
    --groups DOSE 1, PFIZER, VACCINATION \
    --a allergy1, allergy2, allergy3 \
    --h NONE::DOES 2 --h ALL::DOSE 1, PFIZER \
```

Copy and paste:<br>
`vaccination edit Pfizer (Dose 1) --n Pfizer (Dose 2) --groups DOSE 1, PFIZER, VACCINATION --a allergy1, allergy2, allergy3 --h NONE::DOES 2 --h ALL::DOSE 1, PFIZER`
<br><br>
Output:<br>
{some ss}

##### Restrictions

* <code><var>VAX_NAME</var></code> must exist in the system.
* <code><var>NEW_NAME</var></code> must be a name that does not yet exist in the system unless it is the same as <code><var>VAX_NAME</var></code>.

#### `delete` - Deletes a vaccination

Deletes the vaccination with the specified name from the system.

<pre>
vaccination delete <var>VAX_NAME</var>
</pre>

* <code><var>VAX_NAME</var></code> : `<group-name>`

##### Example

After the vaccination add example,

```text
vaccination delete Pfizer (Dose 1)
```
Output:<br>
{some ss}

## Advance

VMS data are saved as a JSON file. Advanced users are welcome to update data directly by editing that data file.

Locations:

1. `[JAR file location]/data/patientlist.json`
2. `[JAR file location]/data/appointmentlist.json`

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, VMS will discard all data and start with an empty data file at the next run.
</div>

<div markdown="block" class="alert alert-info">

**:information_source: JSON syntax presentation**<br>

* The following will have the same meaning as <a href="CLI-presentation-format">CLI presentation</a>.
  * **Pink italicized bolded capitalized words** (<code><var>PLACEHOLDER_EXAMPLE</var></code>)
  * **Three dots with no spaces** (<code><var>ARG</var>...</code> and <code>...<var>ARG</var>...</code>)
  * **Triangle brackets** (`<` and `>`)
* **Square brackets** (`[` and `]`) will no longer mean an optional argument, instead it will be a required character
  for JSON syntax.

</div>

### Vaccination type JSON

Vaccination data are stored as a JSON file in `[JAR file location]/data/vaxtype.json`. It has the following syntax

##### Overall file

<pre>
{
  "types": [...<var>VACCINATION</var>...]
}
</pre>

##### Vaccination

<pre>
{
  "name": <var>VAX_NAME</var>,
  "groups": [...<var>GROUP</var>...],
  "minAge": <var>MIN_AGE</var>,
  "maxAge": <var>MAX_AGE</var>,
  "ingredients": [...<var>INGREDIENT</var>],
  "historyReqs": [...<var>REQUIREMENT</var>...]
}
</pre>

* <code><var>VAX_NAME</var></code> : `<group-name>`
* <code><var>GROUP</var></code> : `<group-name>`
* <code><var>MIN_AGE</var></code> : `<age>`
* <code><var>MAX_AGE</var></code> : `<age>`
* <code><var>INGREDIENT</var></code> : `<group-name>`
* <code><var>REQUIREMENT</var></code> : `<req>`

###### Notes

* All nodes are optional except for `"name"`.

##### Requirement

<pre>
{
  "reqType": <var>REQ_TYPE</var>,
  "reqSet": [...<var>REQUIREMENT</var>...]
}
</pre>

* <code><var>REQ_TYPE</var></code> : `<req-type>`
* <code><var>REQUIREMENT</var></code> : `<group-name>`

##### Example

```json
{
  "types": [
    {
      "name": "Dose 1 (Pfizer)",
      "groups": ["DOSE 1", "Pfizer", "Vaccination"],
      "minAge": 5,
      "historyReqs": [
        {
          "reqType": "NONE",
          "reqSet": ["DOSE 1"]
        }
      ],
      "ingredients": [
        "ALC-0315",
        "ALC-0159",
        "DSPC",
        "Cholesterol",
        "Sucrose",
        "Phosphate",
        "Tromethamine",
        "Tromethamine hydrochloride"
      ]
    }
  ]
}
```
