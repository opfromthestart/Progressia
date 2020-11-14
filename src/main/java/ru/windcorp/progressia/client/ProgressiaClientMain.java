/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package ru.windcorp.progressia.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.windcorp.progressia.ProgressiaLauncher;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.util.crash.analyzers.OutOfMemoryAnalyzer;
import ru.windcorp.progressia.common.util.crash.providers.OSContextProvider;

public class ProgressiaClientMain {

	private static final Logger logger = LogManager.getLogger(ProgressiaClientMain.class.getName());

	public static void main(String[] args) {
		logger.info("App started!");

		CrashReports.registerProvider(new OSContextProvider());
		CrashReports.registerAnalyzer(new OutOfMemoryAnalyzer());
		try {
			@SuppressWarnings("unused")
			long[] ssdss = new long[1 << 30];
		} catch (Throwable t) {
			CrashReports.report(t, "u %s stupid", "vry");
		}

		ProgressiaLauncher.launch(args, new ClientProxy());
	}

}