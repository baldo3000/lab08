package it.unibo.deathnote.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import it.unibo.deathnote.api.DeathNote;

public class DeathNoteImpl implements DeathNote {

    private static long DEATH_CAUSE_TIMEOUT = 40L;
    private static long DEATH_DETAILS_TIMEOUT = 6040L;

    private final Map<String, DeathInfo> names;
    private String lastName;

    public DeathNoteImpl() {
        this.names = new LinkedHashMap<>();
        this.lastName = null;
    }

    @Override
    public String getRule(final int ruleNumber) {
        if (ruleNumber < 1 || ruleNumber > DeathNote.RULES.size()) {
            throw new IllegalArgumentException(ruleNumber + " is not a valid rule number");
        }
        return DeathNote.RULES.get(ruleNumber - 1);
    }

    @Override
    public void writeName(final String name) {
        Objects.requireNonNull(name, "The name cannot be null");
        if (!isNameWritten(name)) {
            this.names.put(name, new DeathInfo());
            this.lastName = name;
        }
    }

    @Override
    public boolean writeDeathCause(final String cause) {
        if (Objects.isNull(cause)) {
            throw new IllegalStateException("Provided death cause is null");
        }
        if (Objects.isNull(this.lastName)) {
            throw new IllegalStateException("There are no names written in the note");
        }
        final DeathInfo death = this.names.get(this.lastName);
        if (canWrite(death, DEATH_CAUSE_TIMEOUT)) {
            death.deathCause = cause;
            return true;
        }
        return false;
    }

    @Override
    public boolean writeDetails(final String details) {
        if (Objects.isNull(details)) {
            throw new IllegalStateException("Provided death details is null");
        }
        if (Objects.isNull(this.lastName)) {
            throw new IllegalStateException("There are no names written in the note");
        }
        final DeathInfo death = this.names.get(this.lastName);
        if (canWrite(death, DEATH_DETAILS_TIMEOUT)) {
            death.deathDetails = details;
            return true;
        }
        return false;
    }

    /**
     * This method takes a DeathInfo (for performance reasons, not accessing Map
     * values several times) and a timeout and returns true if the delta time is
     * less than the one provided
     * 
     * @param death   the info of this death
     * @param timeout the max delta time allowed for writing
     * @return true if it can write, false otherwise
     */
    private boolean canWrite(final DeathInfo death, final long timeout) {
        final long deltaTime = System.currentTimeMillis() - death.deathTime;
        return deltaTime <= timeout;
    }

    @Override
    public String getDeathCause(final String name) {
        if (!isNameWritten(name)) {
            throw new IllegalArgumentException("Person \"" + name + "\" is not written in the note");
        }
        return this.names.get(name).deathCause;
    }

    @Override
    public String getDeathDetails(final String name) {
        if (!isNameWritten(name)) {
            throw new IllegalArgumentException("Person \"" + name + "\" is not written in the note");
        }
        return this.names.get(name).deathDetails;
    }

    @Override
    public boolean isNameWritten(final String name) {
        return this.names.containsKey(name);
    }

    /**
     * This nested class is used to keep tracking of a death and contains
     * the time of the execution, the death cause and the death details
     */
    private static class DeathInfo {
        private static String DEFAULT_CAUSE = "heart attack";
        private static String DEFAULT_DETAILS = "";

        private long deathTime;
        private String deathCause;
        private String deathDetails;

        private DeathInfo() {
            this.deathTime = System.currentTimeMillis();
            this.deathCause = DeathInfo.DEFAULT_CAUSE;
            this.deathDetails = DeathInfo.DEFAULT_DETAILS;
        }
    }
}
