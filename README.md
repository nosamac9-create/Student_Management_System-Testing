#  Coverage-Based Testing of a Java Student Management System

> **CCSW 323 — Software Testing & Validation · University of Jeddah**

This repository is a fork of [Mohammed-3tef/Student_Management_System](https://github.com/Mohammed-3tef/Student_Management_System), extended with a comprehensive **JUnit 5 test suite** that applies three coverage models — **Logic Coverage**, **Input Space Partitioning**, and **Graph Coverage** — to three core functions of the system.

The original Java application is unchanged. All testing work, documentation, and analysis in the `tests/` and `report/` folders are our contribution as part of a class project.

## 📌 Project Overview

The goal was to design and execute a structured test suite using three different coverage models, with two criteria per model, on a real-world Java console application. The Student Management System (SUT) was chosen because it is fully backend, uses only the Java standard library, and has enough decisions, loops, and branches to exercise all three models meaningfully.

## 🎯 Testing Approach

| Model | Function Under Test | Criterion 1 | Criterion 2 |
|-------|---------------------|-------------|-------------|
| **Logic Coverage** | `displayTop5()` | Predicate Coverage (PC) | Correlated Active Clause Coverage (CACC) |
| **Input Space Partitioning** | `mergeStudentSystem()` | Each Choice Coverage (ECC) | Base Choice Coverage (BCC) |
| **Graph Coverage** | `removeStudentByID()` | Node Coverage (NC) | Prime Path Coverage (PPC) |

**Total: 30 test cases** (5 per criterion × 2 criteria × 3 models)

## 📊 Results

| Criterion | Total | Passed | Failed | Pass Rate |
|-----------|-------|--------|--------|-----------|
| Predicate Coverage (PC) | 5 | 5 | 0 | 100% |
| Correlated Active Clause (CACC) | 5 | 5 | 0 | 100% |
| Each Choice Coverage (ECC) | 5 | 5 | 0 | 100% |
| Base Choice Coverage (BCC) | 5 | 5 | 0 | 100% |
| Node Coverage (NC) | 5 | 5 | 0 | 100% |
| Prime Path Coverage (PPC) | 5 | 5 | 0 | 100% |
| **TOTAL** | **30** | **30** | **0** | **100%** |

All 30 tests pass on the current implementation.

##  Repository Structure

```
Student_Management_System/
├── src/                              # Original SUT (unchanged)
│   ├── App.java
│   ├── Student.java
│   ├── StudentSystem.java
│   ├── InputValidator.java
│   ├── CsvFileHandler.java
│   └── TxtFileHandler.java
├── tests/                            # ⭐ Our testing contribution
│   ├── LogicCoverageTest.java        # PC + CACC tests for displayTop5()
│   ├── InputSpacePartitioningTest.java  # ECC + BCC tests for mergeStudentSystem()
│   └── GraphCoverageTest.java        # NC + PPC tests for removeStudentByID()
└── report/                           # ⭐ Documentation
    ├── CCSW323_Project_Report.pdf    # Full 25-page project report
    └── Project_Presentation.pdf      # Slide deck
```

##  Tools Used

- **JUnit 5 (Jupiter)** — Test framework with `@Test`, `@BeforeEach`, `@DisplayName`
- **Java Standard Library** — `System.setOut` + `ByteArrayOutputStream` for stdout capture
- **VS Code** — Development and test execution

##  Running the Tests

1. Clone this repository
2. Ensure JUnit 5 is on your classpath
3. Compile the source and test files together
4. Run with your IDE's JUnit runner or the JUnit standalone console launcher:

```bash
java -jar junit-platform-console-standalone-1.10.0.jar \
  --class-path "out:out-tests" \
  --scan-class-path
```

## 📄 Detailed Report

The full project report — including truth tables, input domain models, control flow graphs, test case tables, and execution screenshots — is available here:

 **[CCSW323 Project Report (PDF)](report/CCSW323_Project_Report.pdf)**

 **[Project Presentation (PDF)](report/Project_Presentation.pdf)**

##  Authors

**Course:** CCSW 323 — Software Testing & Validation  
**Instructor:** Dr. Mona Altherwi  
**Institution:** University of Jeddah — Department of Software Engineering

- Enas Hamed Alqarni 
- Joury Ghorab 
- Jana Akkad 

## 🙏 Credits

The **Student Management System** application being tested in this repository was developed by [**Mohammed-3tef**](https://github.com/Mohammed-3tef). Full credit for the original codebase goes to them — this fork exists purely for academic testing purposes.

Original repository: https://github.com/Mohammed-3tef/Student_Management_System

##  License

This testing work was prepared for academic purposes as part of a university course. The original Student Management System follows the license of its upstream repository.
