// =====================================================================
// CCSW 323 — Input Space Partitioning Test Suite
// =====================================================================
// Function under test: StudentSystem.mergeStudentSystem(StudentSystem)
//
// IDM (Input Domain Model):
//
//   C1  Size of the receiving (current) system:
//        B1  empty,                B2  contains 1 student,
//        B3  contains many students
//
//   C2  Size of the incoming (other) system:
//        B1  empty,                B2  contains 1 student,
//        B3  contains many students
//
//   C3  ID overlap between the two systems:
//        B1  no IDs in common,     B2  some IDs in common,
//        B3  all IDs in common
//
//   C4  Name overlap between the two systems:
//        B1  no names in common,   B2  some names in common
//
// Criteria covered:
//   - Each Choice Coverage (ECC)   : 5 unique tests
//   - Base Choice Coverage (BCC)   : 5 unique tests
// =====================================================================

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import packages.system.Student;
import packages.system.StudentSystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class InputSpacePartitioningTest {

    private StudentSystem main;
    private StudentSystem incoming;
    private ByteArrayOutputStream captured;
    private PrintStream realStdout;

    @BeforeEach
    public void freshFixture() {
        main = new StudentSystem();
        incoming = new StudentSystem();
        captured = new ByteArrayOutputStream();
        realStdout = System.out;
        System.setOut(new PrintStream(captured));
    }

    private void releaseStdout() { System.setOut(realStdout); }

    private void seedMain_three() {
        main.addStudent("Alpha", 100, 3.50, "First",  "CS", true);
        main.addStudent("Beta",  101, 3.00, "Second", "IS", true);
        main.addStudent("Gamma", 102, 2.80, "Third",  "AI", true);
    }

    // =====================================================================
    // Each Choice Coverage (ECC):
    // Use at least one value from EACH block of every characteristic.
    // The number of tests = max(blocks per characteristic) = 3.
    // We choose 5 tests: 3 minimal ECC tests + 2 extra to cover blocks
    // not yet visited (e.g. C2=B2, C4=B2).
    // =====================================================================

    @Test
    @DisplayName("TC-ISP-ECC-01  ECC: C1=B2 one, C2=B3 many, C3=B1 no IDs, C4=B1 no names")
    public void ecc_singleMain_distinctMany() {
        main.addStudent("Solo", 700, 3.10, "First", "CS", true);          // C1 = B2
        incoming.addStudent("Nora", 200, 3.20, "Second", "IS", true);     // C2 = B3
        incoming.addStudent("Omar", 201, 3.30, "Third",  "AI", true);
        incoming.addStudent("Pari", 202, 3.40, "Fourth", "DS", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(4, main.getStudentList().size(),
                "Solo + 3 distinct incomings => 4");
        assertTrue(captured.toString().contains("merged successfully"));
    }

    @Test
    @DisplayName("TC-ISP-ECC-02  ECC: C1=B2 one, C2=B1 empty, C3=B1, C4=B1")
    public void ecc_singleMain_emptyIncoming() {
        main.addStudent("Solo", 750, 3.20, "Second", "IS", true);   // C1 = B2
        // incoming is empty (C2 = B1)

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(1, main.getStudentList().size(),
                "Empty incoming => main unchanged");
        assertTrue(captured.toString().contains("No new students"));
    }

    @Test
    @DisplayName("TC-ISP-ECC-03  ECC: C1=B1 empty, C2=B2 single, C3=B1, C4=B1")
    public void ecc_emptyMain_singleIncoming() {
        // C1 = B1 (empty main), C2 = B2 (single incoming), no overlap.
        incoming.addStudent("Tariq", 800, 3.50, "First", "CS", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(1, main.getStudentList().size(),
                "Empty main + 1 fresh incoming => 1");
        assertTrue(captured.toString().contains("Added successfully"));
    }

    @Test
    @DisplayName("TC-ISP-ECC-04  ECC: C1=B3 many, C2=B3 many, C3=B3 all IDs match, C4=B2 names overlap")
    public void ecc_allOverlap_nothingMerged() {
        seedMain_three();
        // Same IDs and same names as main => everything rejected.
        incoming.addStudent("Alpha", 100, 3.50, "First",  "CS", true);
        incoming.addStudent("Beta",  101, 3.00, "Second", "IS", true);
        incoming.addStudent("Gamma", 102, 2.80, "Third",  "AI", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(3, main.getStudentList().size(),
                "All IDs match => nothing added");
        assertTrue(captured.toString().contains("No new students"));
    }

    @Test
    @DisplayName("TC-ISP-ECC-05  ECC: C1=B2 one, C2=B3 many, C3=B2 some IDs, C4=B1 no names")
    public void ecc_singleMain_partialIDOverlap() {
        main.addStudent("Solo", 600, 3.00, "First", "CS", true);   // C1 = B2
        // C2 = B3 (many); one ID matches Solo's 600 (C3 = B2); names are all different.
        incoming.addStudent("Liam", 600, 2.50, "Second", "IS", true);  // ID overlap
        incoming.addStudent("Maya", 601, 3.40, "Third",  "AI", true);
        incoming.addStudent("Noor", 602, 3.20, "Fourth", "DS", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(3, main.getStudentList().size(),
                "Solo + 2 fresh (one rejected for duplicate ID) => 3");
        assertTrue(captured.toString().contains("non-unique ID"));
    }

    // =====================================================================
    // Base Choice Coverage (BCC):
    // Pick a base choice for every characteristic, run that base test,
    // then vary ONE characteristic at a time.
    //
    // Base choice (the "happy path"):
    //   C1 = B3 (main has many)
    //   C2 = B3 (incoming has many)
    //   C3 = B1 (no ID overlap)
    //   C4 = B1 (no name overlap)
    //
    // Vary each characteristic once => 1 base + 4 varied = 5 tests.
    // =====================================================================

    @Test
    @DisplayName("TC-ISP-BCC-01  BCC base: many+many, no overlap => fully merged")
    public void bcc_base_happyPath() {
        seedMain_three();                                                // C1=B3
        incoming.addStudent("Riya",   400, 3.55, "Fourth", "DS", true);
        incoming.addStudent("Selim",  401, 2.95, "First",  "IT", true);
        incoming.addStudent("Tariq",  402, 3.45, "Second", "AI", true);  // C2=B3,C3=B1,C4=B1

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(6, main.getStudentList().size(),
                "Base case: 3 + 3 disjoint = 6");
        assertTrue(captured.toString().contains("merged successfully"));
    }

    @Test
    @DisplayName("TC-ISP-BCC-02  BCC vary C1=B1 (empty main): all incoming added")
    public void bcc_vary_C1_empty_main() {
        // C1 = B1 (empty), others at base
        incoming.addStudent("Riya",   400, 3.55, "Fourth", "DS", true);
        incoming.addStudent("Selim",  401, 2.95, "First",  "IT", true);
        incoming.addStudent("Tariq",  402, 3.45, "Second", "AI", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(3, main.getStudentList().size());
        assertTrue(captured.toString().contains("Added successfully"));
    }

    @Test
    @DisplayName("TC-ISP-BCC-03  BCC vary C2=B1 (empty incoming): no-op")
    public void bcc_vary_C2_empty_incoming() {
        seedMain_three();
        // C2 = B1 (empty incoming)

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(3, main.getStudentList().size());
        assertTrue(captured.toString().contains("No new students"));
    }

    @Test
    @DisplayName("TC-ISP-BCC-04  BCC vary C3=B3 (all IDs overlap): nothing merged")
    public void bcc_vary_C3_all_id_overlap() {
        seedMain_three();
        // Use the same IDs as main; names are different to isolate C3
        incoming.addStudent("Yara",   100, 3.55, "Fourth", "DS", true);
        incoming.addStudent("Zaid",   101, 2.95, "First",  "IT", true);
        incoming.addStudent("Wael",   102, 3.45, "Second", "AI", true);

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(3, main.getStudentList().size(),
                "All IDs already exist => no incoming students added");
        assertTrue(captured.toString().contains("non-unique ID"));
    }

    @Test
    @DisplayName("TC-ISP-BCC-05  BCC vary C4=B2 (some names overlap): partial merge")
    public void bcc_vary_C4_name_overlap() {
        seedMain_three();
        // IDs are fresh, but the name 'Alpha' is already in main
        incoming.addStudent("Alpha",  500, 3.55, "Fourth", "DS", true); // dup name
        incoming.addStudent("Yusuf",  501, 2.95, "First",  "IT", true); // fresh
        incoming.addStudent("Zayd",   502, 3.45, "Second", "AI", true); // fresh

        main.mergeStudentSystem(incoming);
        releaseStdout();

        assertEquals(5, main.getStudentList().size(),
                "Two fresh students added; 'Alpha' rejected for non-unique name");
        assertTrue(captured.toString().contains("non-unique name"));
    }
}
