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
package ru.windcorp.progressia.server.world.context.impl;

import java.util.Collection;
import java.util.Random;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TileLogicStack;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.tile.TileLogic;

class DefaultServerContextImpl extends DefaultServerContext
	implements DefaultServerContextBuilders.Empty, DefaultServerContextBuilders.WithWorld,
	DefaultServerContextBuilders.WithLocation, DefaultServerContextBuilders.WithTileStack {

	/*
	 * STATE MANAGEMENT & UTIL
	 */

	public DefaultServerContextImpl() {
		reuse();
	}

	/**
	 * The relevant {@link Server} instance. If this is {@code null}, the role
	 * is {@link Role#NONE}.
	 */
	protected Server server;

	/**
	 * The relevant {@link WorldLogic} instance. If this is {@code null}, the
	 * role is {@link Role#NONE}.
	 */
	protected WorldLogic worldLogic;

	/**
	 * The {@link WorldData} accessible through {@link #worldLogic}. This field
	 * is kept always in sync with {@link #worldLogic}.
	 */
	protected WorldData worldData;

	/**
	 * The {@link Random} instance exposed with {@link #getRandom()}.
	 */
	protected final Random random = new Random();

	/**
	 * Determines whether this object currently acts as a builder or a context.
	 */
	protected boolean isBuilder;

	/**
	 * Counts the instances of subcontexting that are currently active. This
	 * value increases by 1 when subcontexting begins and decreases by 1 when it
	 * ends. This is always 0 when the object is a builder.
	 */
	protected int subcontextDepth = 0;

	/**
	 * The Logic view returned by {@link #logic()}.
	 */
	protected final DefaultServerContextImpl.Logic logic = new Logic();

	/**
	 * Returns the Role currently assumed by this object.
	 * 
	 * @return the role
	 */
	@Override
	public Role getRole() {
		if (server == null)
			return Role.NONE;
		if (frame == null)
			return Role.WORLD;
		if (frame.face == null)
			return Role.LOCATION;
		if (frame.layer == -1)
			return Role.TILE_STACK;
		return Role.TILE;
	}

	/**
	 * Throws an {@link IllegalStateException} iff this object does not conform
	 * to the specified role. The object must not be a builder to pass the
	 * check.
	 * 
	 * @param role the required role
	 * @return {@code true} (for convenience with {@code assert})
	 * @throws IllegalStateException when the check fails
	 */
	public boolean requireContextRole(Role role) throws IllegalStateException {

		boolean ok = !isBuilder && getRole().compareTo(role) <= 0;
		if (!ok) {
			complainAboutIllegalState(role, false);
		}
		return true;

	}

	/**
	 * Throws an {@link IllegalStateException} iff this object does not conform
	 * to the specified role. The object must be a builder to pass the check. If
	 * {@code role} is {@code null}, any role except {@link Role#NONE} passes.
	 * 
	 * @param role the required role or {@code null}, see above
	 * @return {@code true} (for convenience with {@code assert})
	 * @throws IllegalStateException when the check fails
	 */
	public boolean requireBuilderRole(Role role) {

		boolean ok = isBuilder && role == null ? (getRole() != Role.NONE) : (getRole() == role);
		if (!ok) {
			complainAboutIllegalState(role, true);
		}
		return true;

	}

	private void complainAboutIllegalState(Role role, boolean builder) {
		throw new IllegalStateException(
			"Required " + (builder ? "builder for" : "context") + " " + role
				+ ", but I am currently " + this
		);
	}

	@Override
	public String toString() {
		String result;

		switch (getRole()) {
		case TILE:
			result = String.format(
				"ServerTileContext[x=%d, y=%d, z=%d, %s, index=%d]",
				frame.location.x,
				frame.location.y,
				frame.location.z,
				frame.face,
				frame.layer
			);
			break;
		case TILE_STACK:
			result = String
				.format("ServerBlockFaceContext[x=%d, y=%d, z=%d, %s]", frame.location.x, frame.location.y, frame.location.z, frame.face);
			break;
		case LOCATION:
			result = String.format("ServerBlockContext[x=%d, y=%d, z=%d]", frame.location.x, frame.location.y, frame.location.z);
			break;
		case WORLD:
			result = String.format("ServerWorldContext");
			break;
		default:
			result = "Uninitialized ReusableServerContext";
			break;
		}

		if (isBuilder) {
			result = "Builder for " + result;
		}

		return result;
	}

	/*
	 * RSC INTERFACE
	 */

	@Override
	public Empty reuse() {

		server = null;
		worldLogic = null;
		worldData = null;
		
		while (isSubcontexting()) {
			pop();
		}

		isBuilder = true;

		return this;

	}

	/*
	 * BUILDER INTERFACE
	 */

	@Override
	public DefaultServerContext build() {
		assert requireBuilderRole(null);
		isBuilder = false;
		return this;
	}

	/*
	 * Empty
	 */

	@Override
	public WithWorld in(Server server, WorldLogic world) {
		requireBuilderRole(Role.NONE);
		this.server = server;
		this.worldLogic = world;
		this.worldData = world.getData();
		return this;
	}

	/*
	 * WithWorld
	 */

	@Override
	public WithLocation at(Vec3i location) {
		requireBuilderRole(Role.WORLD);
		push(location);
		return this;
	}

	/*
	 * WithLocation
	 */

	@Override
	public WithTileStack on(RelFace side) {
		requireBuilderRole(Role.LOCATION);
		frame.face = side;
		return this;
	}

	@Override
	public WithTileStack on(BlockFace side) {
		requireBuilderRole(Role.LOCATION);
		frame.face = side.relativize(worldLogic.getData().getUp(frame.location));
		return this;
	}

	/*
	 * WithTileStack
	 */

	@Override
	public DefaultServerContext index(int index) {
		requireBuilderRole(Role.TILE_STACK);
		frame.layer = index;
		return build();
	}

	/*
	 * LOCATION GETTERS
	 */

	@Override
	public Server getServer() {
		assert requireContextRole(Role.WORLD);
		return server;
	}

	@Override
	public Vec3i getLocation() {
		assert requireContextRole(Role.LOCATION);
		return frame.location;
	}

	@Override
	public RelFace getFace() {
		assert requireContextRole(Role.TILE_STACK);
		return frame.face;
	}

	@Override
	public int getLayer() {
		assert requireContextRole(Role.TILE);
		return frame.layer;
	}

	/*
	 * RO CONTEXT INTERFACE
	 */

	@Override
	public boolean isReal() {
		assert requireContextRole(Role.WORLD);
		return true;
	}

	@Override
	public Random getRandom() {
		assert requireContextRole(Role.WORLD);
		return random;
	}

	@Override
	public double getTickLength() {
		assert requireContextRole(Role.WORLD);
		return server.getTickLength();
	}

	@Override
	public BlockData getBlock(Vec3i location) {
		assert requireContextRole(Role.WORLD);
		return worldData.getBlock(location);
	}

	@Override
	public boolean isLocationLoaded(Vec3i location) {
		assert requireContextRole(Role.WORLD);
		return worldData.isLocationLoaded(location);
	}

	@Override
	public TileData getTile(Vec3i location, BlockFace face, int layer) {
		assert requireContextRole(Role.WORLD);
		return worldData.getTile(location, face, layer);
	}

	@Override
	public boolean hasTile(Vec3i location, BlockFace face, int layer) {
		assert requireContextRole(Role.WORLD);
		return worldData.hasTile(location, face, layer);
	}

	@Override
	public TileData getTileByTag(Vec3i location, BlockFace face, int tag) {
		assert requireContextRole(Role.WORLD);
		TileDataStack stack = worldData.getTilesOrNull(location, face);
		if (stack == null)
			return null;
		int layer = stack.getIndexByTag(tag);
		if (layer == -1)
			return null;
		return stack.get(layer);
	}

	@Override
	public boolean isTagValid(Vec3i location, BlockFace face, int tag) {
		assert requireContextRole(Role.WORLD);
		TileDataStack stack = worldData.getTilesOrNull(location, face);
		if (stack == null)
			return false;
		return stack.getIndexByTag(tag) != -1;
	}

	@Override
	public int getTag() {
		assert requireContextRole(Role.TILE);
		TileDataStack stack = worldData.getTilesOrNull(frame.location, frame.face);
		if (stack == null)
			return -1;
		return stack.getTagByIndex(frame.layer);
	}

	@Override
	public int getTileCount(Vec3i location, BlockFace face) {
		assert requireContextRole(Role.TILE_STACK);
		TileDataStack stack = worldData.getTilesOrNull(frame.location, frame.face);
		if (stack == null)
			return 0;
		return stack.size();
	}

	@Override
	public Collection<EntityData> getEntities() {
		assert requireContextRole(Role.WORLD);
		return worldData.getEntities();
	}

	@Override
	public EntityData getEntity(long entityId) {
		assert requireContextRole(Role.WORLD);
		return worldData.getEntity(entityId);
	}

	@Override
	public GravityModel getGravityModel() {
		assert requireContextRole(Role.WORLD);
		return worldData.getGravityModel();
	}

	@Override
	public float getTime() {
		assert requireContextRole(Role.WORLD);
		return worldData.getTime();
	}

	/*
	 * RW CONTEXT INTERFACE
	 */

	@Override
	public boolean isImmediate() {
		assert requireContextRole(Role.WORLD);
		return true;
	}

	@Override
	public void setBlock(Vec3i blockInWorld, BlockData block) {
		assert requireContextRole(Role.WORLD);
		worldData.setBlock(blockInWorld, block, true);
	}

	@Override
	public void addTile(Vec3i location, BlockFace face, TileData tile) {
		assert requireContextRole(Role.WORLD);
		worldData.getTiles(location, face).addFarthest(tile);
	}

	@Override
	public void removeTile(Vec3i location, BlockFace face, int tag) {
		assert requireContextRole(Role.WORLD);
		TileDataStack stack = worldData.getTilesOrNull(location, face);
		if (stack == null)
			return;
		int layer = stack.getIndexByTag(tag);
		if (layer == -1)
			return;
		stack.remove(layer);
	}

	@Override
	public void addEntity(EntityData entity) {
		assert requireContextRole(Role.WORLD);
		worldData.addEntity(entity);
	}

	@Override
	public void removeEntity(long entityId) {
		assert requireContextRole(Role.WORLD);
		worldData.removeEntity(entityId);
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		assert requireContextRole(Role.WORLD);
		worldData.changeEntity(entity, change);
	}

	@Override
	public void advanceTime(float change) {
		assert requireContextRole(Role.WORLD);
		worldData.advanceTime(change);
	}

	/*
	 * ServerWorldContext.Logic STUFF
	 */

	private class Logic implements ServerTileContext.Logic {

		/*
		 * LOCATION GETTERS
		 */

		@Override
		public Server getServer() {
			return server;
		}

		@Override
		public Vec3i getLocation() {
			return frame.location;
		}

		@Override
		public RelFace getFace() {
			return frame.face;
		}

		@Override
		public int getLayer() {
			return frame.layer;
		}
		
		/*
		 * RO CONTEXT INTERFACE
		 */
		
		@Override
		public boolean isReal() {
			return true;
		}

		@Override
		public Random getRandom() {
			return random;
		}

		@Override
		public double getTickLength() {
			return server.getTickLength();
		}

		@Override
		public BlockLogic getBlock(Vec3i location) {
			assert requireContextRole(Role.WORLD);
			return worldLogic.getBlock(location);
		}

		@Override
		public boolean isLocationLoaded(Vec3i location) {
			return worldData.isLocationLoaded(location);
		}

		@Override
		public boolean hasTile(Vec3i location, BlockFace face, int layer) {
			return worldData.hasTile(location, face, layer);
		}

		@Override
		public boolean isTagValid(Vec3i location, BlockFace face, int tag) {
			return DefaultServerContextImpl.this.isTagValid(location, face, tag);
		}

		@Override
		public TileLogic getTile(Vec3i location, BlockFace face, int layer) {
			assert requireContextRole(Role.WORLD);
			return worldLogic.getTile(location, face, layer);
		}

		@Override
		public TileLogic getTileByTag(Vec3i location, BlockFace face, int tag) {
			assert requireContextRole(Role.WORLD);
			TileLogicStack stack = worldLogic.getTilesOrNull(location, face);
			if (stack == null) {
				return null;
			}
			int layer = stack.getIndexByTag(tag);
			if (layer == -1) {
				return null;
			}
			return stack.get(layer);
		}

		@Override
		public int getTileCount(Vec3i location, BlockFace face) {
			assert requireContextRole(Role.WORLD);
			TileLogicStack stack = worldLogic.getTilesOrNull(location, face);
			if (stack == null) {
				return 0;
			}
			return stack.size();
		}

		@Override
		public int getTag() {
			return DefaultServerContextImpl.this.getTag();
		}

		@Override
		public Collection<EntityData> getEntities() {
			return worldLogic.getEntities();
		}

		@Override
		public EntityData getEntity(long entityId) {
			return worldLogic.getEntity(entityId);
		}
		
		/*
		 * Subcontexting
		 */
		
		@Override
		public Logic push(Vec3i location) {
			DefaultServerContextImpl.this.push(location);
			return this;
		}
		
		@Override
		public Logic push(Vec3i location, RelFace face) {
			DefaultServerContextImpl.this.push(location, face);
			return this;
		}
		
		@Override
		public Logic push(Vec3i location, RelFace face, int layer) {
			DefaultServerContextImpl.this.push(location, face, layer);
			return this;
		}
		
		@Override
		public void pop() {
			DefaultServerContextImpl.this.pop();
		}
		
		/*
		 * MISC
		 */

		@Override
		public DefaultServerContext data() {
			return DefaultServerContextImpl.this;
		}

		@Override
		public String toString() {
			return DefaultServerContextImpl.this + ".Logic";
		}
	}

	@Override
	public Logic logic() {
		assert requireContextRole(Role.WORLD);
		return logic;
	}

}