/*******************************************************************************
 * Optica
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
package ru.windcorp.progressia.common.util;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author <a href="https://stackoverflow.com/users/37416/mike-houston">Mike
 *         Houston</a>, adapted by Javapony
 */
public class ByteBufferInputStream extends InputStream {

	private final ByteBuffer buffer;

	public ByteBufferInputStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public int read() {
		if (!buffer.hasRemaining()) {
			return -1;
		}
		return buffer.get() & 0xFF;
	}

	public int read(byte[] bytes, int off, int len) {
		if (!buffer.hasRemaining()) {
			return -1;
		}

		len = Math.min(len, buffer.remaining());
		buffer.get(bytes, off, len);
		return len;
	}

}