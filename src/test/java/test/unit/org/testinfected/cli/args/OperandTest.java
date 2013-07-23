package test.unit.org.testinfected.cli.args;

import org.junit.Test;
import org.testinfected.cli.args.Operand;

import static org.junit.Assert.assertEquals;

public class OperandTest {

    @Test public void
    acceptsAnOptionalDisplayName() {
        Operand<String> operand = Operand.named("file");
        operand.as("FILENAME");
        assertEquals("argument", "FILENAME", operand.getDisplayName());
    }

    @Test public void
    displaysByDefaultAsUpperCaseName() {
        Operand<String> operand = Operand.named("file");
        assertEquals("argument", "FILE", operand.getDisplayName());
    }
}
