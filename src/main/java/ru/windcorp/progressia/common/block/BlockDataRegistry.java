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
package ru.windcorp.progressia.common.block;

import java.util.HashMap;
import java.util.Map;

public class BlockDataRegistry {
	
	private static final Map<String, BlockData> REGISTRY = new HashMap<>();
	
	static {
		register(new BlockData("Test", "Grass"));
		register(new BlockData("Test", "Dirt"));
		register(new BlockData("Test", "Stone"));
		register(new BlockData("Test", "Air"));
		register(new BlockData("Test", "Glass"));
	}
	
	public static BlockData get(String name) {
		return REGISTRY.get(name);
	}
	
	public static void register(BlockData blockData) {
		REGISTRY.put(blockData.getId(), blockData);
	}

}