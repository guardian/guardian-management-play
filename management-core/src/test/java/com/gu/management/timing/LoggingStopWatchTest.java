/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gu.management.timing;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoggingStopWatchTest {

	@Mock private Logger log;

	@Test public void testShouldLogAtTraceLevelOnStartSoItIsSwichedOffOnProductionAndDevMachinesByDefault() throws Exception {
		when(log.isTraceEnabled()).thenReturn(true);

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Done something");
		stopWatch.start();

		verify(log).trace("Done something");
	}

	@Test public void testShouldLogAtInfoLevelOnStopByDefaultSoItIsSwichedOnEverywhereByDefault() throws Exception {
		when(log.isEnabledFor(Level.INFO)).thenReturn(true);

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Done something");
		stopWatch.start();
		stopWatch.stop();

		verify(log).log(Level.INFO, String.format("Done something completed in %d ms", stopWatch.getTime()));
	}

	@Test public void testShouldLogAtSpecifiedLevelOnStop() throws Exception {
		when(log.isEnabledFor(Level.TRACE)).thenReturn(true);

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Done something", Level.TRACE);
		stopWatch.start();
		stopWatch.stop();

		verify(log).log(Level.TRACE, String.format("Done something completed in %d ms", stopWatch.getTime()));
	}

	@Test public void testShouldLogAtStartAndStopIfRunningACallable() throws Exception {
		when(log.isTraceEnabled()).thenReturn(true);
		when(log.isEnabledFor(Level.INFO)).thenReturn(true);

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Simple test");

		stopWatch.executeAndLog(new Callable<Object>() {
			public Object call() throws Exception {
				return null;
			}
		});

		verify(log).trace("Simple test");
		verify(log).log(Level.INFO, String.format("Simple test completed in %d ms", stopWatch.getTime()));
	}

	@Test public void testShouldLogOnFailure() throws Exception {
		when(log.isInfoEnabled()).thenReturn(true);

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Error test");

		try {
			stopWatch.executeAndLog(new Callable<Object>() {
				public Object call() throws Exception {
					throw new UnsupportedLookAndFeelException("I don't like your banter");
				}
			});
		} catch (UnsupportedLookAndFeelException e) {
			// ignore
		}

		verify(log).warn(Mockito.eq(String.format("Error test caused exception after %d ms", stopWatch.getTime())),
		        Mockito.isA(UnsupportedLookAndFeelException.class));
	}

	@Test
	public void testShouldLogAndUpdateTheMetric() throws Exception {
		TimingMetric metric = new TimingMetric();

		LoggingStopWatch stopWatch = new LoggingStopWatch(log, "Error test", metric );
		stopWatch.executeAndLogWithMetricUpdate(new Callable<Object>() {
			public Object call() throws Exception {
				return null;
			}
		});

		assertEquals(1, metric.getCount());
		assertEquals(stopWatch.getTime(), metric.getTotalTimeInMillis());
	}

}