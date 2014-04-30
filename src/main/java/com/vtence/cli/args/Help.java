package com.vtence.cli.args;

import java.io.IOException;

public interface Help
{
    void setProgram(String name);

    void setVersion(String number);

    void setDescription(String description);

    void add(Option<?> option);

    void add(Operand<?> operand);

    void setEnding(String epilog);

    void printTo(Appendable output) throws IOException;
}
