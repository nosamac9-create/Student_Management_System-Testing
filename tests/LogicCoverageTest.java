// =====================================================================
// CCSW 323 — Logic Coverage Test Suite
// =====================================================================
// Function under test: StudentSystem.displayTop5()
// Predicate at line 238 of StudentSystem.java:
//
//   student.year.equals(years[numberOfYears])  &&  count <= 5  &&  student.GPA >= 2.0
//
// Clauses:
//   C1 -> student.year.equals(years[numberOfYears])
//   C2 -> count <= 5
//   C3 -> student.GPA >= 2.0
//
// Predicate definition:  P = C1 AND C2 AND C3
//
// Criteria covered:
//   - Predicate Coverage (PC)             : 5 unique test cases
//   - Correlated Active Clause Coverage   : 5 unique test cases (CACC)
// =====================================================================

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import packages.system.StudentSystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LogicCoverageTest {

    private StudentSystem system;
    private ByteArrayOutputStream captured;
    private PrintStream realStdout;

    @BeforeEach
    public void freshFixture() {
        system = new StudentSystem();
        captured = new ByteArrayOutputStream();
        realStdout = System.out;
        System.setOut(new PrintStream(captured));
    }

    private void releaseStdout() {
        System.setOut(realStdout);
    }

    /** Slice the captured output to just the lines that belong to a given year section.
     *  This is needed because displayTop5 prints every year section in the same buffer. */
    private String firstYearSection(String year) {
        String all = captured.toString();
        String header = year + " Year:";
        int start = all.indexOf(header);
        if (start < 0) return "";
        // The next year header (or end of buffer) closes this section.
        String[] yearLabels = {"First Year:", "Second Year:", "Third Year:", "Fourth Year:"};
        int end = all.length();
        for (String lbl : yearLabels) {
            int idx = all.indexOf(lbl, start + header.length());
            if (idx > 0 && idx < end) end = idx;
        }
        return all.substring(start, end);
    }

    // =====================================================================
    // Predicate Coverage (PC): predicate must evaluate to T at least once
    // and to F at least once. We design 5 unique scenarios so PC is
    // satisfied with margin.
    // =====================================================================

    @Test
    @DisplayName("TC-LC-PC-01  PC: P=T (C1=T,C2=T,C3=T) — passing First-year student appears")
    public void pc_allTrue_studentIsPrinted() {
        system.addStudent("Aisha", 1001, 3.85, "First", "CS", true);

        system.displayTop5();
        releaseStdout();

        assertTrue(firstYearSection("First").contains("Aisha"),
                "Predicate true => student Aisha must appear in First Year section");
    }

    @Test
    @DisplayName("TC-LC-PC-02  PC: P=F via C1=F — Second-year student in First section")
    public void pc_C1False_yearMismatch() {
        system.addStudent("Bilal", 1002, 3.40, "Second", "IS", true);

        system.displayTop5();
        releaseStdout();

        assertFalse(firstYearSection("First").contains("Bilal"),
                "C1 false => Bilal must NOT appear in First Year section");
        assertTrue(firstYearSection("Second").contains("Bilal"),
                "Sanity: Bilal SHOULD appear in his own year section");
    }

    @Test
    @DisplayName("TC-LC-PC-03  PC: P=F via C2=F — sixth First-year student is filtered out")
    public void pc_C2False_topFiveAlreadyFilled() {
        // Five passing First-year students fill the top-5
        system.addStudent("S1", 2001, 3.95, "First", "CS", true);
        system.addStudent("S2", 2002, 3.85, "First", "CS", true);
        system.addStudent("S3", 2003, 3.75, "First", "CS", true);
        system.addStudent("S4", 2004, 3.65, "First", "CS", true);
        system.addStudent("S5", 2005, 3.55, "First", "CS", true);
        // Sixth student — should be filtered when count > 5
        system.addStudent("Cyrus", 2006, 3.10, "First", "CS", true);

        system.displayTop5();
        releaseStdout();

        String section = firstYearSection("First");
        assertFalse(section.contains("Cyrus"),
                "C2 false => Cyrus (the 6th student) must NOT be printed");
        assertTrue(section.contains("S1") && section.contains("S5"),
                "Sanity: the actual top-5 should still appear");
    }

    @Test
    @DisplayName("TC-LC-PC-04  PC: P=F via C3=F — failing GPA hides First-year student")
    public void pc_C3False_failingGPA() {
        system.addStudent("Dina", 1004, 1.50, "First", "AI", true);

        system.displayTop5();
        releaseStdout();

        assertFalse(firstYearSection("First").contains("Dina"),
                "C3 false (GPA<2.0) => Dina must NOT appear in First Year section");
    }

    @Test
    @DisplayName("TC-LC-PC-05  PC: P=F via C1=F & C3=F — Third-year failing student")
    public void pc_C1andC3False_compoundFalse() {
        // Process First-year section: Faisal is Third-year AND failing.
        // Both C1 and C3 are false; predicate is unambiguously false.
        system.addStudent("Faisal", 1005, 1.30, "Third", "DS", true);

        system.displayTop5();
        releaseStdout();

        assertFalse(firstYearSection("First").contains("Faisal"));
        assertFalse(firstYearSection("Third").contains("Faisal"),
                "Faisal also fails C3 in his own year, so should not appear there either");
    }

    // =====================================================================
    // Correlated Active Clause Coverage (CACC):
    //
    // For each clause Ci, the test makes Ci active (i.e., the value of Ci
    // determines the value of P), and the predicate P actually flips when
    // Ci flips. For a conjunctive predicate the recipe is: hold the other
    // clauses TRUE and toggle Ci.
    //
    // We add a 5th case that confirms CACC is genuinely stronger than PC
    // by combining a "near miss" where two clauses are true but the third
    // determines the outcome.
    // =====================================================================

    @Test
    @DisplayName("TC-LC-CACC-01  CACC anchor: all clauses true => P=T")
    public void cacc_anchor_allTrue() {
        system.addStudent("Hala", 3001, 3.95, "Second", "CS", true);

        system.displayTop5();
        releaseStdout();

        assertTrue(firstYearSection("Second").contains("Hala"));
    }

    @Test
    @DisplayName("TC-LC-CACC-02  CACC: C1 active (year flipped, others true) => P toggles")
    public void cacc_C1_active() {
        // Student is Fourth year; while we are in the SECOND year section,
        // C1 = false but C2, C3 are still true. So C1 alone determines P.
        system.addStudent("Idris", 3002, 3.60, "Fourth", "IS", true);

        system.displayTop5();
        releaseStdout();

        assertFalse(firstYearSection("Second").contains("Idris"),
                "C1 false in Second-year section, others true => P false here");
        assertTrue(firstYearSection("Fourth").contains("Idris"),
                "C1 becomes true in Fourth-year section => P true there");
    }

    @Test
    @DisplayName("TC-LC-CACC-03  CACC: C2 active (count>5, others true) => P toggles")
    public void cacc_C2_active() {
        // Five top Third-year students fill the section's top-5
        system.addStudent("T1", 4001, 3.99, "Third", "DS", true);
        system.addStudent("T2", 4002, 3.97, "Third", "DS", true);
        system.addStudent("T3", 4003, 3.95, "Third", "DS", true);
        system.addStudent("T4", 4004, 3.93, "Third", "DS", true);
        system.addStudent("T5", 4005, 3.91, "Third", "DS", true);
        // Jana is sixth; C1 and C3 still true, only C2 makes P false
        system.addStudent("Jana", 4006, 3.50, "Third", "DS", true);

        system.displayTop5();
        releaseStdout();

        String section = firstYearSection("Third");
        assertFalse(section.contains("Jana"),
                "C2 false (count>5), others true => P false, Jana hidden");
        assertTrue(section.contains("T1"));
    }

    @Test
    @DisplayName("TC-LC-CACC-04  CACC: C3 active (GPA<2.0, others true) => P toggles")
    public void cacc_C3_active() {
        system.addStudent("Khaled", 4007, 1.95, "Fourth", "IT", true);

        system.displayTop5();
        releaseStdout();

        assertFalse(firstYearSection("Fourth").contains("Khaled"),
                "C3 false alone => P false, Khaled hidden");
    }

    @Test
    @DisplayName("TC-LC-CACC-05  CACC strength check: PC alone wouldn't notice this")
    public void cacc_subtle_correlation() {
        // This test demonstrates that CACC reveals more behavior than PC.
        // Five top-tier First-year students with strictly higher GPAs than
        // the next two students fill the top-5 deterministically.
        // Then Mira (lower GPA) appears as the 6th First-year => fails C2
        // (count > 5), while Layla fails C3 (failing GPA).
        system.addStudent("Ace1", 5001, 3.99, "First", "CS", true);
        system.addStudent("Ace2", 5002, 3.95, "First", "CS", true);
        system.addStudent("Ace3", 5003, 3.90, "First", "CS", true);
        system.addStudent("Ace4", 5004, 3.85, "First", "CS", true);
        system.addStudent("Ace5", 5005, 3.80, "First", "CS", true);
        // Layla - top GPAs above are all > 3.50, hers is below: she fails C3.
        system.addStudent("Layla", 5006, 1.80, "First", "CS", true);
        // Mira - has GPA above 2.0 but lower than the top 5, so she's the
        // 6th First-year student processed and fails C2 (count > 5).
        system.addStudent("Mira",  5007, 2.50, "First", "CS", true);

        system.displayTop5();
        releaseStdout();

        String section = firstYearSection("First");
        assertFalse(section.contains("Layla"),
                "Layla fails C3 even though C1 and C2 are true => P false");
        assertFalse(section.contains("Mira"),
                "Mira fails C2 even though C1 and C3 are true => P false");
        assertTrue(section.contains("Ace1"),
                "Ace1 satisfies all three clauses => P true");
    }
}
