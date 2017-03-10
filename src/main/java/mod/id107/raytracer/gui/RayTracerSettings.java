package mod.id107.raytracer.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class RayTracerSettings extends GuiScreen {

	private final GuiScreen parentGuiScreen;
	public static final String screenTitle = "Ray Tracer Settings";
	
	public static boolean spherical = false;
	public static boolean stereoscopic = false;
	
	public RayTracerSettings(GuiScreen parentScreenIn) {
		this.parentGuiScreen = parentScreenIn;
	}
	
	@Override
	public void initGui() {
		buttonList.add(new GuiButton(18100, width/2 - 155, height/6 + 24, 150, 20, "360: " + (spherical ? "ON" : "OFF")));
		buttonList.add(new GuiButton(18101, width/2 + 5, height/6 + 24, 150, 20, "3D: " + (stereoscopic ? "ON" : "OFF")));
		//TODO eye width
		buttonList.add(new GuiButton(200, super.width / 2 - 100, super.height / 6 + 168, I18n.format("gui.done", new Object[0])));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			switch (button.id) {
			case 18100:
				spherical = !spherical;
				button.displayString = "360: " + (spherical ? "ON" : "OFF");
				break;
			case 18101:
				stereoscopic = !stereoscopic;
				button.displayString = "3D: " + (stereoscopic ? "ON" : "OFF");
				break;
			case 200:
				mc.displayGuiScreen(parentGuiScreen);
				break;
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, screenTitle, width/2, 15, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
