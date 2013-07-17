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

import org.testinfected.cli.args.CommandLine;
import org.testinfected.cli.args.Format;
import org.testinfected.cli.args.Operand;
import org.testinfected.cli.args.OperandBuilder;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionBuilder;
import org.testinfected.cli.coercion.ClassCoercer;
import org.testinfected.cli.coercion.FileCoercer;
import org.testinfected.cli.coercion.IntegerCoercer;
import org.testinfected.cli.coercion.LocaleCoercer;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;
import org.testinfected.cli.gnu.GnuFormat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CLI
{
    private final Map<Class<?>, TypeCoercer<?>> typeCoercers = new HashMap<Class<?>, TypeCoercer<?>>();
    private final Format format;

    private final CommandLine commandLine;

    {
        coerceType(String.class).using(new StringCoercer());
        coerceType(Integer.class).using(new IntegerCoercer());
        coerceType(int.class).using(new IntegerCoercer());
        coerceType(Locale.class).using(new LocaleCoercer());
        coerceType(File.class).using(new FileCoercer());
        coerceType(Class.class).using(new ClassCoercer());
    }

    public CLI() {
        this(new GnuFormat());
    }

    public CLI(Format format) {
        this.format = format;
        this.commandLine = new CommandLine();
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

    public void define(OptionBuilder builder) {
        defineOption(builder.make());
    }

    public void define(OperandBuilder builder) {
        defineOperand(builder.make());
    }

    public OptionBuilder option(String name, String... schema) {
        return OptionBuilder.option(format.defineOption(name, schema)).using(typeCoercers);
    }

    public OperandBuilder operand(String name) {
        return OperandBuilder.operand(name).using(typeCoercers);
    }

    public OperandBuilder operand(String name, String displayName) {
        return operand(name).as(displayName);
    }

    public OperandBuilder operand(String name, String displayName, String help) {
        return operand(name, displayName).help(help);
    }

    private void defineOption(Option option) {
        commandLine.addOption(option);
    }

    private void defineOperand(Operand operand) {
        commandLine.addOperand(operand);
    }

    public String[] parse(String... args) throws ParsingException {
        return commandLine.parse(format, args);
    }

    public <T> T getOperand(String name) {
        return commandLine.getOperandValue(name);
    }

    public boolean hasOption(String name) {
        return commandLine.hasOptionValue(name);
    }

    public Map<String, ?> getOptions() {
        return commandLine.getOptionValues();
    }

    public int getOptionCount() {
        return getOptions().size();
    }

    public <T> T getOption(String name) {
        return commandLine.getOptionValue(name);
    }

    public void printHelp(Appendable appendable) throws IOException {
        commandLine.printHelp(format);
        format.appendTo(appendable);
    }
}