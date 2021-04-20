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

package ru.windcorp.progressia.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.GraphicsBackend;
import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.Checkbox;
import ru.windcorp.progressia.client.graphics.gui.DynamicLabel;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.Panel;
import ru.windcorp.progressia.client.graphics.gui.RadioButton;
import ru.windcorp.progressia.client.graphics.gui.RadioManager;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.dynstr.DynamicStrings;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.ServerState;

public class LayerTestGUI extends GUILayer {

	public LayerTestGUI() {
		super("LayerTestGui", new LayoutAlign(0, 1, 5));

		Panel panel = new Panel("ControlDisplays", new LayoutVertical(5));

		Vec4 color = Colors.WHITE;
		Font font = new Font().withColor(color).deriveOutlined();

		TestPlayerControls tpc = TestPlayerControls.getInstance();

		Button disableButton = new Button("TestButton",
				new Label("TestButtonLabel", new Font().withColor(Colors.BLACK), "I'm in TestGUI"), (b, p) -> {
					b.setDisabled(!b.isDisabled());
				}, true);

		panel.addChild(disableButton.takeFocus());

		panel.addChild(new Button("TestButton2",
				new Label("TestButtonLabel2", new Font().withColor(Colors.BLACK), "I enable the above button"),
				(b, p) -> {
					disableButton.setDisabled(false);
				}, true));

		panel.addChild(new Checkbox("Checkbox1", new Label("CheckboxLabel", font, "Reset"), (c,b) -> {
			c.getText().setContentSupplier(() -> c.isActive() ? "Set" : "Reset");
			c.getText().update();
		}, true));

		RadioManager manager = new RadioManager();

		panel.addChild(new RadioButton("Radio1,1", new Label("RadioLabel1,1", font, "Option 1"), (rb,p) -> {
		}, manager, true));

		panel.addChild(new RadioButton("Radio1,2", new Label("RadioLabel1,2", font, "Option 2"), (rb,p) -> {
		}, manager, true));

		panel.addChild(
				new Label("IsFlyingDisplay", font, tmp_dynFormat("LayerTestGUI.IsFlyingDisplay", tpc::isFlying)));

		panel.addChild(new Label("IsSprintingDisplay", font,
				tmp_dynFormat("LayerTestGUI.IsSprintingDisplay", tpc::isSprinting)));

		panel.addChild(new Label("IsMouseCapturedDisplay", font,
				tmp_dynFormat("LayerTestGUI.IsMouseCapturedDisplay", tpc::isMouseCaptured)));

		panel.addChild(new Label("CameraModeDisplay", font, tmp_dynFormat("LayerTestGUI.CameraModeDisplay",
				ClientState.getInstance().getCamera()::getCurrentModeIndex)));

		panel.addChild(new Label("GravityModeDisplay", font, tmp_dynFormat("LayerTestGUI.GravityModeDisplay",
				() -> tpc.useMinecraftGravity() ? "Minecraft" : "Realistic")));

		panel.addChild(new Label("LanguageDisplay", font,
				tmp_dynFormat("LayerTestGUI.LanguageDisplay", Localizer.getInstance()::getLanguage)));

		panel.addChild(new Label("FullscreenDisplay", font,
				tmp_dynFormat("LayerTestGUI.IsFullscreen", GraphicsBackend::isFullscreen)));

		panel.addChild(new Label("VSyncDisplay", font,
				tmp_dynFormat("LayerTestGUI.IsVSync", GraphicsBackend::isVSyncEnabled)));

		panel.addChild(
				new DynamicLabel("FPSDisplay", font,
						DynamicStrings.builder().addDyn(new MutableStringLocalized("LayerTestGUI.FPSDisplay"))
								.addDyn(() -> FPS_RECORD.update(GraphicsInterface.getFPS()), 5, 1).buildSupplier(),
						128));

		panel.addChild(new DynamicLabel("TPSDisplay", font, LayerTestGUI::getTPS, 128));

		panel.addChild(
				new DynamicLabel("ChunkUpdatesDisplay", font,
						DynamicStrings.builder().addDyn(new MutableStringLocalized("LayerTestGUI.ChunkUpdatesDisplay"))
								.addDyn(ClientState.getInstance().getWorld()::getPendingChunkUpdates).buildSupplier(),
						128));

		panel.addChild(new DynamicLabel("PosDisplay", font, LayerTestGUI::getPos, 128));

		panel.addChild(new Label("SelectedBlockDisplay", font, tmp_dynFormat("LayerTestGUI.SelectedBlockDisplay",
				() -> tpc.isBlockSelected() ? ">" : " ", () -> tpc.getSelectedBlock().getId())));
		panel.addChild(new Label("SelectedTileDisplay", font, tmp_dynFormat("LayerTestGUI.SelectedTileDisplay",
				() -> tpc.isBlockSelected() ? " " : ">", () -> tpc.getSelectedTile().getId())));
		panel.addChild(new Label("PlacementModeHint", font,
				new MutableStringLocalized("LayerTestGUI.PlacementModeHint").format("\u2B04")));

		getRoot().addChild(panel);
	}

	public Runnable getUpdateCallback() {
		Collection<Label> labels = new ArrayList<>();
		getRoot().getChild(0).getChildren().forEach(c -> {
			if (c instanceof Label) {
				labels.add((Label) c);
			}
		});
		return () -> labels.forEach(Label::update);
	}

	private static class Averager {

		private static final int DISPLAY_INERTIA = 32;
		private static final double UPDATE_INTERVAL = Units.get(50.0, "ms");

		private final double[] values = new double[DISPLAY_INERTIA];
		private int size;
		private int head;

		private long lastUpdate;

		public void add(double value) {
			if (size == values.length) {
				values[head] = value;
				head++;
				if (head == values.length)
					head = 0;
			} else {
				values[size] = value;
				size++;
			}
		}

		public double average() {
			double product = 1;

			if (size == values.length) {
				for (double d : values)
					product *= d;
			} else {
				for (int i = 0; i < size; ++i)
					product *= values[i];
			}

			return Math.pow(product, 1.0 / size);
		}

		public double update(double value) {
			long now = (long) (GraphicsInterface.getTime() / UPDATE_INTERVAL);
			if (lastUpdate != now) {
				lastUpdate = now;
				add(value);
			}

			return average();
		}

	}

	private static class Queue {

		private static final int DISPLAY_INERTIA = 32;
		private static final double UPDATE_INTERVAL = Units.get(50.0, "ms");

		private final double[] values = new double[DISPLAY_INERTIA];
		private int size;
		private int head;

		private long lastUpdate;

		public void add(double value) {
			if (size == values.length) {
				values[head] = value;
				head++;
				if (head == values.length)
					head = 0;
			} else {
				values[size] = value;
				size++;
			}
		}

		public double average() {
			if (size == values.length && head != 0) {
				return (values[head - 1] - values[head]) / DISPLAY_INERTIA * 20;
			} else if (head == 0) {
				return (values[size - 1] - values[0]) / DISPLAY_INERTIA * 20;
			} else {
				return values[head - 1] / size * 20;
			}
		}

		public double update(double value) {
			long now = (long) (GraphicsInterface.getTime() / UPDATE_INTERVAL);
			if (lastUpdate != now) {
				lastUpdate = now;
				add(value);
			}

			return average();
		}

	}

	private static final Averager FPS_RECORD = new Averager();
	private static final Queue TPS_RECORD = new Queue();

	private static final Supplier<CharSequence> TPS_STRING = DynamicStrings.builder()
			.addDyn(new MutableStringLocalized("LayerTestGUI.TPSDisplay"))
			.addDyn(() -> TPS_RECORD.update(ServerState.getInstance().getUptimeTicks()), 5, 1).buildSupplier();

	private static final Supplier<CharSequence> POS_STRING = DynamicStrings.builder()
			.addDyn(new MutableStringLocalized("LayerTestGUI.PosDisplay"))
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().x, 7, 1)
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().y, 7, 1)
			.addDyn(() -> ClientState.getInstance().getCamera().getLastAnchorPosition().z, 7, 1).buildSupplier();

	private static CharSequence getTPS() {
		Server server = ServerState.getInstance();
		if (server == null)
			return Localizer.getInstance().getValue("LayerTestGUI.TPSDisplay.NA");

		return TPS_STRING.get();
	}

	private static CharSequence getPos() {
		Client client = ClientState.getInstance();
		if (client == null)
			return Localizer.getInstance().getValue("LayerTestGUI.PosDisplay.NA.Client");

		Vec3 pos = client.getCamera().getLastAnchorPosition();
		if (Float.isNaN(pos.x)) {
			return Localizer.getInstance().getValue("LayerTestGUI.PosDisplay.NA.Entity");
		} else {
			return POS_STRING.get();
		}
	}

	private static MutableString tmp_dynFormat(String formatKey, Supplier<?>... suppliers) {
		return new MutableStringLocalized(formatKey).apply(s -> {
			Object[] args = new Object[suppliers.length];

			for (int i = 0; i < suppliers.length; ++i) {
				Supplier<?> supplier = suppliers[i];

				Object value = supplier != null ? supplier.get() : "null";
				if (!(value instanceof Number)) {
					value = Objects.toString(value);
				}

				args[i] = value;
			}

			return String.format(s, args);
		});
	}

	// private static class DebugComponent extends Component {
	// private final int color;
	//
	// public DebugComponent(String name, Vec2i size, int color) {
	// super(name);
	// this.color = color;
	//
	// setPreferredSize(size);
	//
	// addListener(new Object() {
	// @Subscribe
	// public void onHoverChanged(HoverEvent e) {
	// requestReassembly();
	// }
	// });
	//
	// addListener(KeyEvent.class, this::onClicked);
	// }
	//
	// private boolean onClicked(KeyEvent event) {
	// if (!event.isMouse()) {
	// return false;
	// } else if (event.isPress() && event.isLeftMouseButton()) {
	// System.out.println("You pressed a Component!");
	// }
	// return true;
	// }
	//
	// @Override
	// protected void assembleSelf(RenderTarget target) {
	// target.fill(getX(), getY(), getWidth(), getHeight(), Colors.BLACK);
	//
	// target.fill(
	// getX() + 2, getY() + 2,
	// getWidth() - 4, getHeight() - 4,
	// isHovered() ? Colors.DEBUG_YELLOW : color
	// );
	// }
	// }
	//
	// public LayerTestGUI() {
	// super("LayerTestGui", new LayoutAlign(1, 0.75, 5));
	//
	// Panel panel = new Panel("Alex", new LayoutVertical(5));
	//
	// panel.addChild(new DebugComponent("Bravo", new Vec2i(200, 100),
	// 0x44FF44));
	//
	// Component charlie = new DebugComponent("Charlie", null, 0x222222);
	// charlie.setLayout(new LayoutVertical(5));
	//
	// //Debug
	// Localizer.getInstance().setLanguage("ru-RU");
	// MutableString epsilon = new MutableStringLocalized("Epsilon")
	// .addListener(() -> ((Label)charlie.getChild(0)).update()).format(34,
	// "thirty-four");
	// // These two are swapped in code due to a bug in layouts, fixing ATM
	// charlie.addChild(
	// new Label(
	// "Delta",
	// new Font().withColor(0xCCBB44).deriveShadow().deriveBold(),
	// "Пре-альфа!"
	// )
	// );
	// charlie.addChild(
	// new Label(
	// "Epsilon",
	// new Font().withColor(0x4444BB).deriveItalic(),
	// () -> epsilon.get().concat("\u269b")
	// )
	// );
	// panel.addChild(charlie);
	//
	//
	// charlie.addListener(KeyEvent.class, e -> {
	// if(e.isPress() && e.getKey() == GLFW.GLFW_KEY_L) {
	// Localizer localizer = Localizer.getInstance();
	// if (localizer.getLanguage().equals("ru-RU")) {
	// localizer.setLanguage("en-US");
	// } else {
	// localizer.setLanguage("ru-RU");
	// }
	// return true;
	// } return false;
	// });
	// charlie.setFocusable(true);
	// charlie.takeFocus();
	//
	// getRoot().addChild(panel);
	// }

	/*
	 * private static class Counter {
	 * 
	 * private int DISPLAY_INERTIA = 200; private long AVERAGE_TIME = 10000;
	 * private long first_time;
	 * 
	 * private final long[] values; private int size; private int head;
	 * 
	 * private long lastUpdate;
	 * 
	 * public Counter(long averageTime, int maxTPS) { DISPLAY_INERTIA = (int)
	 * averageTime*maxTPS/1000; AVERAGE_TIME = averageTime; first_time = -1;
	 * values = new long[DISPLAY_INERTIA]; }
	 * 
	 * public void add(long value) { if (first_time==-1) {
	 * first_time=System.currentTimeMillis(); } if (size == values.length) {
	 * values[head] = value; head++; if (head == values.length) head = 0; } else
	 * { values[size] = value; size++; } }
	 * 
	 * public double average() { double count=0; long ctime =
	 * System.currentTimeMillis(); for (int i=0;i<size;i++) { if
	 * ((ctime-values[i])<AVERAGE_TIME) { count++; } } if
	 * ((ctime-first_time)<AVERAGE_TIME) { if ((ctime-first_time)<10) { return
	 * 20.0; } return count/(ctime-first_time)*1000; } return
	 * count/AVERAGE_TIME*1000; }
	 * 
	 * public double update() { long now = (long) (GraphicsInterface.getTime() /
	 * .05); if (lastUpdate != now) { lastUpdate = now;
	 * add(System.currentTimeMillis()); }
	 * 
	 * return average(); }
	 * 
	 * }
	 */
}
