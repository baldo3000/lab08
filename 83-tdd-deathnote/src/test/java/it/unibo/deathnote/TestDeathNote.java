package it.unibo.deathnote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.deathnote.api.DeathNote;

class TestDeathNote {

    private static final String NAME1 = "Pierino";
    private static final String NAME2 = "Piergiorgio";
    private static final String NAME3 = "Mariangiongiangela";
    private static final String DEFAULT_CAUSE = "heart attack";
    private static final String DEATH_CAUSE1 = "karting accident";
    private static final String DEATH_CAUSE2 = "explosion";
    private static final String DETAILS1 = "ran for too long";
    private static final String DETAILS2 = "very very hot";
    private static final String DEFAULT_DETAILS = "";

    private DeathNote note;

    @BeforeEach
    public void setUp() {
        this.note = new DeathNoteImpl();
    }

    @Test
    public void testInvalidRules() {
        List<Integer> invalidRules = List.of(-1, 0, DeathNote.RULES.size() + 1);
        for (final var rule : invalidRules) {
            try {
                this.note.getRule(rule);
                fail("Tried to find rules that do not exists");
            } catch (IllegalArgumentException e) {
                assertEquals(rule + " is not a valid rule number", e.getMessage());
            }
        }
    }

    @Test
    public void testAllRules() {
        assertNotNull(DeathNote.RULES);
        for (int i = 1; i < DeathNote.RULES.size() + 1; i++) {
            final var rule = this.note.getRule(i);
            assertNotNull(rule);
            assertNotEquals("", rule);
        }
    }

    @Test
    public void testNameWriting() {
        assertFalse(this.note.isNameWritten(NAME1));
        this.note.writeName(NAME1);
        assertTrue(this.note.isNameWritten(NAME1));
        assertFalse(this.note.isNameWritten(NAME2));
        assertFalse(this.note.isNameWritten(""));
        try {
            this.note.writeName(null);
            fail("The name is null");
        } catch (NullPointerException e) {
            assertEquals("The name cannot be null", e.getMessage());
        }
    }

    @Test
    public void testCauseWriting() throws InterruptedException {
        try {
            this.note.writeDeathCause(DEATH_CAUSE1);
            fail("Assigning death cause without any name written");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't assign death cause because there's no name written in the note", e.getMessage());
        }
        this.note.writeName(NAME1);
        assertEquals(DEFAULT_CAUSE, this.note.getDeathCause(NAME1));
        assertTrue(this.note.writeDeathCause(DEATH_CAUSE1));
        assertEquals(DEATH_CAUSE1, this.note.getDeathCause(NAME1));
        Thread.sleep(100);
        assertFalse(this.note.writeDeathCause(DEATH_CAUSE2));
        assertEquals(DEATH_CAUSE1, this.note.getDeathCause(NAME1));
        try {
            this.note.getDeathCause(NAME2);
            fail("Trying to get death cause from a person that's not written in the note");
        } catch (IllegalArgumentException e) {
            assertEquals("Person \"" + NAME2 + "\" is not written in the note", e.getMessage());
        }
    }

    @Test
    public void testDetailsWriting() throws InterruptedException {
        try {
            this.note.writeDetails(DETAILS1);
            fail("Writing death details without any name written");
        } catch (IllegalArgumentException e) {
            assertEquals("Can't write death details because there's no name written in the note", e.getMessage());
        }
        this.note.writeName(NAME1);
        assertEquals(DEFAULT_DETAILS, this.note.getDeathCause(NAME1));
        assertTrue(this.note.writeDetails(DETAILS1));
        assertEquals(DETAILS1, this.note.getDeathDetails(NAME1));
        this.note.writeName(NAME2);
        Thread.sleep(6100);
        assertFalse(this.note.writeDetails(DETAILS2));
        assertEquals(DEFAULT_DETAILS, this.note.getDeathDetails(NAME2));
        try {
            this.note.getDeathCause(NAME3);
            fail("Trying to get death details from a person that's not written in the note");
        } catch (IllegalArgumentException e) {
            assertEquals("Person \"" + NAME3 + "\" is not written in the note", e.getMessage());
        }
    }
}