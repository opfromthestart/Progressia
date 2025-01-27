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
 
package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.util.MultiLOC;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.context.impl.ReportingServerContext;
import ru.windcorp.progressia.server.world.ticking.TickerTask;

public class WorldAccessor implements ReportingServerContext.ChangeListener {

	private final MultiLOC cache;
	{
		MultiLOC mloc = new MultiLOC();
		Consumer<TickerTask> disposer = mloc::release;

		cache = mloc
			.addClass(SetBlock.class, () -> new SetBlock(disposer))
			.addClass(AddTile.class, () -> new AddTile(disposer))
			.addClass(RemoveTile.class, () -> new RemoveTile(disposer))
			.addClass(ChangeEntity.class, () -> new ChangeEntity(disposer))

			.addClass(BlockTriggeredUpdate.class, () -> new BlockTriggeredUpdate(disposer))
			.addClass(TileTriggeredUpdate.class, () -> new TileTriggeredUpdate(disposer));
	}

	private final Server server;

	public WorldAccessor(Server server) {
		this.server = server;
	}

	@Override
	public void onBlockSet(Vec3i blockInWorld, BlockData block) {
		SetBlock change = cache.grab(SetBlock.class);
		change.getPacket().set(block, blockInWorld);
		server.requestChange(change);
	}

	@Override
	public void onTileAdded(Vec3i blockInWorld, RelFace face, TileData tile) {
		AddTile change = cache.grab(AddTile.class);
		change.getPacket().set(tile, blockInWorld, face.resolve(AbsFace.POS_Z));
		server.requestChange(change);
	}

	@Override
	public void onTileRemoved(Vec3i blockInWorld, RelFace face, int tag) {
		RemoveTile change = cache.grab(RemoveTile.class);
		change.getPacket().set(blockInWorld, face.resolve(AbsFace.POS_Z), tag);
		server.requestChange(change);
	}
	
	@Override
	public void onEntityAdded(EntityData entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onEntityRemoved(long entityId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTimeChanged(float change) {
		// TODO Auto-generated method stub
		System.err.println("WorldAccessor.onTimeChanged() NYI!");
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void onEntityChanged(
		SE entity,
		StateChange<SE> stateChange
	) {
		ChangeEntity change = cache.grab(ChangeEntity.class);
		change.set((EntityData) entity, stateChange);
		server.requestChange(change);
	}

	public void tickBlock(Vec3i blockInWorld) {
		// TODO
		System.err.println("WorldAccessor.tickBlock(Vec3i) NYI!");
	}

	/**
	 * When a block is the trigger
	 * 
	 * @param blockInWorld
	 */
	// TODO rename to something meaningful
	public void triggerUpdates(Vec3i blockInWorld) {
		BlockTriggeredUpdate evaluation = cache.grab(BlockTriggeredUpdate.class);
		evaluation.init(blockInWorld);
		server.requestEvaluation(evaluation);
	}

	/**
	 * When a tile is the trigger
	 * 
	 * @param blockInWorld
	 * @param face
	 */
	// TODO rename to something meaningful
	public void triggerUpdates(Vec3i blockInWorld, BlockFace face) {
		TileTriggeredUpdate evaluation = cache.grab(TileTriggeredUpdate.class);
		evaluation.init(blockInWorld, face.resolve(AbsFace.POS_Z));
		server.requestEvaluation(evaluation);
	}

}
