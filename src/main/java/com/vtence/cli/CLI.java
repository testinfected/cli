package com.vtence.cli;

import com.vtence.cli.args.Args;
import com.vtence.cli.args.CommandLine;
import com.vtence.cli.args.Help;
import com.vtence.cli.args.Operand;
import com.vtence.cli.args.OperandSpec;
import com.vtence.cli.args.Option;
import com.vtence.cli.args.OptionSpec;
import com.vtence.cli.args.Syntax;
import com.vtence.cli.coercion.BooleanCoercer;
import com.vtence.cli.coercion.ClassCoercer;
import com.vtence.cli.coercion.FileCoercer;
import com.vtence.cli.coercion.IntegerCoercer;
import com.vtence.cli.coercion.LocaleCoercer;
import com.vtence.cli.coercion.StringCoercer;
import com.vtence.cli.coercion.TypeCoercer;
import com.vtence.cli.gnu.GnuHelp;
import com.vtence.cli.gnu.GnuParser;
import com.vtence.cli.gnu.GnuSyntax;
import com.vtence.cli.args.Parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CLI
{
    private final CommandLine commandLine;
    private final Syntax syntax;
    private final Help help;
    private final Map<Class<?>, TypeCoercer<?>> typeCoercers = new HashMap<Class<?>, TypeCoercer<?>>();

    private Args detected = new Args();

    {
        coerceType(String.class).using(new StringCoercer());
        coerceType(Boolean.class).using(new BooleanCoercer());
        coerceType(Integer.class).using(new IntegerCoercer());
        coerceType(int.class).using(new IntegerCoercer());
        coerceType(Locale.class).using(new LocaleCoercer());
        coerceType(File.class).using(new FileCoercer());
        coerceType(Class.class).using(new ClassCoercer());
    }

    public CLI() {
        this(new GnuSyntax(), new GnuParser(), new GnuHelp());
    }

    public CLI(Syntax syntax, Parser parser, Help help) {
        this.commandLine = new CommandLine(parser);
        this.syntax = syntax;
        this.help = help;
    }

    public <T> CoercerDefinition<T> coerceType(Class<T> type) {
        return new CoercerDefinition<T>(type);
    }

    public class CoercerDefinition<T>
    {
        private Class<T> type;

        public CoercerDefinition(Class<T> type) {
            this.type = type;
        }

        public void using(TypeCoercer<? extends T> typeCoercer) {
            typeCoercers.put(type, typeCoercer);
        }
    }

    public void name(String program) {
        commandLine.setProgram(program);
    }

    public void version(String number) {
        commandLine.setVersion(number);
    }

    public void description(String desc) {
        commandLine.setDescription(desc);
    }

    public void epilog(String epilog) {
        commandLine.setEpilog(epilog);
    }

    public OptionSpec<String> option(String... definition) {
        return define(syntax.defineOption(definition).using(typeCoercers));
    }

    public OptionSpec<Boolean> flag(String... definition) {
        return define(syntax.defineOption(definition).using(typeCoercers).ofType(Boolean.class));
    }

    public OperandSpec<String> operand(String name) {
        return define(Operand.named(name).using(typeCoercers));
    }

    public OperandSpec<String> operand(String name, String displayName) {
        return operand(name).as(displayName);
    }

    public OperandSpec<String> operand(String name, String displayName, String help) {
        return operand(name, displayName).describedAs(help);
    }

    private <T> Option<T> define(Option<T> option) {
        commandLine.add(option);
        return option;
    }

    private <T> Operand<T> define(Operand<T> operand) {
        commandLine.add(operand);
        return operand;
    }

    public Args parse(String... args) throws ParsingException {
        detected = new Args(commandLine.parse(args));
        return detected;
    }

    public boolean has(String name) {
        return detected.has(name);
    }

    public <T> T get(String name) {
        return detected.get(name);
    }

    public Map<String, ?> options() {
        return detected.values();
    }

    public List<String> others() {
        return detected.others();
    }

    public void printHelp(Appendable appendable) throws IOException {
        commandLine.describeTo(help);
        help.printTo(appendable);
    }
}