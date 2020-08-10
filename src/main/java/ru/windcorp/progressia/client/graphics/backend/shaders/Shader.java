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
package ru.windcorp.progressia.client.graphics.backend.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.Locale;

import ru.windcorp.progressia.client.graphics.backend.OpenGLObjectTracker;
import ru.windcorp.progressia.client.graphics.backend.OpenGLObjectTracker.OpenGLDeletable;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class Shader implements OpenGLDeletable {
	
	public static enum ShaderType {
		VERTEX(GL_VERTEX_SHADER),
		FRAGMENT(GL_FRAGMENT_SHADER);
		
		private final int glCode;
		
		private ShaderType(int glCode) {
			this.glCode = glCode;
		}
		
		public int getGlCode() {
			return glCode;
		}
		
		public static ShaderType guessByResourceName(String resource) {
			resource = resource.toLowerCase(Locale.ENGLISH);
			
			if (resource.contains("vertex")) return VERTEX;
			if (resource.contains("fragment")) return FRAGMENT;
			if (resource.contains("vsh")) return VERTEX;
			if (resource.contains("fsh")) return FRAGMENT;
			
			throw new IllegalArgumentException(
					"Cannot deduce shader type from resource name \"" +
					resource + "\""
			);
		}
	}
	
	private static final String SHADER_ASSETS_PREFIX = "assets/shaders/";
	
	protected static Resource getShaderResource(String name) {
		return ResourceManager.getResource(SHADER_ASSETS_PREFIX + name);
	}
	
	private final int handle;
	private final ShaderType type;
	
	public Shader(ShaderType type, String source) {
		handle = glCreateShader(type.getGlCode());
		OpenGLObjectTracker.register(this);
		
		this.type = type;
		
		glShaderSource(handle, source);
		glCompileShader(handle);
		
		if (glGetShaderi(handle, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("***************** ERROR ******************");
			System.out.println(source);
			throw new RuntimeException("Bad shader:\n" + glGetShaderInfoLog(handle));
		}
	}
	
	public Shader(String resource) {
		this(
				ShaderType.guessByResourceName(resource),
				getShaderResource(resource).readAsString()
		);
	}
	
	@Override
	public void delete() {
		glDeleteShader(handle);
	}
	
	public int getHandle() {
		return handle;
	}
	
	public ShaderType getType() {
		return type;
	}

}