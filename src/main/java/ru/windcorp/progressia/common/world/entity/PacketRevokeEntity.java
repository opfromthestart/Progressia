package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketRevokeEntity extends PacketAffectEntity {
	
	public PacketRevokeEntity() {
		this("Core:RevokeEntity");
	}

	protected PacketRevokeEntity(String id) {
		super(id);
	}
	
	@Override
	public void set(long entityId) {
		super.set(entityId);
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
	}

	@Override
	public void apply(WorldData world) {
		world.removeEntity(getEntityId());
	}

}
