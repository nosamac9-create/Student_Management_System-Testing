// =====================================================================
// CCSW 323 — Graph Coverage Test Suite
// =====================================================================
// Function under test: StudentSystem.removeStudentByID(int ID)
//
// Source code:
//   for (int i = 0; i < this.studentList.size(); i++) {
//       if (this.studentList.get(i).ID == ID) {
//           this.studentList.remove(i);
//           System.out.println("Student removed successfully!");
//           return;
//       }
//   }
//   System.out.println("Student not found!");
//
// Control flow graph (using the standard for-loop CFG convention):
//
//   N = { 1, 2, 3, 4, 5, 6 }
//   1 : i = 0                        (initialization)
//   2 : i < studentList.size()       (loop condition)
//   3 : studentList.get(i).ID == ID  (body decision)
//   4 : remove + print "removed" + return         (success exit)
//   5 : i++                          (increment)
//   6 : print "Student not found!"   (failure exit)
//
//   E = { (1,2), (2,3), (2,6), (3,4), (3,5), (5,2) }
//   N0 = { 1 },   Nf = { 4, 6 }
//
// Prime Paths (using the simple-path / no-proper-subpath rule):
//
//   PP1 : [1, 2, 6]                          loop body skipped (empty list)
//   PP2 : [1, 2, 3, 4]                       match on first iteration
//   PP3 : [1, 2, 3, 5]                       one mismatch (loop entered)
//   PP4 : [2, 3, 5, 2]                       internal loop body cycle
//   PP5 : [3, 5, 2, 3]                       internal loop body cycle (alt anchor)
//   PP6 : [3, 5, 2, 6]                       mismatch then exhausted (interior)
//   PP7 : [5, 2, 3, 4]                       increment then match
//   PP8 : [5, 2, 3, 5]                       increment then mismatch (loop continues)
//
// Note: simple paths may be loops (first node = last node),
// which is why PP4, PP5 and PP8 are valid prime paths.
//
// Criteria covered:
//   - Node Coverage (NC)        : 5 unique tests
//   - Prime Path Coverage (PPC) : 5 unique tests touring all 8 prime paths
// =====================================================================

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import packages.system.Student;
import packages.system.StudentSystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class GraphCoverageTest {

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

    private void releaseStdout() { System.setOut(realStdout); }

    private boolean containsId(int id) {
        return system.getStudentList().stream().anyMatch(s -> s.ID == id);
    }

    // =====================================================================
    // Node Coverage (NC):
    // Every node {1,2,3,4,5,6} must be visited by at least one test path.
    // The five tests below walk five different paths through the CFG;
    // together they cover all six nodes.
    // =====================================================================

    @Test
    @DisplayName("TC-GC-NC-01  NC: two students, none match — two full mismatches then exit")
    public void nc_twoMismatches_thenExit() {
        // Loop iterates twice with no match, then takes the not-found branch.
        system.addStudent("Aisha", 100, 3.0, "First",  "CS", true);
        system.addStudent("Bilal", 101, 3.5, "Second", "IS", true);

        system.removeStudentByID(9999);
        releaseStdout();

        assertEquals(2, system.getStudentList().size());
        assertTrue(captured.toString().contains("Student not found"));
    }

    @Test
    @DisplayName("TC-GC-NC-02  NC: three students, last one matches")
    public void nc_threeStudents_lastMatches() {
        system.addStudent("Cyrus",  200, 3.2, "First",  "IS", true);
        system.addStudent("Dina",   201, 3.4, "Second", "CS", true);
        system.addStudent("Ehab",   202, 3.6, "Third",  "AI", true);

        system.removeStudentByID(202);
        releaseStdout();

        assertEquals(2, system.getStudentList().size());
        assertFalse(containsId(202));
        assertTrue(captured.toString().contains("removed successfully"));
    }

    @Test
    @DisplayName("TC-GC-NC-03  NC: four students, last one matches")
    public void nc_fourStudents_lastMatches() {
        system.addStudent("Fatima", 300, 3.1, "First",  "DS", true);
        system.addStudent("Ghada",  301, 3.3, "Second", "DS", true);
        system.addStudent("Hadi",   302, 3.5, "Third",  "DS", true);
        system.addStudent("Iman",   303, 3.7, "Fourth", "DS", true);

        system.removeStudentByID(303);
        releaseStdout();

        assertEquals(3, system.getStudentList().size());
        assertFalse(containsId(303));
        assertTrue(captured.toString().contains("removed successfully"));
    }

    @Test
    @DisplayName("TC-GC-NC-04  NC: four students, none match — four full mismatches then exit")
    public void nc_fourMismatches_thenExit() {
        system.addStudent("Jude",   400, 3.0, "First",  "CS", true);
        system.addStudent("Khaled", 401, 3.2, "Second", "IS", true);
        system.addStudent("Layla",  402, 3.4, "Third",  "AI", true);
        system.addStudent("Mira",   403, 3.6, "Fourth", "DS", true);

        system.removeStudentByID(9999);
        releaseStdout();

        assertEquals(4, system.getStudentList().size());
        assertTrue(captured.toString().contains("Student not found"));
    }

    @Test
    @DisplayName("TC-GC-NC-05  NC: five students, last one matches")
    public void nc_fiveStudents_lastMatches() {
        system.addStudent("Nora",  500, 3.0, "First",  "CS", true);
        system.addStudent("Omar",  501, 3.2, "Second", "IS", true);
        system.addStudent("Pari",  502, 3.4, "Third",  "AI", true);
        system.addStudent("Qasim", 503, 3.6, "Fourth", "IT", true);
        system.addStudent("Rana",  504, 3.8, "First",  "DS", true);

        system.removeStudentByID(504);
        releaseStdout();

        assertEquals(4, system.getStudentList().size());
        assertFalse(containsId(504));
        assertTrue(captured.toString().contains("removed successfully"));
    }

    // =====================================================================
    // Prime Path Coverage (PPC):
    // Each test path tours one or more prime paths; together the five tests
    // tour all eight prime paths PP1..PP8. Test paths can have internal
    // loops, which is why a single test path can tour
    // several prime-path cycles at once.
    //
    //   Test                Path                                Prime paths toured
    //   --------------     -----------------------------------  -----------------------
    //   ppc_PP1            [1, 2, 6]                            PP1
    //   ppc_PP2            [1, 2, 3, 4]                         PP2
    //   ppc_PP3            [1, 2, 3, 5, 2, 6]                   PP3, PP4, PP6
    //   ppc_PP4            [1, 2, 3, 5, 2, 3, 4]                PP3, PP4, PP5, PP7
    //   ppc_PP5_loop       [1, 2, 3, 5, 2, 3, 5, 2, 6]          PP3, PP4, PP5, PP6, PP8
    //
    // Coverage check: union of toured paths = {PP1..PP8}, all eight covered.
    // =====================================================================

    @Test
    @DisplayName("TC-GC-PPC-01  PP1: empty list, loop body skipped")
    public void ppc_PP1_emptyList() {
        system.removeStudentByID(123);
        releaseStdout();

        assertEquals(0, system.getStudentList().size());
        assertTrue(captured.toString().contains("Student not found"));
    }

    @Test
    @DisplayName("TC-GC-PPC-02  PP2: match on first iteration (single student)")
    public void ppc_PP2_matchAtFirst() {
        system.addStudent("Ali", 555, 3.0, "First", "CS", true);

        system.removeStudentByID(555);
        releaseStdout();

        assertEquals(0, system.getStudentList().size());
        assertTrue(captured.toString().contains("removed successfully"));
    }

    @Test
    @DisplayName("TC-GC-PPC-03  PP3 + PP4 + PP6: one mismatch then loop exhausted")
    public void ppc_PP3_oneMismatch_thenExit() {
        // Student at index 0 does NOT match; loop increments to 1, condition
        // becomes false (size == 1), and the not-found branch is taken.
        system.addStudent("Hadi", 700, 3.0, "First", "IS", true);

        system.removeStudentByID(8888);
        releaseStdout();

        assertEquals(1, system.getStudentList().size(),
                "ID not present, so list is unchanged");
        assertTrue(captured.toString().contains("Student not found"));
    }

    @Test
    @DisplayName("TC-GC-PPC-04  PP3 + PP4 + PP5 + PP7: one mismatch then match on second iteration")
    public void ppc_PP4_secondIterationMatches() {
        system.addStudent("Iman", 800, 3.0, "First",  "CS", true); // index 0 (no match)
        system.addStudent("Jude", 801, 3.5, "Second", "IS", true); // index 1 (match)

        system.removeStudentByID(801);
        releaseStdout();

        assertEquals(1, system.getStudentList().size());
        assertTrue(containsId(800));
        assertFalse(containsId(801));
        assertTrue(captured.toString().contains("removed successfully"));
    }

    @Test
    @DisplayName("TC-GC-PPC-05  PP3 + PP4 + PP5 + PP6 + PP8: loop iterates more than once, exhausted")
    public void ppc_PP5_loopMoreThanOnce_thenExit() {
        // Three mismatches in a row force the loop to truly iterate more
        // than once before the not-found branch is taken. This is the
        // "execute loop more than once" prime-path category.
        system.addStudent("K", 900, 3.0, "First",  "CS", true);
        system.addStudent("L", 901, 3.0, "Second", "IS", true);
        system.addStudent("M", 902, 3.0, "Third",  "AI", true);

        system.removeStudentByID(9999);
        releaseStdout();

        assertEquals(3, system.getStudentList().size(),
                "ID never matches; nothing removed");
        assertTrue(captured.toString().contains("Student not found"));
    }
}
