package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.BiConsumer;
import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;

public class RadioButton extends BasicButton {
	private RadioManager manager;
	private boolean isSelected;

	public RadioButton(String name, Label textLabel, BiConsumer<RadioButton,Boolean> onSelect, RadioManager manager, boolean addDefault) {
		super(name, textLabel, onSelect);
		setPreferredSize(getText().getPreferredSize().x + 23, getText().getPreferredSize().y);
		this.manager = manager;
		this.manager.addOption(this);
		if (addDefault)
		{
			addDefaultListener();
		}
	}

	public boolean isSelected() {
		return isSelected;
	}

	void setSelected(boolean selected) {
		isSelected = selected;
	}

	protected void assembleSelf(RenderTarget target) {
		if (isDisabled()) {
			target.fill(getX() + getWidth() - getHeight(), getY(), getHeight(), getHeight(), Colors.DISABLED_GRAY);
		} else if (isClicked() || isHovered() || isFocused()) {
			target.fill(getX() + getWidth() - getHeight(), getY(), getHeight(), getHeight(), Colors.BLUE);
		} else {
			target.fill(getX() + getWidth() - getHeight(), getY(), getHeight(), getHeight(), Colors.LIGHT_GRAY);
		}
		// Inside area
		if (!isClicked() && isHovered() && !isDisabled()) {
			target.fill(getX() + getWidth() - getHeight() + 2, getY() + 2, getHeight() - 4, getHeight() - 4,
					Colors.HOVER_BLUE);
		} else if (!isClicked() || isDisabled()) {
			target.fill(getX() + getWidth() - getHeight() + 2, getY() + 2, getHeight() - 4, getHeight() - 4,
					Colors.WHITE);
		}
		if (isSelected()) {
			if (!isDisabled()) {
				target.fill(getX() + getWidth() - getHeight() + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.BLUE);
			} else {
				target.fill(getX() + getWidth() - getHeight() + 4, getY() + 4, getHeight() - 8, getHeight() - 8,
						Colors.DISABLED_BLUE);
			}
		}

		target.pushTransform(new Mat4().identity().translate(getX(), getY(), 0));
		getText().assembleSelf(target);
		target.popTransform();
	}

	@Override
	public void trigger(boolean isPress) {
		if (!isPress)
		{
			manager.selectSelf(this);
		}
	}
}
