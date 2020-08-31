package ru.windcorp.progressia.client.graphics.font;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.model.Renderable;

public class Font {
	
	private final Typeface typeface;
	
	private final int style;
	private final float align;
	private final int color;
	
	public Font(Typeface typeface, int style, float align, int color) {
		this.typeface = typeface;
		this.style = style;
		this.align = align;
		this.color = color;
	}
	
	public Font(Typeface typeface) {
		this(typeface, Typeface.Style.PLAIN, Typeface.ALIGN_LEFT, Colors.WHITE);
	}
	
	public Font() {
		this(Typefaces.getDefault());
	}
	
	public Typeface getTypeface() {
		return typeface;
	}
	
	public int getStyle() {
		return style;
	}
	
	public float getAlign() {
		return align;
	}
	
	public int getColor() {
		return color;
	}
	
	public Renderable assemble(
			CharSequence chars, int maxWidth
	) {
		return typeface.assemble(chars, style, align, maxWidth, color);
	}

	public int getWidth(CharSequence chars, int maxWidth) {
		return typeface.getWidth(chars, style, align, maxWidth);
	}

	public int getHeight(CharSequence chars, int maxWidth) {
		return typeface.getHeight(chars, style, align, maxWidth);
	}

	public Vec2i getSize(CharSequence chars, int maxWidth, Vec2i result) {
		return typeface.getSize(chars, style, align, maxWidth, result);
	}

	public boolean supports(char c) {
		return typeface.supports(c);
	}

	/**
	 * Creates a new {@link Font} with the specified {@code style} exactly. This
	 * object's style is ignored.
	 * @param style the new style
	 * @return the new font
	 */
	public Font withStyle(int style) {
		return new Font(getTypeface(), style, getAlign(), getColor());
	}
	
	public Font deriveBold() {
		return withStyle(getStyle() | Typeface.Style.BOLD);
	}
	
	public Font deriveItalic() {
		return withStyle(getStyle() | Typeface.Style.ITALIC);
	}
	
	public Font deriveUnderlined() {
		return withStyle(getStyle() | Typeface.Style.UNDERLINED);
	}
	
	public Font deriveStrikethru() {
		return withStyle(getStyle() | Typeface.Style.STRIKETHRU);
	}
	
	public Font deriveShadow() {
		return withStyle(getStyle() | Typeface.Style.SHADOW);
	}
	
	public Font deriveNotBold() {
		return withStyle(getStyle() & ~Typeface.Style.BOLD);
	}
	
	public Font deriveNotItalic() {
		return withStyle(getStyle() & ~Typeface.Style.ITALIC);
	}
	
	public Font deriveNotUnderlined() {
		return withStyle(getStyle() & ~Typeface.Style.UNDERLINED);
	}
	
	public Font deriveNotStrikethru() {
		return withStyle(getStyle() & ~Typeface.Style.STRIKETHRU);
	}
	
	public Font deriveNotShadow() {
		return withStyle(getStyle() & ~Typeface.Style.SHADOW);
	}
	
	public Font withAlign(float align) {
		return new Font(getTypeface(), getStyle(), align, getColor());
	}
	
	public Font withColor(int color) {
		return new Font(getTypeface(), getStyle(), getAlign(), color);
	}

}
