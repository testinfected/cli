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

import org.testinfected.cli.args.ArgsFormat;
import org.testinfected.cli.args.gnu.GnuFormat;
import org.testinfected.cli.coercion.ClassCoercer;
import org.testinfected.cli.coercion.FileCoercer;
import org.testinfected.cli.coercion.IntegerCoercer;
import org.testinfected.cli.coercion.LocaleCoercer;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;
import org.testinfected.cli.option.Option;
import org.testinfected.cli.option.OptionBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CLI
{
    private final Map<Class<?>, TypeCoercer<?>> typeCoercers = new HashMap<Class<?>, TypeCoercer<?>>();
    private final ArgsFormat argsFormat;

    private final CommandLine commandLine = new CommandLine();

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

    public CLI(ArgsFormat argsFormat) {
        this.argsFormat = argsFormat;
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

    public void withBanner(String banner) {
        commandLine.setBanner(banner);
    }

    public void define(OptionBuilder builder) {
        defineOption(builder.make());
    }

    public OptionBuilder option(String name, String... schema) {
        OptionBuilder builder = OptionBuilder.option(argsFormat.defineOption(name, schema));
        return builder.using(typeCoercers);
    }

    private void defineOption(Option option) {
        commandLine.addOption(option);
    }

    public String[] parse(String... args) throws ParsingException {
        commandLine.parse(argsFormat, args);
        return commandLine.getOperands();
    }

    public String getParameter(int index) {
        return commandLine.getParameter(index);
    }

    public String[] getParameters() {
        return commandLine.getOperands();
    }

    public int getOperandCount() {
        return commandLine.getOperandCount();
    }

    public boolean hasOption(String name) {
        return commandLine.hasOptionValue(name);
    }

    public Map<String, ?> getOptions() {
        return commandLine.getOptionValues();
    }

    public Object getOption(String name) {
        return commandLine.getSingleOptionValue(name);
    }

    public void writeUsageTo(Appendable appendable) throws IOException {
        commandLine.formatHelp(argsFormat);
        argsFormat.appendTo(appendable);
    }
}