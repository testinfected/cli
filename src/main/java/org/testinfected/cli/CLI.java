/*
 * Copyright (c) 2006 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */

package org.testinfected.cli;

import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.CommandLine;
import org.testinfected.cli.args.Help;
import org.testinfected.cli.args.Operand;
import org.testinfected.cli.args.OperandSpec;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionSpec;
import org.testinfected.cli.args.Parser;
import org.testinfected.cli.args.Syntax;
import org.testinfected.cli.coercion.ClassCoercer;
import org.testinfected.cli.coercion.FileCoercer;
import org.testinfected.cli.coercion.IntegerCoercer;
import org.testinfected.cli.coercion.LocaleCoercer;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;
import org.testinfected.cli.gnu.GnuHelp;
import org.testinfected.cli.gnu.GnuParser;
import org.testinfected.cli.gnu.GnuSyntax;

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

        public void using(TypeCoercer<T> typeCoercer) {
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

    public void ending(String epilog) {
        commandLine.setEnding(epilog);
    }

    public OptionSpec option(String name, String... definition) {
        return define(syntax.defineOption(name, definition).using(typeCoercers));
    }

    public OperandSpec operand(String name) {
        return define(Operand.named(name).using(typeCoercers));
    }

    public OperandSpec operand(String name, String displayName) {
        return operand(name).as(displayName);
    }

    public OperandSpec operand(String name, String displayName, String help) {
        return operand(name, displayName).describedAs(help);
    }

    private Option define(Option option) {
        commandLine.add(option);
        return option;
    }

    private Operand define(Operand option) {
        commandLine.add(option);
        return option;
    }

    public List<String> parse(String... args) throws ParsingException {
        detected = new Args(commandLine.parse(args));
        return detected.others();
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
        commandLine.printTo(help);
        help.appendTo(appendable);
    }
}