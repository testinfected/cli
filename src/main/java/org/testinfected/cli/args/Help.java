package org.testinfected.cli.args;

import java.io.IOException;

public interface Help
{
    void displayProgram(String name);

    void displayVersion(String number);

    void displayDescription(String description);

    void displayEnding(String epilog);

    void displayOption(Option option);

    void appendTo(Appendable output) throws IOException;

    void displayOperand(Operand operand);
}
