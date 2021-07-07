/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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
 */
package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.test.gen.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.test.gen.surface.SurfaceWorld;

public class TestBushFeature extends SurfaceTopLayerFeature {

	public TestBushFeature(String id) {
		super(id);
	}
	
	private void tryToSetLeaves(SurfaceWorld world, Vec3i posSfc, BlockData leaves) {
		if (world.getBlockSfc(posSfc).getId().equals("Test:Air")) {
			world.setBlockSfc(posSfc, leaves, false);
		}
	}
	
	@Override
	protected void processTopBlock(SurfaceWorld world, Request request, Vec3i topBlock) {
		if (request.getRandom().nextInt(10*10) > 0) return;
		
		Vec3i center = topBlock.add_(0, 0, 1);

		BlockData log = BlockDataRegistry.getInstance().get("Test:Log");
		BlockData leaves = BlockDataRegistry.getInstance().get("Test:TemporaryLeaves");
		
		world.setBlockSfc(center, log, false);
		
		VectorUtil.iterateCuboidAround(center.x, center.y, center.z, 3, 3, 3, p -> {
			tryToSetLeaves(world, p, leaves);
		});
		
		VectorUtil.iterateCuboidAround(center.x, center.y, center.z, 5, 5, 1, p -> {
			tryToSetLeaves(world, p, leaves);
		});
	}
	
	@Override
	protected boolean isSolid(SurfaceWorld world, Vec3i surfaceBlockInWorld) {
		return BlockLogicRegistry.getInstance().get(world.getBlockSfc(surfaceBlockInWorld).getId()).isSolid(RelFace.UP);
	}

}
