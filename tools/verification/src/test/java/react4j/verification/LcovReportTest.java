package react4j.verification;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class LcovReportTest {
    @Test
    public void countersRoundTripDeterministically() {
        var source = new LcovReport.Source(
                Map.of(8, 2L, 3, 0L),
                Map.of(new LcovReport.Branch(8, 2, 1), 1L, new LcovReport.Branch(8, 2, 0), 0L),
                Map.of("Owner::method ()V", new LcovReport.Function(3, 2)));
        String report = LcovReport.write(Map.of("root/Owner.java", source));
        assertEquals(LcovReport.read(report), Map.of("root/Owner.java", source));
        assertEquals(LcovReport.write(LcovReport.read(report)), report);
        assertTrue(report.indexOf("DA:3,0") < report.indexOf("DA:8,2"));
        assertEquals(
                LcovReport.read(report.replace("BRDA:8,2,0,0", "BRDA:8,2,0,-")), Map.of("root/Owner.java", source));
        assertEquals(
                LcovReport.read("TN:test\n" + report.replace("root/Owner", "root\\Owner")),
                Map.of("root/Owner.java", source));
    }

    @Test
    public void rejectsIncompleteOrInconsistentExecutionEvidence() {
        String valid = LcovReport.write(Map.of(
                "root/Owner.java",
                new LcovReport.Source(
                        Map.of(1, 1L),
                        Map.of(new LcovReport.Branch(1, 0, 0), 0L),
                        Map.of("Owner::method ()V", new LcovReport.Function(1, 1)))));
        for (String invalid : List.of(
                valid + valid,
                valid.replace("SF:root/Owner.java", "SF:"),
                valid.replace("SF:root/Owner.java", ""),
                valid.replace("DA:1,1", "DA:1,1\nDA:1,0"),
                valid.replace("DA:1,1", "DA:0,1"),
                valid.replace("DA:1,1", "DA:1,-1"),
                valid.replace("DA:1,1", "DA:1"),
                valid.replace("LF:1", "LF:2"),
                valid.replace("LH:1", "LH:0"),
                valid.replace("BRF:1", "BRF:0"),
                valid.replace("BRH:0", "BRH:1"),
                valid.replace("FNDA:1,Owner::method ()V", ""),
                valid.replace("FN:1,Owner::method ()V", ""),
                valid.replace("BRDA:1,0,0,0", "BRDA:1,0,0"),
                valid.replace("BRDA:1,0,0,0", "BRDA:1,0,0,-2"),
                valid.replace("FNF:1", "FNF:0"),
                valid.replace("FNH:1", "FNH:0"),
                valid.replace("LF:1\n", ""),
                valid.replace("BRF:1\n", ""),
                valid.replace("FNF:1\n", ""))) {
            assertThrows(IllegalArgumentException.class, () -> LcovReport.read(invalid));
        }
    }
}
