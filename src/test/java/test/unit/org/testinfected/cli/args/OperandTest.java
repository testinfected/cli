package test.unit.org.testinfected.cli.args;

import org.junit.Test;
import org.testinfected.cli.args.Operand;

import static org.junit.Assert.assertEquals;

public class OperandTest {

    @Test public void
    acceptsAnOptionalDisplayName() {
        Operand operand = new Operand("file");
        operand.setDisplayName("FILENAME");
        assertEquals("argument", "FILENAME", operand.getDisplayName());
    }

    @Test public void
    displaysByDefaultAsUpperCaseName() {
        Operand operand = new Operand("file");
        assertEquals("argument", "FILE", operand.getDisplayName());
    }
}
