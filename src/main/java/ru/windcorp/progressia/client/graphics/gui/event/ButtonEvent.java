package ru.windcorp.progressia.client.graphics.gui.event;

import ru.windcorp.progressia.client.graphics.gui.BasicButton;

public class ButtonEvent extends ComponentEvent{
	private boolean isPress;
	
	public ButtonEvent(BasicButton button, boolean isPress)
	{
		super(button);
		this.isPress = isPress;
	}
	
	public boolean isPress()
	{
		return isPress;
	}
	
	public boolean isRelease()
	{
		return !isPress;
	}
}
