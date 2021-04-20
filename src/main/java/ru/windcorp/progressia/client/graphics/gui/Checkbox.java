package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.BiConsumer;
import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.input.InputEvent;

public class Checkbox extends BasicButton {

	private boolean isActive;

	public <T extends InputEvent> Checkbox(String name, Label textLabel, BiConsumer<Checkbox, Boolean> onChange,
			boolean addDefault) {
		super(name, textLabel, onChange, new LayoutAlign(0, .5, 1));
		if (addDefault) {
			addDefaultListener();
		}
	}

	public <T extends InputEvent> Checkbox(String name, Label textLabel, BiConsumer<Checkbox, Boolean> onChange) {
		this(name, textLabel, onChange, false);
	}

	public boolean isActive() {
		return isActive;
	}

	@Override
	public void trigger(boolean isPress) {
		if (!isPress) {
			isActive = !isActive;
		}
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		// Border
		if (isDisabled()) {
			target.fill(getX() + getText().getPreferredSize().x, getY(), getWidth() - getText().getPreferredSize().x,
					getHeight(), Colors.DISABLED_GRAY);
		} else if (isClicked() || isHovered() || isFocused()) {
			target.fill(getX() + getText().getPreferredSize().x, getY(), getWidth() - getText().getPreferredSize().x,
					getHeight(), Colors.BLUE); // blue
		} else {
			target.fill(getX() + getText().getPreferredSize().x, getY(), getWidth() - getText().getPreferredSize().x,
					getHeight(), Colors.LIGHT_GRAY);
		}
		// Inside area
		if (!isClicked() && isHovered() && !isDisabled()) {
			target.fill(getX() + 2 + getText().getPreferredSize().x, getY() + 2,
					getWidth() - getText().getPreferredSize().x - 4, getHeight() - 4, Colors.HOVER_BLUE); // light
			// blue
		} else if (!isClicked() || isDisabled()) {
			target.fill(getX() + 2 + getText().getPreferredSize().x, getY() + 2,
					getWidth() - getText().getPreferredSize().x - 4, getHeight() - 4, Colors.WHITE);
		}
		if (isActive() && !isClicked()) {
			if (isDisabled()) {
				target.fill(getX() + getWidth() - getHeight() + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.DISABLED_BLUE);
			} else {
				target.fill(getX() + getWidth() - getHeight() + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.BLUE); // blue
			}
		} else if (!isClicked()) {
			if (isDisabled()) {
				target.fill(getX() + getText().getPreferredSize().x + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.DISABLED_GRAY);
			} else if (isFocused() || isHovered()) {
				target.fill(getX() + getText().getPreferredSize().x + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.BLUE); // blue
			} else {
				target.fill(getX() + getText().getPreferredSize().x + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.LIGHT_GRAY);
			}
			target.fill(getX() + getText().getPreferredSize().x + 6, getY() + 6, getHeight() - 12, getHeight() - 12,
					Colors.WHITE);
		}
		target.pushTransform(new Mat4().identity().translate(getX(), getY(), 0));
		getText().assembleSelf(target);
		target.popTransform();
	}
}
