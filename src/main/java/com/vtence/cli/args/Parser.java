package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

import java.util.List;

public interface Parser
{
    List<String> parse(Args detected, Options options, Input args) throws ParsingException;
}
