package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.graphics.gui.event.ButtonEvent;
import ru.windcorp.progressia.client.graphics.gui.event.FocusEvent;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;

public abstract class BasicButton extends Component {

	protected boolean isDisabled;
	protected boolean isClicked;
	private Label label;
	private LayoutAlign align;

	public <T extends BasicButton> BasicButton(String name, Label textLabel, BiConsumer<T, Boolean> onClick,
			LayoutAlign inputAlign) {
		super(name);
		label = textLabel;
		addChild(textLabel);
		setFocusable(true);

		align = inputAlign;
		setLayout(align);

		addListener(new Object() {
			@Subscribe
			public void onHoverChanged(HoverEvent e) {
				requestReassembly();
			}
		});

		addListener(new Object() {
			@Subscribe
			public void onFocusChanged(FocusEvent e) {
				requestReassembly();
			}
		});

		addListener((Class<KeyEvent>) KeyEvent.class, (InputListener<KeyEvent>) e -> {
			if ((e.isLeftMouseButton() && containsCursor()) || (e.getKey() == GLFW.GLFW_KEY_ENTER && isFocused())) {
				isClicked = e.isPress();
				dispatchEvent(new ButtonEvent(this, isClicked));
				return true;
			}
			return false;
		});

		BasicButton tempButton = this;

		addListener(new Object() {
			@SuppressWarnings("unchecked") // (TODO) Make this unnecessary,
											// remove the need for a cast to T
											// hopefully
			@Subscribe
			public void onClick(ButtonEvent b) {
				onClick.accept((T) tempButton, b.isPress());
			}
		});
	}

	public abstract void trigger(boolean isPress);

	public void addDefaultListener() {
		BasicButton tempButton = this;
		addListener(new Object() {
			@Subscribe
			public void onClick(ButtonEvent b) {
				if (!tempButton.isDisabled()) {
					setClicked(b.isPress());
					trigger(b.isPress());
					takeFocus();
				}
				requestReassembly();
			}
		});
	}

	public BasicButton(String name, Label textLabel) {
		this(name, textLabel, new LayoutAlign());
	}

	public BasicButton(String name, Label textLabel, LayoutAlign layoutAlign) {
		this(name, textLabel, (bb, p) -> {
		}, layoutAlign);
	}

	public <T extends BasicButton> BasicButton(String name, Label textLabel, BiConsumer<T, Boolean> onClick) {
		this(name, textLabel, onClick, new LayoutAlign());
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean clicked) {
		isClicked = clicked;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
		setFocusable(!isDisabled);
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public Label getText() {
		return label;
	}

	// public void alignSelf()
	// {
	// align.layout(this);
	// }
}
