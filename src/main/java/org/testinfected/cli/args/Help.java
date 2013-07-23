package org.testinfected.cli.args;

import java.io.IOException;

public interface Help
{
    void printProgram(String name);

    void printVersion(String number);

    void printDescription(String description);

    void print(Option<?> option);

    void print(Operand<?> operand);

    void printEnding(String epilog);

    void appendTo(Appendable output) throws IOException;
}
