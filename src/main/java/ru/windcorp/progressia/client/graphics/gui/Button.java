package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.BiConsumer;
import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.Colors;

public class Button extends BasicButton {

	public Button(String name, Label textLabel, BiConsumer<Button, Boolean> onClick, boolean addDefault) {
		super(name, textLabel, onClick);
		setPreferredSize(107, 34);
		if (addDefault)
		{
			addDefaultListener();
		}
	}
	
	public Button(String name, Label textLabel, BiConsumer<Button, Boolean> onClick)
	{
		this(name, textLabel, onClick, false);
	}

	public Button(String name, Label textLabel, boolean addDefault) {
		this(name, textLabel, (b,p) -> {
		},addDefault);
	}
	
	public Button(String name, Label textLabel)
	{
		this(name, textLabel, false);
	}

	@Override
	public void trigger(boolean isPress) {

	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		// Border
		if (isDisabled()) {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.DISABLED_GRAY);
		} else if (isClicked() || isHovered() || isFocused()) {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.BLUE);
		} else {
			target.fill(getX(), getY(), getWidth(), getHeight(), Colors.LIGHT_GRAY);
		}
		// Inside area
		if (!isClicked() && isHovered() && !isDisabled()) {
			target.fill(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, Colors.HOVER_BLUE);
		} else if (!isClicked() || isDisabled()) {
			target.fill(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, Colors.WHITE);
		}
		Font tempFont = new Font().withColor(Colors.BLACK);
		if (isDisabled()) {
			tempFont = tempFont.withColor(Colors.GRAY_A);
		} else if (isClicked()) {
			tempFont = tempFont.withColor(Colors.WHITE);
		}

		target.pushTransform(new Mat4().identity()
				.translate(getX() + .5f * getWidth() - .5f * getText().getPreferredSize().x, getY(), 0));
		getText().setFont(tempFont);
		getText().assembleSelf(target);
		target.popTransform();
	}
}
