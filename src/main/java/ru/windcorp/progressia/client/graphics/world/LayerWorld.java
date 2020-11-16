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
package ru.windcorp.progressia.client.graphics.world;

import java.util.ArrayList;
import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.comms.controls.InputBasedControls;
import ru.windcorp.progressia.client.graphics.Layer;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.collision.Collideable;
import ru.windcorp.progressia.common.collision.colliders.Collider;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.test.CollisionModelRenderer;
import ru.windcorp.progressia.test.TestPlayerControls;

public class LayerWorld extends Layer {
	
	private final WorldRenderHelper helper = new WorldRenderHelper();
	
	private final Client client;
	private final InputBasedControls inputBasedControls;
	private final TestPlayerControls tmp_testControls = TestPlayerControls.getInstance();

	public LayerWorld(Client client) {
		super("World");
		this.client = client;
		this.inputBasedControls = new InputBasedControls(client);
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void doValidate() {
		// Do nothing
	}
	
	@Override
	protected void doRender() {
		Camera camera = client.getCamera();
		if (camera.hasAnchor()) {
			renderWorld();
		}
		
		if (client.getLocalPlayer() != null) {
			client.getLocalPlayer().update(client.getWorld());
		}
	}

	private void renderWorld() {
		client.getCamera().apply(helper);
		FaceCulling.push(true);
		
		this.client.getWorld().render(helper);
		
		tmp_doEveryFrame();
		
		FaceCulling.pop();
		helper.reset();
	}
	
	private final Collider.ColliderWorkspace tmp_colliderWorkspace = new Collider.ColliderWorkspace();
	private final List<Collideable> tmp_collideableList = new ArrayList<>();
	
	private static final boolean RENDER_COLLISION_MODELS = false;
	
	private void tmp_doEveryFrame() {
		float tickLength = (float) GraphicsInterface.getFrameLength();
		
		try {
			tmp_performCollisions(tickLength);
			tmp_drawSelectionBox();
			
			tmp_testControls.applyPlayerControls();
			
			for (EntityData data : this.client.getWorld().getData().getEntities()) {
				tmp_applyFriction(data);
				tmp_applyGravity(data, tickLength);
				tmp_renderCollisionModel(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("OLEGSHA is to blame. Tell him he vry stupiDD!!");
			System.exit(31337);
		}
	}

	private void tmp_renderCollisionModel(EntityData entity) {
		if (RENDER_COLLISION_MODELS) {
			CollisionModelRenderer.renderCollisionModel(entity.getCollisionModel(), helper);
		}
	}

	private void tmp_performCollisions(float tickLength) {
		tmp_collideableList.clear();
		tmp_collideableList.addAll(this.client.getWorld().getData().getEntities());
		
		Collider.performCollisions(
				tmp_collideableList,
				this.client.getWorld().getData(),
				tickLength,
				tmp_colliderWorkspace
		);
	}

	private void tmp_drawSelectionBox() {
		LocalPlayer player = client.getLocalPlayer();
		if (player == null) return;
		
		Vec3i lookingAt = player.getLookingAt();
		if (lookingAt == null) return;
		
		helper.pushTransform().translate(lookingAt.x, lookingAt.y, lookingAt.z).scale(1.1f);
		CollisionModelRenderer.renderCollisionModel(client.getWorld().getData().getCollisionModelOfBlock(lookingAt), helper);
		helper.popTransform();
	}

	private void tmp_applyFriction(EntityData entity) {
		final float frictionCoeff = 1 - 1e-5f;
		entity.getVelocity().mul(frictionCoeff);
	}
	
	private void tmp_applyGravity(EntityData entity, float tickLength) {
		if (ClientState.getInstance().getLocalPlayer().getEntity() == entity && tmp_testControls.isFlying()) {
			return;
		}
		
		final float gravitationalAcceleration;
		
		if (tmp_testControls.useMinecraftGravity()) {
			gravitationalAcceleration = 32f * Units.METERS_PER_SECOND_SQUARED; // plz dont sue me M$
		} else {
			gravitationalAcceleration = 9.81f * Units.METERS_PER_SECOND_SQUARED;
		}
		entity.getVelocity().add(0, 0, -gravitationalAcceleration * tickLength);
	}

	@Override
	protected void handleInput(Input input) {
		if (input.isConsumed()) return;
		
		tmp_testControls.handleInput(input);
		
		if (!input.isConsumed()) {
			inputBasedControls.handleInput(input);
		}
	}

}
