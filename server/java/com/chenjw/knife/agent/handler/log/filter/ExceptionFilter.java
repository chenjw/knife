package com.chenjw.knife.agent.handler.log.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.chenjw.knife.agent.Agent;
import com.chenjw.knife.agent.event.Event;

public class ExceptionFilter implements Filter {

	@Override
	public void doFilter(Event event, FilterChain chain) {
		try {
			chain.doFilter(event);
		} catch (Throwable t) {
			Agent.println("exception found, " + t.getClass().getName() + ":"
					+ t.getMessage());
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			String errorTrace = sw.toString();
			BufferedReader br = new BufferedReader(new StringReader(errorTrace));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					Agent.println(line);
				}
			} catch (IOException e1) {
			}
		}
	}

}
