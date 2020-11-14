package ru.windcorp.progressia.client.world.entity;

import java.io.IOException;

import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.NamespacedRegistry;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class EntityRenderRegistry extends NamespacedRegistry<EntityRender> {

	private static final EntityRenderRegistry INSTANCE =
			new EntityRenderRegistry();
	
	public static EntityRenderRegistry getInstance() {
		return INSTANCE;
	}

	public static TexturePrimitive getEntityTexture(String name) {
		try {
			return new TexturePrimitive(
					TextureLoader.loadPixels(
							ResourceManager.getTextureResource(
									"entities/" + name
							),
							new TextureSettings(false)
					).getData()
			);
		} catch (IOException e) {
			CrashReports.report(e, "__DOC__ME__");
			return null;
		}
	}

}
